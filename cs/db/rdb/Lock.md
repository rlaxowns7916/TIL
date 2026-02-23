# Lock (RDB 락 개념 정리)

> 이 문서는 **MySQL(InnoDB)** 기준으로 설명하되, 운영에서 자주 마주치는 **PostgreSQL 차이점**도 함께 메모한다.
>
> 목적: “왜 막히는지/왜 데드락이 나는지/어떻게 재현하고 해결하는지”를 실무 관점에서 빠르게 판단할 수 있게 정리.

---

## 0. 락을 이해하기 위한 전제

- 락은 크게 다음 두 축으로 이해하면 된다.
  - **무엇을 잠그는가**: row / range(범위) / table / metadata
  - **무슨 의도로 잠그는가**: 읽기 일관성 보장(phantom 방지), 쓰기 충돌 방지, 스키마 변경 보호

- InnoDB는 MVCC로 “일반 SELECT”는 보통 락을 잡지 않고도 일관된 읽기(consistent read)를 제공한다.
  - 하지만 `SELECT ... FOR UPDATE` 같은 **locking read**는 실제로 락을 잡는다.

---

## 1. Shared / Exclusive (S / X) 기본

### 1.1 Shared Lock (S)
- 여러 트랜잭션이 **동시에** 획득 가능(공유).
- 다른 트랜잭션의 **X 락**과는 충돌한다.

MySQL(InnoDB) 예시
- `SELECT ... FOR SHARE`
- `SELECT ... LOCK IN SHARE MODE` (구버전 호환)

> 참고: DB/격리수준/쿼리 형태에 따라 “일반 SELECT가 S 락을 잡는가”는 다를 수 있다.
> - 예: SQL Server는 기본 잠금 정책이 달라서 `WITH (NOLOCK)` 같은 힌트를 쓰는 문화가 있다.

### 1.2 Exclusive Lock (X)
- 단 한 트랜잭션만 획득 가능(배타).
- S/X 모두와 충돌한다.

MySQL(InnoDB)에서 X 락이 걸리는 대표 케이스
- `INSERT`, `UPDATE`, `DELETE`
- `SELECT ... FOR UPDATE`

---

## 2. InnoDB의 “범위 락” 계열: Record / Gap / Next-key

InnoDB에서 헷갈리는 포인트는 **row만 잠그는 게 아니라, 인덱스 구간(범위)도 잠글 수 있다는 것**이다.
이게 phantom read 방지와 직결되고, “왜 INSERT가 막히지?” 같은 이슈의 핵심 원인이 된다.

### 2.1 Record Lock (레코드 락)
- **인덱스 레코드(키)** 자체를 잠금.
- 보통 “특정 row 1개”를 잠근다고 생각하는 락.

조건
- 적절한 인덱스를 타고 “정확히 어떤 레코드”인지 특정되는 경우가 많다.

### 2.2 Gap Lock (갭 락)
- 실제 레코드가 아니라 **레코드 사이의 간격(gap)** 을 잠금.
- 목표: 해당 범위에 **새로운 레코드(INSERT)가 끼어드는 것**을 막아 phantom을 방지.

특징
- 갭 락이 잡혀 있으면, 그 갭에 들어오는 `INSERT`가 블로킹될 수 있다.

### 2.3 Next-key Lock (넥스트키 락)
- **Record Lock + Gap Lock**의 조합.
- InnoDB에서 range 조건을 포함하는 locking read가 자주 잡는 형태.

> 실무 요약
> - “`FOR UPDATE`를 걸었는데 INSERT가 막힌다” → next-key/gap 관련일 가능성이 높다.

### 2.4 Insert Intention Lock (삽입 의도 락)
- 이름이 비슷해서 헷갈리기 쉬운데, **갭에 INSERT 하려는 의도 표시**에 가깝다.
- 서로 양립 가능한 경우가 많지만, 다른 트랜잭션의 gap/next-key와 충돌하면서 대기할 수 있다.

---

## 3. Intention Lock (IS / IX): 테이블 레벨의 “의도 표시”

InnoDB는 다중 단위 락(MGL: Multiple Granularity Locking)을 위해 **테이블 레벨의 의도 락**을 둔다.

- **IS (Intention Shared)**: “이 테이블의 일부 row에 S 락을 잡을 거야/잡았어”
- **IX (Intention Exclusive)**: “이 테이블의 일부 row에 X 락을 잡을 거야/잡았어”

핵심
- IS/IX는 보통 row 락과 함께 자동으로 잡히며,
- “테이블 전체 락”과의 호환성 판단을 빠르게 하기 위한 장치다.

---

## 4. Metadata Lock (MDL): 스키마 변경이 막히는 이유

MySQL에는 **메타데이터 락(MDL)** 이 있다.

