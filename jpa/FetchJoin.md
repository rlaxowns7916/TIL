# N + 1 문제
- JPA 일반적인 JOIN은 프록시이기 떄문에, 그때 그때 쿼리가 날라감 
- JPA는 객체를 보고 JPQL을 생성
- JPQL은 글로벌 페치전략을 신경쓰지 않음 (Eager, Lazy 상관없이 둘다 발생)

# Fetch Join
- **JPQL에서 성능 최적화를 위해서 제공하는 기능**
- 연관된 것을 한번에 모두 가져옴
- 객체그래프를 SQL 한번에 조회 하는 것(즉시 로딩)
- 최적화가 필요한 곳에 Fetch Join 적용 
```jpaql
#JPQL
SELECT m FROM Member m join fetch m.team
```

**JOIN되는 ROW의 갯수와 똑같이 나온다 (SQL과 유사)**

## DISTNICT
**SQL DISTINCT에 추가적인 기능**

1. SQL DISTINCT (결과가 같아야 중복제거)
2. 어플리케이션 엔티티(같은 식별자)의 중복 제거

## 한계
1. Fetch Join 대상에는 Alias를 줄 수 없다.
2. 둘 이상의 Collection을 Fetch Join 할 수 없다.
3. Collection FetchJoin시, 페이징을 사용 할 수 없다.
    - Join대상에 맞게 Row수가 늘어나는데, 페이징이 중간에 짤릴 수 있다.
    - 객체그래프는 연관된 모든것이 로딩 되어있어야 하기 때문
    - Hibernate는 메모리에 모두 올리고 그 이후 페이징 (매우 위험)

## FetchJoin 페이징
1.  쿼리의 방향을 OneToMany 방향이 아니라, ManyToOne 방향으로 바꾸자
    - Row의 갯수가 늘어나지 않음
2. @BatchSize
    - GlobalSetting 으로 가능 (추천)
        - hibernate.default_batch_fetch_size 
    - in()으로 BatchSize에 명시된 수 만큼 검색 (N+1이 아닌 Row수로 맞출 수 있음 ) 
    
3. 리턴값을 DTO로 스위칭해서 가져온다.
