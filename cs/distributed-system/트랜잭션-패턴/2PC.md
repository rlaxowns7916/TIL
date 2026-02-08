# 2PC 패턴 (Two-Phase Commit Protocol)

## 개요

2단계 커밋 프로토콜(Two-Phase Commit Protocol, 2PC)은 분산 시스템에서 여러 데이터베이스나 시스템에 걸쳐진 원자적 트랜잭션을 조정하기 위한 합의(Consensus) 프로토콜입니다. 모든 참여자(Participant)가 트랜잭션을 커밋할지 롤백할지에 대해 전원 일치(commit 또는 abort)하는 것을 보장합니다.

## 핵심 원리

### 기본 동작 방식

2PC는 Coordinator(코디네이터)와 Participants(참여자)로 구성되며, 두 개의 단계로 나뉩니다:

```
┌─────────────────────────────────────────────────────────┐
│                    Phase 1: 준비 (Prepare)              │
├─────────────────────────────────────────────────────────┤
│ Coordinator → Participant: "Prepare T"                  │
│ Participant → Coordinator: "Ready T" 또는 "Abort T"      │
│                                                          │
│ 모든 참여자가 Ready 응답 → Phase 2: 커밋                │
│ 하나라도 Abort 응답 → 전체 Abort                        │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    Phase 2: 완료 (Commit)               │
├─────────────────────────────────────────────────────────┤
│ Coordinator → Participant: "Commit T"                  │
│ Participant → Coordinator: "Ack T"                       │
│                                                          │
│ 또는                                                  │
│ Coordinator → Participant: "Abort T"                   │
│ Participant → Coordinator: "Ack T"                       │
└─────────────────────────────────────────────────────────┘
```

## 상태 천이도

### 참여자(Participant) 상태

```
[INITIAL] → [PREPARED] → [COMMITTED] / [ABORTED]
    ↑           ↓
    └───────────┘
         (prepare 실패 시 ABORTED로 이동)
```

**상태 정의:**
- **INITIAL:** 트랜잭션 시작 전 상태
- **PREPARED:** Phase 1에서 로컬 트랜잭션 준비 완료, 커밋 대기 중
- **COMMITTED:** 트랜잭션 커밋 완료
- **ABORTED:** 트랜잭션 중단 완료

### 코디네이터(Coordinator) 상태

```
[INIT] → [WAITING] → [COMMITTED] / [ABORTED]
           ↓
      [TIMEOUT] (타임아웃 발생 시)
```

**상태 정의:**
- **INIT:** 코디네이터 시작
- **WAITING:** 모든 참여자의 응답 대기 중
- **COMMITTED:** 전체 트랜잭션 커밋 결정
- **ABORTED:** 전체 트랜잭션 중단 결정
- **TIMEOUT:** 응답 대기 시간 초과

## 데이터베이스별 구현

### PostgreSQL 2PC 구현

PostgreSQL은 X/Open XA 표준을 따르는 2PC를 지원합니다.

```sql
-- Phase 1: 트랜잭션 준비
BEGIN;
UPDATE accounts SET balance = balance - 1000 WHERE id = 1;
PREPARE TRANSACTION 'tx_20260203_001';

-- Phase 2: 커밋 또는 중단
COMMIT PREPARED 'tx_20260203_001';
-- 또는
ROLLBACK PREPARED 'tx_20260203_001';
```

**상태 확인:**
```sql
SELECT * FROM pg_prepared_xacts;
```

**중요:** PostgreSQL에서 `max_prepared_transactions` 파라미터가 0(기본값)이면 2PC가 비활성화됩니다.

### MySQL XA 트랜잭션

MySQL은 XA 트랜잭션 표준을 통해 2PC를 구현합니다.

