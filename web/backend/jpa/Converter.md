# Converter
- JPA의 표준스펙이다.
- Entity의 데이터를 변환하여 DB에 저장 할 수 있다.
- @Converter 에노테이션을 통해서 쉽게 사용 가능하다.
- PersistenceContext에 의해서 관리되며, 조회(SELECT, 저장(Save) 시점에 수행된다.

```java
@Getter
@Entity
@AllArgsConstructor
public class Member{
  
  @Id
  @GeneratedValue
  private Long id;
  
  @Column
  private String name;
  
  @Column
  @Convert(YNToBooleanConverter.class)
  private boolen isActive;
  
  @Column
  @Convert(NumberStringsToListConverter.class)
  private List<Integer> numbers;
  
}


/**
 * AttributeConverter<EntityProperty,DBColumn>
 */
@Converter
public class YNToBooleanConverter implements AttributeConverter<Boolean, String> {
  
  public String convertToDatabaseColumn(Boolean attribute) {
    return (attribute != null && attribute) ? "Y" : "N";
  }

  public Boolean convertToEntityAttribute(String s) {
    return "Y".equals(s);
  }
}

@Converter
public class NumberStringsToListConverter implements AttributeConverter<List<Integer>, String> {

  private static final String SPLIT_CHAR = ",";

  @Override
  public String convertToDatabaseColumn(List<Integer> list) {
    return list.stream().map(Object::toString).collect(Collectors.joining(SPLIT_CHAR));
  }

  @Override
  public List<Integer> convertToEntityAttribute(String joined) {
    return Arrays.stream(joined.split(SPLIT_CHAR))
        .map(Integer::valueOf)
        .collect(Collectors.toList());
  }
}
```