# Kafka Exactly-Once Semantics(EOS) 정리

> 용어 주의
> - **Exactly-Once Semantics(EOS)**: “중복 없이 한 번만 처리된 것처럼 보이는” *처리 관점*의 의미(엔드-투-엔드 보장에는 설계가 필요)
> - **Idempotent Producer**: 프로듀서 → 브로커 쓰기 구간에서 중복 저장을 최대한 방지(동일 PID/시퀀스 기반)
> - **Transactions**: 여러 레코드(여러 파티션/토픽 가능)와 컨슈머 오프셋 커밋을 하나의 원자 단위로 묶어 **read_committed** 컨슈머에게 “커밋된 것만” 보이게 함

---

## 1) Idempotent Producer (enable.idempotence)

### 목적
네트워크 재시도/타임아웃 등으로 인해 **동일 레코드가 중복 전송**되는 상황에서, 브로커 로그에 **중복 저장되는 것을 방지**한다.

### 핵심 메커니즘
- 프로듀서는 브로커에 레코드를 보낼 때 **(PID, sequence number)** 를 함께 전송한다.
- 브로커는 파티션별로 `(PID, sequence)`를 추적하여
  - 이미 처리한 sequence면 **중복으로 간주하고 저장하지 않되 ack는 응답**한다.
  - sequence가 예상보다 “앞서” 도착하면 **OutOfOrderSequenceException** 등으로 실패 처리하여 재시도를 유도한다.

### 제약/주의
- 시퀀스는 **PID-파티션 단위**로 증가한다.
- 기본적으로 프로듀서 재시작/세션 변경 시 PID가 바뀔 수 있어(환경/설정에 따라) **완전한 엔드-투-엔드 EOS**를 단독으로 보장하지는 않는다.
- Idempotent Producer는 “브로커에 **정확히 한 번 저장**”에 초점이고, “다운스트림에서 **정확히 한 번 처리**”는 별도 설계가 필요하다.

### 빠른 설정 체크리스트
- `enable.idempotence=true`
- `acks=all` (idempotence 활성화 시 내부적으로 강제되는 조건 중 하나)
- `retries` 및 `max.in.flight.requests.per.connection`는 Kafka 버전/클라이언트 가이드에 맞춰 확인(순서 보장과 상충 가능)

---

## 2) Kafka Transactions (transactional.id)

### 목적
다음 2가지를 한 덩어리(원자)로 묶는다.
1) 프로듀서가 보낸 레코드들
2) 해당 레코드들을 처리한 컨슈머의 **오프셋 커밋**

즉, “처리 결과(쓰기)와 오프셋 커밋이 함께 커밋/중단”되게 만들어 **중복 처리/유실**을 줄이는 기반을 제공한다.

### 구성 요소
- **Transaction Coordinator**: `transactional.id` 기준으로 트랜잭션 상태/프로듀서 PID 매핑을 관리
- **Transaction Log(내부 토픽)**: 트랜잭션 메타데이터/상태 저장
- **컨슈머 isolation.level=read_committed**
  - `read_committed`: 커밋된 트랜잭션의 레코드만 읽음(Abort된 트랜잭션은 건너뜀)
  - `read_uncommitted`: Abort된 트랜잭션 레코드까지 읽을 수 있음

### 동작 흐름(개념)

```
Producer                    Coordinator/Broker                    Consumer(read_committed)
   | initTransactions()  ->  find coordinator + PID mapping
   | beginTransaction()
   | send(records...)    ->  append as "transactional" records
   | sendOffsetsToTransaction(offsets)
   | commitTransaction() ->  mark txn COMMITTED
                                                           ->     now visible

(에러 시)
   | abortTransaction()  ->  mark txn ABORTED
                                                           ->     not visible
```

### 주의: “EOS”의 범위
Kafka 트랜잭션은 강력하지만, 아래가 충족되어야 실질적인 “정확히 한 번처럼 보이는 처리”에 근접한다.
- 출력 토픽/DB/외부 시스템까지 포함한 설계(예: **Transactional Outbox**, idempotent consumer)
- 재처리/중복에 대한 **비즈니스 키 기반 멱등성**
- 장애 시나리오(프로듀서 크래시, 네트워크 분리, 컨슈머 리밸런스)별 운영 전략

---

## 3) 간단 예시 코드(개념)

> 아래 코드는 “형태”를 보여주기 위한 최소 예시이며, 실제 환경에서는 예외 처리/타임아웃/재시도 전략을 포함해야 한다.

```java
// pseudo-code
Properties props = new Properties();
props.put("bootstrap.servers", "...");
props.put("enable.idempotence", "true");
props.put("transactional.id", "order-service-1");

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();

try {
  producer.beginTransaction();

  producer.send(new ProducerRecord<>("orders", "k1", "v1"));
  producer.send(new ProducerRecord<>("orders", "k2", "v2"));

  // 컨슈머 처리 결과 오프셋을 트랜잭션에 포함
  // producer.sendOffsetsToTransaction(offsets, consumerGroupId);

  producer.commitTransaction();
} catch (Exception e) {
  producer.abortTransaction();
  throw e;
}
```

---

## 4) 참고자료(공식/권위 있는 소스)
- Apache Kafka Documentation: Producer Configs / Idempotence / Transactions
- Confluent Docs: Exactly Once Semantics(EOS) & Transactions 개념 정리
- Kafka Improvement Proposals(KIP) 중 Transactions/EOS 관련 문서(개념/동기)
