# R2DBC란
- Reactive Relational Database Connectivity
- **비동기 NonBlocking기반 관계형 데이터베이스 드라이버**
- Reactive Streams 스펙을 제공하여, Proeject Reactor 기반으로 구현

## 기존의 프로젝트는 비동기가 안되는가?
- JDBC는 Blocking기반으로 동작한다.
  - JDBC기반으로 동작하는 JPA도 마찬가지다.
- 이미 너무나 많은 곳에서 동기방식의 JDBC를 사용하고 있기 때문에, 스펙변경이 쉽지 않다.


## R2DBC 지원 데이터베이스

### 공식지원
1. r2dbc-h2
2. r2dbc-mssql
3. r2dbc-pool

### Vendor사 지원
- oracle-r2dbc
- r2dbc-mariadb
- r2dbc-postgresql

### 커뮤니티
- r2dbc-mysql
  - r2dbc-spi와 Netty를 기반으로 만들어졌다.
  - r2dbc-spi 스펙을 구현하여, 여러 데이터베이스 시스템과 호환된다.

## R2DBC-SPI
- R2DBC 스펙의 기본이다.
- R2DBC-ServiceProviderInterface
- Connection, ConnectionFactory등 DB Connection 스펙을 정의한다.
  - R2DBC Exception
  - R2DBCTimeOutException
  - R2DBCBadGrammerException 
- Result 스펙을 정의한다.
  - Result
  - Row
  - RowMetaData
  - Statement
- Transaction 스펙을 정의한다.
  - TransactionDefinition (Isolation, readOnly, lockWaitTime 을 정의)
  - SavePoint 제공
  - Transaction Commit혹은 Rollback

## SpringDataR2DBC
- JPA와 같은 ORM이 아니다.
  - 그렇기 때문에, 연관관계를 맺는 기능을 제공하지 않는다.
  - Caching, 지연로딩 또한 제공하지 않는다.