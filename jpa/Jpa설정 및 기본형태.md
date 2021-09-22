# JPA 기본

## JPA설정
**resources/META-INF/persistence.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             version="2.1">
    <persistence-unit name="simple-jpa-application">
        <properties>
            <!-- 필수 속성 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/> <!--DB접속 Driver-->
            <property name="javax.persistence.jdbc.user" value="sa"/> <!--DB userName -->
            <property name="javax.persistence.jdbc.password" value=""/> <!--DB password-->
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/><!--DB URL-->
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/> <!--DB에 맞는 방언-->>
        </properties>
    </persistence-unit>
</persistence>
```
1. javax는 java 표준 설정이다. (구현체가 바뀌어도 그대로 적용된다.)
2. dialect는 JPA의 문법에 따라 작성된 코드를 DB의 고유문법에 맞게 변환시켜주는 것이다.
    <br>ex) MySQL Limit && Oracle RowNum
    
3. 그 외에 show-sql, format-sql 등의 설정을 통해서 query가 날라가는것도 볼 수 있다.
4. 의존성은 JPA구현체를 import 하면된다.
5. 복잡한 쿼리는 JPQL을 통해서 해결 할 수 있다.
    ex) **em.createQuery([JPQL]).getResultSet()**

## JPA 문법
```java
class Main{
    public static void main(String[] args){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            /**
             * Entity 선언 및 변경 ...
             */
            tx.commit();   
        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close(); //EntityManager는 내부적으로  DB Connection을 갖고있으므로 꼭 닫아줘야한다.
        }
        emf.close(); //Was 를 내릴 때 Factory도 닫기
    }   
}
```


1. EntityManager를 통해서 쿼리를 날린다.
2. EntityManagerFactory를 통해서 필요할 때 EntityManager를 받는다.
    1. **EntityManagerFactory는 Application 전체 공유**
    2. **EntityManager는 쓰레드 간 공유x, 사용 후 버려야한다.**
3. 모든 변경사항은 트랜잭션 안에서 이루어져야한다. (EntityManager를 통해서 받기가능)

## JPQL
1. 테이블 중심이아닌, 객체를 중심으로하는 객체지향형 쿼리
2. SQL과 유사한 문법을 가지고있으며, SQL과 유사하며, 추상화 했기 때문에 특정 DB에 종속적이지 않다.