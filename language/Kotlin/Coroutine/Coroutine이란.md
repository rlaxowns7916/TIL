# Coroutine
- 비동기 코드를 쉽게 작성(동기 방식으로) 할 수 있게 한다.
  - 비동기코드를 순차적으로 실행될 수 있게 도와준다.
- I/O, Network 요청등 시간이 오래걸리는 작업에 유용하다.
- 경량화된 동시성 (Concurrency) 기법
  - Thread와 유사하지만, Thread보다 경량화 되어있다. (light-weighted Thread)
  - **운영체제의 개입 없이, 소프트웨어 레벨로 동작한다.**
  - 동일 Thread에서 같은 Coroutine이 실행된다면, ContextSwitching이 불필요하다.
- 가독성이 좋다.
  - callback방법이나, Promise보다 훨씬 경량화 되어있다.
- 라이브러리로 존재한다.
  - kotlinx.coroutines 라이브러리를 사용해 Coroutine을 구현할 수 있다.
  - kotlinx.serialization 라이브러리를 사용해 객체의 Serialization, Deserialization을 구현할 수 있다.
  - kotlinx.lincheck 라이브러리를 통해 동시성 버그를 탐지하고, 디버깅 할 수 있는 환경을 제공한다.
- 프로세스가 종료되면 Coroutine의 생명주기가 살아있더라도 종료된다. 

## 원리
- CPS(Continuation Passing Style) 이다.
  - 내부적으로 Continuation을 전달하여 로직의 끝날 때마다, Callback 처럼 사용한다.
- 특정 Thread에 종속되어있지 않다.
  -  중단지점(suspend)를 경계로 유휴 Thread가 실행가능한 Coroutine을 그때 그때 실행한다.
- Coroutine 실행 도중, suspend(정지)가 발생하고  Stack, Register 정보가 메모리에 저장된다.
- 다시 resume(재개)가 될 경우 메모리를 참조하여 루틴을 시작한다. 
- Suspend(중지)가 된 시점에, 다른 로직을 실행한다.

## Coroutine 내에서 Thread.sleep
- Thread.sleep()은 Thread를 Blocking하는 것이기 때문에, 사용해서는 안된다.
- **suspend 함수인 delay()를 사용하는 것이 적절하다.**
  - delay는 Blocking이 아니라 Suspend 하면서 다른 로직에게 순서를 양보한다.
  - delay는 Coroutine내부에서의 대기일 뿐, Coroutine 외부의 순서에 관여하지 않는다.

## 동작원리

### FSM (Finite State Machine)
- StateMachine은 시스템이 가질 수 있는 상태를 표현하는 모델이다.
  - State: 시스템의 특정한 상황
  - Transition: 하나의 State가 다른 State로 이동
  - Event: Transition을 트리거 하는 사건
- FinateStateMachine은 유한한 상태를 가지는 머신이다.
  - 한번에 오직 하나의 State를 가질 수 있다.
  - Event를 통해서 하나의 State에서 다른 하나의 State로 Transition이 가능하다.

```kotlin
/**
 * 재귀 함수를 통한 FSM의 구현
 * State를 가지고 있기 때문에, 무한 Loop가 발생하지 않는다.
 */

private val log = KLogger()

class FsmExample {
  fun execute(label: Int = 0) {
    var nextLabel: Int? = null

    when(label) {
      0 -> {
        log.info("Initial")
        nextLabel = 1
      }
      1 -> {
        log.info("State1")
        nextLabel = 2
      }
      2 -> {
        log.info("State2")
        nextLabel = 3
      }
      3 -> {
        log.info("End")
      }

      //transition
      if (nextLabel != null) {
        this.execute(nextLabel)
      }
    }
  }
}
```

### Continuation PassingStyle
- 함수형 프로그래밍에서 따왔다.
- Caller가 Calle를 호출하는 상황에서, Callee는 값을 계산하여 continuation을 실행하고, 인자로 값을 전달
- continuation은 callee가장 마지막에서 딱 한번 실행

```kotlin
private val log = KLogger()
object CpsCalculator {
    fun calculate(initialValue: Int, continuation: (Int) -> Unit){
        initialize(initialValue) { initial ->
            addOne(initial){ added ->
                multiplyTwo(added) { multiplied ->
                    continuation(multiplied)
                }
            }
        }
    }
  
    private fun initialize(value:Int, continuation (Int) -> Unit){
        log.info("Initial")
        continuation(value)
    }
  
    private fun addOne(value:Int, continuation: (Int) -> Unit){
        log.info("Add One")
      continuation(value+1)
    }
    
    private fun multiplyTwo(value:Int, continuation: (Int) -> Unit){
        log.info("Multiply two")
        continuation(value * 2)
    }
}
```

#### Continuation vs Callback
- Continuation
  - 다음에 무엇을 해야하는지 정의한다.
  - 모든 결과를 계산하고, 다음으로 넘어가는 상황에 호출한다.
  - 마지막에 딱 한번 호출하고, 제어권을 넘긴다.
- Callback
  - 추가로 무엇을 해야하는지 정의한다.
  - 특정 이벤튼가 발생했을 때 호출한다.
  - 함수 호출의 마지막이 아닌, 어디에서나 호출이 가능하다.

#### Continuation Interface
```kotlin
package kotlin.coroutines

public interface Continuation<in T>{
    public val context: CoroutineContext
    public fun resumeWith(result: Result<T>)
}
```
- 내부적으로 CoroutineContext를 저장할 수 있다.
  - 모든 suspend함수에 전달된다.
  - 저장되있는 CoroutineContext 또한 함께 전달되어 Coroutine간의 정보를 전파 할 수 있다.
- resumeWith를 구현하여, 외부에서 continuation을 실행할 수 있는 EndPoint를 제공한다.