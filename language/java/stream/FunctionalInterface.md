# Functional Interface

- java.util.function 하위 패키지에 있다.
- Java에서 함수를 1급객체로 만들어주는 것이다.

## 구현방법

### 1. Functional Interface를 구현하는 객체

```java
class Example {

  public static void main(String[] args) {
    Function<Integer, Integer> addFunc = new AddFunction();
    int result = addFunc.apply(10); //20
  }
}


public class AddFunction implements Function<Integer, Integer> {

  public Integer apply(Integer x) {
    return x + x;
  }
}

@FunctionalInterface
public interface Function<T, R> {

  R apply(T t);
}
```

### 2. Lambda Expression

- 이름이 없는 함수이다.
- 매개변수의 타입이 추론 가능할 경우, 타입 생략이 가능하다.
- 매개변수가 하나일 경우, 괄호 생략이 가능하다.
- 바로 리턴하는 경우 중괄호 생략이 가능하다.

```java
class Example {

  Function<Integer, Integer> addFunc1 = (Integeer x) -> {
    return x + x;
  };

  Function<Integer, Integer> addFunc2 = x -> x + x;

  boolean isSame = addFunc1.apply(10) == addFunc2.apply(10);
}
```

## @FunctionalInterface

- 단 하나의 Abstract Method를 가지는 인터페이스 (Single Abstract Method Interface)
- DefaultMethod와 Static Method는 이미 구현되어있으므로 괜찮다.
- Runnable, Comparator, Callable ...

### Custom Functional Interface 만들기

```java

@FunctionalInterface
class Example {

  public static void main(String[] args) {
    TriFunction<Integer, Integer, Integer, Integer> addFunc = (x, y, z) -> {
      return x + y + z;
    };
  }
}

public interface TriFunction<T, U, V, R> {

  R apply(T t, U u, V v);
}
```

## Functional Interface 종류

### 1) Function <T,R>

- Input <T>를 받아서 <R>을 리턴하는 역할을 한다.
- **apply**라는 Abstract Method를 가진다.

```java
class Example {

  public static void main(String[] args) {
    Function<String, String> prefixParser = (x) -> "prefix_" + x;
    System.out.println(prefixParser.apply("apple"));
  }
}
```

### 2) Consumer<T>

- Input <T>를 받기만한다.
- 아무것도 Return 하지 않는다. (말 그대로 소비만 한다.)
- **accept()**라는 Abstract Method를 가진다.

```java
class Example {

  public static void main(String[] args) {
    Consumer<Integer> printer = x -> System.out.println(x);
    printer.accept(10);
  }
}
```

### 3) Supplier<T>

- 아무런 Input도 받지 않는다.
- <T>를 Return 한다. (말 그대로 공급만 한다.)
- **get()** 이라는 Abstract Method를 가진다.

```java
class Example {

  public static void main(String[] args) {
    Supplier<Integer> randomNumGenerator = () -> (int) (Math.random() * 100);
    System.out.println(randomNumGenerator.get());
  }
}
```

### 4) Predicate <T>

- Input <T>를 받고, boolean을 리턴한다.
- **test()**라는 Abstract Method를 가진다.
- **and()** **or()** **negate()** 이라는 Default Method를 가지고 있다.

```java
class Example {

  public static void main(String[] args) {
    Predicate<Integer> isPositive = x -> x > 0;
    Predicate<Integer> isZero = x -> x == 0;
    
    boolean isNonNegative = isPositive.or(isZero).test(10); // 둘 중하나만이라도 참일 때
    boolean alwaysFalse = isPositive.and(isZero)/test(10); // 두가지 조건이 모두 참일 때
    isNegative = isPositive.negate().test(-10); //true  --> 기본 Predicate에 not(!)
  }
}
```
