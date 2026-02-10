# Transactional Outbox + CDC(Debezium) 운영 체크리스트

> 목적: Outbox+CDC 패턴을 "개념"이 아니라 **운영 가능한 시스템**으로 만들기 위한 실패 모드/모니터링/재처리 전략을 정리한다.

---

## 1. Outbox+CDC가 해결하는 것 / 해결하지 못하는 것

### 해결하는 것
- DB 트랜잭션과 메시지 브로커(예: Kafka) 사이의 **원자성(atomicity) 간극**
- 애플리케이션 크래시/네트워크 이슈로 인한 "DB는 커밋, 이벤트 발행은 실패" 문제

### 해결하지 못하는 것(현실적인 주의점)
- 이벤트 처리 소비자(Consumer) 측 **업무 멱등성**은 여전히 필요
- 스키마 진화(schema evolution) 정책이 없으면 결국 장애로 귀결
- Outbox 테이블이 방치되면 DB 성능/디스크에 직접 악영향

---

## 2. 기본 구성과 데이터 흐름

ASCII 다이어그램:

```
[App Service]
  |  (1) DB TX
  v
[Business Tables] + [Outbox Table]
  |  (2) Debezium reads WAL/binlog
  v
[Debezium Connector]
  |  (3) produce
  v
[Kafka Topic]
  |  (4) consume
  v
[Downstream Service]
```

---

## 3. 실패 모드(운영에서 자주 터지는 케이스)

### 3.1 Debezium 지연(Lag) 증가
증상:
- Outbox 적재는 정상인데 Kafka 토픽 반영이 늦어짐

가능 원인:
- 커넥터 처리량 부족(배치/폴링/스냅샷 설정)
- Kafka produce 병목
- DB WAL/binlog 생성량 급증

대응:
- 커넥터별 lag 지표 모니터링(소스별 offset)
- Outbox 테이블 적재량/최대 지연시간 SLA 정의

### 3.2 중복 이벤트(At-least-once)
증상:
- 다운스트림에서 동일 이벤트가 2번 처리됨

원인:
- 재시도, 리밸런싱, 커넥터 재시작, 네트워크 일시 장애

대응(필수):
- 다운스트림은 "정확히 한 번"을 가정하지 말고 **업무 멱등성 키**로 중복 제거
- 이벤트에 `event_id`(UUID) + `aggregate_id` + `version` 권장

### 3.3 순서 역전(Out-of-order)
증상:
- 같은 aggregate에 대한 이벤트가 순서대로 도착하지 않음

원인:
- 파티셔닝 키 설계 실패(aggregate 기준으로 파티션 고정이 안 됨)

대응:
- Kafka key를 `aggregate_id`로 고정
- 다운스트림은 version 기반으로 적용(낮은 version discard)

### 3.4 Outbox 테이블 무한 성장
증상:
- DB 용량 증가, 쿼리 성능 저하, vacuum/autovacuum 부담 증가

대응:
- Outbox row lifecycle 명확화
  - (권장) "발행 완료" 마킹 후 TTL 삭제
  - 또는 Debezium 토픽/오프셋 기준으로 안전 삭제(주의: 복잡도 증가)
- 파티셔닝/아카이빙 고려

---

## 4. 메시지 스키마(계약) 권장안

최소 필드:
- `event_id`: 전역 유니크(중복 제거)
- `event_type`: 라우팅/핸들러 선택
- `occurred_at`: 이벤트 발생 시각
- `aggregate_id`: 파티셔닝 키
- `version`: 순서/정합성 제어
- `payload`: 비즈니스 데이터

권장(추가):
- `trace_id`: 분산 추적
- `producer`: 서비스명/버전

---

## 5. 모니터링 체크리스트

### 5.1 DB/Outbox
- Outbox 테이블 row count / 증가율
- Outbox 최대 적재 시간(현재 시각 - outbox.occurred_at)
- 삭제/아카이브 작업 실패 여부

### 5.2 Debezium
- 커넥터 상태(RUNNING/FAILED)
- source offset lag
- 에러 로그(스키마 변경/권한/네트워크)

### 5.3 Kafka
- produce/consume 에러율
- consumer lag
- DLQ(Dead Letter Queue) 발생량(운영 정책 선택 시)

---

## 6. 재처리(Replay) 전략

Outbox+CDC에서 재처리는 "가능"하지만 **싸게 하려면 설계가 필요**하다.

선택지:
1) Kafka 토픽 리텐션 기반 replay
- 장점: 가장 단순
- 단점: 리텐션 밖은 불가

2) Outbox 원본 기반 replay
- 장점: 장기 보관 가능
- 단점: DB 부담, 삭제 정책과 충돌

3) DLQ + 수동/자동 재처리
- 장점: 특정 실패 케이스 집중
- 단점: 운영 플로우 추가

공통 전제:
- 소비자 멱등성(중복 허용 설계)

---

## 7. 오늘의 결론
- Outbox+CDC는 "원자성"만 해결한다. 운영 난이도는 **스키마/멱등성/청소/관측**에서 결정된다.
- 가장 ROI 높은 3가지는:
  1) 이벤트 ID + 멱등성
  2) lag/적재시간 모니터링
  3) Outbox cleanup(수명주기) 자동화

## 참고
- 기존 개념 정리: `web/backend/msa/pattern/TransactionalOutBox.md`
- Kafka EOS 문서: `web/backend/kafka/EOS.md`
