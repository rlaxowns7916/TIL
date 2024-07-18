# CoroutineScope Builder
- CorutineScope Builder는 말그대로 CoroutineScope를 만들어주는 Builder이다.
- 옵션을 통해서 Dispatcher를 지정할 수 있다.

## coroutineScope
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
  coroutineScope {
    launch {
      // 첫 번째 작업
      delay(1000L)
      println("First Task")
    }

    coroutineScope {
      launch {
        // 두 번째 작업
        delay(500L)
        println("Second Task")
      }
    }
  }
  /**
   * First Task
   * Second Task
   */
}
```
- coroutineScope는 별도의 CoroutineScope를 생성하는 Builder이다.
- coroutineScope는 자식 Coroutine이 모두 실행될 때 까지 대기한다.


## launch
- CoroutineScope의 확장함수이다.
- Job객체를 반환한다.
  - 함수 호출의 결과를 반환하지 않는다.
  - Job객체를 통해서 LifeCycle을 제어할 수 있다.
- 바로 실행을 시작한다.
  - CoroutineStart.LAZY를 사용하면, job.join()이 호출 될 때에만 시작한다.
  - CoroutineStart.DEFAULT가 기본이며 바로시작한다.
- Root Coroutine에서 Exception이 발생하면, 바로 상위 Coroutine으로 전파된다.

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
- await 키워드를 만나면, async블록이 끝났는지 확인하고 끝나지 않았다면 suspend되었다가 나중에 다시꺠어나서 반환한다.
  - await는 Join + get의 역할을 한다.
- **Root Coroutine에서 Exception이 발생했을 때, await()을 하지 않으면 무시된다.**
```kotlin
val deferred = CoroutineScope(Dispatchers.Default).async(Dispatchers.IO) {
     logincSomething()
}
val result = deferred.await()  
```

## coroutinescope
- 임시 Scope를 만들고싶을 때 사용한다.
  - 여러 suspend를 묶어 병렬처리를 하는 가교역할의 함수를 따로 뺄 때 주로 사용한다.
- 내부의 Block이 바로 실행된다.
  - 내부 Block이 완료되면 다음 코드로 넘어간다.
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

## withContext
- CorutineScope의 확장함수이다.
- 내부의 Block이 바로 실행된다.
- Corutine내에서 Context를 일시적으로 변경할 수 있게 한다.
  - 잠시 변경한 후, 원래의 Context로 복귀한다.
- 지정한 Context내에서 Coroutine을 실행하고, 결과 값을 반환 후 원래의 Context로 복귀한다.
  - Deferred<T>가 아닌 실제 반환값 T를 리턴한다.
- **Corutine의 LifeCycle이나 Exception Handling에는 영향을 미치지 않는다.
- withContext내에서 실행된 자신 Coroutine들은, withContext에서 지정된 CoroutineContext를 그대로 상속받는다.
  - withContext내의 Coroutine들이 실행될 때 까지 대기한다.

```kotlin
suspend fun loadData(): Data {
    // I/O 디스패처에서 데이터 로드
    return withContext(Dispatchers.IO) {
        // IO 디스패처에서 실행
        loadFromDisk()
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
    - 내부에서 사용시 this(암묵적) 사용으로 StructuredConcurrency를 통해서 순차적인 실행을 가능하게 한다.
- 생성한 Corutine로직(내부에서 생성된 자식 Coroutine 까지)이 모두 실행될 때 까지 대기(Blocking) 한다.
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
