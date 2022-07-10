# Lock
- 동시성을 해결하기 위한 방법
- 데이터 정합성을 지킬 수 있다.
- Lock을 선점해야 데이터를 변경 할 수 있다.
  - Lock을 선점하지못하면, 데이터를 변경하지 못한다.
  - 선점한 세션이 Lock을 풀 때까지 대기한다.
    - 무한정 대기하는 것은 아니고, TimeOut 오류가 발생한다. (Lock을 대기하는 쪽에서)
    - 대기시간은 설정 할 수 있다.
- 데이터의 조회는 Lock을 획득하지 않는다.
  - 조회시점에 Lock을 원한다면 'SELECT FOR UPDATE' 구문을 사용하면된다.
    - 조회하는 동안에, 다른곳에서 변경을 하지못하게 막을 수 있다.
    - 조회하는 쿼리의 트랜잭션 동안 유지된다.
    

## SQL-Lock
```mysql
SET LOCK_WAIT_TIMEOUT=60000;
set autocommit=false;
```
- LOCK_WAIT_TIMEOUT을 설정안해줘도 DB의 default 설정을 따라간다.
- 트랜잭션이 시작되면 자동으로 Lock을 획득한다.
- Commit 혹은 Rollback 시, Lock을 풀게 된다.

## JDBC-Lock
