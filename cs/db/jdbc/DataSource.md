# DataSource
- Connection을 맺는 방법을 추상화한 인터페이스이다.
  - Connection을 맺는 방법을 구현체에 의존하지 않고 DataSource 인터페이스를 통해서 진행하게된다.
  - Connection 로직과 어플리케이션 로직의 분리가 가능해진다.
  - 가장 효과를 보는 예시는, ConnectionPool의 종류를 변경 할 때 이다.
- 핵심 기능은 **Connection조회** 이다.
  - 대부분의 Connection Pool은 DataSource를 구현해놓았다.
  - 기본적으로 DriverManager는 DataSource구현체를 가지고 있지 않다.
    - Spring에서는 DriverManagerDataSource를 구현해놓았다.