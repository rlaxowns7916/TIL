# 동시성 프레임워크 (Executor Framework)
- Thread를 직접 관리하지 않기위해 사용하는 방법이다.
  - Thread를 직접 사용하면 아래와 같은 문제점이 있다.
    1. Thread 생성 시간에 따른 성능문제
       - 메모리 할당
       - OS Resource 사용
       - OS Scheduler 사용
    2. Thread 관리 문제
- ThreadPool을 통해서 재사용 한다.

# [1] Executor
```java
    public interface Executor {
        void execute(Runnable command);
    }
```
- Java에서 제공하는 동시성 프로그래밍 추상화 인터페이스
  - Runnable을 받아 실행하는 Method 하나를 갖고있다.
- 단일 Method를 정의하고 있고, 작 업을 제출하면 각 구현체가 적절한 Thread를 생성하여 작업을 실행한다.
  - 구현체에 따라서, Runnable객체는 새로운 Thread, ThraedPool, 호출 Thread 등에 의해 실행될 수 있다.
  - 작업을 제출하면 작업실행은 Executor내부에 위임하는 것이 유연하고 좋은 설계이다.


# [2] ExecutorService
- Executor의 확장 버전
  - **Callable을 인자로 받고, Future를 리턴하는 submit Method를 가지고 있다.**
- 작업의 제출, ThreadPool의 종료를 관리하기 위한 추가적인 Method를 제공한다.
  - **기본 구현체 ==> ThreadPool Executor**
- 명시적인 종료가 필요하다.
  - 종료하지 않으면, Thread가 종료되지 않아 메모리 누수가 발생할 수 있다.
  - 사용자 스레드가 계속 실행 중이면 JVM이 종료되지 않으므로, 스레드 풀이 종료된 후 정상적인 종료가 가능하다.

## 메소드
1. invokeAll(Colection<? extends Callable<T>> tasks)
   - 모든 작업이 완료될 때 까지 Blocking된다.
   - 모든 작업이 완료되면, 결과를 List로 반환한다. 
2. invokeAny(Collection<? extends Callable<T>> tasks)
   - 하나의 작업이 완료될 때 까지 Blocking된다.
   - 하나의 작업이 완료되면, 결과를 반환한다.
   - 완료되지 않은 다른 작업들은 모두 취소된다. (나머지 작업들에 대해서 interrupt)
3. close()
   - java19부터 제공
   - shutdown()과 동일한 기능을 수행한다. (하루 이상 작업이 종료되지 않으면, shutdownNow()를 호출한다.)
   - 호출한 Thread에서 Interrupt가 발생했을 경우, shutdownNow()를 호출한다.
4. shutdown()
   - NonBlocking 메소드이다.
   - graceful shutdown을 지원한다. (이미 제출된 작업은 모두 실행 후 종료)
5. shutdownNow()
   - NonBlocking 메소드이다.
   - 실행중인 작업을 즉시 중단하고 (interrupt를 통한 종료) 즉시 종료한다.
     - **Thread가 interrupt를 받을 수 없는 경우, Java 프로세스 자체를 종료해야한다.**
6. awaitTermination(long timeOut, TimeUnit unit)
   - Blocking 메소드이다.
   - 모든 작업이 완료되기를 기다린다. (timeOut 지정 시간까지)

# [3] ThreadPoolExecutor
- ExecutorService의 기본 구현체이다.
- 크게 2가지(ThreadPool, Queue)로 나뉜다.
  - Producer: Task를 생성하여 BlockingQueue에 작업을 보관한다.
  - Consumer: ThreadPool에 있는 Thread는 BlockingQueue에 있는 Task를 소비한다. (없다면 WAITING) 
- 설정
  - corePoolSize: ThreadPool에서 관리되는 기본 Thread 갯수
  - maximumPoolSize: ThreadPool에서 관리되는 최대 Thread 갯수
  - keepAliveTime: 기본Thread를 초과하여 생성된 (maximumThreadPoolSize 까지) Thread가 생존 할 수 있는 시간이다.
  - blockingQueue: 작업을 보관할 BlockingQueue (구현체에 따라 Producer도 Blocking 될 수 있다.)
- 설정에 따른 동작
  1. 요청이 들어올 때마다, CorePoolSize만큼 Thread가 생성된다.
     - prestartAllCoreThreads()를 통해서, CorePoolSize만큼 Thread를 미리 생성할 수 있다.
  2. blockingQueue가 다차면 maximumPoolSize만큼 Thread가 생성된다.
  3. maximumPoolSize까지 Thread가 생성되어도, blockingQueue가 다차면, RejectedExecutionHandler가 동작한다.
  4. keepAliveTime이 지나면, corePoolSize만큼의 Thread를 제외하고는 종료된다.
     - 초과 Thread가 작업을 수행 할 때마다 KeepAliveTime은 초기화된다.

## ThreadPoolExecutor Policy

### [1] FixedThreadPool
- Thread를 n개까지 생성한다. (초과 Thread는 생성하지 않는다.)
- QueueSize에 제한을 두지 않는다. (LinkedBlockingQueue)
- Resource 사용에 대한 예측이 가능하다.
```java
ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
```
```java
new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
```

### [2] CachedThreadPool
- **coreThread를 사용하지 않고, 60초의 생명주기를 가진 초과 Thread만 사용한다.**
- 초과 Thread의 갯수에는 제한이 없다.
- **Queue에 작업을 저장하지 않고, ThreadPool의 Thread가 바로 처리한다.**
- SynchronousQueue
  - 내부에 저장공간을 가지고있지 않다.
  - Producer의 작업을 Consumer에게 바로 전달한다.
    - Consumer가 작업을 가져갈 때 까지, Producer는 대기한다.
  - Producer와 Consumer 사이의 동기화 Queue이며, 일종의 직거래이다.
- 대기가 없기 떄문에 작업속도는 빠르지만, Resource 예측이불가능하다.

```java
ExecutorService executorService = Executors.newCachedThreadPool();
```
```java
new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
```

## Rejection Policy
- RejectedExecutionHandler를 통해서, ThreadPool이 꽉 찼을 때, 어떻게 처리할지 정의한다.
  - Custom하게 구현 가능하다.

### [1] AbortPolicy(default)
- Queue와 ThreadPool이 모두 꽉 차면, RejectedExecutionException을 발생시킨다.

### [2] DiscardPolicy
- 새로 제출된 Task를 버린다.

### [3] CallerRunsPolicy
- 새로운 Task를 제출한 Thread가 돌리게한다.

***

# [4] ScheduledThreadPoolExecutor
- ThreadPoolExecutor의 확장버전이다.
- 특정 시간 또는 주기적으로 작업을 가능하게 하는 스케줄링 Executor
```java
ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize);
```

### [1] schedule(Runnable command, long delay, TimeUnit unit)
- 지연실행
- 특정 시간의 Delay 이후에 작업을 수행한다.

### [2] scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimUnit unit)
- 주기적 실행
- 작업이 길어지면 실행주기가 겹칠 수 있다.