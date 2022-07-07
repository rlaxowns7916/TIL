# Transaction
- DB의 작업 단위
  - 일관된 상태를 유지하는 성공과 실패를 보장한다.
- DB 벤더마다 구현방식은 다르다.
- 결과반영은 Commit, 되돌리기는 Rollback이다.


## Commit
- Commit 이전까지는 임시데이터 이다.
    - 해당 트랜잭션을 진행하고 있는 세션에서만 조회 가능하다.
    - 각 트랜잭션 끼리는 독립적이기 때문에 조회가 불가능하다. (IsolationLevel 이 ReadUncommited 라면 보임)
- 자동Commit(default)과 수동Commit 이 있다.
  - Query가 실행 될 때마다 Commit이 된다.
  - Transaction의 기능을 제대로 사용할 수 없다.
    - Transaction은 수동Commit을 사용해야 한다.
  - 수동Commit으로 전환하는 것을 관례상 **트랜잭션의 시작** 이라고 한다.
```sql
set autocommit false;
insert into member(member_id,money) values(1,10000);
insert into member(member_id,money) values(2,20000);
commit ;
```

## Rollback
- 트랜잭션 시작 이전 시점으로 되돌리는 것
- 임시데이터가 DB에 반영되지 않고 삭제된다.
```sql
set autocommit false;
insert into member(member_id,money) values(1,10000);
insert into member(member_id,money) values(2,20000);
rollback;
```
