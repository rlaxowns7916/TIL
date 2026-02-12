# Kafka Offset Commit과 처리 보장(Processing Guarantees) 정리

## 목적
- Kafka Consumer의 **offset commit**이 무엇을 의미하는지(= “어디까지 *읽었다/처리했다*고 간주할지”의 체크포인트) 정확히 정리한다.
- commit 타이밍에 따라 달라지는 **at-most-once / at-least-once / exactly-once(EOS)** 를 구분한다.
- Web/API + DB + Kafka 환경에서 실무적으로 쓰는 안전한 패턴(멱등성, outbox, 트랜잭션)을 연결한다.

---

## 1) 오프셋(offset)과 커밋(commit)의 의미
- Kafka의 offset은 파티션 로그에서 레코드의 위치(증분 번호)다.
- Consumer Group은 “각 파티션을 어디까지 진행했는지”를 **committed offset**으로 저장한다.
- 재시작/리밸런싱 시 consumer는 보통 **마지막 committed offset 이후부터** 다시 읽는다.

### 핵심 포인트
- **commit은 처리(process)가 아니라 “체크포인트 기록”** 이다.
- 커밋이 빠르면 유실 위험, 늦으면 중복 위험이 커진다.

---

## 2) commit 타이밍으로 보는 3가지 처리 보장

### (A) At-most-once (중복은 없지만 유실 가능)
“처리 전에 commit”하면 장애 시 레코드가 유실될 수 있다.

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
“처리 후 commit”하면 유실은 줄지만, commit 전에 장애가 나면 중복 처리된다.

```
Time →

poll() -> record R1
process(R1)
commit(offset=R1)

(처리 후 commit 전에 장애가 나면?)
- committed offset이 R1 이전
- 재시작 시 R1을 다시 읽음 => 중복
```

### (C) Exactly-once (EOS)
정리 포인트가 2가지다.
- **Kafka 내부(Kafka-to-Kafka)** 파이프라인(예: Kafka Streams)에서는 트랜잭션/EOS로 *실질적인 exactly-once*에 근접 가능
- **DB/외부 시스템을 끼우는 end-to-end**에서는 “완전한 exactly-once”가 보통 비싸고 복잡하며, 결국 **멱등/업서트/중복 허용 설계**가 필요해지는 경우가 많다.

실무 결론:
- 기본은 **at-least-once + 멱등 처리**가 표준이다.

---

## 3) auto commit vs manual commit

### enable.auto.commit=true (자동 커밋)
- 주기적으로 마지막 poll된 offset을 커밋하는 방식.
- 처리 시간이 길거나, 처리 실패가 섞이면 **유실/중복의 경계가 흐려져 운영이 어려워진다.**

### enable.auto.commit=false (수동 커밋 권장)
- 메시지를 “성공적으로 처리했다”고 판단한 시점에 커밋한다.

대표 구조:

```
for record in poll():
  ok = process(record)          # DB 저장/외부 호출 포함
  if ok:
    markSuccess(record)

commit(offset=lastSuccess)
```

---

## 4) commit API 선택: commitSync vs commitAsync

### commitSync()
- 장점: **성공/실패를 호출 스레드에서 확정**할 수 있어 단순함
- 단점: 동기 블로킹 → 처리량 저하 가능

### commitAsync()
- 장점: 지연이 낮고 처리량에 유리
- 단점: 실패 시 재시도/보정 로직이 필요(콜백 기반)

실무 가이드(보수적 기본값):
- **평상시 commitAsync + 종료/리밸런싱 직전 commitSync 1회** 패턴이 흔하다.

---

## 5) 리밸런싱과 커밋: “중복 범위”를 결정하는 구간
리밸런싱은 운영에서 자주 발생한다(스케일링/배포/장애/세션 타임아웃).

- 파티션이 revoke(회수)되기 전에
  - in-flight 처리를 정리하고
  - “안전한 지점”까지 커밋하지 않으면
  - **재할당 후 재처리 범위가 커진다.**

개념 구조:

```
[assigned partitions]
   | poll
   v
[processing]
   | success
   v
[commit checkpoint]

(rebalance 발생)
- revoke 전에 checkpoint를 최대한 앞으로 당기면 중복 범위가 줄어듦
```

(언어/라이브러리별로 Rebalance Listener 훅 제공)

---

## 6) 실무 패턴 1: Consumer 측 멱등 처리(Idempotency)
At-least-once에서 중복은 “정상”이므로, 중복을 견디게 만드는 쪽이 비용 대비 효과가 크다.

### 대표 방식
- 이벤트에 `event_id`를 포함
- consumer는 `event_id`를 dedup store(예: DB UNIQUE)로 기록
- 이미 처리된 `event_id`면 skip

```
             +------------------+
Kafka -----> | Consumer         |
             | 1) dedup insert  |----(duplicate)----> skip
             | 2) do business   |
             | 3) commit        |
             +------------------+
```

---

## 7) 실무 패턴 2: DB 작업과 commit의 정합성(가장 흔한 함정)
- DB 트랜잭션은 DB에만 원자성 제공
- offset commit은 Kafka group metadata에만 반영

즉, 일반적으로 다음은 불가능하다.
- “DB commit과 offset commit을 하나의 원자 트랜잭션으로 묶기”

현실적인 목표는 다음 중 하나다.
- (권장) **중복 처리를 허용하고 멱등하게 만든다**
- (특정 구성) Kafka Transaction(EOS)을 활용하는 아키텍처를 채택한다(제약/비용 고려)

---

## 8) 체크리스트
- [ ] `enable.auto.commit=false`로 두고, 성공 기준을 명확히 했는가?
- [ ] 처리 실패 시 재시도 전략(재처리 / DLQ / backoff)이 있는가?
- [ ] 중복 처리(duplicate)를 견딜 수 있도록 consumer 멱등성을 구현했는가?
- [ ] 리밸런싱 시점(revoke/assign)에서 in-flight 작업/커밋을 안전하게 다루는가?
- [ ] 운영 메트릭(consumer lag, rebalance 빈도, 처리 실패율)이 알림으로 연결되어 있는가?

---

## 참고 문헌 (공식/준공식)
- Apache Kafka Documentation — Consumer Configs / Semantics
  - https://kafka.apache.org/documentation/
  - https://kafka.apache.org/documentation/#consumerconfigs
  - https://kafka.apache.org/documentation/#semantics
- Apache Kafka Javadoc — `KafkaConsumer` / `ConsumerConfig`
  - https://kafka.apache.org/39/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html
  - https://kafka.apache.org/39/javadoc/org/apache/kafka/clients/consumer/ConsumerConfig.html
- Confluent Docs — Processing Guarantees / Consumer Offsets
  - https://docs.confluent.io/
