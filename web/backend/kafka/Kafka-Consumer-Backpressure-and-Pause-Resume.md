# Kafka Consumer Backpressure: pause()/resume() + poll 루프 설계

## 목표
Kafka Consumer가 처리량 부족/외부 의존성(DB, HTTP, Downstream Queue) 병목으로 인해 **Lag가 증가할 때**, 무조건적인 scale-out 이전에 다음을 안전하게 설계한다.

- `poll()` 루프에서 처리/커밋/리밸런싱 타임아웃을 깨지 않기
- 처리량(throughput)과 지연(latency) 사이의 의도된 트레이드오프 만들기
- 장애 시 중복 처리(at-least-once) 범위를 통제하기

> 핵심 결론: 백프레셔는 "더 적게 가져오거나(max.poll.records)" + "가져온 뒤 잠시 멈추거나(pause)" + "커밋 지점을 명확히 하거나"의 조합으로 푼다.

---

## 1) 어디서 문제가 시작되나: poll/processing/commit 3단 분리

Kafka consumer는 개념적으로 아래 3단계를 반복한다.

```
+---------+      +----------------+      +------------------+
|  poll   | ---> |   processing   | ---> |  commit (offset) |
+---------+      +----------------+      +------------------+
     |                    |                       |
     |                    |                       |
     v                    v                       v
브로커에서 가져옴     비즈니스 처리           "여기까지 읽었다" 저장
```

- `poll()`이 멈추면(너무 오래 처리만 하면) **max.poll.interval.ms** 위반으로 그룹에서 퇴출 → 리밸런싱 유발
- heartbeat가 끊기면 **session.timeout.ms** 위반 → 리밸런싱
- 커밋 타이밍을 잘못 잡으면
  - 너무 빨리 커밋 → at-most-once(처리 누락 가능)
  - 너무 늦게 커밋 → at-least-once(중복 가능) + 리밸런싱 시 재처리 범위 확대

---

## 2) pause()/resume()는 "가져온 뒤" 백프레셔다

### pause()/resume()의 의미
- `pause(partitions)`는 **해당 파티션에 대한 fetch를 일시 중지**한다.
- 이미 `poll()`로 받아온 레코드는 그대로 애플리케이션이 들고 있으며, 그 레코드 처리 자체를 취소해주진 않는다.

즉, pause는 아래 상황에서 가치가 크다.

- 처리 큐/스레드풀/DB 커넥션 풀이 꽉 차서 "더 가져오면" 메모리 폭증/지연 급증이 예상될 때
- 특정 파티션만 병목인 경우(핫키/특정 shard), **파티션 단위**로 intake를 조절하고 싶을 때

### 전형적인 패턴: 내부 큐 수위로 intake 조절

```
[Kafka poll] -> [in-memory queue] -> [workers] -> [db/http]
                 ^
                 |
           high/low watermark
```

- high watermark 초과 시: `pause(assignedPartitions)`
- low watermark 이하 시: `resume(assignedPartitions)`

> 주의: pause 상태에서도 heartbeat/poll 자체는 주기적으로 돌려야 한다. (그렇지 않으면 max.poll.interval 위반)

---

## 3) max.poll.interval.ms vs session.timeout.ms: 자주 헷갈리는 포인트

- `session.timeout.ms` / `heartbeat.interval.ms`
  - **브로커(Coordinator) 관점**에서 "이 consumer가 살아있나" 감시
- `max.poll.interval.ms`
  - **클라이언트 라이브러리 관점**에서 "너무 오래 poll을 안 불렀다" 감시

실무적으로는 "처리가 느려서" 터지는 건 대부분 `max.poll.interval.ms`다.

### 대응 가이드
- 처리 시간이 길어질 수 있으면
  - `max.poll.records`를 줄여 **poll 1회당 처리량**을 제한
  - 처리 스레드/큐를 분리하되 poll thread는 주기적으로 poll
  - 필요 시 `max.poll.interval.ms`를 늘리되, 무한대로 늘려 문제를 숨기지 말 것

---

## 4) 커밋 전략과 백프레셔의 결합

### (A) processing 완료 후 커밋 (권장 기본값)
- 레코드 처리 성공 → 그 레코드까지 커밋
- 실패/재시작 시: 중복 처리 가능(멱등성 필요)

### (B) 배치 커밋 + pause
- 큐가 과도하게 쌓이는 상황에서는
  - intake를 pause로 멈추고
  - "처리 완료된" 레코드 범위만 배치로 커밋

### (C) 외부 트랜잭션(DB)과의 정합성
- DB에 먼저 반영하고 커밋하면: 중복 처리 시 DB unique/upsert로 방어(멱등)
- Outbox 패턴을 쓰면: DB 트랜잭션 안에서 이벤트를 outbox에 기록 → 별도 CDC/relay

---

## 5) 운영 체크리스트 (Runbook)

### 증상별 1차 판단
- Lag 증가 + CPU/DB 부하 증가
  - 처리량 부족(정상) → scale-out 또는 병목 제거
- Lag 증가 + consumer 재시작/리밸런싱 반복
  - poll loop 문제(설정/코드) 가능성 ↑

### 즉시 확인(현장 5분)
- `max.poll.interval.ms` 위반 로그가 있는가?
- 리밸런싱 빈도가 급증했는가?
- 특정 파티션만 Lag이 큰가?
- DB 커넥션 풀/외부 API rate limit이 병목인가?

### 코드 레벨 점검
- poll thread가 processing에 "묶여" 있지 않은가?
- 커밋 위치가 processing 성공과 동기화되어 있는가?
- pause/resume이 적용되어도 poll이 계속 돌고 있는가?

---

## 참고문헌
- Apache Kafka Javadoc: `KafkaConsumer` (pause/resume, poll/commit 개념) — https://kafka.apache.org/39/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html
- Apache Kafka Javadoc: `ConsumerConfig` (max.poll.*, session.timeout.*, heartbeat.*) — https://kafka.apache.org/39/javadoc/org/apache/kafka/clients/consumer/ConsumerConfig.html
- KIP-429: Incremental Cooperative Rebalance (리밸런싱 다운타임 완화 배경) — https://cwiki.apache.org/confluence/display/KAFKA/KIP-429%3A+Kafka+Consumer+Incremental+Rebalance+Protocol
