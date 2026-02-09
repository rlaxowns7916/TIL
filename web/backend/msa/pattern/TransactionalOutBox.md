# Transactional Outbox Pattern

## 1. 개요 (Overview)
**Transactional Outbox Pattern**은 분산 시스템(MSA) 환경에서 **"DB 트랜잭션 실행"**과 **"메시지(이벤트) 발행"** 간의 **원자성(Atomicity)**을 보장하기 위한 아키텍처 패턴이다.

"비즈니스 로직 처리"와 "이벤트 발행"을 하나의 DB 트랜잭션으로 묶어, **DB에는 반영되었으나 메시지는 발행되지 않는(또는 그 반대)** 데이터 불일치 문제를 해결한다.

---

## 2. 문제 상황 (Dual Write Problem)
서비스가 DB에 데이터를 쓰고(Write), 동시에 메시지 브로커(Kafka, RabbitMQ)에 이벤트를 발행해야 할 때 다음과 같은 문제가 발생할 수 있다.

1. **DB 커밋 성공 → 메시지 발행 실패**: 다운스트림 서비스가 변경 사항을 모름 (데이터 불일치).
2. **메시지 발행 성공 → DB 커밋 실패**: 없는 데이터에 대한 이벤트가 발행됨 (유령 이벤트).
3. **2PC (Two-Phase Commit)**: 강한 일관성을 보장하지만, 성능 저하와 가용성 문제로 MSA에서는 지양된다.

---

## 3. 해결책: Outbox Pattern

### 핵심 아이디어
메시지 브로커에 직접 발행하는 대신, **"보내야 할 메시지"를 DB 테이블(Outbox)에 먼저 저장**한다. 이 과정은 비즈니스 로직과 **동일한 로컬 트랜잭션** 내에서 수행되므로 원자성이 보장된다.

```mermaid
%% ASCII Diagram으로 대체 권장되나, 이해를 돕기 위해 논리적 흐름 서술
[App Service]
    | (Transaction Start)
    |-- 1. INSERT/UPDATE Domain Data
    |-- 2. INSERT Outbox Event (Payload)
    | (Commit)
```

이후, 별도의 프로세스가 `Outbox` 테이블을 읽어 실제 브로커로 메시지를 전송(Relay)한다.

---

## 4. 구현 방식 (Implementation Strategies)

Outbox 테이블에 저장된 데이터를 브로커로 옮기는 방식에는 크게 두 가지가 있다.

### 방식 A: Polling Publisher (전통적 방식)
애플리케이션(또는 별도 스케줄러)이 주기적으로 DB를 조회하여 미전송 이벤트를 발행한다.

- **동작**:
  1. `SELECT * FROM outbox WHERE published = false`
  2. 메시지 브로커로 발행 (Publish)
  3. `UPDATE outbox SET published = true WHERE id = ...` (또는 DELETE)
- **장점**:
  - 구현이 단순하다. 별도의 인프라(Kafka Connect 등)가 필요 없다.
  - DB만 있으면 어디서든 적용 가능하다.
- **단점**:
  - **Polling 부하**: 주기가 짧으면 DB 부하가 커지고, 길면 지연(Latency)이 발생한다.
  - **경합(Race Condition)**: 여러 인스턴스가 동시에 Polling 할 경우 락 관리가 필요하다.

### 방식 B: Log Tailing with CDC (Debezium)
DB의 트랜잭션 로그(WAL, Binlog)를 실시간으로 읽어 변경 사항을 감지하는 **CDC(Change Data Capture)** 기술을 활용한다.

- **동작**:
  1. App이 Outbox 테이블에 `INSERT`. (끝)
  2. **Debezium(Kafka Connect)** 이 DB 로그에서 INSERT 이벤트를 감지.
  3. 감지된 이벤트를 Kafka 토픽으로 즉시 전송.
- **장점**:
  - **실시간성(Low Latency)**: Polling 대기 시간이 없다.
  - **DB 부하 최소화**: 쿼리가 아닌 로그를 읽으므로 DB 성능 영향이 적다.
  - **애플리케이션 단순화**: App은 "저장"만 하면 된다. 발행/재시도는 인프라가 담당한다.
- **단점 (Trade-off)**:
  - **인프라 복잡도(Infrastructure Complexity)**: Kafka Connect, Zookeeper, Debezium 커넥터 등을 구축하고 운영해야 한다.
  - **관리 포인트 이관**: 이벤트 발행의 책임이 개발 코드에서 **인프라/운영 영역**으로 넘어간다. (커넥터 장애 시 개발자가 대응하기 어려울 수 있음)

---

## 5. 결론 및 선택 가이드

| 비교 항목 | Polling Publisher | Log Tailing (CDC) |
|---|---|---|
| **복잡도** | 낮음 (코드 레벨 구현) | 높음 (인프라 레벨 구축) |
| **실시간성** | 낮음 (Polling 주기에 의존) | 높음 (Near Real-time) |
| **운영 비용** | 낮음 (DB 부하는 증가) | 높음 (별도 미들웨어 관리) |
| **추천 상황** | 트래픽이 적거나 인프라 제어가 제한적일 때 | **이벤트 발행량이 많고 실시간성이 중요할 때** |

실무에서는 **트래픽이 적은 초기 단계엔 Polling**으로 시작하고, **시스템 규모가 커지면 CDC**로 고도화하는 것이 일반적이다. 단, CDC 도입 시에는 **"인프라 운영 역량"**이 반드시 전제되어야 한다.
