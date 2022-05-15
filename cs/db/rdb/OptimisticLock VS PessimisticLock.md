# OptimisticLock(낙관적 락) VS Pessimistic Lock(비관적 락)
- 동시성 문제를 해결하는 방법이다.

## Optimistic Lock (낙관적 락)
- **동시성 문제가 발생하지 않을 것이라고 가정한다.**
- Application 수준에서의 Lock
  - Version을 통행서 Lock을 관리한다.
    - WHERE 절을 이용해서 버전이 올바른지 여부를 판단하여 갱신을 수행한다.
  - 충돌이 발생하면 Rollback 을 수행한다.
- Lock을 선점하지 않고, 문제가 발생하면 그때 해결한다.
- 동시성 수준이 높다.
- 잦은 충돌이 일어나는 경우 롤백 처리에 대한 비용이 많이 소모 될 수 있다.


## Pessimistic Lock (비관적 락)
- **동시성문제가 발생 할 것이라고 가정한다.**
- Database 수준에서의 Lock
  - Row 수준의 Lock이다. 
  - SharedLock (쓰기 잠금) 이나 ExclusiveLock(읽기/쓰기 잠금)을 설정한다.
- 데이터의 무결성 보장수준이 매우 높다.
- 동시성이 떨어진다는 단점이 있다.
- DeadLock 이 발생할 수 도 있다.
  - Lock의 유효기간을 설정함으로 해결할 수 있다.