```sql
-- XA 트랜잭션 시작
XA START 'xid_20260203_001';

-- 비즈니스 로직 실행
UPDATE accounts SET balance = balance - 1000 WHERE id = 1;

-- Phase 1: 준비 (END + PREPARE)
XA END 'xid_20260203_001';
XA PREPARE 'xid_20260203_001';

-- Phase 2: 커밋 또는 중단
XA COMMIT 'xid_20260203_001';
-- 또는
XA ROLLBACK 'xid_20260203_001';
```

## 장애 모드 및 복구 메커니즘

### 1. 참여자(Participant) 장애

#### Case 1: 참여자가 Phase 1 전에 장애 발생

**시나리오:** 참여자가 Prepare 메시지를 받기 전에 크래시

**복구 절차:**
1. 참여자 재시작 후 로그(Log) 검사
2. 트랜잭션 상태가 없음 → 코디네이터에 Abort로 간주
3. 코디네이터가 트랜잭션 Abort 결정

```
[참여자 장애]
   ↓
[참여자 재시작]
   ↓
[로그 확인: 해당 트랜잭션 기록 없음]
   ↓
[Abort로 간주 → 자동 롤백]
```

#### Case 2: 참여자가 Ready 응답 전에 장애 발생

**시나리오:** Prepare 메시지를 받았으나, 응답 전에 크래시

**복구 절차:**
1. 코디네이터는 응답이 없으므로 Abort 결정
2. 참여자 재시작 후 로그 확인 → 아직 PREPARED가 아님
3. 참여자는 자동으로 Abort 상태로 전환

```
[코디네이터] → [Prepare]
            [참여자 장애]
                ↓
           [타임아웃]
                ↓
         [Abort 결정]
                ↓
[참여자 재시작] → [로그 확인: PREPARED 아님] → [자동 Abort]
```

#### Case 3: 참여자가 Ready 응답 후 장애 발생

**시나리오:** Ready 응답을 보낸 후 Phase 2 메시지를 받기 전에 크래시

**복구 절차:**
1. 참여자 재시작 후 로그 확인 → PREPARED 상태 확인
2. 코디네이터에 최종 결정(Commit/Abort) 요청
3. 받은 결정에 따라 COMMIT PREPARED 또는 ROLLBACK PREPARED 실행

```
[참여자 Ready 응답] → [참여자 장애]
                            ↓
                   [참여자 재시작]
                            ↓
                   [로그: PREPARED 상태]
                            ↓
              [코디네이터에 상태 질의]
                            ↓
                [최종 결정 수신 후 처리]
```

### 2. 코디네이터(Coordinator) 장애

#### Case 1: 코디네이터가 Phase 1 전에 장애 발생

**시나리오:** Prepare 메시지를 보내기 전에 크래시

**복구 절차:**
1. 코디네이터 재시작
2. 트랜잭션 로그 확인 → 아직 시작하지 않음
3. 트랜잭션 재시작 또는 Abort

#### Case 2: 코디네이터가 Phase 2 메시지 전송 전 장애 발생

**시나리오:** 모든 Ready 응답을 받았으나 Commit 메시지를 보내기 전에 크래시

**복구 절차:**
1. 코디네이터 재시작 후 로그 확인 → 모든 참여자가 Ready 응답
2. Commit 결정하고 Phase 2 메시지 재전송
3. PREPARED 상태의 참여자는 Commit 메시지 수신 시 정상 처리

```
[Ready × N 수신] → [코디네이터 장애]
                         ↓
                  [코디네이터 재시작]
                         ↓
               [로그: 모두 Ready 확인]
                         ↓
              [Commit 결정 후 메시지 재전송]
```

#### Case 3: 코디네이터가 Phase 2 메시지 전송 중 장애 발생

**시나리오:** 일부 참여자에게만 Commit 메시지를 보낸 후 크래시

**복구 절차:**
1. 코디네이터 재시작 후 로그 확인
2. Commit을 결정했음 확인 → Commit 메시지를 받지 못한 참여자에게 재전송
3. 이미 Commit한 참여자는 메시지 재수신 시 무시 (멱등성)

