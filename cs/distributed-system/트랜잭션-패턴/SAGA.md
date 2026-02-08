# SAGA 패턴 (Saga Pattern)

## 개요

SAGA 패턴은 1987년 Hector Garcia-Molina와 Kenneth Salem이 제안한 분산 트랜잭션 처리 패턴으로, 여러 서비스에 걸쳐진 긴 실행 시간의 비즈니스 트랜잭션을 일련의 로컬 트랜잭션 시퀀스로 분리하여 관리합니다. 단일 원자적 트랜잭션으로 처리할 수 없는 분산 환경에서 데이터 정합성을 유지하는 핵심 패턴입니다.

## 핵심 원리

### 기본 동작 방식

SAGA는 비즈니스 트랜잭션을 여러 개의 독립적인 **로컬 트랜잭션(Local Transactions)**으로 분리하고, 각 단계가 완료되면 다음 단계를 트리거합니다.

```
[T1: 로컬 트랜잭션 1] → [T2: 로컬 트랜잭션 2] → [T3: 로컬 트랜잭션 3] → ...
      ↓ 실패 시
[C1: 보상 트랜잭션 1] ← [C2: 보상 트랜잭션 2]
```

### 보상 트랜잭션 (Compensating Transactions)

로컬 트랜잭션이 실패하면, 이전에 성공한 모든 단계의 변경 사항을 취소하기 위해 보상 트랜잭션을 순차적으로 실행합니다. 보상 트랜잭션은 원래 트랜잭션의 논리적 역연산이어야 합니다.

| 원래 트랜잭션 | 보상 트랜잭션 |
|-------------|-------------|
| 재고 확보 (reserve) | 재고 해제 (release) |
| 결제 처리 (process payment) | 환불 처리 (refund) |
| 주문 생성 (create order) | 주문 취소 (cancel order) |

## 조합 방식

### 1. 코레오그래피 (Choreography)

각 서비스가 자율적으로 이벤트를 발행하고 다른 서비스의 이벤트를 수신하여 협업하는 방식입니다.

```
[주문 서비스] → OrderCreated 이벤트 → [재고 서비스] → InventoryReserved 이벤트 → [결제 서비스]
                              ↓ 실패 시
                    ← PaymentFailed 이벤트 ← [결제 서비스]
                              ↓
               InventoryReleased 이벤트 (보상)
```

**장점:**
- 중앙 조정자가 없어 단일 장애점(Single Point of Failure)이 없음
- 서비스 간 결합도가 낮음
- 확장성 우수

**단점:**
- 복잡한 비즈니스 로직의 흐름 파악이 어려움
- 사이클(순환 의존성) 발생 가능
- 디버깅이 어려움

### 2. 오케스트레이션 (Orchestration)

중앙 조정자(Orchestrator)가 각 단계를 순서대로 호출하고 결과를 관리합니다.

```
[오케스트레이터] → [주문 서비스: createOrder]
    ↓ 성공
[오케스트레이터] → [재고 서비스: reserveInventory]
    ↓ 실패
[오케스트레이터] → [주문 서비스: cancelOrder] (보상)
```

**장점:**
- 트랜잭션 흐름이 명확하고 이해하기 쉬움
- 복잡한 비즈니스 로직을 중앙에서 관리
- 디버깅 및 모니터링 용이

**단점:**
- 오케스트레이터가 단일 장애점이 될 수 있음
- 서비스 간 결합도가 높음

## 장애 모드 및 복구 메커니즘

### 1. 로컬 트랜잭션 실패 (Local Transaction Failure)

**시나리오:** T1, T2는 성공했으나 T3에서 제약 조건 위배로 실패

**복구 절차:**
1. T3의 로컬 트랜잭션 롤백
2. C2(T2의 보상 트랜잭션) 실행
3. C1(T1의 보상 트랜잭션) 실행

**핵심:** 보상 트랜잭션은 역순으로 실행되어야 데이터 일관성이 유지됩니다.

### 2. 서비스 중단 (Service Outage)

**시나리오:** T2를 실행할 서비스가 응답하지 않음

**복구 전략:**

**오케스트레이션 방식:**
```
1. 재시도 정책(Retry Policy) 적용 (Exponential Backoff)
2. 재시도 횟수 초과 시 보상 트랜잭션 실행
3. "수동 복구 필요" 상태로 표시
```

