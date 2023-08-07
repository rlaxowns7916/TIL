# CoroutineContext
- **Coroutine이 실행되는 환경을 의미한다.**
  - MetaData를 저장하거나, 자식 Coroutine에 넘길 정보를 저장하는 저장소의 용도로 볼 수 있다.
  - 임의의 값이 아닌 CoroutineContext.Element 인터페이스를 구현하는 값만 들어갈 수 있다.
- Job,Dispatcher와 CoroutineExceptionHandler,ThreadLocl 모두 환경의 일부이다.
- Interface가 존재한다.
  - Map과 같은 형식으로, get을 통해서 CoroutineContext를 가져오거나 없다면 null을 반환한다.
  - plus()를 통해서 Context를 합칠 수 있다.
- 자식 Coroutine은 Dispatcher를 지정하지 않을 경우 부모의 Context를 덮어쓴다.

### 접근하는 방법
1. CoroutineScope 내부
```text
runBlocking, launch, async와 같은 CoroutineScope 내부에 있다면,

CoroutineScope.coroutineContext로 접근 가능하다.
Scope내에서는 CoroutineScope가 this로 접근이 가능하므로, this.coroutineContext로 접근 가능하다.
```

2. Continuation
```text
Continuation.coroutineContext로 접근이 가능하다.
```

3. suspend 함수 내부
```text
suspend함수 내부에서, coroutineContext접근이 가능하다.
```

## Job
- Coroutine의 생명주기를 관리하는데 사용된다.
  - 하나 혹은 여러개의 Coroutine을 제어 할 수 있다.
- CoroutineScope와 마찬가지로 Job또한 계층구조를 가지고 있다.
- 자식 Coroutine의 Exception으로 인한 실패는 부모에게 전파된다.
  - 역도 성립한다.

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

### SupervisorJob
- Exception의 전파를 아래로만 전달한다.
  - 자식 Coroutine의 예외가 부모 Coroutine에게로 전파되지 않는다.
- context에 SuperVisorJob() 생성을 통해 인자를 넘겨주는 행위는 계층구조를 끊는 방식이다.
```kotlin
fun main() = runBlocking{
    val scope = CoroutineScope(Dispatchers.IO + SuperVisorJob() + exceptionHandler)
    val job1 = scope.launch {printRandom1()}
    val job2 = scope.launch {printRandom2()}
  
   joinAll(job1, job2)

  /**
   * Exception Occurred -> job1
   * 80 -> job2
   * SuperVisorJob이기 떄문에, Exception이 아래방향으로만 전파된다.
   * 그렇기 떄문에 부모 Coroutine과, 형제 Coroutine인 job1은  정상적으로 실행된다.
   */
}
```


## Element
- CoroutineContext의 요소이다.
  - CoroutineDispatcher
  - Job
  - CoroutineName
  - CoroutineExceptionHandler
  - ThreadLocl
  - ... Custom Coroutine Context Element
- coroutineContext[Element이름] 으로 조회 가능하다.
  - \+ 연산으로 Elemet 끼리의 결합이 가능하다.
    - 새로운 CoroutineContext를 생성한다.
    - 이미 같은 Key값을 갖고있는 Elemnt가 있다면 Override한다.
    - 리턴 값은 더해진 이후의 CoroutineContext
  - minusKey 연산을 제공한다.
    - 해당 Key값의 Element를 coroutineContext에서 제외한다.
  - \- 연산은 Element를 제거한다.
  - 리턴 값은 은 - 연산이 진행된 후의 결과이다.
- CoroutineContext Interface의 구현체이다.


```kotlin
import kotlin.concurrent.thread
import kotlinx.coroutines.*

fun main() = runBlocking {
  /**
   * launch가 CoroutineContext를 인자로 받고
   * plus 연산을 통한 Element의 결합으로 새로운 Coroutine Context를 생성한다.
   * 
   *  CoroutineDispatcher, CoroutineName은 Element를 검색하는 Key값이다.
   */
  val threadLocal = ThreadLocal<String>()
  threadLocal.set("hello")
  val job1 = launch {
    launch(Dispatchers.IO + CoroutineName("launch1") + threadLocal.asContextElement()) {
      println(coroutineContext)
      println(coroutineContext[CoroutineName])
    }
  }

  val job2 = launch(Dispatchers.Default + CoroutineName("launch2")) {
    println(coroutineContext[CoroutineDispatcher])
    println(coroutineContext[CoroutineName])
  }

  job1.join()
  job2.join()
}
/**
 * Dispatchers.Default
 * CoroutineName(launch2)
 * [CoroutineName(launch1), ThreadLocal(value=hello, threadLocal = java.lang.ThreadLocal@1f763139), CoroutineId(4), "launch1#4":StandaloneCoroutine{Active}@504ad22b, Dispatchers.IO]
 * CoroutineName(launch1)
 */
```


## Dispatcher
- **Coroutine이 실행되는 Thread 또는 ThreadPool을 지정하는데 사용된다.**
  - 지정하지 않는다면 부모의 Context를 따라간다.
- 여러개의 Dispatcher를 갖고있다.

### [1] Default
- Core 수에 비례하는 ThreadPool에서 수행한다.
- 복잡한 로직을 수행하는데 유리하다.

### [2] I/O
- Core 수보다 훨씬 많은 Thread를 가지는 ThreadPool에서 수애한다.
- I/O작업은 CPU를 덜 소모하기 때문이다.

### [3] UnConfined
- 특정 Thread 어디에도 속하지 않는다.
  - 처음에는 부모 Thread에서 수행된다.
  - 한번이라도 Suspend되면, 어느 Thread에서 동작하게 될지 알 수 없다.
- 사용하지 않는 것이 좋다.

### [4] newSingleThreadContext
- 새로운 Thread를 생성한다.
- Thread의 이름또한 지을 수 있다.
```kotlin
launch(newSingleThreadContext("Custom Thread"))
```

## CoroutineExceptionHandler
- Coroutine 내에서 발생하는 Exception을 제어하는데 사용된다.
  - Interface이며, CoroutineContext의 하나의 요소로서 동작한다.
- Default와 I/O의 경우 ThreadPool을 사용하는데, ThreadPool가용범위를 넘었을 경우 Thread를 받지 못할 수 있다. 이럴 경우를 대비해서 사용가능하다.
- CoroutineExceptionHandler 함수를 통해서 생성 가능하다.
  - 첫번쨰 인자로는 CoroutineContext, 두 번째 인자로는 Exception을 받는다.
- **해당 Scope에만 종속적이다.**
  - 자식 CoroutineContext로 전파되지 않는다.
  - 각 CoroutineScope의 ExceptionHandling의 유연함을 제공하기 위해서 설계되었다.
  - 하나의 Scope에 국한됨으로써, 예외처리가 더욱 명확해진다.
```kotlin
/**
 * CoroutineContext는 사용하지않기 떄문에 _로 받을 수도 있음
 */
val ceh = CoroutineExceptionHandler { _, throwable ->
  println("Exception Occurred in $throwable")
}

fun main() = runBlocking {
  val scope = CoroutineScope(Dispatchers.IO)
  val job = scope.launch(ceh + CoroutineName("Context With ExceptionHandler") {
    launch {
      println("Coroutine 1")
    }

    launch {
      println("Coroutine 2")
    }
  })
}
```