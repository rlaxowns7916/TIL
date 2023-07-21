# CoroutineScope
- Coroutine이 실행되는 범위를 의미한다.
  - Coroutine Scope가 아닌 곳에서 Corutine을 사용할 경우 컴파일 에러가 발생한다.
- Interface로 구현되어있다.
  - **내부적으로 CoroutineContext를 감싸고 있는 Wrapper이다.**
- 함수도 존재한다.
  - 내부적으로 ContextCoroutine을 리턴한다.
  - ```kotlin
     public fun CoroutineScope(context: CoroutineContext): CoroutineScope =
       ContextScope(if (context[Job] != null) context else context + Job())
    
      fun main(){
        val scope = CoroutineScope(Dispatchers.Default)
        val job = scope.launch(Dispatchers.IO){
            launch { println()}
        }
      }
    ```
- 각각의 CoroutineScope는 별개이며, 순서에 영향을 미치지 않는다.
- 같은 CoroutineScope에서는 순차적으로 실행된다.


## Global Scope
- 원래부터 존재하는 전역적인 Scope이다.
- CorutineScope의 구현체이다.
- Application이 시작하고 종료할 때 까지 계속 유지된다.
    - GlobalScope Corutine의 실행이 Process를 지속시키는 것은 아니다. (역은 성립)
    - Singleton이다.
    - 전역에서 접근이 가능하기 때문에 사용이 간편하다.
- Memory관리에 유의해야 한다.
    - 어플리케이션이 종료되지않으면 Coroutine이 계속해서 실행되기 때문이다.
    - StructuredConcurrency를 따르지 않는다.
        - 부모-자식 관계의 Scope를 따르지 않고 Application의 생명주기에 Binding되기 떄문이다.
```kotlin
fun main(){
    GlobalScope.launch{
        fetchData()
        updateView()
    }
}
```

## Supervisor Scope
- 자신의 자손 Coroutine에게만 Exception이 전파된다.
- Exception의 전파가 부모, 형제에게는 발생하지 않는다.
```kotlin
suspend fun supervisoredFunc(exceptionHandler: CoroutineExceptioHnadler) = supervisorScope {
    launch { printRandom1() }
    launch(exceptionHandler) { 
        throw RuntimeException("Exception Occurred In SupervisorScope")
    }

  /**
   * Exception이 발생하는 곳에 기본적으로 
   * 1. CoroutineExceptionHandler를 Context인자에 넣어주거나
   * 2. try-catch 
   * 를 사용하여 ExceptionHandling을 해야 한다.
   * 안그러면 Exception이 외부로 전파되기 때문이다.
   * 
   */
}
```

### 생성하는 방법
- 아래 두가지 방법을 제외하면 모두 일반 Scope를 가진다.
```kotlin
supervisorScope {
    launch { // 일반 Job
        // ...
    }

    supervisorScope { // SupervisorJob
        // ...
    }
}
```

#### [1] superVisorScope
- superVisorScope 함수를 통한 생성
- 계층구조를 유지하는 방법이다.
```kotlin
supervisorScope {
    launch { printRandom1() }
    launch(exceptionHandler) {
        throw RuntimeException("Exception Occurred In SupervisorScope")
    }
}
```

#### [2] superVisorJob
- coroutineScope를 통한 scope 생성 시, SuperVisorJob을 Context로 넘김
- 새로운 SuperVisorJob()을 인자로 넘겼기 떄문에, 계층구조 (부모 - 자식)이 끊기게 되고 생명주기도 독립적으로 가져가게 된다.
```kotlin
val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
```