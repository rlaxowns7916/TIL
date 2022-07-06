# DataSource
- Connection을 맺는 방법을 추상화한 인터페이스이다.
  - Connection을 맺는 방법을 구현체에 의존하지 않고 DataSource 인터페이스를 통해서 진행하게된다.
  - Connection 로직과 어플리케이션 로직의 분리가 가능해진다.
  - 가장 효과를 보는 예시는, ConnectionPool의 종류를 변경 할 때 이다.
- 핵심 기능은 **Connection조회** 이다.
  - 대부분의 Connection Pool은 DataSource를 구현해놓았다.
  - 기본적으로 DriverManager는 DataSource구현체를 가지고 있지 않다.
    - Spring에서는 DriverManagerDataSource를 구현해놓았다.
  - DriverManager와 다르게, 객체를 만든 이후 Connection을 맺을 때는, URL, USERNAME, PASSWORD가 필요하지않다.
    - 설정과 사용을 분리한 것이다.
    - 설정정보들이 어플리케이션 전역으로 퍼지는걸 막을 수 있다.
- ConnectionPool과 사용된다.
## 1. DriverManger
```java
class Example{

  public static void main(String[] args) {
    /**
     * Spring에서 제공해준다.
     * 매 요청마다 새로운 Connection을 제공해준다.
     * DataSource를 구현하고 있다.
     */
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD)
    Connection conn = dataSource.getConnection();
  }
}
```

## 2. ConnectionPool
```java
class Example{
  public static void main(String[] args) {
    /**
     * Spring Default Connection Pool
     * DataSource를 구현하고있다.
     * 별도의 Thread로 커넥션 풀을 채운다. (커넥션을 채우는동안 서비스가 실행대기중이면 안되기 때문이다)
     */
    
    //구체적인 타입이어야 한다. (DI 할 때는 DataSource로 넘겨주면됨
      HikariDataSource hikariDataSource = new HikariDataSource();
      hikariDataSource.setJdbcUrl(URL);
      hikariDataSource.setUsername(USERNAME)
      hikariDataSource.setPassword(PASSWORD);
      hikariDataSource.setMaximumPoolSize(10); //default

      Connection conn = dataSource.getConnection();
  }
}
```