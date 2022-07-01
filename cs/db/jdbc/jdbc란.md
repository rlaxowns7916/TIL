# JDBC(Java DataBase Connectivity)

## 일반적인 DB Connection
1. 커넥션 연결 (TCP/IP를 사용해서 맺는다.)
2. 커넥션이 맺어지면 DB가 이해할 수 있는 SQL문을 전달한다.
3. DB는 SQL의 응답값을 서버에게 전송한다.

## JDBC는 왜 만들어졌는가?
- **각 DB마다 문법이 달랐기 때문이다.**
  - 커넥션을 맺는 방법
  - SQL문법
  - 결과를 처리하는 방법
- 1997년에 출시된 Java 표준 DB접근 인터페이스
  - 커넥션을 맺고, SQL질의를 하며, 결과를 가져오는 API를 제공한다.
  - **Connection**: DB와 커넥션을 맺는 객체
  - **Statement/PreparedStatement**: SQL을 전달하는 객체
  - **ResultSet**: DB로 부터 결과를 받아오는 객체
- JDBC 드라이버가 구현체이다.
  - 각각의 DB벤더들이 JDBC 인터페이스 스펙에 맞게 구현하여 라이브러리로 제공한다.

## JDBC를 사용하는 기술들 

### 1. SQL MAPPER
- JdbcTemplate, MyBatis
- JDBC의 반복적인 코드들을 줄여준다.
- SQL실행결과를 쉽게 객체에 매핑해준다.
- SQL작성에 관한 지식이 있다면 쉽게 배울 수 있다.
- SQL의존적인 개발에서 벗어나지 못한다.

### 2. ORM
- JPA
- SQL을 객체지향 패러다임에 맞게 사용이 가능하다.
- SQL의존적인 개발을 하지 않아도 된다.
  - SQL을 작성하지 않아도 된다.
- 특정 DB 벤더에 의존적인 문법이 아니다.
- 러닝커브가 높다.