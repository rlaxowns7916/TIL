# Kafka Offset Commit과 처리 보장(Processing Guarantees) 정리

## 목적
- Kafka Consumer의 **offset commit**이 무엇을 의미하는지(= "어디까지 처리했다"의 기록) 정확히 정리한다.
- commit 타이밍에 따라 달라지는 **at-most-once / at-least-once / exactly-once(현실적 의미)** 를 구분한다.
- Web/API + DB + Kafka 환경에서 실무적으로 쓰는 안전한 패턴(멱등성, outbox, 트랜잭션)을 연결한다.

---

## 1) 오프셋(offset)과 커밋(commit)의 의미
- Kafka의 offset은 파티션 로그에서 레코드의 위치(증분 번호)다.
- consumer group은 "각 파티션을 어디까지 읽었는지"를 **committed offset**으로 저장한다.
- 재시작/리밸런싱 시 consumer는 일반적으로 **마지막 committed offset 이후부터** 다시 읽는다.

### 핵심 포인트
- **commit은 처리(process)가 아니라 "진도 기록"** 이다.
- 커밋이 빠르면 유실 위험, 늦으면 중복 위험이 커진다.

---

## 2) commit 타이밍으로 보는 3가지 처리 보장

### (A) At-most-once (중복은 없지만 유실 가능)
"처리 전에 commit"하면 장애 시 레코드가 유실될 수 있다.

```
Time →

poll() -> record R1
commit(offset=R1)   # 먼저 진도 기록
process(R1)

(여기서 장애가 나면?)
- committed offset은 이미 R1을 넘어섬
- 재시작 시 R1은 다시 읽지 않음 => 유실
```

### (B) At-least-once (유실은 줄지만 중복 가능)
"처리 후 commit"하면 유실은 줄지만, commit 전에 장애가 나면 중복 처리된다.

```
Time →

poll() -> record R1
process(R1)
commit(offset=R1)

(처리 후 commit 전에 장애가 나면?)
- committed offset이 R1 이전
- 재시작 시 R1을 다시 읽음 => 중복
```

### (C) Exactly-once ("소비자만"으로는 보통 불가능, 시스템적으로 설계)
Kafka는 특정 조건에서 EOS를 제공하지만,
- "DB 업데이트 + 메시지 발행"까지 포함하는 end-to-end exactly-once는
  **대부분의 마이크로서비스 환경에서 비용이 크거나, 구성 제약이 있다.**

실무 결론:
- 기본은 **at-least-once + 멱등 처리**가 표준이다.

---

## 3) auto commit vs manual commit

### enable.auto.commit=true (자동 커밋)
- 주기적으로 마지막 poll된 offset을 커밋하는 방식.
- 처리 시간이 길거나, 처리 실패가 섞이면 **유실/중복을 예측하기 어렵다.**

### enable.auto.commit=false (수동 커밋 권장)
- 메시지를 "내가 성공적으로 처리했다"고 판단한 시점에 커밋한다.
- 보통 다음과 같이 설계한다.

```
for record in poll():
  process(record)             # DB 저장/외부 호출 포함
  markSuccess(record)

commit(offset=lastSuccess)
```

---

## 4) 실무 패턴 1: 멱등 처리(Consumer Side Idempotency)
At-least-once에서 중복은 "정상"이므로, 중복을 견디게 만드는 쪽이 비용 대비 효과가 크다.

### 대표 방식
- 이벤트에 `event_id`를 포함
- consumer는 `event_id`를 dedup store에 기록(UNIQUE)
- 이미 처리된 event_id면 skip

```
             +------------------+
Kafka -----> | Consumer         |
             | 1) dedup insert  |----(duplicate)----> skip
             | 2) do business   |
             | 3) commit        |
             +------------------+
```

장점:
- 리밸런싱/재시작/재처리에도 안전
- offset commit 전략을 단순화(= 늦게 커밋해도 중복 비용이 제한됨)

---

## 5) 실무 패턴 2: DB 작업과 commit의 정합성(가장 흔한 함정)

### 문제: DB 커밋과 Kafka offset commit은 "서로 다른 시스템"
- DB 트랜잭션은 DB에만 원자성 제공
- offset commit은 Kafka group metadata에만 반영

즉, 다음이 불가능하다:
- "DB commit과 offset commit을 하나의 원자 트랜잭션"으로 묶기

### 따라서 목표는 현실적으로 다음 중 하나다
- (권장) **중복 처리를 허용하고 멱등하게 만든다**
- (특정 구성) Kafka Transaction(EOS)을 활용하는 아키텍처를 쓴다(제약 있음)

---

## 6) 실무 패턴 3: Kafka Transaction(EOS)을 쓸 때의 현실적 범위
Kafka는 Producer/Consumer 조합에서 EOS를 제공할 수 있다.
대표 시나리오:
- Kafka에서 읽고(Kafka consumer)
- Kafka에 쓰는(Kafka producer) "Kafka-to-Kafka" 처리(예: Kafka Streams)

핵심은 다음 개념들이다.
- Producer idempotence
- Transactions
- read_committed / isolation.level

> 하지만 "DB 업데이트"를 끼우면 end-to-end exactly-once가 깨지기 쉽고,
> 결국 DB 쪽 멱등성/업서트/버전 관리가 필요해지는 경우가 많다.

---

## 7) 커밋 전략 체크리스트
- [ ] `enable.auto.commit=false`로 두고, 성공 기준을 명확히 했는가?
- [ ] 처리 실패 시 재시도 전략(재처리/ DLQ / backoff)이 있는가?
- [ ] 중복 처리(duplicate)를 견딜 수 있도록 consumer 멱등성을 구현했는가?
- [ ] 리밸런싱 시점(revoke/assign)에서 in-flight 작업/커밋을 안전하게 다루는가?
- [ ] 운영 메트릭(consumer lag, rebalance 빈도, 처리 실패율)이 알림으로 연결되어 있는가?

---

## 참고 문헌 (공식/준공식)
- Apache Kafka Documentation (Consumer / Offset / EOS 관련)
  - https://kafka.apache.org/documentation/
- Confluent Docs — Consumer Offsets / Processing Guarantees / EOS
  - https://docs.confluent.io/
- "Exactly-once semantics" 개념(카프카 트랜잭션/격리 수준)
  - https://kafka.apache.org/documentation/#semantics