**코레오그래피 방식:**
```
1. DLQ(Dead Letter Queue)로 이벤트 전송
2. 서비스 복구 후 DLQ에서 이벤트 재처리
3. 메시지 브로커의 재시도 메커니즘 활용
```

### 3. 타임아웃 (Timeout)

**시나리오:** T2가 무기한 응답하지 않음 (Blocking)

**해결책:**
- 각 단계에 명시적인 타임아웃 설정
- 타임아웃 발생 시 보상 트랜잭션 실행
- 분산 트랜잭션 상태 테이블 관리

```
[상태 테이블 예시]
| saga_id | step | status | created_at | updated_at |
|---------|------|--------|-------------|-------------|
| S001    | T1   | COMPLETED | 2026-02-03 10:00:00 | ... |
| S001    | T2   | TIMEOUT | 2026-02-03 10:01:00 | ... |
| S001    | C1   | PENDING | 2026-02-03 10:01:00 | ... |
```

### 4. 부분 완료 상태에서 시스템 장애 (System Crash Mid-Saga)

**시나리오:** T3 실행 중에 오케스트레이터가 크래시

**복구 절차:**
1. 오케스트레이터 재시작 시 진행 중이던 SAGA 상태 조회
2. 마지막 완료된 단계 확인
3. 중단된 단계부터 재시작 또는 보상 실행 결정

```
[복구 로직]
if (status == COMPLETED && next_step == PENDING) {
    // 계속 진행
    continueSaga(sagaId);
} else if (status == FAILED) {
    // 보상 실행
    executeCompensatingTransactions(sagaId);
}
```

### 5. 보상 트랜잭션 실패 (Compensation Failure)

**시나리오:** C2 실행 중 장애 발생

**대응 전략:**
1. 재시도 (보상 트랜잭션은 멱등성이 보장되어야 함)
2. 재시도 실패 시 수동 개입 요구
3. 운영자 대시보드에 "수동 복구 필요" 알림

```
[상태 천이]
COMPLETED → COMPENSATING → COMPENSATION_FAILED → MANUAL_INTERVENTION
```

### 6. 분할 브레인(Split Brain) 및 경쟁 조건

**시나리오:** 네트워크 분할로 인해 여러 인스턴스가 동시에 보상 실행 시도

**해결책:**
- 분산 락(Distributed Lock) 사용
- Optimistic Locking (버전 기반)
- 유니크 제약 조건 활용

```sql
-- 예시: 버전 기반 낙관적 락
UPDATE saga_state
SET status = 'COMPENSATING',
    version = version + 1
WHERE saga_id = 'S001'
  AND status = 'FAILED'
  AND version = 5;  -- 현재 버전 확인

-- UPDATE 행 수가 1이면 성공, 0이면 이미 다른 인스턴스가 처리
```

## 아웃박스 패턴 (Outbox Pattern)과의 결합

SAGA 패턴은 메시지 전달의 신뢰성을 위해 아웃박스 패턴과 함께 사용하는 것이 권장됩니다.

### 아웃박스 패턴 개요

로컬 트랜잭션과 메시지 발행을 원자적으로 처리하기 위해:

1. 트랜잭션 내에서 비즈니스 데이터와 아웃박스 테이블에 이벤트 기록
2. 별도의 프로세스가 아웃박스 테이블을 스캔하여 메시지 발행
3. 발행 완료 후 아웃박스 테이블에서 삭제

```sql
BEGIN;

-- 비즈니스 트랜잭션
UPDATE orders SET status = 'CONFIRMED' WHERE id = 123;

-- 아웃박스 이벤트 기록
INSERT INTO outbox_events
(id, aggregate_type, aggregate_id, event_type, payload, published)
VALUES
(uuid_generate_v4(), 'order', 123, 'OrderConfirmed', '{"orderId": 123}', false);

COMMIT;
```

### 장애 시 복구

아웃박스 프로세스가 실패하더라도:
- 이벤트는 데이터베이스에 보존
- 재시작 시 아웃박스 테이블 스캔으로 복구
- 중복 발행 방지를 위해 published 플래그 확인

## SAGA 패턴의 한계점

### 1. 이벤트 순서 보장 어려움

비동기 메시지 환경에서 이벤트 순서가 섞일 수 있습니다.

