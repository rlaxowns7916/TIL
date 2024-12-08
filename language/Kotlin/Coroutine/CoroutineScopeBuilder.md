# CoroutineScope Builder
- CorutineScope Builder는 말그대로 CoroutineScope를 만들어주는 Builder이다.
- 옵션을 통해서 Dispatcher를 지정할 수 있다.
- scope에 Job객체를 넘기면, 해당 Job객체가 새로운 Job의 부모가 된다.

## coroutineScope
- 임시 Scope를 만들고싶을 때 사용한다.
  - 여러 suspend를 묶어 병렬처리를 하는 가교역할의 함수를 따로 뺄 때 주로 사용한다.
- 내부의 Block이 바로 실행된다.
  - 내부 Block이 완료되면 다음 코드로 넘어간다.
- 현재 Scope내에서 임시 Scope를 만드는 것이다.
  - 부모의 CoroutineContext를 상속하게 된다.
  - CoroutineScope()를 사용하면, 기존 Scope와의 연결고리를 끊고 새로운 CoroutineScope를 만든다는 점에서 차이가 있다.
- ```kotlin
    suspend fun calculateResult(): Int = coroutinesScope{
        
        val num1= async{
            delay(1000L)
            10
        } 
        
        val num2 = async{
            delay(1000L)
            20
        } 
        num1.await() + num2.await()
    }
  ```

## launch
- CoroutineScope의 확장함수이다.
- Job객체를 반환한다.
  - 함수 호출의 결과를 반환하지 않는다.
  - Job객체를 통해서 LifeCycle을 제어할 수 있다.
- 바로 실행을 시작한다.
  - CoroutineStart.LAZY를 사용하면, job.join()이 호출 될 때에만 시작한다.
  - CoroutineStart.DEFAULT가 기본이며 바로시작한다.
- Root Coroutine에서 Exception이 발생하면, 바로 상위 Coroutine으로 전파된다.
- Caller에서 join()을 통해서 launch Coroutine의 종료 까지 suspend 할 수 있다.

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
- Caller에서 await()을 통해서 async Coroutine의 종료 까지 suspend 할 수 있으며, 결과를 전달받는다.
- **Root Coroutine에서 Exception이 발생했을 때, await()을 하지 않으면 무시된다.**
```kotlin
val deferred = CoroutineScope(Dispatchers.Default).async(Dispatchers.IO) {
     logincSomething()
}
val result = deferred.await()  
```

## withContext
- CorutineScope의 확장함수이다.
- 내부의 Block이 바로 실행된다.
- Corutine내에서 Context를 일시적으로 변경할 수 있게 한다.
- 지정한 Context내에서 Coroutine을 실행하고, **결과 값을 반환 후** 원래의 Context로 복귀한다.
  - Deferred<T>가 아닌 실제 반환값 T를 리턴한다.
  - withContext 실행하는 동안, 호출한 Coroutine은 Block된다.
  - withContext 내부의 예외는 호출한 Coroutine으로 전파된다.
- withContext내에서 실행된 자신 Coroutine들은, withContext에서 지정된 CoroutineContext를 그대로 상속받는다.
  - withContext내의 Coroutine들이 실행될 때 까지 대기한다. (Caller는 Coroutine 제어권을 반납)
  - 비동기(새로운 Coroutine을 생성)로 동작하는 것이 아니라, 하나의 Coroutine에서 실행 Thread를 변경 하는 것이기 때문
- 호출한 Coroutine은 withContext의 실행이 완료될 때 까지 suspend된다.

```kotlin
suspend fun loadData(): Data {
    // I/O 디스패처에서 데이터 로드
    return withContext(Dispatchers.IO) {
        // IO 디스패처에서 실행
        loadFromDisk()
    }
}
```

## supervisorScope
- superVisorJob을 가지는 scope를 생성한다.
  - 이 superVisorJob은 supervisorScope를 호출한 Job을 부모로 가진다.
- 자신과, 자식 Coroutine이 모두 완료되면 종료된다.
- 자식 Coroutine에서 발생한 Exception을 부모에게 전파하지 않는다.
  - 부모 Coroutine은 자식 Coroutine의 Exception을 무시한다.
  - 자식 Coroutine은 부모 Coroutine의 취소에 영향을 받지 않는다.
```kotlin
suspend fun loadData(): Data {
    return supervisorScope {
        // 자식 Coroutine
        val deferred = async {
            // IO 디스패처에서 실행
            loadFromDisk()
        }
        deferred.await()
    }
}
```


### Lazy Start Async
```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val time = measureTimeMillis {
        val deferred1 = async(start = CoroutineStart.LAZY) { getRandom1() }
        val deferred2 = async(start = CoroutineStart.LAZY) { getRandom2() }
      
        deferred1.start()
        deferred2.start()
        
        println(deferred1.await() + deferred2.await())
    }
    println(time) // 1초 가량
}

suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0,500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0,500)
}
```
- 명시적으로 start를 해줄 경우에는 비동기적으로 동작한다.
- start 없이 await을 만난 경우, 그제서야 실행되고 동기적으로 수행된다.

## runBlocking
- 최상위 함수이다.
  - 내부적으로 CorutineScope를 생성한다.
  - 내부에서 launch, async와 같은 Builder의 사용이 가능하다.
- **부모 Job이 없는 rootJob이 생성된다.**
- runBlocking을 실행시키는 Thread를 Blocking시키는 것이다.
  - 모든 것에서 Blocking 되는 것은 아니고, 자식 Job들을 점유 가능하다. 
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

## 커스텀 Coroutine Builder
```kotlin
fun <T> customCoroutineBuilder(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        parent: Job? = null,
        block: suspend () -> T
){
    
}
```


## Suspend 함수 안에서 CoroutineScope Builder의 사용
- 호출하는 쪽에 CoroutineScope가 존재한다고 호출되지는 않는다.
- suspend함수에도 corutineScope를 명시해야한다.
```kotlin
import kotlinx.coroutines.*
fun main() = runBlocking{
        launch{
            makeNewCoroutine()
            println("Main Inner coroutine")
        }
        println("Main Outer Coroutine")
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
