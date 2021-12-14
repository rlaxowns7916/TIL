# Aop(Aspect Oriented Programming
- **핵심적인 기능으로는 볼 수 없지만, 핵심적인 기능에 추가되어 동작을 하는 로직**
- **부가적인 기능들을 모듈화 하여 재사용 하는 것이 목적**
- **여러 부분에서 공통적으로 쓰이지만 핵심로직이 아닌 이것들을 흩어진 관심사(CrossCutting Concern)라고한다.**
- **OOP의 대안이 아니라 OOP를 보완해주는 개념이다.**
***

## Aop 구현체
- SpringAop
  - 프록시 기반 Aop
    - SpringBean에만 적용가능  
      - 기존 Bean이 ProxyBean으로 교체
    - DynamicProxy (런타임 시점에 프록시를 만드는 기법)를 사용해서 구현
      - Java가 제공하는 기본 방법은 인터페이스 기반 프록시 생성기법
      - CGlib은 클래스 기반도 지원 
  - 에노테이션 기반 Aop
- AspectJ 

### Spring Aop vs AspectJ
![스크린샷, 2021-12-14 12-19-52](https://user-images.githubusercontent.com/57896918/145927167-5b350b9e-5aa3-4d2a-b08c-4a6761ff6aca.png)

## Aop 주요 용어
 - **Aspect** : CrossCutting Concern을 모듈 화 한 것 (Advice + Target)
 - **Target** : Aspect를 실제로 적용 하는 곳(Method, Class ...)
 - **Advice** : 실질적인 부가기능을 담은 구현체 
 - **JoinPoint** : Advice가 적용될 위치 --> 끼어드는 지점 
 - **PointCut** : JoinPoint를 상세화 한것 (표현식으로 주로 지정)
 - **Weaving** : 중심로직에 영향을 끼치지 않으면서 PointCut에 Advice를 삽입하는 것을 말함함
***

## Aop 적용 시점
- 컴파일 시점
  - Java파일을 Class파일로 변경 하는 시점에 바이트코드 조작을 통해서 Weaving
- 로드 시점
  - Class파일에는 나타나지 않으나, JVM메모리 상에 Weaving 
- 런타임 시점
  - SpringAop가 사용하는 방식
  - ProxyBean을 만들고, 이 ProxyBean이 Aop가 적용됨


# Spring Aop

### 의존성
```groovy
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

## Advice의 종류
- @Around : JoinPoint의 앞과 뒤에서 실행되는 Advice
- @Before: JoinPoint의 앞에서 실행되는 Advice
- @After: JoinPoint의 결과 (성공,에러)에 상관없이 실행되는 Advice
- @AfterReturning: JoinPoint의 실행결과가 성공적일 때 실행되는 Advice
- @AfterThrowing: JoinPoint의 실행결과가 에러를 던질 때 실행되는 Advice
***

## PointCut

### 명시 가능 PointCut
- execution: Advice를 적용할 메소드를 지정할 때 사용
  - **execution(\[수식어] 리턴타입 \[클래스이름].이름(파라미터)))**
    - 수식어: public, private 등 (Optional)
    - 리턴타입: 메소드의 리턴타입
    - 클래스티름: FQCN (Optional)
    - 이름: 메소드의 이름을 지정
    - 파라미터: 메소드의 파라미터 지정
    - \"*" : 모든 값을 의미
    - \"..": 0개이상을 의미
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
- within: 특정타입에 속하는 메소드를 JoinPoint로 지정 할 때 사용
- bean: Spring Bean을 이용하여 JoinPoint 지정
 


