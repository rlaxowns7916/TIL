# CoroutineCancel
- Coroutine을 종료시킨다.
  - 이미 완료된 Coroutine에는 영향을 미치지 않는다.
  - 중복 호출은 무시된다. (최초의 호출만 유효하다.)
  - **새로운 Coroutine 호출은 무시된다.**
  - **새로운 Suspension(중지) 호출 시 Exception을 던진다.**
- CoroutineCooperative(협조적)이어야 한다.
    - cancel 함수는 단순히 Flag를 세우는 역할 (취소예정) 
    - **suspend(중단) 지점이 있어야 한다.**
    - 중단지점에서 외부의 신호를 받으면 (cancel) Coroutine을 종료한다.
    - **사용할 중단지점이 없다면 yield()를 사용하면 된다.**
      - yield()는 중단 후 바로 재개한다.
    - cancel() 이후에 실행되야 하는 작업이 있다면, cancelAndJoin()을 사용하면 된다.
      - cancelAndJoin()은 cancel()과 join()을 동시에 호출하는 함수이다.
      - cancel()이 될 때 가지 Caller를 suspend 시킨다.
    - suspend function은 결국 Coroutine의 Switching을 유발하기 떄문에, isActive를 사용하면 성능상으로 유리 할 수 있다.
- **직계 자식 Coroutine 또한 함꼐 종료된다.**

### 방법 1 - Exception
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking{
    val job = launch{
        try{
          repeat(1000) {
            i ->
            println("Time: $i")
            delay(500L)
          }   
        }catch (e: Exception){
            //CancellationException 발생
        }finally {
            //Coroutine 내부 사용 리소스 해제
        }
    }
  
  delay(1300L)
  println("Coroutine Cancellation")
  job.cancelAndJoin()
}

/**
 * Time 0
 * Time 1
 * Time 2
 * Coroutine Cancellation
 */
```
#### 동작 원리
- suspend 함수가 Exception을 던져준다.
- 이미 취소인데 suspend함수를 호출하려고 해서 발생한다.
- Coroutine 내부에서 발생하므로, Application 실행흐름에서는 확인 할 수 없다.
  - 부모 Coroutine으로 예외가 전파되지 않는다.
  - 부모가 자식 Coroutine을 Cancel시켰다는 정보를 이미 알고있기 때문이다.

### 방법2 - isActive
```kotlin
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        do {
            Thread.sleep(200)
            println("Printing")
        } while (isActive)
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}
// Printing
// Printing
// Printing
// Printing
// Printing
// Printing
// Cancelled successfully
```

#### 동작원리
- Exception을 던지지 않는다.
- isActive로 cancel여부를 판단하고 로직을 실행한다.
  - CorutineContext 내부의 상태값(isActive)을 통해서 Coroutine의 종료여부를 체크한다.
  - Canceling상태 일 때 부터 false를 리턴한다.


## 이미 Cancel상태가 된 Corutine에서 Suspend 다시 사용하기 & 취소가 불가능한 Coroutine 만들기
```kotlin
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(200)
            println("Coroutine finished")
        } finally {
            println("Finally")
            withContext(NonCancellable) {
                delay(1000L)
                println("Cleanup done")
            }
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}
// Finally
// Cleanup done
// Done
```
- 매우 특수한 경우이다.
- finally블록에서도 사용가능하다.
- withContext + NonCancellable Context를 통해서 사용 가능하다.
  - NonCancellable은 Cancel 불가능한 Job이다.
  - 무조건 해제가 되야하는 리소스를 처리할 떄 유용하다.

## 리소스 해제
- try-catch-finally를 사용하면 된다.
- Coroutine이 Cancel되면 CancellationException이 발생하기 떄문이다.


# Coroutine TimeOut

## [1] withTimeOut
- timeOut을 지정하고, 그 이전에 끝나지 않는다면 Exception이 발생한다.
- CancellationException과 다르게 Exception처리를 해주어야한다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    try {
        withTimeout(1000L) { // 1000 milliseconds = 1 second
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L) // delay half a second
            }
        }
    } catch (e: TimeoutCancellationException) {
        println("Job didn't complete within the time limit.")
    }
}
```


## [2] withTimeOutOrNull
- withTimeOut과 유사하지만 Exception을 던지지 않는다.
- TimeOut이 발생했다면 null을 리턴한다.
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val result = withTimeoutOrNull(1000L) {
        repeat(1000) { i ->
            println("Job: I'm sleeping $i ...")
            delay(500L)
        }
        "Done" // will get cancelled before it returns this result
    }

    println("Result is $result") // "Result is null"
}
```