# Kafka 트랜잭션과 Exactly-Once Semantics (EOS)

## 개요
메시징 시스템에서 데이터의 손실이나 중복 없이 정확히 한 번만 처리하는 것(Exactly-Once Processing)은 시스템의 신뢰성을 결정짓는 핵심 요소입니다. Apache Kafka는 **Idempotent Producer**와 **Transaction API**를 통해 이를 실무 수준에서 보장합니다.

---

## 1. Idempotent Producer (멱등성 프로듀서)

네트워크 오류 등으로 인해 동일한 메시지가 중복 전송되더라도 브로커가 이를 식별하여 단 한 번만 저장하는 기능입니다.

### 동작 원리
-   **PID (Producer ID) & Sequence Number**: 프로듀서가 초기화될 때 브로커로부터 PID를 할당받고, 각 메시지마다 Partition 단위의 Sequence Number를 부여합니다.
-   **중복 제거**: 브로커는 이미 저장된 (PID, Sequence Number) 조합의 메시지가 다시 들어오면 이를 기록하지 않고 ACK만 보냅니다.
-   **설정**: `enable.idempotence=true` (Kafka 3.0 이상에서는 기본값).

---

## 2. Kafka 트랜잭션 (Transaction API)

여러 파티션이나 토픽에 대한 다수의 쓰기 작업을 하나의 원자적 단위(Atomic Unit)로 묶어 처리합니다. "All or Nothing"을 보장합니다.

### 핵심 구성 요소
-   **Transaction Coordinator**: 트랜잭션의 상태(Ongoing, Prepare, Commit/Abort)를 관리하는 브로커 내 모듈입니다.
-   **Transactional ID**: 프로듀서가 재시작되어도 기존 트랜잭션을 식별할 수 있게 해주는 고유 ID입니다.
-   **Control Message**: 트랜잭션의 성공/실패 여부를 나타내는 특수 메시지로, 컨슈머가 이를 보고 읽기 여부를 결정합니다.

### 컨슈머의 격리 수준 (Isolation Level)
-   **read_committed**: 커밋된 메시지만 읽습니다. 트랜잭션이 완료될 때까지 해당 메시지들은 컨슈머에게 노출되지 않습니다.
-   **read_uncommitted (기본값)**: 트랜잭션 상태와 상관없이 모든 메시지를 읽습니다.

---

## 3. Exactly-Once Semantics (EOS) 흐름

Kafka Streams나 "Consume-Transform-Produce" 패턴에서 EOS를 달성하는 흐름은 다음과 같습니다.

1.  **트랜잭션 시작**: `producer.beginTransaction()`
2.  **데이터 소비 및 처리**: 소스 토픽에서 데이터를 읽고 가공합니다.
3.  **데이터 발행**: 목적지 토픽으로 가공된 데이터를 보냅니다.
4.  **오프셋 커밋**: `producer.sendOffsetsToTransaction()`을 통해 컨슈머의 오프셋 커밋을 트랜잭션의 일부로 포함시킵니다.
5.  **트랜잭션 완료**: `producer.commitTransaction()`을 호출하여 모든 작업을 원자적으로 확정합니다.

---

## 4. 실무 고려 사항
-   **성능 오버헤드**: 트랜잭션 코디네이터와의 통신 및 제어 메시지 발행으로 인해 일반적인 발행보다 지연 시간(Latency)이 소폭 증가합니다.
-   **만료 시간**: `transactional.id.expiration.ms` 내에 트랜잭션이 완료되지 않으면 브로커는 해당 트랜잭션을 강제로 중단합니다.
-   **좀비 프로듀서 차단**: 새로운 프로듀서가 동일한 `transactional.id`로 접속하면, 이전 프로듀서의 요청은 거부(Fencing)됩니다.

---

## 참고 문헌
-   **Apache Kafka Documentation**: "Exactly Once Semantics".
-   **Confluent Blog**: "Transactions in Apache Kafka".
-   **Baeldung**: "Kafka Exactly-Once Semantics".
