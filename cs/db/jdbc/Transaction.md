# Transaction

- DB의 작업 단위
    - 일관된 상태를 유지하는 성공과 실패를 보장한다.
- DB 벤더마다 구현방식은 다르다.
- 결과반영은 Commit, 되돌리기는 Rollback이다.
- 트랜잭션이 유지되려면, 같은 Connection에서 유지되어야한다.

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
set autocommit = false;
insert into member(member_id, money)
values (1, 10000);
insert into member(member_id, money)
values (2, 20000);
commit;
```

## Rollback

- 트랜잭션 시작 이전 시점으로 되돌리는 것
- 임시데이터가 DB에 반영되지 않고 삭제된다.

```sql
set autocommit = false;
insert into member(member_id, money)
values (1, 10000);
insert into member(member_id, money)
values (2, 20000);
rollback;
```

## JDBC Transaction - Connection 객체를 Parameter

```java
import java.sql.PreparedStatement;
import java.sql.SQLException;

class Example {
    public static void main(String[] args) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD)
        Connection conn = dataSource.getConnection();

        try {
            /**
             * 트랜잭션 시작
             */
            conn.setAutoCommit(false);

            /**
             * 트랜잭션은 같은 Connection에서 유지되어야한다.
             * Parameter로 Connection을 넘기는 것은 가장 쉬운 방법이다.
             */
            Member member = findById(conn, 1);
            /**
             * 문제가 없다면 Commit
             */
            conn.commit();
        } catch (Exception e) {
            conn.rollback(); //실패시 롤백
        } finally {
            if (conn != null) {
                try {
                    /**
                     * ConnectionPool에 반환을 하는 것으로, 
                     * 설정이 적용 된 채로 살아 있기 때문에,
                     * AutoCommit을 다시 설정해주어야 한다.
                     *
                     * 만약 Connection을 그때 그때 새롭게 만든다면
                     * 상관없다.
                     */
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Member findById(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE member_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        /**
         * Transacton은 계속 유지되어야 하기 때문에,
         * finally에서 Connection을 Close하지 않는다.
         */
    }
}
```

- 비즈니스 로직이 JDBC 기술에 너무 의존적이다.
    - 다른 기술로 교채시에 서비스로직이 변경할 부분이 많다.
- 중복되는 작업이 잦다.
    - 트랜잭션 시작
    - try-catch
    - SQL Exception

## 트랜잭션 추상화 (Spring)

- PlatformTransactionManager라는 공통 인터페이스를 가진다.
    - DataSourceTransactionManager, JpaTransactionManager 등 각각의 구현체가 인터페이스를 구현한다.
- 서비스로직이 DI를 통해서 해당 인터페이스에 의존하므로, 특정 기술의 종속성에서 벗어날 수 있다.

## PlatformTransactionManager

- 내부적으로 **TransactionSynchronizationManager**를 사용한다.
  - 트랜잭션은 같은 Connection에서 실행되어야 한다.
  - ThreadLocal을 사용하여, 멀티쓰레드 환경에서 안전하게 커넥션을 동기화 시켜준다.
- DAO에서 TransactionManager가 관리하는 Connection을 얻기위해서는 **DataSourceUtils** 를 사용해야한다.
  - DataSource.getConnection(): ThreadLocal에서 관리되는 Connection을 얻는다.
  - DataSource.releaseConnection(): 트랜잭션이 종료되었다면 Connection을 종료한다. (무조건 종료아님)

1. DataSource를 통해서 Connection을 생성한다.
2. 생성한 Connection을 TransactionSynchronizationManger에 보관한다.
3. TransactionSynchronizationManager에서 Connection을 꺼내어 사용한다.
4. 트랜잭션이 종료되면, 보관했던 Connection을 종료한다.

```java

@RequiredArgsConstructor
class Example {
    private final PlatformTransactionManager transactionManager;

    public static void main(String[] args) {
        /**
         * 트랜잭션 시작
         */
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            bizLoginc();
            transactionManager.commit(status); //성공시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        /**
         * Connection 릴리즈 또한 TransactionManager가 처리해준다.
         */
    }
}
```