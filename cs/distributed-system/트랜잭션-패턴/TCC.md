# TCC 패턴 (Try-Confirm-Cancel)

## 개요

TCC(Try-Confirm-Cancel) 패턴은 분산 시스템에서 데이터 일관성을 보장하기 위한 **보상 기반(Compensation-based)** 트랜잭션 패턴 중 하나입니다. 2PC(Two-Phase Commit)의 성능 저하(Blocking) 문제를 해결하고, SAGA 패턴보다 더 강력한 **격리성(Isolation)**을 제공하기 위해 고안되었습니다.

주로 **결제, 재고 관리, 쿠폰 사용** 등 **강력한 데이터 정합성**이 요구되는 비즈니스 로직에 사용됩니다.

## 핵심 메커니즘

TCC는 하나의 비즈니스 작업을 3단계로 명시적으로 나누어 처리합니다.

### 1. Try (시도/예약)
- **목적:** 비즈니스 검증 및 자원 예약
- **동작:** 실제 변경을 가하지 않고, 필요한 자원을 "가예약" 상태로 잡습니다.
- **예시:** 결제 금액만큼 잔액 동결(Freeze), 재고 차감 대기(Pending) 상태로 변경.
- **특징:** 트랜잭션의 일관성 체크가 이 단계에서 모두 이루어져야 합니다.

### 2. Confirm (확정)
- **목적:** 실제 비즈니스 로직 수행 및 예약된 자원 소모
- **동작:** Try 단계가 성공하면 호출됩니다. 자원을 실제로 차감하거나 상태를 완료로 변경합니다.
- **특징:** 멱등성(Idempotency)이 보장되어야 합니다. Try가 성공했다면 Confirm은 반드시 성공해야 합니다(실패 시 재시도).

### 3. Cancel (취소/보상)
- **목적:** 예약된 자원 해제 및 롤백
- **동작:** Try 단계가 실패하거나, 전체 트랜잭션 중 다른 서비스가 실패했을 때 호출됩니다.
- **특징:** 멱등성이 보장되어야 합니다. 동결된 자원을 원래대로 복구합니다.

## 동작 흐름 (Workflow)

> TIL 문서에서는 Mermaid 같은 렌더링 의존 도구 대신, **ASCII 다이어그램**으로 흐름을 시각화합니다.

### 성공 플로우 (All Try Success → Confirm)

```
Orchestrator
  |-- Try(A: 자원 예약) ------------------------> ServiceA
  |<-------------------------- OK --------------|
  |-- Try(B: 자원 예약) ------------------------> ServiceB
  |<-------------------------- OK --------------|
  |
  |-- Confirm(A: 자원 확정) --------------------> ServiceA
  |<-------------------------- OK --------------|
  |-- Confirm(B: 자원 확정) --------------------> ServiceB
  |<-------------------------- OK --------------|
  |
  +--> DONE
```

### 실패 플로우 (Any Try Fail → Cancel)

```
Orchestrator
  |-- Try(A) ----------------------------------> ServiceA
  |<-------------------------- OK --------------|
  |-- Try(B) ----------------------------------> ServiceB
  |<------------------------ FAIL --------------|
  |
  |-- Cancel(A: 예약 해제/보상) ----------------> ServiceA
  |<-------------------------- OK --------------|
  |-- Cancel(B: 부분 예약이 있었다면 해제) -----> ServiceB
  |<-------------------------- OK --------------|
  |
  +--> ROLLBACK (COMPENSATED)
```

## 예제 시나리오: 항공권 예약 및 결제

### 1. 서비스 정의
- **Airline Service:** 항공권 좌석 관리
- **Payment Service:** 결제 처리

### 2. 단계별 로직

#### Airline Service
- **Try:** `UPDATE seats SET status = 'RESERVED' WHERE id = ?`
  - 좌석을 다른 사람이 예약하지 못하도록 선점.
- **Confirm:** `UPDATE seats SET status = 'SOLD' WHERE id = ?`
  - 실제로 판매 처리.
- **Cancel:** `UPDATE seats SET status = 'AVAILABLE' WHERE id = ?`
  - 예약을 취소하고 다시 판매 가능 상태로 변경.

#### Payment Service
- **Try:** `UPDATE accounts SET balance = balance - 100, frozen = frozen + 100 WHERE id = ?`
  - 잔액에서 금액을 차감하지 않고 "동결" 필드로 이동.
- **Confirm:** `UPDATE accounts SET frozen = frozen - 100 WHERE id = ?`
  - 동결된 금액을 실제로 차감(소멸).
- **Cancel:** `UPDATE accounts SET balance = balance + 100, frozen = frozen - 100 WHERE id = ?`
  - 동결된 금액을 다시 잔액으로 복구.

## 구현 코드 예시 (Java/Spring style)

```java
public interface TccAction {
    boolean tryAction(Context ctx);
    void confirmAction(Context ctx);
    void cancelAction(Context ctx);
}

@Service
public class PaymentTccService implements TccAction {

    @Transactional
    public boolean tryAction(Context ctx) {
        // 1. 잔액 확인
        Account account = repo.findById(ctx.getUserId());
        if (account.getBalance() < ctx.getAmount()) return false;
        
        // 2. 잔액 동결 (자원 예약)
        account.freeze(ctx.getAmount());
        return true;
    }

    @Transactional
    public void confirmAction(Context ctx) {
        // 3. 실제 차감 (멱등성 보장 필요)
        if (alreadyConfirmed(ctx.getTxId())) return;
        
        Account account = repo.findById(ctx.getUserId());
        account.useFrozen(ctx.getAmount());
    }

    @Transactional
    public void cancelAction(Context ctx) {
        // 4. 보상 처리 (멱등성 보장 필요)
        if (alreadyCancelled(ctx.getTxId())) return;
        
        Account account = repo.findById(ctx.getUserId());
        account.unfreeze(ctx.getAmount());
    }
}
```

