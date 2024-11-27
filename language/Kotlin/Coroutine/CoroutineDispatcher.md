# Coroutine Dispatcher
- Coroutine을 보내는 주체
- **Coroutine이 실행되는 Thread 또는 ThreadPool을 지정하는데 사용된다.**
    - 지정하지 않는다면 부모의 Context(Dispatcher)를 따라간다.
- 여러개의 Dispatcher가 기본으로 제공된다.
  - StructuredConcurrency 에 따라서 자식 Coroutine은 부모의 Dispatcher를 상속받는다.


## 내부구조
- 작업 대기열을 가지고 있다.
- 작업을 수행할 수 있는 ThreadPool을 가지고 있다.

### [0] Main
- 일반적으로 사용불가능하다.
- library(구현체)를 통해서 주입받아야 한다.
  - Android에서 사용가능하다

### [1] Default
- Core 수에 비례하는 ThreadPool에서 수행한다.
- 복잡한 로직을 수행하는데 유리하다. (CPU Bounded Job)
- CPU Core수 만큼 사용되기 때문에, CPU Bounded Job에 유리하다.
  - CPU를 지속적으로 점유하는 경우 (연산 특화)
- Thread수: max(2, CPU Core수)

### [2] I/O
- 많은 Thread를 가지는 ThreadPool을 생성한다.
  - Thread수: max(64, CPU Core수)
- I/O작업은 CPU를 덜 소모하기 때문이다.
  - I/O Bounded Job에 유리하다.


### [3] UnConfined
- 특정 Thread 어디에도 속하지 않는다.
    - 처음에는 Caller Thread에서 수행된다.
    - 한번이라도 Suspend되면, 어느 Thread에서 동작하게 될지 알 수 없다.
- 사용하지 않는 것이 좋다.
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch(Dispatchers.UnConfined) {
        // UnConfined 스레드에서 실행됩니다.
        println("UnConfined: I'm working in thread ${Thread.currentThread().name}")
        delay(500)
        // delay 후에 다른 스레드에서 재개될 수 있습니다.
        println("UnConfined: After delay in thread ${Thread.currentThread().name}")
    }
    launch {
        // 부모의 runBlocking 스레드에서 실행됩니다.
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }
}
```

### [4] ExecutorCoroutineDispatcher
- 새로운 Thread 혹은 ThreadPool을 생성한다.
  - 이름또한 지을 수 있다.
- 공식문서에서 주의할 것을 요하고 있다.
  - ```text
    This is a delicate API and its use requires care. 
    Make sure you fully read and understand documentation of the declaration that is marked as a delicate API.
    ```
  - ThreadPool과 같이 미리 생성하여 메모리에 올려놓게 되기 때문에 주의해야 한다.
```kotlin
val single = newSingleThreadContext("Single")
val fixed = newFixedThreadPoolContext(4,"fixed")

val job = launch(single){
    log.info("thread1: {}",threadName())
    withContext(fixed){
        log.info("thread2: {}",threadName())
    }
}
```

## Default Dispatcher와 I/O Dispatcher의 ThreadPool
- 같은 ThreadPool을 공유한다.
  - 각각의 Dispatcher에 맞게 사용하는 Thread가 독립적이지 않다.
  - ThreadPool에 있는 모든 Thread는 두 Dispatcher에게 공유된다.
- 두 Dispatcher의 차이는, ThreadPool에 Thread를 가변적으로 추가할 수 있는지 여부의 차이이다.
  - I/O: 가변, 새로운 Thread의 추가가 가능하다.
    - I/O Dispatcher는 내부적으로 Thread를 늘리는 임계치가 존재한다.
    - 늘어난 ThreadPool의 크기는 다시 줄어들지 않는다.
    - limit parallelism: 기존 I/O가 사용하던 ThreadPool 이외에, 공유ThreadPool에서 새로운 Thread를 사용한다. (다른작업에 방해를 받지 않게 하기 위해서이다.)
  - Default: 고정, 이미 ThreadPool이 모두 사용중이면 대기에 걸린다.
    - limited parallelism: 작업을 수행하는 최대 Thread수를 설정하여, 아예 작업을 못하게 되는 경우를 방지한다.
- 왜 이러한 설계를 가지게 되었는가?
  - 독립된 ThreadPool은 JVM에 관리부하를 준다.
  - 일반적인 경우에는 충분히 효율적이다.