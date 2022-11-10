# Method Reference

- 기존에 이미 선언되어 있는 Method를 사용하고 싶을 때 사용한다.
- :: 오퍼레이터를 사용한다.
- 생략이 많기때문에, Method의 Parameter Type과, ReturnType을 미리 숙지해야 한다.
    - Method 이름만 지정한다.
    - 파라미터나 리턴 값 같은 것은 지정하지 않는다.
    -

## Method Reference 종류

### 1. ClassName::staticMethod

- 객체의 staticMethod를 호출할 때 사용한다.
- ```java
    class Example{
        public static void main(String[] args){
          Function<String,Integer> integerParser = Integer::parseInt;
          int six = integerParser.apply("6");
        }
    }
  ```

### 2. ObjectName::instanceMethod

- 객체의 instanceMethod를 호출할 때 사용한다.
- ```java
    class Example{
      public static void main(String[] args){
        String hello = "hello";
        Predicate<String> isEqualToHello = hello::equals;
        
        boolean isEqual = isEqualToHello.test("bye");
      }
    }
  ```

### 3. ClassName::instanceMethod

- 객체의 instanceMethod를 호출할 때 사용한다.
- ```java
    class Example{
      public static void main(String[] args){
        Function<String,Integer> lengthProcessor = String::length;
        
        int length = lengthProcessor.apply("hello"); //5
      }
    }
  ```

### 4. ClassName::new

- 생성자를 호출 할 때 사용한다.
- ```java
    class Example{
        public static void main(String[] args){
           List<String> names = List.of("lee","kim","park");
           List<Person> persons = names.stream().map(Person::new).collect(Collectors.toList());
        
          Function<String,Person> personCreator = Person::new;
          Person kim = personCreator.apply("kim");
      }
    }
  
    class Person{
      String name;
      public Person(String name){
        this.name = name;
      } 
    }
  ```