## TCC vs 2PC 비교

> 한 줄 요약: **2PC는 트랜잭션 매니저/DB 계층에서 원자성을 강제**하고, **TCC는 애플리케이션 계층에서 의미적(semantic) 예약 + 보상**으로 정합성을 맞춥니다.

| 구분 | TCC (Try-Confirm-Cancel) | 2PC (Two-Phase Commit / XA) |
|---|---|---|
| 결정 주체 | 오케스트레이터(애플리케이션) | 코디네이터/트랜잭션 매니저(프로토콜) |
| 핵심 아이디어 | **자원 예약(Try) → 확정(Confirm) → 해제(Cancel)** | **Prepare(투표) → Commit/Abort** |
| 격리/잠금 | 예약 상태(Frozen/Reserved)로 **업무적으로 격리** | PREPARED 상태에서 **락/자원 점유**(Blocking 위험) |
| 장애 시 거동 | Cancel/재시도로 복구 설계(멱등성 필수) | PREPARED에서 결정 미확인 시 **블로킹**(자동 결정 금지) |
| 구현 위치 | 도메인/서비스 코드 침투 큼 | DB/XA 지원 필요, 인프라 의존 큼 |
| 장점 | 2PC 대비 성능/가용성 개선 여지, 비즈니스에 맞춘 설계 가능 | 강한 원자성/일관성(정상 조건), 프로토콜 기반 |
| 단점 | 구현 비용↑, 모든 작업에 T/C/C 필요 | 가용성↓(블로킹), 운영 난이도↑ |

### ASCII 시각화로 본 차이

**2PC (Prepare → Commit/Abort)**
```
Coordinator
  |-- PREPARE ------------------------------> P1
  |-- PREPARE ------------------------------> P2
  |<-- VOTE-YES ----------------------------|
  |<-- VOTE-YES ----------------------------|
  |
  |-- COMMIT(or ABORT) ---------------------> P1
  |-- COMMIT(or ABORT) ---------------------> P2

(P1/P2는 PREPARED에서 결정이 올 때까지 잠금/점유가 발생할 수 있음)
```

**TCC (Try 예약 → Confirm 확정 / 실패 시 Cancel)**
```
Orchestrator
  |-- Try(예약) ----------------------------> ServiceA
  |-- Try(예약) ----------------------------> ServiceB
  |<-- OK/FAIL -----------------------------|
  |
  |-- Confirm(확정) ------------------------> A,B   (모두 OK일 때)
  |-- Cancel(해제/보상) --------------------> A,B   (하나라도 FAIL일 때)

(잠금 대신 '예약 상태'로 업무적으로 격리하고, Cancel/재시도를 설계로 흡수)
```

## TCC vs SAGA 비교

| 특성 | TCC (Try-Confirm-Cancel) | SAGA (Choreography/Orchestration) |
|-----|-------------------------|-----------------------------------|
| **일관성 수준** | 상대적으로 높음 (Try 단계에서 예약) | 결과적 일관성 (중간에 데이터 불일치 구간 존재) |
| **구현 복잡도** | **매우 높음** (모든 로직에 3단계 구현 필요) | 높음 (보상 트랜잭션만 구현하면 됨) |
| **격리성(Isolation)** | **지원함** (자원 예약을 통해 Dirty Read 방지 가능) | 지원하지 않음 (보상 전까지 데이터 변경이 노출됨) |
| **성능** | 2PC보다 좋지만, 2번의 DB 갱신 필요 | 비동기 메시징으로 높은 처리량 가능 |
| **적합한 사례** | 금융, 재고 등 **데이터 무결성**이 중요한 곳 | 주문 처리, 배송 등 긴 비즈니스 프로세스 |

## 장단점 분석

### 장점
1. **강력한 일관성:** Try 단계에서 리소스를 선점하므로, Confirm 단계의 실패 가능성이 매우 낮음.
2. **격리성 제어:** 예약 상태(Frozen/Pending)를 통해 다른 트랜잭션이 해당 자원에 접근하는 것을 제어할 수 있음.
3. **유연성:** 다양한 리소스(DB, 외부 API 등)에 대해 적용 가능.

### 단점
1. **비즈니스 로직 침투:** 비즈니스 코드 내에 T/C/C 로직이 강하게 결합됨.
2. **개발 비용:** 모든 작업에 대해 3가지 로직을 구현해야 하므로 코드가 비대해짐.
3. **멱등성 관리:** Confirm과 Cancel은 네트워크 장애 등으로 중복 호출될 수 있으므로 반드시 멱등성을 보장해야 함.

## 결론

TCC는 **"분산 환경에서의 2PC 대안"**으로 불릴 만큼 데이터 정합성을 중요시합니다. SAGA 패턴으로 해결하기 어려운 **동시성 제어**나 **자원 선점**이 필수적인 경우(예: 한정판 상품 판매, 포인트 결제)에 TCC 패턴 도입을 고려해야 합니다. 다만, 구현 복잡도가 높으므로 일반적인 워크플로우에는 SAGA 패턴이 더 효율적일 수 있습니다.
