# Lock
- 동시성 문제를 해결하기 위한 방법이다.
- 크게 2가지가 있다.
  - MySQL NamedLock까지 포함하면 3가지

## 1. 비관적 락 (Pessimistic Lock)
- DB의 Lock을 실제 사용하는 것
- ExclusiveLock (Select For Update) 를 사용한다.
- @Transactional에 속해 있어야 한다.
  - Lock의 Release가 Transaction의 생명주기를 따라가기 떄문이다.
  - Select에서 Lock을 획득하고 Update 로직을 수행하면 된다.
- Lock을 실제로 건다는 측면에서 동시성에서는 문제를 야기할 수 있으나, Conflict이 자주 발생하는 환경이라면 오히려 좋을 수 있다.
```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM PessimisticEntity  p WHERE p.id = :id")
  Optional<PessimisticEntity> findPessimisticEntitiesWithLock(@Param("id") Long id);
```


### TimeOut 설정
- DB의 Lock획득에는 많은 시간이 걸릴 수 있기 때문에, TimeOut을 지정해주어서 리소스 낭비를 최소화 해야한다.
- DB Vendor마다 제공해주는 것이 다르다.
  - https://blog.mimacom.com/handling-pessimistic-locking-jpa-oracle-mysql-postgresql-derbi-h2/ 
![스크린샷 2022-11-12 오후 5 13 20(2)](https://user-images.githubusercontent.com/57896918/201474705-e6f535cd-50cd-4205-9a6e-df39f9d363a6.png)

### Lock Scope
- Lock의 Scope를 지정해 줄 수 있다.
- 기본적으로는 해당 Entity에만 Lock을 건다. (NORMAL)
- 연관된 Entity에 까지 Lock을 걸 수 있다. (EXTENDED)


## 2. 낙관적 락 (Optimistic Lock)
- Version + WHERE을 사용해서 Lock을 걸지않고 동시성문제를 해결하는 방법이다.
- Conflict이 자주 발생하는 환경이라면, Rollback에 오히려 더 많은 리소스르 소모하게 될 수도 있다.
  - 개발자가 Rollback에 따른 전략을 직접 정의해야 한다.
- JPA에서는 아래의 조건을 통해 설정 해 줄 수 있다.
  - Entitiy에 @Vesrion 컬럼
  - Read용이라면, LockModeType.OPTIMISTIC
  - Write용이라면, LockModeType.OPTIMISTIC_FORCE_INCREMENT
- Transaction격리수준에 신경써야한다.
  - 같은 Transaction에 있으면 RepetableRead로 인해서 Conflict 발생 후 재시도에도, 똑같은 Version을 읽게 될 것이다
  - 실행부와 구현부의 Transaction을 분리하거나, TransactionLevel을 낮춰야한다.

```java
  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  @Query("SELECT o FROM OptimisticEntity  o WHERE o.id = :id")
  Optional<OptimisticEntity> findEntityWithOptimisticLock(@Param(value = "id") Long id);
```

### JPA OptimisticLock의 타입

#### 1. @Version 
- javax.persistence.version
- Entity에 @Version만 추가해도 OptimisticLock이 설정된다.
- Update 할때의 Version 체크를 수행한다.

#### 2. @Version + Optimistic
- Read시에도 Version을 체크한다.
- Dirty Read와 Non-RepeatableRead를 방지한다.

#### 3.@Version + OPTIMISTIC_FORCE_WRITE
- Version 정보를 강제로 증가시킨다.
- Entity를 수정하지 않아도 Transaction Commit시에 버전이 증가한다.
  - 연관관계조회 때 에도 Version이 증가한다.
  - 연관관계를 논리적인 묶음으로 Version을 통해서 관리 할 수 있다.