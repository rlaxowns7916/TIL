# Mutable (가변객체)

- 값이 변경 가능한 것
- 멀티 쓰레드 환경에서 값을 공유 하려면 별도의 처리를 해주어야한다.
- append(),add()등으로 이어붙임

- List
- HashMap
- StringBuilder
- StringBuffer  
  . <br>
  . <br>
  .

# Immutable (불변 객체)

- 값이 변경 불가능 한 것
- 값이 변경 되는 것 아닌가? --> 새롭게 객체를 할당 받고 래퍼런스를 변경
- 객체에 대한 신뢰도의 상승
- 동기화 처리없이 객체 공유 가능 (Thread-safe)


- String
- Boolean
- Integer
- Float
- Long

## Immutable Collection

- Java9에서 생성에 편리한 팩토리 메소드들이 추가되었다.
- Immutable이기 때문에, 값을 추가하거나 삭제하려고 하면 **java.lang.UnsupportedOperationException**이 발생한다.

### Java9 이전

```java
class BeforeJava9 {

  public static void main(String[] args) {
    /**
     * 가장 기본적인 방법
     */
    List<String> people1 = new ArrayList<>();

    people1.add("Kim");
    people1.add("Lee");
    people1.add("Park");
    people1 = Collections.unmodifiableList(people1);

    /**
     * Immutable Collection으로 변경된 이후이기 떄문에
     * UnSupportedOperationException이 발생한다.
     */
    people1.add(Jeong);

    /**
     * Stream을 사용한 방법
     */
    List<String> people2 = Stream
        .of("Kim", "Park", "Lee")
        .collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }
}
```

### Java9

```java
  class Java9 {

  public static void main(String[] args) {
    List<Integer> numList = List.of(1, 2, 3, 4);
    Set<Integer> numSet = Set.of(1, 2, 3);
    Map<Integer, String> numMap = Map.of(1, "One", 2, "Two", 3, "Three");
  }
}
```
- of 팩토리메소드로 만들 떄, 모두 Immutable한 Collection이다.