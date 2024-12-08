# Throwable

- Error와 Exception의 공통 조상 클래스
- 예외처리를 할 때 Throwable까지 처리를 해서는 안된다.

# Error

- java.lang.error 패키지
- 시스템에 비정상적인 상황이 발생 한 것
    - 시스템의 비정상 종료를 유발할 만큼 심각한 오류이다.
    - 복구 불가능
- 컴파일 시점에 에러발생 여부를 알 수 없다.
- 런타임 시점에 에러가 발생한다.
- ex) OutOfMemory, StackOverFlow

# Exception

- java.lang.exception 패키지
- 정상적인 프로램 실행에 어긋나는 것을 의미
- CheckedException은 컴파일 시점에 확인 가능, UncheckedException은 런타임에 확인가능
- try-catch를 통해 예방 가능
    - catch한 예외의 하위 Type까지 함께 처리된다.
- 자신이 처리를 하지 못하는 경우, throws를 통해서 자신을 호출한 측에 에러 처리를 미룬다.

## CheckedException

- Compile시점에 확인 가능
- 에러 처리를 하지 않으면 Compile불가
  - override 메소드는 부모의 CheckedException의 하위 Exception들 을 던져야하며, 더 큰 예외(상위 예외) 는 던질 수 없다. (일관성 보호 측면)
  - 원래 Method (부모의 Method)가 CheckedException을 던지지 않는다면, Override한 Method도 던질 수 없다.
- IOException,ClassNotFoundException ...
    - RuntimeException을 제외한 Exception의 하위 클래스들이다.
- Exception 클래스는 CheckedException의 범주에 속한다.
    - Exception을 상속받으면 CheckedException이다.
- 반드시 처리를 해주어야하는 Critical 예외를 CustomCheckedException을 사용하는 것이 좋다.
    - 비즈니스로직에서 처리가 불가능한 경우에는 CheckedException을 사용하면 안된다.
        - 로깅과 모니터링을 통해서 빨리 복구하는 것이 더 좋다.
    - 특정 기술에 의존적인 CheckedException은 사용해서는 안된다.
- throws를 해주든, try-catch를 하든 에러처리를 해주어야 한다.

```text
공식적으로는 예측 가능하지만 예방불가능하며 빠른 복구가 가능할 때 사용하라고 명시되어있다.
현실적으로는 그러한 경우가 흔하지 않으며, 
특정 기술에 의존적인 CheckedException을 UnCheckedException으로 변환시켜서 던지는 것이 현실적일 것이다.
```

## UnCheckedException

- RuntimeException을 상속
- 컴파일러가 예외를 체크하지 않는다.
    - 예외처리를 하지 않아도됨
    - throws를 생략해도 자동으로 던져진다.
- NullPointerException,ClassCastException...
- 특정 기술의 CheckedException을 예외처리하고 RuntimeException으로 다시 던짐으로써, 의존관계를 최소화 할 수 있다.

**복구 불가능한 예외는 UnCheckedException으로 던지는 것이 좋다.**

### Custom UnCheckedException

```java
class CustomException extends RuntimeException {

  public CustomException(Throwable throwable) {
    super(throwable);
  }
}
```

- 예외를 전환 할 때는 기존 예외를 포함해야 한다.
- 기존 예외를 포함해야 StackTrace 통한 정확한 에러 확인이 가능하다.
    - 기존 예외를 넘기지 않으면, 메세지 포함 Exception이 터진 위치를 확인 할 수 없다.

## 예외 처리를 하지 못했을 때

1. Java Main Thread

- 처리를 하지못한다면, 에러로그를 출력하면서 프로그램이 종료된다.

2. Web Application

- 하나의 예외발생으로, 다수의 사용자가 사용하는 서버가 종료되어서는 안되기 때문에, WAS가 해당예외를 받아서 처리한다.
- ErrorPage를 보여주는것이 일반적이다.

# 계층도

<img width="803" alt="스크린샷 2022-05-08 오후 4 40 47" src="https://user-images.githubusercontent.com/57896918/167286700-6594a62d-fbdb-4580-8c81-38c41534d58f.png">
