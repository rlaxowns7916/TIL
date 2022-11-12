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