**중요:** 이 시나리오가 2PC의 핵심 장애 복구 메커니즘입니다. PREPARED 상태의 참여자는 코디네이터가 복구될 때까지 대기합니다.

### 3. 네트워크 분할 (Network Partition)

#### Case 1: 코디네이터와 일부 참여자 간 분할

**시나리오:** 코디네이터가 일부 참여자에게 도달 불가

**동작:**
1. 코디네이터 타임아웃 발생 → Abort 결정
2. 도달 가능한 참여자에게 Abort 메시지 전송
3. 도달 불가한 참여자는 PREPARED 상태로 유지 (블로킹)

**복구:**
1. 네트워크 복구 후 참여자가 코디네이터에 연결
2. 참여자가 PREPARED 상태임을 확인
3. 코디네이터가 Abort 결정했음 → Abort 메시지 전송

#### Case 2: 코디네이터와 모든 참여자 간 분할

**시나리오:** 코디네이터가 모든 참여자에게 도달 불가

**동작:**
1. 코디네이터 타임아웃 → Abort 결정
2. 모든 참여자가 PREPARED 상태로 유지 (블로킹)
3. 네트워크 복구까지 대기

**복구:**
1. 네트워크 복구 후 코디네이터가 Abort 메시지 전송
2. 모든 참여자 Abort 처리

### 4. 블로킹 문제 (Blocking Problem)

**2PC의 가장 큰 단점:** 코디네이터 장애 시 PREPARED 상태의 참여자가 무기한 대기할 수 있습니다.

**시나리오:**
1. 참여자가 PREPARED 상태로 진입
2. 코디네이터가 Commit을 결정한 직후 장애 발생
3. 참여자는 최종 결정을 알 수 없음 → 블로킹

**해결책:**
1. **타임아웃 설정:** 참여자가 일정 시간 동안 응답이 없으면 Abort
2. **3PC (Three-Phase Commit):** Pre-commit 단계 추가 (단순성 희생)
3. **수동 개입:** 운영자가 직접 COMMIT PREPARED 또는 ROLLBACK PREPARED 실행

**PostgreSQL 예시:**
```sql
-- 블로킹된 트랜잭션 확인
SELECT * FROM pg_prepared_xacts;

-- 수동 해결
COMMIT PREPARED 'tx_20260203_001';
-- 또는
ROLLBACK PREPARED 'tx_20260203_001';
```

### 5. 분할 브레인(Split Brain) 문제

**시나리오:** 네트워크 분할로 인해 코디네이터와 참여자가 서로 다른 결정

**예:**
- 코디네이터는 Commit 결정 (일부 참여자에게 메시지 전송 성공)
- 분할로 인해 다른 참여자는 타임아웃으로 Abort 결정

**결과:**
- 일부 참여자는 Commit, 다른 참여자는 Abort
- 데이터 불일치 발생

**예방책:**
- 명시적인 장애 탐지(Failure Detector) 사용
- 코디네이터 장애 시 새 코디네이터 선출
- 복구 전에 기존 코디네이터 상태 확인

### 6. 로그(Log) 기반 복구

**핵심:** 2PC의 모든 상태는 영구 로그(Persistent Log)에 기록되어야 합니다.

**로그 기록 규칙:**
1. Prepare 메시지 전송 전 → [PREPARE] 로그 기록
2. Commit 결정 전 → [COMMIT] 로그 기록
3. Abort 결정 전 → [ABORT] 로그 기록

**복구 절차:**
```typescript
function recoverParticipant(participantId: string) {
  const log = readRecoveryLog(participantId);

  if (!log) {
    // 아직 시작하지 않음
    return State.INITIAL;
  }

  switch (log.lastState) {
    case State.PREPARED:
      // 코디네이터에 최종 결정 질의
      const decision = queryCoordinator(log.transactionId);
      if (decision === 'COMMIT') {
        executeCommitPrepared(log.transactionId);
      } else {
        executeRollbackPrepared(log.transactionId);
      }
      break;

    case State.COMMITTED:
    case State.ABORTED:
      // 이미 완료, 복구 불필요
      break;

    default:
      throw new Error(`Invalid state: ${log.lastState}`);
  }
}
```

