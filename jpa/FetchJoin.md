# Fetch Join
- **JPQL에서 성능 최적화를 위해서 제공하는 기능**
- JPA 일반적인 JOIN은 프록시이기 떄문에, 그때 그때 JOIN 쿼리가 날라감 **(N+1)**
- 연관된 것을 한번에 모두 가져옴
- 객체그래프를 SQL 한번에 조회 하는 것(즉시 로딩)
```jpaql
#JPQL
SELECT m FROM Member m join fetch m.team
```

**JOIN되는 ROW의 갯수와 똑같이 나온다 (SQL과 유사)**

## DISTNICT
**SQL DISTINCT에 추가적인 기능**

1. SQL DISTINCT (결과가 같아야 중복제거)
2. 어플리케이션 엔티티(같은 식별자)의 중복 제거
