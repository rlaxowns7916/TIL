# Spring @Async
- Spring에서 제공해주는 비동기 처리 Annotation
- AOP에 의해서 동작한다.
  - Bean으로 등록됨 Method 중 @Async가 등록된 메소드를 찾는다.
  - 해당 메소드가 실행 될 시에, Spring이 가로채서 다른 Thread에 실행을 위임한다.

## 제한 사항
1. private Method, protected method 에는 사용 불가
2. self-invocation (자기호출) 불가능
   - AOP로 동작하고 Proxy 기반이기 떄문이다.
   - @Transactional 문제와 똑같다.
3. QueueCapacity 초과의 요청에 대한 방어코드 작성 필수
   - QueueSize를 넘어서 Task를 실행하려고 하면 **RejectionPolicy** 에 따라서 동작이 결정된다.

   

## 순서
### 1. @EnableAsync
```java
@EnableAsync
@SpringBootApplication
public class SpringBootApplication{
    
}
```
- @Configuration 관련 설정 Class에 @EnableAsync를 설정해준다.


### 2. 설정
```java
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport{

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // ThreadPool에서 항상 Active 한 Thread의 개수
        executor.setMaxPoolSize(10); // ThreadPool에서 사용할 수 있는 최대 Thread의 개수
        executor.setQueueCapacity(500); // ThreadPool Queue Size
        executor.setThreadNamePrefix("Async Executor-"); // Thread Prefix
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy()); // Rejection Policy
        executor.initialize();
        return executor;
    }
}
```
- 기본적으로 제공해주지만 설정을 튜닝하는게 좋다.
  - 설정을 안해주면 SimpleAsyncExecutor를 사용하게 된다.
  - SimpleAsyncExecutor는 ThreadPool을 사용하지 않는다.
- ThreadPool 설정을 해주어 사용하자.
  1. CoreSize만큼 Active하게 Task를 실행한다.
  2. QueueSize만큼 Task들을 보관한다.
  3. QueueSize를 넘어가면 MaxPoolSize만큼 Thread를 생성한다.
  4. keepAliveSecond (default 60 sec) 동안 MaxPool에서 IDLE 할 경우 다시 Core Size로 줄인다.

### Rejection Policy
- ThreadPoolExecutor에서 더이상 Task를 받을 수 없을 때 사용된다.
- 받을 수 없을 때 처리 정책이다.

1. AbortPolicy (default): RejectExecutionException을 던진다.
    - 이 경우 에러처리를 해주어야 한다.
2. CallerRunsPolicy: 호출한 Thread에서 작업을 대신 실행한다.
3. DiscardPolicy: 작업을 버린다.
4. DiscardOldestPolicy: Queue에서 가장 오래되고 처리되지 않은 작업을 버린다.



## 비동기 메소드의 리턴값 별 처리

### 1. 리턴 값이 없을 때
- 리턴값을 void로 놓고 실행하면 된다.

### 2. 리턴 값이 있는 경우
#### 모두 AsyncResult로 묶는다
- **Future**
  - Return 값을 받을 때 까지 Blocking 된다.
  - 성능상에 결함이 있기 때문에 거의 사용하지 않는다.
- **ListenableFuture**
  - Callback을 통한 논 블로킹 처리가 가능하다.
  - 호출하는 쪽에서 리턴값을 받아 addCallback을 통해서 콜백 메소드를 전달 해 줄 수 있다.
- **CompletableFuture**
  - ListenableFuture를 보완한 것이다.
    - ListenableFuture은 Callback Hell을 유발 할 수 있다.
  - 가독성이 훨씬 좋아지기 때문에 사용이 권장된다.

 