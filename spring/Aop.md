# Aop(Aspect Oriented Programming

- **핵심적인 기능으로는 볼 수 없지만, 핵심적인 기능에 추가되어 동작을 하는 로직**
- **부가적인 기능들을 모듈화 하여 재사용 하는 것이 목적**
- **여러 부분에서 공통적으로 쓰이지만 핵심로직이 아닌 이것들을 흩어진 관심사(CrossCutting Concern)라고한다.**
- **OOP의 대안이 아니라 OOP를 보완해주는 개념이다.**
- **비즈니스 메소드를 더욱 세밀하게 조정하고 싶을 때 사용한다.**
- **메소드 단위로 실행된다**
***

## Aop 구현체

- SpringAop
    - 프록시 기반 Aop
        - 원본 코드, 바이트코드를 조작하지 않기 때문에, Method에만 가능하다.
    - AspectJ를 차용하고, 부분적으로 제공한다.
        - @Aspect 에노테이션을 자동으로 읽어, PointCut과 Advice를 자동으로 Advisor로 등록한다.
    - SpringBean에만 적용가능
        - DynamicProxy (런타임 시점에 프록시를 만드는 기법)를 사용해서 구현
            - Java가 제공하는 기본 방법은 인터페이스 기반 프록시 생성기법
            - CGlib은 클래스 기반도 지원
- AspectJ
    - 실제코드에 조작이 일어나기 때문에, Method 뿐만 아니라, 생성자, 필드 등에 AOP 적용이 가능하다.
    - 컴파일 시점 AOP
        - 부가적인 컴파일러 설정이 필요하다.
        - Java파일을 Class파일로 변경 하는 시점에 바이트코드 조작을 통해서 Weaving
    - 런타임 시점 AOP
        - Class파일에는 나타나지 않으나, JVM메모리 상에 Weaving
        - ClassLoader가 Class 정보를 Method 영역에 올릴 때 Weaving
        - ClassLoader에 대한 설정이 필요하다.

### Spring Aop vs AspectJ