- DML(SELECT/INSERT/UPDATE/DELETE)도 MDL을 잡는다.
- 그래서 긴 트랜잭션/긴 쿼리 하나가
  - `ALTER TABLE ...` 같은 DDL을 블로킹하거나,
  - 반대로 DDL이 다른 쿼리를 막는 상황이 생길 수 있다.

운영 팁
- “ALTER가 안 끝난다”는 현상은, 실제로는 **먼저 시작한 긴 SELECT가 MDL을 잡고 있는 경우**가 흔하다.

---

## 5. 격리수준과 락의 상호작용(요약)

- **READ COMMITTED**
  - InnoDB에서 gap lock을 덜 잡는 구성도 가능(버전/설정/쿼리 형태 영향).
  - phantom을 완전히 막는 대신 동시성을 더 확보하는 트레이드오프가 된다.

- **REPEATABLE READ (InnoDB 기본)**
  - locking read에서 next-key/gap이 적극적으로 관여할 수 있다.
  - “범위 조건 + FOR UPDATE”는 특히 주의.

- **SERIALIZABLE**
  - 동시성 급락(읽기에도 강한 잠금).
  - 일반적으로 시스템 전체 처리량을 크게 떨어뜨릴 수 있어 신중히.

---

## 6. 블로킹/데드락을 머릿속으로 그리는 ASCII 타임라인

### 6.1 블로킹 예시: range + FOR UPDATE 때문에 INSERT가 막힘

가정
- 테이블: `orders(id PK, user_id idx, created_at idx, ...)`
- 트랜잭션 A가 특정 유저의 “오늘 주문”을 갱신하려고 범위 잠금을 건다.

```
T1 (A): BEGIN
T2 (A): SELECT * FROM orders
        WHERE user_id = 10 AND created_at >= '2026-02-19'
        FOR UPDATE
        -- next-key/gap이 걸리며 범위가 잠길 수 있음

T3 (B): BEGIN
T4 (B): INSERT INTO orders(user_id, created_at, ...) VALUES (10, '2026-02-19 09:00:00', ...)
        -- A가 잡은 gap/next-key와 충돌 -> 대기(Blocking)

T5 (A): COMMIT
T6 (B): INSERT 진행
```

대응
- 범위 잠금을 반드시 해야 하는지 재검토(업데이트 대상이 “정확히 한 건”인가?).
- 조건이 인덱스로 잘 좁혀지는지 확인(불필요한 범위 잠금 확대 방지).
- 더 좁은 조건(예: 고유키)으로 잠금 범위를 줄이기.

### 6.2 데드락 예시: 업데이트 순서가 다르면 교착이 생김

```
T1 (A): BEGIN
T2 (B): BEGIN

T3 (A): UPDATE accounts SET ... WHERE id = 1;  -- id=1 X lock
T4 (B): UPDATE accounts SET ... WHERE id = 2;  -- id=2 X lock

T5 (A): UPDATE accounts SET ... WHERE id = 2;  -- B가 잡은 락 때문에 대기
T6 (B): UPDATE accounts SET ... WHERE id = 1;  -- A가 잡은 락 때문에 대기

=> 서로가 서로를 기다림(Deadlock) -> DB가 한 쪽을 롤백
```

대응
- 여러 row를 갱신해야 한다면 **항상 같은 순서(예: PK 오름차순)** 로 락을 획득하도록 코딩.

---

## 7. 운영 체크리스트(현장에서 바로 쓰는 것)

- “왜 느린가?”가 락 대기인지부터 확인
  - MySQL: `SHOW ENGINE INNODB STATUS` / `performance_schema` / `sys` 스키마
  - 대기 이벤트가 `lock`/`metadata lock`인지 확인

- 쿼리가 인덱스를 제대로 타는지 확인
  - 인덱스를 못 타면, 잠금 단위가 불필요하게 커질 수 있다(더 많은 레코드/갭을 잠금).

- 긴 트랜잭션 제거
  - 애플리케이션 레벨에서 “BEGIN 후 외부 API 호출” 같은 패턴은 락 유지 시간을 폭증시킨다.

- 타임아웃/재시도 정책
  - `lock wait timeout` / deadlock 발생 시 재시도(backoff + jitter) 전략을 준비.

---

## 8. PostgreSQL 간단 메모(차이점만)

- PostgreSQL은 MVCC 기반이지만 **gap lock(next-key)** 같은 개념이 MySQL과 다르게 나타난다.
- phantom 방지는 주로 **predicate locking(Serializable)** 같은 메커니즘을 통해 제공된다.
- 실무적으로는 “MySQL에서 발생하는 INSERT 블로킹 패턴”이 Postgres에서는 동일하게 재현되지 않을 수 있다.

---

## 참고
- MySQL 8.0 Reference Manual: InnoDB Locking
- MySQL 8.0 Reference Manual: Metadata Locking
- PostgreSQL Docs: MVCC / Explicit Locking / Serializable Isolation