## 2PC의 한계점

### 1. 성능 저하

**원인:**
- 동기식 통신: 각 단계에서 왕복 메시지 필요
- 락: 트랜잭션이 완료될 때까지 자원 락 유지
- 차단: 참여자가 PREPARED 상태에서 대기

**해결책:**
- SAGA 패턴 사용 (비동기 처리)
- 짧은 트랜잭션으로 설계
- 적절한 타임아웃 설정

### 2. 확장성 제약

**원인:**
- 코디네이터 병목: 모든 통신이 코디네이터를 거쳐야 함
- 참여자 수 증가 시 장애 확률 증가
- 네트워크 트래픽 급증

**해결책:**
- 하이어라컬(Hierarchical) 코디네이터
- 트리(Tree) 구조로 참여자 그룹화
- 샤딩(Sharding)으로 독립된 2PC 실행

### 3. 블로킹 문제

**상세 설명:**
```
[참여자 A] ←→ [코디네이터] ←→ [참여자 B]
     PREPARED          (장애)        PREPARED
        ↓                              ↓
     [대기]                        [대기]
        ↓                              ↓
   (무기한 블로킹)              (무기한 블로킹)
```

**실제 영향:**
- PREPARED 상태의 참여자가 다른 트랜잭션을 처리할 수 없음
- 리소스 점유로 시스템 전체 성능 저하
- 휴먼 인터벤션 필요

## 2PC vs 3PC 비교

| 특성 | 2PC | 3PC (Three-Phase Commit) |
|-----|-----|--------------------------|
| 단계 수 | 2단계 (Prepare, Commit) | 3단계 (CanCommit, PreCommit, DoCommit) |
| 블로킹 가능성 | 있음 (코디네이터 장애 시) | 없음 (분할 허용 시 불가) |
| 복잡도 | 낮음 | 높음 (추가 단계 및 메시지) |
| 네트워크 비용 | 적음 | 많음 (추가 왕복) |
| 실제 사용 | 널리 사용 (PostgreSQL, MySQL 등) | 거의 사용 안 됨 |

**3PC 단점:**
- 추가 단계로 인한 지연 증가
- 복잡성 증가로 인한 버그 가능성
- 실제로는 분할 상황이 드물어 효과 미미

## 구현 가이드라인

### 1. 타임아웃 설정

```typescript
const config = {
  coordinatorTimeout: 30000,    // 30초
  participantTimeout: 60000,   // 60초
  recoveryInterval: 5000,       // 5초마다 복구 시도
  maxRetryAttempts: 3
};

// 코디네이터 타임아웃 처리
async function coordinateWithTimeout(participants: Participant[]) {
  const timeout = setTimeout(() => {
    abortTransaction(transactionId);
  }, config.coordinatorTimeout);

  const responses = await Promise.allSettled(
    participants.map(p => p.prepare(transactionId))
  );

  clearTimeout(timeout);

  if (responses.some(r => r.status === 'rejected')) {
    await abortTransaction(transactionId);
  } else {
    await commitTransaction(transactionId);
  }
}
```

### 2. 로깅 전략

```typescript
interface TransactionLog {
  transactionId: string;
  coordinatorId: string;
  state: State;
  participants: Participant[];
  timestamp: Date;
}

function writeLog(log: TransactionLog) {
  // 비동기 로깅을 사용하면 안 됨
  // 로그가 기록되기 전에 장애 발생 시 데이터 손실
  fs.writeFileSync(`logs/${log.transactionId}.log`, JSON.stringify(log));
}
```

### 3. 멱등성(Idempotency) 보장

