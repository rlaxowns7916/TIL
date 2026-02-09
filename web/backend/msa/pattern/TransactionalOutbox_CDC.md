# Transactional Outbox + CDC(Debezium) 패턴

## 0) 한 줄 요약
DB 트랜잭션 안에서 **도메인 변경 + Outbox 이벤트 기록**을 함께 커밋하고, CDC(Change Data Capture)가 Outbox 테이블을 스트리밍하여 Kafka 등으로 전달함으로써 **DB와 메시지 브로커 간 일관성**을 실무적으로 달성하는 패턴.

---

## 1) 왜 필요한가: “DB 업데이트 + 이벤트 발행”의 원자성 문제

### 흔한 실패 시나리오
- 케이스 A: DB 커밋 성공 → 메시지 발행 실패
  - 다운스트림(검색 인덱스/캐시/다른 서비스)이 갱신을 못 받아 **데이터 불일치**
- 케이스 B: 메시지 발행 성공 → DB 커밋 실패
  - 이벤트는 나갔는데 DB는 롤백되어 **유령 이벤트** 발생

### 2PC로 풀면 되지 않나?
- 분산 트랜잭션(2PC)은 복잡도/운영비용/성능/가용성 측면에서 실무 적용이 어렵거나 피하는 경우가 많다.

---

## 2) 핵심 아이디어

### 원칙
- “메시지 브로커에 보내는 행위”를 애플리케이션 트랜잭션에서 분리한다.
- 대신 **Outbox 테이블에 이벤트를 기록하는 것**을 DB 트랜잭션의 일부로 포함한다.

### 구조(개념)

```
[App Service]
   |
   | 1) BEGIN TX
   | 2) UPDATE domain tables
   | 3) INSERT outbox_event
   | 4) COMMIT
   v
[DB]
   |
   | (CDC reads WAL/binlog)
   v
[Debezium Connector] ---> [Kafka Topic(s)] ---> [Consumers]

(Consumers는 idempotent 처리 + 재처리 내성 필요)
```

---

## 3) 데이터 모델(권장 필드)

예시: outbox_event
- `id` (UUID / ULID) : 이벤트 식별자(컨슈머 멱등 처리 키)
- `aggregate_type` : 예) Order
- `aggregate_id` : 예) orderId
- `event_type` : 예) OrderCreated
- `payload` (JSON) : 이벤트 본문
- `occurred_at` (timestamp)
- `trace_id` / `span_id` (옵션) : 추적성
- `schema_version` (옵션) : 진화 대응

주의:
- **컨슈머 멱등성**은 사실상 필수다. “Exactly-once delivery”가 아니라 “effectively-once processing”로 접근.

---

## 4) 전달 방식 2가지: Polling vs CDC

### (1) Polling Publisher(애플리케이션이 주기적으로 Outbox를 읽어 publish)
장점
- 단순(커넥터/CDC 인프라 없이 가능)
단점
- 폴링 지연/부하, 락/경쟁, 스케줄러 장애 처리 등 운영 요소가 늘어남

### (2) CDC(Debezium) 기반
장점
- DB 로그(WAL/binlog)를 기반으로 변경을 스트리밍 → 낮은 지연, 높은 신뢰
- “Outbox 행이 커밋된 사실”이 곧 스트림에 반영됨
단점
- Debezium/Connect 운영(커넥터 설정, 리밸런스, 스키마/권한, 모니터링)

---

## 5) Ordering(순서)와 중복(멱등) 전략

### Ordering
- “단일 aggregate(예: orderId) 단위 순서”가 필요하면
  - Kafka key를 `aggregate_id`로 설정하여 **같은 키가 같은 파티션**으로 가게 한다.
  - 이벤트 버전에 `version`(증분)을 두고 컨슈머가 gap/역순을 감지하도록 할 수 있다.

### Duplicates
- CDC/브로커/컨슈머 어떤 계층에서도 재전송/재처리 가능
- 컨슈머는 최소한 다음 중 하나를 갖춰야 한다.
  - (권장) `event_id` 기반 처리 로그(Processed Events 테이블)
  - UPSERT/unique constraint 등 DB 제약으로 멱등 보장

---

## 6) 장애/운영 관점 체크리스트

- Debezium 커넥터 재시작/재배포 시
  - 오프셋(Connect offsets) 관리가 중요
- Outbox 테이블 무한 증가 방지
  - TTL/아카이빙 정책 필요(단, “전달 완료” 기준 정의 필요)
- 스키마 진화
  - payload 버전/스키마 레지스트리(선택) 고려
- 관측성
  - outbox insert rate, lag(커밋→토픽 도착 지연), DLQ, 컨슈머 리트라이율

---

## 7) 참고자료(공식/권위 있는 소스)
- Debezium Documentation: Outbox Event Router(패턴 및 커넥터 구성)
- Confluent Documentation: Change Data Capture(CDC) & Kafka Connect 개요
- Microsoft Architecture(권고안): Transactional Outbox / Integration Events(마이크로서비스 통합 이벤트)
