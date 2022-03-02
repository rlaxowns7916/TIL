# Private 생성자로 싱글톤임을 보증하자

## 싱글톤(Singleton)

인스턴스를 오직 하나만 생성. 주로 무상태(Stateless)인 객체에 적용

## 싱글톤을 생성하는 방법

1. Public static final 멤버 + private 생성자

- 멤버를 static으로 선언하여 직접 접근이 가능하게한다.
- 리플렉션을 통한 생성자 접근을 제한하기 위해서, private 생성자가 다시 호출될 때, 예외를 던지게 한다.
- 간결하며, Singleton이라는게 쉽게 드러난다.

```java
public class Elvis {
    public static final Elvis Instance = new Elvis();

    private Elvis() {
    }
}
```

2. private static 멤버 + static Getter + private 생성자 (정적 팩토리 방식)

```java
public class Elvis {
    private static final Elvis Instance = new Elvis();

    private Elvis() {
    }

    public static Elvis getInstance() {
        return Instance;
    }
}
  ```
- private static final 멤버 + private 생성자
- 객체의 반환은 Factory Method를 통해서 제공한다.
- Reflection을 통해서 접근하려고 할 때는 예외를 리턴한다.
- 쉽게 변경가능하다. (Singletion -> Singleton(x))
- 정적메소드를 Supplier로 사용 가능하다. --> (Elivis::getInstance);

