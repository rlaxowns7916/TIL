# N + 1 문제
- 조회된 부모의 숫자 만큼 쿼리가 더 날라가는 것 (부모 검색\<1> + 부모와 연관된 자식 \<N>)
- JPA 일반적인 JOIN은 프록시이기 떄문에, 그때 그때 쿼리가 날라감 
- SELECT만 영속화 되기때문에 JPQL일반 Join으로는 N+1문제가 해결되지 않는다. (쿼리는 Join을 사용하지만, 영속화(X))
- JPA는 메소드 이름을보고 JPQL을 생성 (JpaRepository상속 후 메소드 사용시)
  - em.find()를 사용하는 경우 JPA가 내부적으로 JOIN을 해주기 떄문에 단건 조회시 N+1은 발생하지 않는다.
    - Repository의 findById(단건조회)는 em.find()를 사용한다.
  - 식별자를 통한 검색이 아닌, findBy()의 경우 메소드 네이밍으로 JPQL을 생성하기 떄문에 N+1이 발생한다.
- JPQL은 글로벌 페치전략을 신경쓰지 않고 SQL생성 (Eager, Lazy 상관없이 둘다 발생)
- 메모리와의 Trade-off 이다.

# Fetch Join
- **JPQL에서 성능 최적화를 위해서 제공하는 기능**
- 연관된 것을 한번에 모두 가져옴
- 연관 엔티티 까지 영속화
- 객체그래프를 SQL 한번에 조회 하는 것(즉시 로딩)
- 최적화가 필요한 곳에 Fetch Join 적용 
```jpaql
#JPQL
SELECT m FROM Member m join fetch m.team
```

- **JOIN되는 ROW의 갯수와 똑같이 나온다 (SQL과 유사)**
  - 결국 내부적으로 SQL Inner Join을 사용하기 떄문이다.
  - distinct를 통해서 중복을 제거해야한다.
## DISTNICT
**SQL DISTINCT에 추가적인 기능**

1. SQL DISTINCT (결과가 같아야 중복제거)
2. 어플리케이션 엔티티(같은 식별자)의 중복 제거

## 한계
1. Fetch Join 대상에는 Alias를 줄 수 없다.
2. 둘 이상의 Collection을 Fetch Join 할 수 없다.
    - MultipleBagFetchException 발생
    - ToOne 관계에는 몇개든 사용 가능
    - ToMany 관계에는 하나만 사용 가능
    - **QueryDSL 도 똑같다**
3. Collection FetchJoin시, 페이징을 사용 할 수 없다.
    - Join대상에 맞게 Row수가 늘어나는데, 페이징이 중간에 짤릴 수 있다.
    - 객체그래프는 연관된 모든것이 로딩 되어있어야 하기 때문
    - Hibernate는 메모리에 모두 올리고 그 이후 페이징 (매우 위험)

## FetchJoin 페이징
1.  쿼리의 방향을 OneToMany 방향이 아니라, ManyToOne 방향으로 바꾸자
    - Row의 갯수가 늘어나지 않음
2. @BatchSize
    - GlobalSetting 으로 가능 (추천)
        - **hibernate.default_batch_fetch_size**
    - in()으로 BatchSize에 명시된 수 만큼 검색 (N+1이 아닌 Row수로 맞출 수 있음 ) 
    
3. 리턴값을 DTO로 스위칭해서 가져온다.

## 결론
- **1:N은 최대한 LazyLoading으로 처리하고 가장 많이 성능이 저하되는 곳(데이터가 많은 곳)에 FetchJoin**
- **GlobalBatchSize설정으로 FetchJoin을 적용하지 못한 1:N관계에 대한 성능 개선을 한다.
