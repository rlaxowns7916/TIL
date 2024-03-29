# Scope
- SpringContainer에서 관리되는 Bean의 범위
- 기본적으로 Singleton이며, prototype, request, session, application 등이 있다.

# Spring Batch의 Scope
- 모두 ProxyBean으로 동작한다.
  1. Application 구동시점에는 ProxyBean이 생성
  2. Bean의 접근시점에 실제 Bean을 생성 및 호출 
- **해당 Scope가 선언되면, Bean의 생성이 컨테이너 구동시점이 아닌, Bean 실행시점에 이루어진다.**
  - @Value를 통해서 Bean의 실행시점에 값을 읽어오며, 일종의 LazyBinding이 가능해진다.
  - @Value를 선언하고, @JobScope나 @StepScope와 같은 Scope를 선언하지 않으면 Exception이 발생한다.
  - ```kotlin
        /**
          * 1. JobParameter
          * 2. JobExecutionContext
          * 3. StepExecutionContext
          */
        @Value("#{jobParameters['파라미터명']}")
        @Value("#{jobExecutionContext['파라미터명']}")
        @Value("#{stepExecutionContext['파라미터명']}")
    ```
- 병렬 처리 시, 각 Thread마다 Bean이 생성되기 떄문에, ThreadSafe하다.

## [1] JobScope
- Step에 선언한다.
- ```kotlin
    @JobScope
    @Scope(value="job", proxyMode=ScopedProxyMode.TARGET_CLASS)
  ```
- 실제 Bean을 등록하고, 해제하는 역할을 한다.
- 실제 Bean을 저장하고 있는 JobContext를 가지고 있다.
  - Bean을 저장하는 Context 역할
  - ProxyBean이 실제 Bean을 참조 할 때, JobContext를 통해서 참조한다.


## [2] StepScope
- Tasklet, Reader, Processor, Writer에 선언한다.
- ```kotlin
    @StepScope
    @Scope(value="step", proxyMode=ScopedProxyMode.TARGET_CLASS)
  ```
- 실제 Bean을 등록하고, 해제하는 역할을 한다.
- 실제 Bean을 저장하고 있는 StepContext를 가지고 있다.
  - Bean을 저장하는 Context 역할
  - ProxyBean이 실제 Bean을 참조 할 때, StepContext를 통해서 참조한다.


# JobScope 아키텍쳐
1. ApplicationContext가 구동된다.
2. @Scope에 해당하는 Annotation이 있는지 확인하고, 없다면 Singleton으로 생성한다.
3. Spring 초기화 후, JobLauncher가 Job을 실행
4. Job은 Step의 Proxy객체를 가지고 있다. (Job은 원래 자신의 Step들을 관리한다.)
5. Step실행 시, StepProxy객체는 실제 Step객체를 호출하기 위해 JobScope에 접근한다.
6. JobScope의 JobContext를 통해서 Bean을 찾은 후 실제 Step객체에 접근하고, Step을 실행한다.
   - JobContext에 Bean이 존재하지 않는다면 새롭게 생성한다. (@Value 바인딩도 이시점에 발생한다.)