**대응:**
- 타임스탬프 기준 정렬
- 시퀀스 번호 활용
- Saga ID로 그룹화하여 처리

### 2. 중간 상태 노출 (Read Uncommitted)

SAGA 실행 중에는 시스템이 일관되지 않은 중간 상태를 가질 수 있습니다.

**대응:**
- 비즈니스 레벨에서 일시적인 불일치 허용
- CQRS 패턴과 결합하여 읽기 모델에서 최종 일관성 보장

### 3. 롤백 불가능한 작업

이미 외부에 영향을 준 작업은 보상 불가능할 수 있습니다.

**예시:**
- 이메일 발송 (보상: 취소 이메일 발송)
- 외부 API 호출 (보상: 취소 API 호출 또는 수동 처리)

**대응:**
- 가능한 한 늦게 실행되도록 설계
- 인간 개입이 필요한 수동 보상 단계 마련

## 구현 가이드라인

### 1. 보상 트랜잭션 설계 원칙

- **멱등성(Idempotency):** 동일한 보상을 여러 번 실행해도 안전해야 함
- **완료성(Completeness):** 원래 작업의 효과를 완전히 제거해야 함
- **역순 실행:** 실행 역순으로 보상해야 데이터 일관성 유지

### 2. 상태 관리

```typescript
enum SagaState {
  STARTED = 'STARTED',
  COMPLETED = 'COMPLETED',
  COMPENSATING = 'COMPENSATING',
  COMPENSATED = 'COMPENSATED',
  FAILED = 'FAILED',
  MANUAL_INTERVENTION = 'MANUAL_INTERVENTION'
}

interface SagaExecution {
  sagaId: string;
  currentStep: number;
  state: SagaState;
  compensating: boolean;
  createdAt: Date;
  updatedAt: Date;
  error?: string;
}
```

### 3. 재시도 전략

```typescript
const retryPolicy = {
  maxAttempts: 5,
  initialDelay: 1000, // ms
  maxDelay: 10000, // ms
  backoffMultiplier: 2,
  retryableErrors: [
    'ECONNRESET',
    'ETIMEDOUT',
    'ECONNREFUSED'
  ]
};
```

## SAGA vs 2PC 비교

| 특성 | SAGA | 2PC (Two-Phase Commit) |
|-----|------|----------------------|
| 트랜잭션 유형 | 비동기, 이벤트 기반 | 동기, 락 기반 |
| 데이터 정합성 | 최종 일관성 (Eventual Consistency) | 강한 일관성 (Strong Consistency) |
| 성능 | 높음 (비동기 처리) | 낮음 (동기 대기, 락) |
| 차단 여부 | 비차단 (Non-blocking) | 차단 가능 (Blocking) |
| 사용 사례 | 마이크로서비스, 긴 트랜잭션 | 단일 DB 또는 분산 DB 클러스터 |

## 공식 출처 및 참고 문헌

1. **원본 논문**:
   - Garcia-Molina, H., & Salem, K. (1987). "Sagas". *Proceedings of the 1987 ACM SIGMOD International Conference on Management of Data*.
   - URL: https://dl.acm.org/doi/10.1145/38713.38742

2. **마이크로서비스.io (Martin Fowler)**:
   - "Pattern: Saga" - 마이크로서비스 아키텍처에서의 SAGA 패턴 표준 문서
   - URL: https://microservices.io/patterns/data/saga.html

3. **Microsoft Azure Architecture Center**:
   - "Saga design pattern" - 클라우드 환경에서의 SAGA 구현 가이드
   - URL: https://learn.microsoft.com/en-us/azure/architecture/patterns/saga

## 요약

SAGA 패턴은 분산 환경에서 긴 실행 시간의 비즈니스 트랜잭션을 안정적으로 처리하기 위한 실용적인 솔루션입니다. 핵심은:

1. 트랜잭션을 작은 로컬 단위로 분해
2. 실패 시 보상 트랜잭션으로 역순 복구
3. 코레오그래피 또는 오케스트레이션으로 조합
4. 다양한 장애 시나리오에 대비한 복구 메커니즘 마련

장애 상황에서의 복구 가능성을 설계의 최우선 순위로 삼아야 하며, 운영 환경에서의 모니터링과 수동 복구 프로세스는 필수적입니다.
