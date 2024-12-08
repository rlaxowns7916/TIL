# Job
- CoroutineContext의 요소이다.
- Coroutine의 생명주기를 관리하는데 사용된다.
    - 하나 혹은 여러개의 Coroutine을 제어 할 수 있다.
- CoroutineScope와 마찬가지로 Job또한 계층구조를 가지고 있다.
- 자식 Coroutine의 Exception으로 인한 실패는 부모에게 전파된다.
    - 역도 성립한다.

## Job의 종료
- Builder (launch, async, ...) 를 통해서 생성된 Job은 내부 실행이 완료된 후 자동으로 완료(complete) 된다.
- 생성자(Job())를 통해서 만들어진 Job은 명시적으로 종료하지 않으면 종료상태가 아니게 된다.

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
- 자식 Coroutine에서 예외를 전파받지않는 특수한 Job객체이다.
  - 예외를 전파받지 않기 떄문에, 자식에게 예외를 전달받아도 취소되지 않는다.
  - 자식 Coroutine에서 발생된 예외가 부모와 다른 자식 Coroutine에게 영향을 주지 않게 할 수 있다.
- context에 SuperVisorJob() 생성을 통해 인자를 넘겨주는 행위는 계층구조를 끊는 방식이다.
```kotlin
fun main() = runBlocking{
    val scope1 = CoroutineScope(Dispatchers.IO + SuperVisorJob() + exceptionHandler)

  /**
   * scope1의 코루틴(job1, job2)은 SupervisorJob을 부모로 가지며, runBlocking과의 구조적 동시성을 깨게 된다. 
   * 즉, runBlocking의 취소나 예외가 이 코루틴들에 영향을 주지 않으며, 반대로 이 코루틴들에서 발생한 예외도 runBlocking에 영향을 미치지 않는다.
    */
  val job1 = launch(scope1) {printRandom1()}
    val job2 = launch(scope1) {printRandom2()}
  
   joinAll(job1, job2)


  val scope2 = CoroutineScope(Dispatchers.IO + SuperVisorJob(parent = this.coroutineContext[Job]) + exceptionHandler)

  /**
   * scope2는 runBlocking의 Job을 부모로 설정하여, runBlocking과의 구조적 동시성을 유지하면서 SupervisorJob의 특성을 활용한다. 
   * 이를 통해 자식 코루틴 간 독립성을 보장하면서도 부모-자식 관계를 유지한다.
   */
  val job3 = launch(scope) {printRandom1()}
  val job4 = launch(scope) {printRandom2()}

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