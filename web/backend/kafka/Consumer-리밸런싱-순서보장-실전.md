# Kafka Consumer 리밸런싱(Rebalancing) & 순서 보장(Ordering) — 실전 가이드

## 목표
- 리밸런싱이 발생해도 **처리 중단/중복/순서 꼬임**을 최소화한다.
- “Kafka가 순서를 보장한다”의 범위를 정확히 정의하고, **앱 레벨에서 지켜야 할 규칙**을 정리한다.

---

## 1. Kafka의 순서 보장 범위(팩트)

### 1.1 Partition 단위 순서
- Kafka는 **같은 partition 안의 record 순서**를(로그 append 순서) 제공한다.
- 즉, **전역(Topic 전체) 순서**는 보장하지 않는다.

```
Topic
  P0:  a1 -> a2 -> a3
  P1:  b1 -> b2 -> b3

"a"와 "b"의 전역 순서는 정의되지 않음
```

### 1.2 Consumer Group에서의 순서
- 같은 consumer group에서는 **한 partition은 동시에 한 consumer에게만 할당**된다.
- 따라서 정상 상태에서는 partition 단위로 **단일 소비자(단일 스레드 처리 기준)**가 순서를 유지한다.

> 주의: 애플리케이션이 partition 내 메시지를 **병렬 처리**하면 순서는 쉽게 깨진다.

---

## 2. 리밸런싱이 일어나는 이유(실전에서 자주 보는 케이스)

- consumer 프로세스 재시작/배포
- consumer 추가/삭제(오토스케일)
- `max.poll.interval.ms` 초과(처리가 너무 오래 걸려서 “죽었다”고 판단)
- 네트워크 단절/GC pause
- coordinator 장애/리더 변경

**결론**: 리밸런싱은 “예외”가 아니라 “상시 발생 가능한 정상 이벤트”다.

---

## 3. 리밸런싱이 순서/중복을 깨는 메커니즘

### 3.1 커밋(Commit)과 처리(Processing)의 불일치
- Kafka 커밋은 “이 offset까지는 처리 완료했다”라는 **체크포인트**다.
- 리밸런싱 시, 새 consumer는 **마지막 커밋 offset부터 재시작**한다.

따라서 아래가 중요하다.
- **처리 완료 전에 커밋**하면 → 장애/리밸런싱 시 **유실 가능**
- **처리 완료 후 커밋**하면 → 리밸런싱 타이밍에 따라 **중복 가능(At-least-once)**

```
(권장) 처리 완료 -> 커밋

처리 시작 -----처리 완료----커밋
                ^
        여기서 리밸런싱/크래시
        => 재시작 시 중복 처리 발생 가능
```

**결론**: 대부분의 운영 시스템은 기본적으로 **중복을 허용**하고, 멱등성(Idempotency)으로 정리한다.

---

## 4. 실전 운영 규칙(체크리스트)

### 4.1 순서를 지키려면 “Partition 내 처리”는 직렬로
- 같은 partition에 대해:
  - 단일 스레드 처리
  - 혹은 “키 단위로 순서 보장”이 목표면, **key를 partition key로 고정**

### 4.2 `max.poll.interval.ms` vs 처리시간
- 처리 시간이 긴 작업이 있다면:
  - 처리 시간을 줄이거나(배치 분해)
  - `max.poll.interval.ms`를 늘리되,
  - `max.poll.records`를 줄여 “poll 후 처리 시간”이 너무 길어지지 않게 한다.

### 4.3 리밸런싱 시점 안전장치: Cooperative Rebalancing 고려
- 가능한 경우 `cooperative-sticky`(incremental cooperative rebalancing)을 사용하면
  전체 stop-the-world 리밸런싱보다 충격이 줄어든다.

### 4.4 커밋 전략
- **자동 커밋(auto.commit)**은 실전에서 사고가 많아 보통 비권장
- 수동 커밋을 기본으로 두고,
  - 처리 성공 시 커밋
  - 실패 시 재시도/보류/ DLQ 정책을 명확히

### 4.5 중복 처리 대비(필수)
- “정확히 한 번”을 목표로 하기보다,
  - DB upsert / unique key / 상태머신 / outbox 등으로 멱등 처리

---

## 5. 추천 구성(안전한 기본형)

- partition key 설계(업무 키 기반)
- consumer는 partition 단위 직렬 처리
- 수동 커밋
- 멱등 처리(중복 허용)
- cooperative-sticky + 모니터링(리밸런스 횟수/지연)

---

## 참고(공식/준공식)
- Apache Kafka Documentation: Consumer Configs, Consumer Groups
- KIP-429 (Incremental Cooperative Rebalancing)
- Confluent Blog: consumer rebalancing / max.poll.interval 관련 글
