# JOIN

## 1. InnerJoin<br>
   - **기본적으로 첫번째 파라미터는 조인 대상, 두번 째는 QClass의 별칭 지정**
   - **식별자로 매칭**
```java
List<Member> result=queryFactory
        .selectFrom(member)
        .join(member.team,team)
        .where(team.name.eq("teamA"))
        .fetch();
```

## 2. ThetaJoin<br>
   **연관관계 없어도 일단 합치고 where절로 filtering**

```java
//ThetaJoin(Cartesian Prodcut)
List<Member> result=queryFactory
        .select(member)
        .from(member,team)
        .where(member.username.eq(team.name))
        .fetch()
```

## 3. On절
    - Join 대상 필터링
    - 연관관계 없는 Entity 외부 조인
    - InnerJoin일 경우, Where로 필터링 하는 것과 동일하다.
    - OuterJoin이나, 연관관계 없는 엔팉이 Join시에 의미가 있는 문법이다.
   

1. 연관관계가 있을 때
```java
List<Tuple> result=queryFactory
        .select(member,team)
        .from(member)
        .leftJoin(member.team,team).on(team.name.eq("teamA"))
        .fetch();
```
2. 연관관계가 없을 때
```java
List<Tuple> result = queryFactory
        .select(member,team)
        .from(member)
        .leftJoin(team).on(member.username.eq(team.name))
        .fetch();
```
**연관관계가 없을 경우, 2개의 Parameter가 아닌, 1개의파라미터 -> Qclass만 지정**

## 4. FecthJoin
- **연관된 엔티티를 한번에 가져오는 기능**
- **JPQL에서 성능최적화를 위한 기능**

```java
Member findMember = queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin() //Join절과 똑같은데 뒤에 fetchJoin()
        .where(member.username.eq("member1"))
        .fetchOne();
```