```typescript
async function handleCommitMessage(transactionId: string) {
  const state = getState(transactionId);

  if (state === State.COMMITTED) {
    // 이미 커밋됨, 멱등하게 처리
    return;
  }

  if (state === State.PREPARED) {
    await commitPrepared(transactionId);
    updateState(transactionId, State.COMMITTED);
  }
}
```

## Recovery / Termination Protocol (종료/복구 프로토콜)

2PC 문맥에서 흔히 말하는 **Recovery Protocol(복구 프로토콜)** 또는 **Termination Protocol(종료 프로토콜)**은,
참여자(Participant)가 **PREPARED** 상태로 멈춰 있는 동안 코디네이터(Coordinator) 장애/네트워크 분할이 발생했을 때
"**최종 결정(Commit/Abort)을 어떻게 알아내고, 어떻게 마무리할 것인가**"를 규정하는 절차입니다.

### 핵심 아이디어
- **PREPARED 상태는 로컬 자원(락/예약)을 잡은 채**로, 최종 결정을 기다리는 상태입니다.
- 참여자는 장애 복구 후에도 로그를 통해 PREPARED를 재구성할 수 있고, 이후 **결정 질의(Decision Query)**를 수행합니다.

### 전형적인 종료/복구 흐름
1. 참여자 재시작 → 로그에서 트랜잭션 상태를 복원
2. 상태가 **PREPARED**라면:
   - (우선) 코디네이터에 `transactionId`로 **최종 결정 질의**
   - 코디네이터가 응답하면 그 결정에 따라 `COMMIT PREPARED` 또는 `ROLLBACK PREPARED`
3. 코디네이터에 도달 불가하면:
   - 다른 참여자에게 질의(구현에 따라 선택)하거나
   - **대기(Blocking)**: 코디네이터 복구/네트워크 복구를 기다림

> 본 문서의 `로그(Log) 기반 복구` 예시 코드에서 `queryCoordinator()`가 바로 이 종료/복구 프로토콜의 핵심 동작입니다.

### 휴리스틱(Heuristic) 종료
현실에서는 무한 대기가 불가능한 경우가 있어 운영자가 강제 종료를 내리기도 합니다.
- 예: PostgreSQL `COMMIT PREPARED` / `ROLLBACK PREPARED`
- 예: XA 환경에서 **Heuristic Commit/Rollback** 발생 가능

**중요:** 휴리스틱 종료는 일관성을 깨뜨릴 수 있으므로,
사전에 "수동 개입 절차", "감사 로그", "재조정(리컨실리에이션)" 프로세스를 함께 설계해야 합니다.

## 공식 출처 및 참고 문헌

1. **PostgreSQL 공식 문서**:
   - "Two-Phase Transactions" - PostgreSQL 2PC 구현 및 사용법
   - URL: https://www.postgresql.org/docs/current/two-phase.html

2. **MySQL 공식 문서**:
   - "XA Transactions" - MySQL XA 트랜잭션 표준 및 구현
   - URL: https://dev.mysql.com/doc/en/xa.html

3. **Wikipedia - Two-phase commit protocol**:
   - 2PC 프로토콜의 이론적 배경, 장애 시나리오, 복구 절차
   - URL: https://en.wikipedia.org/wiki/Two-phase_commit_protocol

## 요약

2PC는 분산 환경에서 강한 일관성(Strong Consistency)을 보장하는 전통적인 프로토콜입니다. 핵심은:

1. 모든 참여자의 합의(Commit 또는 Abort) 보장
2. 로그 기반 복구 메커니즘으로 장애 내성
3. X/Open XA 표준을 따르는 다양한 구현

그러나 다음 제약점을 고려해야 합니다:
- **블로킹:** 코디네이터 장애 시 참여자가 무기한 대기
- **성능:** 동기식 통신과 락으로 인한 지연
- **확장성:** 코디네이터 병목과 참여자 수 제약

마이크로서비스 환경에서는 2PC보다 SAGA 패턴이 더 적합한 경우가 많지만, 전통적인 분산 데이터베이스 시스템에서는 여전히 2PC가 널리 사용됩니다.
