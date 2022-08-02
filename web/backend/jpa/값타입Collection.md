# 값 타입 Collection

**RDB는 기본적으로 Attribute가 Atoimc 해야한다.**<br>
**객체지향에서는 객체에서 Collection의 사용이 가능하다. --> 별도의 Table생성**<br>

 ```java
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @ColumnTable(name = "grounp", joinColumns =
    @JoinTable(name = "user_id"))
    Set<Group> groups;
}
```

**Collection을 따로 영속화 시켜주지않아도, 갖고있는 테이블을 영속화 시켜주면<br>
같이 Insert된다. (값 타입을 갖는 객체가 관계의 주인)**
```java
@ElementCollection을 사용해서 Column Mapping을 할 때,
객체의 경우 @ColumnTable이 필요하지만,
PrimitiveType(ex : String) 은 @Column을 통해서 매핑 가능하다.
```

1. 기본값은 Collection은 지연로딩, PrimitiveType은 즉시로딩이다.
2. Immutable이 원칙이기 때문에 일부를 변경하는게아닌, 새로운 Instance로 바꿔주어야한다.
3. 삭제는 equals()와 hashcode()가 구현되어있어야한다.(값비교로 동일성을 보장해야 하기때문)
4. 값 타입은 식별자가 없기떄문에 추적이 어렵다.<br>
   (Collection 값 변경시 주인 Entity와 연관된 모든 테이블 삭제 후 다시 Insert)
5. Null입력, 중복저장이 불가능하다.

***
1. #### 컬렉션 타입 사용보다, 일대다 테이블로 풀어서 사용하는게 더 편리하다 
2. #### 아주 단순한 Primitive Type같은 경우에만 사용하자
3. #### JPA 사용 + equals() && hashcode() 구현 시 getter 사용하자.(프록시 때문)