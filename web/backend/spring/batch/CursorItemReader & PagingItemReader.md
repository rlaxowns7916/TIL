# CursorItemReader
- 대용량 데이터를 다루는 Batch 어플리케이션에서는 DB I/O 성능문제와, 메모리 자원의 효율성문제를 해결 할 수 있어야 한다.
  - SpringBatch에서는 Cursor기반과, Paging기반을 제공한다.
- JDBC ResultSet이 기본 매커니즘을 사용
- 현재 행의 커서를 유지하며, 다음 데이터를 호출하면 다음행으로 커서를 이동하는 Streaming I/O 방식이다.
- **DB Connection이 연결되면, 배치가 완료될 때 까지 데이터를 읽어오기 떄문에, DB와 SocketTimeOut을 충분히 큰값으로 설정해야 한다.**
    - Connection 연결 유지시간과, 메모리 공간이 충분하다면 대용량 처리가 가능하다. 
    - 적절한 FetchSize 조절이 필요하다.
- 모든 결과를 메모리에 할당하기 때문에, 메모리 사용량이 많아지는 단점이 있다.


## JdbcCursorItemReader
- Cursor기반의 Jdbc구현체, ResultSet과 함께 사용되며, DataSource에서 Connection을 얻어와 SQL을 실행한다.
- ThreadSafe하지 않기 떄문에, MultiThread환경에서 사용시, 동시성 이슈가 발생하지 않도록 동기화 처리가 필요하다.

### 순서
1. ItemStream을 Open한다.
  - DB Connection
  - PreparedStatement
  - ResultSet
2. JdbcCursorItemReader가 Cursor를 이용해서 데이터를 읽고, RowMapper로 매핑한다.
3. 매핑된 ResultSet으로 DB에 데이터를 넣는다.

```java
public JdbcCursorItemReader<T> itemReader(){
    return new JdbcCursorItemReaderBuilder<T>()
            .name()
            // Cursor 방식으로 데이터를 가지고 올 때, 한번에 메모리를 할당할 크기를 설정 (일반적으로 ChunkSize와 동일하게 준다)
            .fetchSize()
            // DB에 접근하기 위한 DataSource
            .dataSource()
            // 쿼리 결과로 반환되는 데이터와 객체를 매핑하기 위한 RowMapper
            .rowMapper()
            // 별도의 RowMapper를 설정 하지 않고, Class타입을 설정하면 자동으로 객체와 매핑
            .beanRowMapper()
            // ItemReader가 조회할 때, 사용할 쿼리 문장 설정
            .sql()
            // 쿼리 파라미터 설정
            .queryArguments()
            // 조회 할 최대 Item 수
            .maxItemCount()
            // 조회 Item의 시작점
            .currentItemCount()
            // ResultSet 오브젝트가 포함될 수 있는 최대 행 수
            .maxRows()
            .build();
}

```
---
# PagingItemReader
- Paging단위로 데이터를 조회하는 방식, PageSize만큼 한번에 메모리를 가지고 온 다음 한개씩 읽는다.
  - limit, offset 방식
- **한 Page를 읽을 때 마다, Connection을 맺고 끊기 떄문에, 대용량 데이터 처리 시에도 SocketTimeOut이 발생하지 않는다.**
- 페이징 단위의 데이터만 메모리에 할당하기 떄문에, 메모리 사용량이 적어진다.
  - Connection 연결시간이 길지 않고, 메모리 공간을 효율적으로 사용해야 하는 데이터에 적합하다.

## JdbcPagingItemReader
- Paging기반의 Jdbc 구현체, offset과 Limit을 지정하여 SQL문을 실행한다.
- SpringBatch에서 offset과 limit을 pageSize에 맞게 자동으로 생성해주며, 데이터를 조회할 때 마다 새로운 쿼리를 실행한다.
- 결과의 순서를 보장하기 위해서 order by를 사용하는 것이 좋다.
  - **select, from, sortKey는 필수아며 where, group by는 필수가 아니다.**
- **ThreadSafe하다.**
### 순서
1. Step이 JdbcPagingItemReader를 실행한다.
2. JdbcPagingItemReader가 ItemStream을 생성하고, ExecutionContext에 업데이트한다.
3. JdbcPagingItemReader가 JdbcTemplate을 사용하여 쿼리를 실행하고, ResultSet을 가져온 후, RowMapper로 파싱 후 리턴한다.