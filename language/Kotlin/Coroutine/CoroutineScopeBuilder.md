# CoroutineScope Builder
- CorutineScope Builder는 말그대로 CoroutineScope를 만들어주는 Builder이다.
- 모든 Corutine은 CorutineScope안에서만 동작한다.
- Coroutine Scope가 아닌 곳에서 Corutine을 사용할 경우 컴파일 에러가 발생한다.
- 부모 Coroutine이 있다면 상속받고, 없다면 새롭게 Coroutine을 생성한다. (StructuredConcurrency)

## launch
- CoroutineScope의 확장함수이다.
- Job객체를 반환한다.
  - 함수 호출의 결과를 반환하지 않는다.
  - Job객체를 통해서 LifeCycle을 제어할 수 있다.
- 바로 실행을 시작한다.
  - CoroutineStart.LAZY를 사용하면, job.join()이 호출 될 때에만 시작한다.
  - CoroutineStart.DEFAULT가 기본이며 바로시작한다.

```kotlin
fun main(){
  val job = CoroutineScope(Dispatchers.Default).launch {
    /**
     * Coroutine Logic
     */
  }
}
```

## async
- CoroutineScope의 확장함수이다.
- 미래에 얻을 수 있는 값의 참조인 Deferred<T> 객체를 반환한다.
  - Job을 상속한 클래스이다.
  - **결과를 가지는 Coroutine의 실행이다.**
  - **await() 함수를 통해서 결과를 받을 수 있다.**
```kotlin
val deferred = CoroutineScope(Dispatchers.Default).async(Dispatchers.IO) {
    return logincSomething()
}
val result = deferred.await()  
```

## runBlocking
- 최상위 함수이다.
  - 내부적으로 CorutineScope를 생성한다.
  - 내부에서 launch, async와 같은 Builder의 사용이 가능하다.
    - 내부에서 사용시 this(암묵적) 사용으로 StructuredConcurrency를 통해서 순차적인 실행을 가능하게 한다.
- Corutine로직이 모두 실행될 때 까지 대기(Blocking) 한다.
- runBlocking을 실행시키는 Thread를 Blocking시키는 것이다.
  - MainThread에서의 사용을 조심해야 한다. (Android의 경우, UI를 뿌려주는 MainThread에서 사용할 경우 UI가 차단된다.)
- Blocking을 하기 때문에, 대량의 병렬작업을 수행할 때는 사용을 하지 않는 것이 좋다.
  - launch나 async같은 AsyncCoroutineBuilder를 사용하는 것이 좋다.
- Default 설정으로는 자식 Conroutine이 될 수 없다.
  - 기본 Context가 EmptyCoroutineContext이기 때문이다.
  - CoroutineContext를 변경 시, 자식 Coroutine이 될 수 있다.

```kotlin
fun main(){
    runBlocking{
        delay(5000)
        println("Corutine Logic")
    }
    println("All Logic Completed")

    /**
     * Corotine로직이 실행되기 전까지 Blocking이 된다.
     * 
     * Corutine Logic
     * All Logic Completed
     */
}
```

## withContext
- CorutineScope의 확장함수이다.
- Corutine내에서 Context를 일시적으로 변경할 수 있게 한다.
  - 잠시 변경한 후, 원래의 Context로 복귀한다.
- 지정한 Context내에서 Coroutine을 실행하고, 결과 값을 반환 후 원래의 Context로 복귀한다.
  - Deferred<T>가 아닌 실제 반환값 T를 리턴한다.
- **Corutine의 LifeCycle이나 Exception Handling에는 영향을 미치지 않는다.

```kotlin
suspend fun loadData(): Data {
    // I/O 디스패처에서 데이터 로드
    return withContext(Dispatchers.IO) {
        // IO 디스패처에서 실행
        loadFromDisk()
    }
}
```


## Suspend 함수 안에서 CoroutineScope Builder의 사용
- 호출하는 쪽에 CoroutineScope가 존재한다고 호출되지는 않는다.
- suspend함수에도 corutineScope를 명시해야한다.
```kotlin
import kotlinx.coroutines.*
fun main() {
    runBlocking{
        launch{
            makeNewCoroutine()
            println("Main Inner coroutine")
        }
        println("Main Outer Coroutine")
    }
    
    println("Finished")
    
}
suspend fun makeNewCoroutine() = coroutineScope{
    launch{
        delay(1000L)
        println("SuspendFunction Inner Coroutine")
    }
    println("SuspendFunction Outer Coroutine")
}

/**
 * Main Outer Coroutine
 * SuspendFunction Outer Coroutine
 * SuspendFunction Inner Coroutine
 * Main Inner coroutine
 * Finished
 */
```
