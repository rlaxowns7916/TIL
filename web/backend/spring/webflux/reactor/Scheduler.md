# Scheduler
- Scheduler는 리액티브 스트림 내에서 특정 연산이 실행될 스레드 또는 스레드 풀의 실행 컨텍스트를 명시적으로 결정하는 역할을 수행한다.
- ReactiveStreams는 동시성을 직접 강제하지 않기 때문에, 개발자는 Scheduler를 통해 명확한 동시성 전략을 구현하여 애플리케이션의 성능, 안정성, 응답성을 관리한다.
- 비동기적이고 논블로킹 환경에서 스레드 실행 컨텍스트를 명시적으로 관리한다.
  - 특정 작업(예: CPU 집약, 블로킹 I/O)에 맞는 적합한 스레드 풀을 제공하여 자원 효율성을 높인다.

## Scheduler 종류
| Scheduler | 특성                                                   | 주 사용 사례 | 블로킹 적합성 |
|-----------|------------------------------------------------------|-------------|-------------|
| **immediate()** | 현재 스레드 즉시 실행                                         | 테스트, 동기적 작업 | 부적합 |
| **single()** | 단일 공유 스레드                                            | 직렬화된 작업, 순서 보장 | 부적합 |
| **newSingle()** | 호출 시마다 전용 스레드 생성                                     | 격리된 직렬 작업 | 부적합 |
| **boundedElastic()** | 제한된 탄력적 스레드 풀(기본: CPU 코어 수 × 10)                     | 블로킹 I/O 작업 | 매우 적합 |
| **parallel()** | CPU 코어 수와 같은 고정 스레드 풀 \n n 지정을 통해서 스레드의 갯수 지정도 가능하다. | CPU 집약적인 병렬 연산 | 부적합 |
| **fromExecutorService(…)** | 기존 ExecutorService 활용                                | 사용자 정의 스레드 관리 | ExecutorService 설정에 따름 |


## 주요 Operator
| Operator | 설명                              |
|-----------|---------------------------------|
| **subscribeOn(Scheduler)** | 데이터 스트림의 구독을 지정된 Scheduler에서 실행 |
| **publishOn(Scheduler)** | 데이터 스트림의 후속 연산을 지정된 Scheduler에서 실행 |


### subscribeOn
- subscribe(구독) 이후 에 실행되는 연산을 지정된 Scheduler에서 실행한다.
  - 즉 publisher가 emit하는 시점부터 지정된 Scheduler에서 실행된다.
- 여러개를 사용해도 마지막에 지정된 subscribeOn만 적용된다.
```kotlin
Mono.fromCallable {
  println("Source 스레드: ${Thread.currentThread().name}")
  "Hello"
}
  .subscribeOn(Schedulers.boundedElastic()) // 소스 생성부터 모든 연산이 boundedElastic에서 실행
  .map {
    println("map 스레드: ${Thread.currentThread().name}")
    it.uppercase()
  }
  .subscribe {
    println("subscribe 스레드: ${Thread.currentThread().name}")
    println("결과: $it")
  }
```

### publishOn
- 데이터 스트림의 후속 연산을 지정된 Scheduler에서 실행한다.
- 여러개를 사용할 수 있다.
```kotlin
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(String::class.java)
    .subscribeOn(Schedulers.boundedElastic())    // HTTP 호출을 I/O 스레드에서
    .map { parseData(it) }                       // 파싱도 I/O 스레드에서
    .publishOn(Schedulers.parallel())            // CPU 집약적 작업을 parallel로
    .map { heavyComputation(it) }               // 무거운 연산
    .publishOn(Schedulers.boundedElastic())      // DB 저장을 다시 I/O 스레드로
    .flatMap { saveToDatabase(it) }             // DB 저장
```

## Custom Scheduler
- Schedulers.newXxx 계열 메서드 사용하여 Custom Scheduler를 생성할 수 있다.
- 커스텀하게 생성된 스케줄러는 애플리케이션 종료 시 명시적으로 dispose()를 호출하여 리소스를 해제해야 한다.

### [1] newParallel
- 지정된 스레드 수로 병렬 스케줄러를 생성한다.
- ```java
    public static Scheduler newParallel(String name, int parallelism);
   ```
### [2] newBoundedElastic
- 지정된 스레드 수로 제한된 탄력적 스케줄러를 생성한다.
- threadCap: 생성가능한 최대 Thread 수
- queuedTaskCap: 대기중인 작업의 최대 수 >> 큐가 가득차면 RejectedExecutionException이 발생한다.
  - Error가 발생하면, ReactiveStream의 onError로 전달된다.
```java
public static Scheduler newBoundedElastic(
    String name,
    int threadCap,
    int queuedTaskCap
);
```
### [3] fromExecutorService
- 기존 ExecutorService를 기반으로 Scheduler를 생성한다.
- ```java
    public static Scheduler fromExecutorService(ExecutorService executorService);
   ```
  
### [4] newSingle
- 새로운 단일 스레드 스케줄러를 생성한다.
  - single()은 하나의 스레드를 재사용 하는 반면, newSingle()은 매번 새로운 스레드를 생성한다.
  - newSingle()은 독립된 단일 스레드이기 때문에 반드시 dispose() 호출이 필요하다.
- daemon: true로 설정하면, JVM이 종료될 때 스레드가 자동으로 종료된다.
- ```java
    public static Scheduler newSingle(String name, boolean daemon); // daemon 기본값은 false
   ```