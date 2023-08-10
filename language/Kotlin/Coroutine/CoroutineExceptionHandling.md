# CoroutineExceptionHandling
- 일반적인 try-catch로는 ExceptionHandling이 불가능하다.
- Root Coroutine(부모 Coroutine)에 지정해야 한다.
  - 자식 Coroutine에서는 ExceptionHandler가 무시된다.

# [1] launch에서의 ExceptionHandling
- **RootCoroutine에 ExceptionHandler를 넣는 것이 좋다.**
- launch의 경우 Exception을 Propagate(전파) 한다.
```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    println("Caught exception: $exception")
}

fun main() = runBlocking {
    val parentJob = launch(handler) { // handler가 여기 위치합니다.
        // 자식 코루틴
        launch {
            println("Child coroutine starts")
            throw RuntimeException("Exception from child")
            println("Child coroutine ends")
        }
    }

    parentJob.join()
    println("Parent coroutine ends")
}
```

## [2] async에서의 ExceptionHandling
- 실제 Exception을 Catch하는 구간은, await()이 호출될 때 이다.
- async는 Exception을 Expose(노출) 한다.
  - async는 scope안에서 await()시에 ArithmeticException이 발생한다.
- **CoroutineExceptionHandler는 async에서 동작하지 않는다.**
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val deferred = async {
        println("Async task starts")
        throw ArithmeticException("Some math error")
        println("Async task ends") // 이 부분은 실행되지 않습니다.
    }

    try {
        deferred.await() // 여기서 예외가 발생합니다.
    } catch (e: ArithmeticException) {
        println("Caught exception: ${e.message}")
    }
}
```

## [3] runBlocking에서의 ExceptionHandling
- runBlocking에서 발생한 Exception은 Propagate(전파) 된다.
  - runBlocking 자체가 try-catch 블록에 들어가 있어야한다.
- CoroutineExceptionHandler가 동작하지 않는다.
```kotlin
fun main() {
    try {
        runBlocking {
            println("Inside runBlocking")
            throw RuntimeException("Exception inside runBlocking")
        }
    } catch (e: Exception) {
        println("Caught exception: ${e.message}")
    }
}

```

## [4] withContext에서의 ExceptionHandling
- withContext에서 발생한 Excpetion은 호출한 Caller에게로 전파된다.
  - withContext또한 try-catch로 묶어야 한다.
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    try {
        withContext(Dispatchers.IO) {
            println("Inside withContext on IO dispatcher")
            throw IOException("Exception inside withContext")
        }
    } catch (e: IOException) {
        println("Caught exception: ${e.message}")
    }
}
```