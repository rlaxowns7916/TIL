# Connection

- JDBC를 통해서 DB Connection을 얻는과정

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

## DriverManager
- JDBC가 제공해준다.
- 라이브러리에 등록된 DB 드라이버들을 관리한다.
  - 라이브러리에 등록된 드라이버들을 자동으로 인식한다.
  - 각각의 드라이버에게 던져보고, 드라이버들이 본인이 실행할 수 있으면 실행을 하며, 뒤로 요청이 넘어가지 않는다.
- 커넥션을 맺는 역할을 수행한다.