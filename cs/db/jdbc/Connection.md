# Connection
- Client가 DB에 작업을 하기위해 접근하는 것
  - 주로 WAS나 DB접근 툴 같은 클라이언트를 통해서 DB서버에 접근한다.
1. DB서버에 연결을 요청하여 Connection을 맺는다.
2. DB는 서버 내부에 세션을 생성하며, 모든 동작은 세션을 통해서 진행된다. (트랜잭션 시작, Commit, Rollback 등)
3. 사용자가 Connection을 닫거나, DBA가 강제로 세션을 종료하면 세션이 종료된다.


## DriverManager
- JDBC가 제공해준다.
- 라이브러리에 등록된 DB 드라이버들을 관리한다.
  - 라이브러리에 등록된 드라이버들을 자동으로 인식한다.
  - 각각의 드라이버에게 던져보고, 드라이버들이 본인이 실행할 수 있으면 실행을 하며, 뒤로 요청이 넘어가지 않는다.
- 커넥션을 맺는 역할을 수행한다.

## DriverManager 를 통해서 DB Connection을 얻는과정
```java
@Slf4j
class Example {
    public static void main(String[] args) {
        try {
            Connection connection =
                    DriverManager.getConnection(ConnectionProperties.CONNECTION_URL, ConnectionProperties.USER_NAME, ConnectionProperties.PASSWORD);
            log.info("get Connection {}, class ={}", connection, connection.getClass());
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}

```
