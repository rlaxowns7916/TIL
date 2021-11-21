# Paging
- SpringDataJpa가 제공하는 Pageable객체를 통해서도 사용가능

## 1. 기본
```java
    QueryResult<Member> queryResult = 
        queryFactory
            .select(member)
            .from(member)
            .offset(pageable.getOffset()) //어디서부터 시작한다.
            .limit(pageable.getPageSize()) //몇개 가져온다.
            .fetchResult();

        List<Member> result = queryResult.getResults();
        long total = queryResult.getTotal();
        
        return new PageImpl<>(content,pageable,total); //Page객체 리턴 가능
```
- fetchResults()는 Content쿼리와 더불어 Count쿼리도 같이 날라간다.

## 2. 최적화
```java
List<Member> content = 
        queryFactory
            .select(member)
            .from(member)
            .offset(pageable.getOffset()) //어디서부터 시작한다.
            .limit(pageable.getPageSize()) //몇개 가져온다.
            .fetch();
        
long total =  queryFactory
        .select(member)
        .from(member)
        .fetchCount();       
        
        return new PageImpl<>(content,pageable,total); //Page객체 리턴 가능
```
- fetchResults()의 Count쿼리는 조건을 모두 가져간다.
- 간단하게 Count쿼리를 얻어올 수 있는경우에는 손해이기 때문에 최적화하는게 유리
- 데이터가많을 경우, 성능 상승효과를 얻을 수 있다.

## 3. Count 쿼리를 생략 가능한 경우 
- 페이지 시작이면서, 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
- 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체사이즈를 구함)

```java
List<Member> content =
        queryFactory
        .select(member)
        .from(member)
        .offset(pageable.getOffset()) //어디서부터 시작한다.
        .limit(pageable.getPageSize()) //몇개 가져온다.
        .fetch();

JPAQuery<Member> countQuey = queryFactory
                .select(member)
                .from(member)
                .fetchCount();

return PageableExecutionUtils.getPage(content,pageable,() -> countQuery.fetchCount());
//위 2개의 조건이 만족하면 3번째 인자인 함수를 실행하지 않는다.
```