![스크린샷, 2021-12-14 12-19-52](https://user-images.githubusercontent.com/57896918/145927167-5b350b9e-5aa3-4d2a-b08c-4a6761ff6aca.png)

# Spring Aop

- AspectJ의 문법을 차용한다.
- AspectJ의 문법만 사용할 뿐 사용하는 것이 아니다.
- Proxy 기반으로 동작한다.
- SpringBean에만 동작한다.

### 의존성

```groovy
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

## Aop 주요 용어

- **Aspect** : CrossCutting Concern을 모듈 화 한 것 ((Advice + Target)이 여러개 존재 할 수 있다.)
- **Target** : Aspect를 실제로 적용 하는 곳(Method, Class ...)
- **Advice** : 실질적인 부가기능을 담은 구현체
- **JoinPoint** : Advice가 적용될 위치 --> 끼어드는 지점 (AOP가 적용될 수 있는 모든 지점)
- **PointCut** : JoinPoint를 상세화 한것 (표현식으로 주로 지정)
- **Weaving** : 중심로직에 영향을 끼치지 않으면서 PointCut에 Advice를 삽입하는 것을 말함함

***

## @Aspect

- @Aspect Class안에 여러개의 Advice + PointCut 로직이 존재 할 수 있다.
- BeanFactoryAspectJAdvisorsBuilder 에서 PointCut,Advice,Advisor를 생성하고 보관한다.
    - 이후에는 BeanPostProcessor에서 AdvisorBean과 @AspectAdvisor를 조회하고 로직에 따라서 ProxyBean이 생성되고 등록된다.
- @Aspect 내부의 메소드는 하나의 Advisor로 동작하게된다.

```java

@Aspect
public class LogAspect {
    /**
     * PointCut역할
     */
    @Around("execution(* sample.app..*(..))")
    /**
     * Advice 역할
     */
    public Object execute(ProceedingJoinPoint pjp) {
        //BeforeAdvice
        Object result = pjp.proceed();
        //AfterAdvice
        return result;
    }
}
```

## Adivce 의 순서

### @Aspect 클래스 내부

1. @Around
2. @Before
3. @After
4. @AfterReturning
5. @AfterThrowing

**같은 종류의 Advice일 경우, 같은 @Aspect 내부에서는 실행 순서를 보장할 수 없다.**

### @Aspect 클래스 외부

- @Order를 통해서 순서를 지정한다.
    - 같은 @Aspect 내부 Class끼리는 순서를 지정해 줄 수 없다.
    - 순서를 지정해주어야하는 Aspect라면 Class로 분리 시켜주어야 한다.
    - Spring이 제공해주는 @Order를 통해서 순서를 지정해 줄 수 있다.

```java

@Aspect
@Order(1)
public class LogAspect {
    @Around("execution(* sample.app.*Service..*(..))")
}
```

## Advice의 종류

- @Around : JoinPoint의 앞과 뒤에서 실행되는 Advice
    - ProceedingJoinPoint를 매개변수로 가진다.
    - 메소드 실행 전 후에 관여할 수 있는 막강한 메소드 이다.
    - proceed()를 통해서 TargetMethod를 실행한다.
    - proceed()를 여러번 실행 할 수 있다.
- @Before: JoinPoint의 앞에서 실행되는 Advice
    - JoinPoint를 매개변수로 가진다.
    - 리턴해주지 않아도, 실행흐름이 끝나면 TargetMethod가 실행된다.
- @After: JoinPoint의 결과 (성공,에러)에 상관없이 실행되는 Advice
    - finally와 같은 흐름이다.
    - 일반적으로 리소스를 해제하는데 사용한다.
- @AfterReturning: JoinPoint의 실행결과가 성공적일 때 실행되는 Advice
- @AfterThrowing: JoinPoint의 실행결과가 에러를 던질 때 실행되는 Advice

**여러가지로 분리하는 이유는, @Around가 막강한 기능이긴 하지만, 실수 할 가능성이 있으므로 제약사항을 두기 위해서 이다.**
***

## PointCut

### 명시 가능 PointCut

- execution: Advice를 적용할 메소드를 지정할 때 사용
    - **execution(\[수식어] 리턴타입 \[FQCN].메소드이름(파라미터) \[예외]))**
        - 수식어: public, private 등 (Optional)
        - 리턴타입: 메소드의 리턴타입
        - 클래스이름: FQCN (Optional)
        - 이름: 메소드의 이름을 지정
        - 파라미터: 메소드의 파라미터 지정
        - 예외(Optional)
        - \"*" : 모든 값을 의미
        - \".": 정확하게 하나를 의미 (파라미터는 1개, 패키지는 정확하게 해당위치)
        - \"..": 0개이상을 의미 (파라미터는 0~N, 패키지는 해당 패키지 + 하위패키지)
      ```
      execution(public Integer com.example.study.*.*(*))
       - com.exmample.study 패키지에 속해있고, 파라미터가 1개인 모든 메서드
       - 
      execution(* com.example..*.find*(..))
      - com.example 패키지 및 하위 패키지에 속해있고, 이름이 find로 시작하는 파라미터가 0개 이상인 모든 메서드 
      - 
      execution(* com.example.study..*Controller.*(..))
      - com.example.study 패키지 및 하위 패키지에 속해있고, 이름이 Controller르 끝나는 클래스의 파라미터가 0개 이상인 모든 메서드
      ```
- within: 특정타입 (패키지 하위 클래스 및 클래스)에 속하는 메소드를 JoinPoint로 지정 할 때 사용
  - 특정 클래스에 정확하게 일치하는 것이다.
  - 특정 클래스에 속하는 모든 메소드 모두가 PointCut이다.
```java
@Pointcut("within(com.example.app.*)") //com.exmaple.app 패캐지의 모든 메소드가 PointCut
@PointCut("within(com.example.app..*") //com.exmaple.app 패캐지 및 하위패키지의 모든 메소드가 PointCut
@PointCut("within(com.example.app.SomeService") //com.exmaple.app.SomeService의 모든 메소드가 PointCut
```
- target: 특정 타입과 부모 타입에 속하는 모든 메소드를 JoinPoint로 지정한다.
```java

```
- bean: Spring Bean을 이용하여 JoinPoint 지정

#### @Pointcut 분리하기

- PointCut 공통부분에 대한 모듈화가 가능하다.
- 외부의 Class로 뺏을 때는 FQCN을 통해 참조가 가능하다.

```java

@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("execution(* sample.app.*Service..*(..))")
    public void allService() {
    }

    @Around("allservice()")
    public Object logging(ProceedingJoinPoint pjp) {
        log.info("BeforeLogging");
        Object result = pjp.proceed();
        log.info("AfterLogging");

        return result;
    }
}

```

## 주의해야 할점
- PointCut은 최대한 범위를 줄여야한다.
- Spring AOP는 런타임에 프록시를 통해서 구성된다.
  - 우선 프록시를 생성해야 AOP 적용여부를 판단 할 수 있다는 의미이다.
- final이 붙어있다면 프록시 생성이 불가능한 에러가 발생한다.
  - 실제 적용되는 범위만으로 제한하여, 이러한 문제를 예방해야 한다.