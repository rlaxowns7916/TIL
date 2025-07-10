# PreparedStatement
- JDBC 기능이 아닌 **DBMS의 기능**
- MySQL의 경우 `PREPARE`을 통해서 사용한다.
- 주 목적은 `캐싱을 통한 성능향상`, `SQL Injection 방어` 이다.

## Statement vs PreparedStatement 비교

| 구분 | Statement | PreparedStatement |
|------|-----------|-------------------|
| **컴파일** | 매 실행 시 하드 파싱 | 생성 시 1회, 이후 소프트 파싱 |
| **성능** | 반복 실행 시 낮음 | 반복 실행 시 높음 |
| **보안** | SQL 인젝션 취약 | SQL 인젝션 방어 |
| **매개변수화** | 미지원 | ? 플레이스홀더 지원 |
| **주 용도** | DDL (CREATE, ALTER, DROP) | DML (SELECT, INSERT, UPDATE, DELETE) |

## SQL Injection
- PreparedStatement는 쿼리문과 데이터를 구분한다.
- 매개변수를 **바이너리 프로토콜**로 전송하여 SQL 명령으로 해석되지 않게 한다.

### 취약한 코드 (Statement)
```java
String userName = request.getParameter("userName");
String query = "SELECT * FROM users WHERE name = '" + userName + "'";
// 입력: ' OR '1'='1 → 모든 데이터 유출
```

### 안전한 코드 (PreparedStatement)
```java
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
pstmt.setString(1, userName);
// 악의적 입력도 단순 문자열로 처리
```


## 성능향상

### SQL 쿼리 생명주기
1. **파싱(Parse)** - SQL 구문 분석
2. **재작성/분석(Rewrite/Analyze)** - 쿼리 검증
3. **최적화/계획(Optimize/Plan)** - 실행 계획 생성
4. **실행(Execute)** - 실제 실행

- **Statement**: 매 실행마다 1-3단계 반복
- **PreparedStatement**: 1-3단계는 한 번만, 이후 4단계만 실행


##  JDBC 드라이버 설정 (MySQL Connector/J)
| 옵션 | 기본값 | 설명 | 영향도 |
|------|--------|------|--------|
| `useServerPrepStmts` | **false** | MySQL 서버에 PREPARE 명령 실행 여부 |
| `cachePrepStmts` | **false** | JDBC 드라이버 레벨 PreparedStatement 객체 재사용 | 
| `prepStmtCacheSize` | 25 | **Connection당** 캐시할 PreparedStatement 개수 | 
| `prepStmtCacheSqlLimit` | 256 | 캐시할 쿼리 최대 바이트 크기 |

### 중요한 제약사항
1. **Connection 단위 캐시**: 동일 쿼리라도 다른 Connection이면 별도 PreparedStatement 생성
2. **Global 공유 없음**: MySQL Connector/J는 Connection 간 PreparedStatement 공유하지 않음
3. **캐시 크기 계산**: `Connection Pool 크기 × prepStmtCacheSize = 실제 MySQL 서버 캐시 개수`


## 운영 환경 제약사항
- DBMS에서 PreparedStatement 갯수관리가 중요하다.
- LRU와 같은 정책을 사용하는 것이 아닌, MAX 갯수를 넘어서면 Exception이 발생한다.

### MySQL 서버 제한사항
```sql
-- 기본 설정 확인
SHOW VARIABLES LIKE 'max_prepared_stmt_count';  -- 기본값: 16,382개

-- 현재 상태 모니터링
SHOW GLOBAL STATUS LIKE 'Prepared_stmt_count';
SELECT * FROM performance_schema.prepared_statements_instances;
```

### 제한 초과 시 문제
```java
// max_prepared_stmt_count 초과 시
SQLException: Can't create more than max_prepared_stmt_count statements (current value: 16382)
// Error Code: 1461
```

**중요**: 기존 PreparedStatement를 **자동으로 제거하지 않고 에러 발생**