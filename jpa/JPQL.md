# JPQL

## JPA의 쿼리 작성 방법

1. **JPQL**
    1. SQL을 추상화한 객체지향 쿼리언어 제공
    2. SQL문법과 유사하며, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 제공
    3. 엔티티 객체를 대상으로 쿼리
2. JPA Criteria
    1. 자바코드로 SQL 작성
    2. JPQL 빌더 사용 가능
    3. JPA 공식 표준기능
    4. 복잡함
3. **QueryDSL**
    1. 오픈소스 라이브러리
    2. SQL과 유사, 자바코드로 작성
    3. 설정이 어려움
    4. 동적쿼리 작성 용이
4. Native SQL
    1. 직접 SQL 쿼리
    2. 특정 DB에 의존적인 기능을 사용하고 싶을 때

## JPQL 특성

1. 엔티티와 속성은 대소문자 구분한다.
2. 키워드는 대소문자를 구분하지 않는다. (SELECT,FROM ...)
3. **엔티티 이름을 사용 테이블(X)**
4. **별칭 필수**

#### 파라미터 바인딩

1. 위치기반: 사용하지말 것

```jpaql
    SELECT m FROM Member m WHERE m.sername =?1
    query.setParameter(1,usernameParam);
```

2. 이름 기반

```jpaql
    SELECT m FROM Member m WHERE m.sername =:username
    query.setParameter("username",usernameParam);
```

#### 프로젝션

**SELECT 절에서 뽑아올 Column을 명시하는 것**

1. 프로젝션 대상은 엔티티, 임베디드타입, 스칼라 타입 등 모두 가능하다.
2. 엔티티 타입의 프로젝션은 영속성의 관리를 받는다.
3. DTO로 값을 받거나, Object타입으로 가져와서 타입캐스팅

### Paging

```jpaql
String jpql = "SELECT m FROM Member M OREDER BY m.name DESC";
List<Member> resultList = em.createQuery(jpql,Member.class);
                .setFirstResult(1) //시작페이지
                .setMaxRestuls(10) //페이징 갯수
                .getResultList();

```

### Join

1. 내부 조인(InnerJoin) //(inner)JOIN
2. 외부 조인(OuterJoin) //LEFT(Outer)JOIN
3. 세타 조인(Theta Join) 
```jpaql
     SELECT m Member m, Team t WHERE m.username=t.name; 
```
**결과가 없다면 crossJoin으로 쿼리가 나간다.**

4. ON 절을 이용한 쿼리 (연관관계없는 것도 지정해주면 OuterJoin)



