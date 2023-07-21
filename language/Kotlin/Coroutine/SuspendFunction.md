# SuspendFunction
- 함수앞에 suspend 키워드를 통해서 사용가능하다.
- 같은 Coroutine 내에서는 기본적으로 순차적으로 실행되며, Suspend가 걸린시점에서 실행 할 수 있는 다른 로직을 실행한다.
  - 실행할 다른 로직이 suspend 일 경우, launch(), async()를 통해서 병렬적으로 실행한다.
  - 실행할 다른 로직이 모두 suspend인데, 별도의 Corutine을 생성해주지 않는다면 Blocking과 같이 동작한다.

## 동기적으로 실행되는 Coroutine Suspend 함수
```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val time = measureTimeMillis {
        val num1 = getRandom1() 
        val num2 = getRandom2() 

        println(num1 + num2)
    }
    println(time) // 2초가량
}

suspend fun getRandom1(): Int{
    delay(1000L)
    return Random.nextInt(0,500)
}

suspend fun getRandom2(): Int{
    delay(1000L)
    return Random.nextInt(0,500)
}
```
- suspend가 걸린시점에서 실행할 다른로직도 suspend이다.
- Coroutine의 기본적인 프로그래밍 구조는 순차적이다.
- 별도의 Corutine을 생성 하지 않을경우 suspend함수를 병렬적으로 실행시킬 수 없다.


## 병렬적으로 실행되는 Coroutine Suspend 함수
```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val time = measureTimeMillis {
        val deferred1 = async { getRandom1() }
        val deferred2 = async { getRandom2() }
        
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
- async builder를 통해서 별도의 CoroutineScope를 생성한다.
  - 결과를 받기위한 async인 것이지, launch를 통한 별도의 CoroutineScope 또한 병렬실행을 가능하게한다.
- 별도의 CoroutineScope를 가지는 Suspend함수는 병렬적으로 실행된다.