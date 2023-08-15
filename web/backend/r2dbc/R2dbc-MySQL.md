# R2dbc-MySQL
- R2dbc-spi를 구현했다.


## R2dbc MySql Connection
- Connection Interface를 구현했다.
- Transaction의 Commit, Rollback을 지원한다.
- ConnectionFactory로 부터 Connection을 얻을 수 있다.
```text
Statement    < - - - - - - - - - MySqlStatement
    ^                                    ^
  create                               create
    |                                    |
Connection   < - - - - - - - - - MySqlConnection
    |                                    |
   has                                  has
    v                                    v
ConnectionMetadata < - - - - - - MySqlConnectionMetaData
```

## R2dbc 설정

### [1] MysqlConnectionConfiguration.builder
- 프로그래밍 방식의 연결방식 제공
- MySQL 특화 설정 가능
```java
public class MySqlConnectionBuilderExample {
    public static void main(String[] args) {
        MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
                .host("localhost")
                .port(3306)
                .username("username")
                .password("password")
                .database("test")
                .build();

        ConnectionFactory connectionFactory = MySqlConnectionFactory.from(configuration);

        // 이후에 connectionFactory를 사용하여 데이터베이스 연결 및 쿼리 수행
    }
}
```

### [2] ConnectionFactoryOptions
- R2dbc 범용 설정 방법
  - 다양한 R2dbc 구현체를 갈아 낄 수 있다.
- 연결 설정시 Key-Value 쌍을 명시해야 하기 때문에 덜 명시적일 수 있다.
```java
public class ConnectionFactoryOptionsExample {
    public static void main(String[] args) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, "localhost")
                .option(PORT, 3306)
                .option(USER, "username")
                .option(PASSWORD, "password")
                .option(DATABASE, "test")
                .build();

        ConnectionFactory connectionFactory = ConnectionFactories.get(options);

        // 이후에 connectionFactory를 사용하여 데이터베이스 연결 및 쿼리 수행
    }
}
```


## R2dbc MySql ConnectionFactory
- MySqlConnection을 Mono형태로 포함한다.
  - Create메소드를 사용하는데, Singleton 형태로 DI받았던 것을 그대로 리턴한다.
- MySqlConnectionFactoryMetadata를 반환한다.
- **MySqlConnectionConfiguration을 인자로 받아서, MySqlConnectionFactory를 생성한다.**