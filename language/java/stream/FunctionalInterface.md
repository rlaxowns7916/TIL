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
