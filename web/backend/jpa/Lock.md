# JPA Lock (비관적 락/낙관적 락) 정리

동시성 업데이트(동일 행을 여러 요청이 동시에 변경)에서 **Lost Update**를 막기 위한 대표 전략을 JPA 관점에서 정리합니다.

- 핵심 선택 기준
  - **충돌이 잦다** → 비관적 락(대기/교착 관리 필요)
  - **충돌이 드물다** → 낙관적 락(재시도/에러처리 필요)

---

## 0. 먼저 확인할 것: “락”이 필요한 문제인가?

아래처럼 **같은 레코드를 동시에 갱신**할 때가 락(또는 버전)이 필요한 전형적 상황입니다.

- 재고 차감
- 쿠폰/포인트 사용
- 좌석 예약
- 상태 전이(예: PENDING → PAID)

반대로 단순 조회/통계처럼 **쓰기 경합이 없거나**,
업데이트가 **멱등(upsert) + 조건부 갱신(WHERE 상태 조건)** 으로 해결되는 문제는 락 없이 풀리는 경우가 많습니다.

---

## 1. 비관적 락(Pessimistic Lock)

DB가 제공하는 락을 이용해 **다른 트랜잭션의 접근을 물리적으로 제한**합니다.

### 1.1 동작 개요

- 보통 `SELECT ... FOR UPDATE` 계열 쿼리로 행 락을 획득합니다.
- 락의 해제 시점은 **트랜잭션 종료(커밋/롤백)** 입니다.
- 따라서 락은 반드시 `@Transactional` 범위 안에서 획득/사용되어야 합니다.

ASCII 흐름(행 락 기준):

```
Tx-A                         Tx-B
 |   SELECT ... FOR UPDATE     |
 |---------------------------->| (row lock acquired)
 |   business update           |
 |   COMMIT                    |
 |---------------------------->| (lock released)
 |                             |   SELECT ... FOR UPDATE
 |                             |-------------------------> (now succeeds)
```

### 1.2 JPA에서 사용하는 LockMode

- `PESSIMISTIC_READ`
  - DB/격리수준에 따라 동작이 달라질 수 있습니다(공유 락/읽기 락).
- `PESSIMISTIC_WRITE`
  - 업데이트 목적의 대표 선택. 일반적으로 `FOR UPDATE`.
- `PESSIMISTIC_FORCE_INCREMENT`
  - 버전 필드가 있을 때, 락 + 버전 증가를 강제(업데이트 충돌 신호를 더 강하게 남김).

Repository 예시:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select p from Product p where p.id = :id")
Optional<Product> findByIdForUpdate(@Param("id") Long id);
```

### 1.3 락 대기 시간(timeout) 설정

락은 대기열을 만들 수 있으므로 **무한 대기**를 피해야 합니다.

- JPA 표준 힌트: `javax.persistence.lock.timeout` (ms)
- Hibernate 힌트(버전에 따라 키가 다를 수 있음):
  - `javax.persistence.lock.timeout`
  - `jakarta.persistence.lock.timeout`

예시:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
@Query("select p from Product p where p.id = :id")
Optional<Product> findByIdForUpdateWithTimeout(@Param("id") Long id);
```

주의:
- DB 벤더별로 “NOWAIT / SKIP LOCKED” 지원 여부 및 매핑이 다릅니다.
- 운영에서는 **timeout + 재시도 정책**(또는 사용자에게 재시도 안내)을 함께 설계합니다.

### 1.4 데드락(교착상태)와 운영 체크리스트

비관적 락을 쓰면 교착상태가 현실적으로 발생할 수 있습니다.

대표 패턴:

```
Tx-A: lock(row 1) -> lock(row 2)
Tx-B: lock(row 2) -> lock(row 1)
=> 서로 대기 => deadlock
```

대응 원칙:
- 락 획득 순서를 **항상 동일하게** 유지(정렬된 PK 순서 등)
- 트랜잭션을 **짧게** 유지(외부 API 호출/긴 연산 금지)
- timeout/데드락 에러에 대한 **재시도**(지수 백오프, 최대 횟수)
- 로깅/메트릭
  - 락 대기 시간, 데드락 빈도, 타임아웃 비율

---

## 2. 낙관적 락(Optimistic Lock)

DB 락을 오래 잡지 않고, **버전 컬럼(version)** 을 이용해 “경합을 커밋 시점에 감지”합니다.

### 2.1 동작 개요 (@Version)

Entity에 `@Version` 필드를 추가합니다.

```java
@Entity
class Product {
  @Id Long id;

  @Version
  Long version;

  Long stock;
}
```

업데이트 시점에 다음 형태로 동작합니다(개념):

```
UPDATE product
   SET stock = ?, version = version + 1
 WHERE id = ?
   AND version = ?
```

- 다른 트랜잭션이 먼저 업데이트해 version이 바뀌면 `UPDATE 0 row`가 되어 충돌 감지
- JPA/Hibernate는 보통 `OptimisticLockException` 또는 `ObjectOptimisticLockingFailureException`(Spring)으로 매핑

ASCII 흐름:

```
Tx-A: read (version=10) -----> update where version=10 (success, version=11)
Tx-B: read (version=10) -----> update where version=10 (0 row) => OptimisticLockException
```

### 2.2 LockModeType.OPTIMISTIC vs FORCE_INCREMENT

- `OPTIMISTIC`
  - 읽기 시점에도 버전 일관성을 강하게 요구(구현체에 따라 동작 차이가 있을 수 있음)
- `OPTIMISTIC_FORCE_INCREMENT`
  - 엔티티를 “수정하지 않아도” 커밋 시 버전을 올려, 동시 접근에게 충돌을 강제
  - 논리적으로 묶인 자원(예: aggregate) 동시 수정 신호를 만들 때 사용

### 2.3 낙관적 락을 쓸 때 필수: 재시도 전략

낙관적 락은 충돌이 나면 결국 “실패”하므로, 서비스 레벨에서 정책을 정해야 합니다.

- 자동 재시도(권장: 짧은 백오프 + 최대 횟수)
- 사용자에게 재시도/새로고침 안내
- 도메인 정책(예: 재고는 최신 값을 다시 읽고 재계산)

간단 예시(개념):

```java
for (int i = 0; i < 3; i++) {
  try {
    service.decreaseStock(productId, amount);
    return;
  } catch (ObjectOptimisticLockingFailureException e) {
    sleep(backoff(i));
  }
}
throw new ConflictException("동시 요청으로 인해 처리에 실패했습니다. 다시 시도해주세요.");
```

---

## 3. 어떤 걸 선택할까? (실전 가이드)

- **정확성이 최우선 + 충돌이 잦음**: 비관적 락
  - 단, 트랜잭션 짧게 / 락 순서 고정 / timeout 필수
- **대부분 충돌이 드묾 + 고성능 필요**: 낙관적 락
  - 재시도/충돌 처리 UX/에러 모델을 반드시 설계

추가로 자주 쓰는 대안:
- 조건부 업데이트(상태 전이):
  - `UPDATE ... WHERE id=? AND status='PENDING'` 형태로 CAS처럼 처리
- 분산락/애플리케이션락(예: Redis, DB named lock)
  - “같은 키에 대한 임계구역”을 만들고 싶을 때
  - 단, 장애/만료/재진입/공정성/관측성을 포함해 운영 난이도가 올라갑니다.

---

## 참고
- Jakarta Persistence: Locking / @Lock / LockModeType
- Hibernate User Guide: Locking, Pessimistic locking timeout hints
- Spring Data JPA: @Lock, @QueryHints
