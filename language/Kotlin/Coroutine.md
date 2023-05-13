# Coroutine
- I/O, Network 요청등 시간이 오래걸리는 작업에 유용한다.
- 경량화된 동시성 (Concurrency) 기법
  - Thread와 유사하지만, Thread보다 경량화 되어있다. (light-weighted Thread)
  - **운영체제의 개입 없이, 소프트웨어 레벨로 동작한다.**
  - **Thread를 사용하지않는다.**
- 가독성이 좋다.
  - callback방법이나, Promise보다 훨씬 경량화 되어있다.
- JavaScript의 async-await과 유사하다.
- kotlinx.coroutines 라이브러리를 사용해 Coroutine을 구현할 수 있습니다.

## 원리
- Thread 하나로 동작한다.
- Coroutine 실행 도중, suspend(정지)가 발생하고  Stack, Register 정보가 메모리에 저장된다.
- 메모리에 저장 한 이후, 다른 Corutine을 실행한다.
  - 여러개의 Coroutine이 실행 될 수 있다.

## Scope
- GlobalScope - 프로그램 어디서나 제어, 동작이 가능한 기본 범위
- CoroutineScope - Dispatcher를 지정하여 제어 및 동작한 가능 범위
  - launch: Return 값이 없는 Job객체
  - async: Return 값이 있는 Deffered 객체

## Dispatchers
- Dispatchers.Default: 기본적인 백그라운드
- Dispatchers.IO: I/O에 특화
- Dispatchers.Main: Main Thread에서 동작

## Code
```kotlin
import kotlinx.coroutines.*

// runBlocking은 동기 코드에서, Corutine을 실행 가능하게 하는 Bridge 코드이다. //subRoutine이 끝날 때 까지 기다려준다.
fun main() = runBlocking {
   
    //launch를 통한 coroutine block
    launch{
       coroutineTask()
    }
    println("Starting Task")
}

//subroutine
suspend fun coroutineTask(){
    delay(1000L)
    println("Task Completed")
}
```
- suspend 키워드를 통해서 coroutine 함수를 정의한다.
- 다른 Coroutine 함수에서만 호출 가능하다.