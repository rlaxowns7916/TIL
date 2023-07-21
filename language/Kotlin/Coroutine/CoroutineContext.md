# CoroutineContext
- **Coroutine이 실행되는 환경을 의미한다.**
- Job,Dispatcher와 CoroutineExceptionHandler 모두 환경의 일부이다.
- Interface가 존재한다.
  - Map과 같은 형식으로, get을 통해서 CoroutineContext를 가져오거나 없다면 null을 반환한다.
  - plus()를 통해서 Context를 합칠 수 있다.
- 자식 Coroutine은 Dispatcher를 지정하지 않을 경우 부모의 Context를 덮어쓴다.

## Job
- Coroutine의 생명주기를 관리하는데 사용된다.
  - 하나 혹은 여러개의 Coroutine을 제어 할 수 있다.
- CoroutineScope와 마찬가지로 Job또한 계층구조를 가지고 있다.

### 부모 없는 Job
```kotlin

 import kotlin.coroutines.coroutineContextfun main() = runBlocking{
    launch(Job()){
        println(coroutineContext[Job])
    }
}
```
- 새로운 Job을 만듬으로써, 계층구조를 끊는다. 
  - 기존 Job은 부모Scope나, 계층구조를 알고있기 때문이다.
- 계층구조가 끊겼기 떄문에, 부모 Scope는 끊긴 자식 Scope의 실행을 기다리지 않는다.
  - 예외 또한 형제나, 부모 Scope로 전파되지 않는다.

## Element
- CoroutineContext의 요소이다.
  - CoroutineDispatcher
  - Job
  - CoroutineName
  - CoroutineExceptionHandler
  - ... Custom Coroutine Context Element
- coroutineContext[Element이름] 으로 조회 가능하다.
- \+ 연산으로 ELemet 끼리의 결합이 가능하다.
  - 새로운 CoroutineContext를 생성한다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
  /**
   * launch가 CoroutineContext를 인자로 받고
   * plus 연산을 통한 Element의 결합으로 새로운 Coroutine Context를 생성한다.
   * 
   *  CoroutineDispatcher, CoroutineName은 Element를 검색하는 Key값이다.
   */
  launch {
    launch(Dispatchers.IO + CoroutineName("launch1")) {
      println(coroutineContext[CoroutineDispatcher])
      println(coroutineContext[CoroutineName])
    }
  }

  launch(Dispatchers.Default + CoroutineName("launch2")) {
    println(coroutineContext[CoroutineDispatcher])
    println(coroutineContext[CoroutineName])
  }
}
/**
 * Dispatchers.Default
 * Dispatchers.IO
 * CoroutineName(launch2)
 * CoroutineName(launch1)
 */
```


## Dispatcher
- Coroutine이 실행되는 Thread 또는 ThreadPool을 지정하는데 사용된다.
  - 지정하지 않는다면 부모의 Context를 따라간다.
- 여러개의 Dispatcher를 갖고있다.

### [1] Default
- Core 수에 비례하는 ThreadPool에서 수행한다.
- 복잡한 로직을 수행하는데 유리하다.

### [2] I/O
- Core 수보다 훨씬 많은 Thread를 가지는 ThreadPool에서 수애한다.
- I/O작업은 CPU를 덜 소모하기 때문이다.

### [3] UnConfined
- 어디에도 속하지 않는다.
  - 처음에는 부모 Thread에서 수행된다.
  - 한번이라도 Suspend되면, 어느 Thread에서 동작하게 될지 알 수 없다.
- 사용하지 않는 것이 추천된다.

### [4] newSingleThreadContext
- 새로운 Thread를 생성한다.
- Thread의 이름또한 지을 수 있다.
```kotlin
launch(newSingleThreadContext("Custom Thread"))
```

## CoroutineExceptionHandler
- Coroutine 내에서 발생하는 Exception을 제어하는데 사용된다.
- Default와 I/O의 경우 ThreadPool을 사용하는데, ThreadPool가용범위를 넘었을 경우 Thread를 받지 못할 수 있다. 이럴 경우를 대비해서 사용가능하다.