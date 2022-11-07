# Aop Weaving
- Aspect와 Aspect 대상코드를 연결하는 과정
- 3가지의 전략을 가진다.

## AspectJ를 언급하는 이유

- Spring은 RuntimeWeaving (Proxy)를 사용하지만, 표현식은 AspectJ 형식을 사용한다.
- 즉, 코드의 변경없이 쉽게 Weaving전략을 바꿀 수 있다는 장점이 있다.
- CompileTimeWeaving(CTW)과 LoadTimeWeaving(LTW)을 제공한다.
- Spring Framework에 의존적인 것이 아니며, 독자적인 Framework 이다.

## 1. CompileTime Weaving

- 3가지 전략 중 가장 성능이 좋다.
- AspectJ를 필요로 한다. (추가적인 종속성)
- JavaCompiler를 확장한 AspectJ Complier (AJC)를 사용하여 바이트코드를 생성한다.
- 컴파일 시점에 바이트 코트 조작을 통해서 Advisor 코드를 직접 삽입하는 방식이다. (정적인 방식)
- 다른 바이트코드 조작 라이브러리와 충돌을 일을킬 가능성이 있다. (ex: Lombok)

## 2. LoadTime Weaving
- AspectJ를 필요로 한다. (추가적인 종속성)
- ClassLoader가 JVM에 Class를 Loading 할 때, 바이트 코드 조작을 하는 방식이다,
- Compile시간은 CTW에 비해서 이점이 있지만, 메모리에 Load 할 때 시간이 걸리기 때문에 Runtime시간은 CTW에비해 좋지않다.
- 설정이 복잡하다.

## 3. Post-Compile Weaving (==Binary Weaving)
- AspectJ를 필요로 한다. (추가적인 종속성)
- 이미 존재하는 class파일, jar파일을 Weaving 할 때 사용한다.


## 4. RunTime Weaving
- Spring-AOP의 방식이다.
- Proxy방식을 사용한다.
  - 순수한 Java코드로 동작한다.
    - ```text
          One of the central tenets of the Spring Framework is that of non-invasiveness.
          (Spring은 개발자에게 선택지는 제공하지만, 특정 프레임워크의 클래스나 인터페이스가 도메인 코드의 침입을 방지하는 것을 주요 개념으로 한다)
      ```
- Proxy에서 다시 원본객체를 호출하기 때문에, 런타임시 성능이 좋지않다.
- 동일 Class 호출 문제가 존재한다.
- 느리면 위의 2가지방식을 고려해볼 수 있다.
