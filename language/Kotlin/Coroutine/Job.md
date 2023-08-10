# Job
- CoroutineContext의 요소이다.
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

## SupervisorJob
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
***