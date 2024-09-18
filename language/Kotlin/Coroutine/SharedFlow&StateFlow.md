# SharedFlow & StateFlow

## [1] SharedFlow
```kotlin
fun main() = runBlocking{
    val mutableSharedFlow = MutableSharedFlow<String>()
    launch{
        mutableSharedFlow.collect{
            println("Coroutine1 received $it" )
        }
    }
    
    launch{
        mutableSharedFlow.collect{
            println("Coroutine2 received $it" )
        }
    }
    
    mutableSharedFlow.emit("Message1")
    mutableSharedFlow.emit("Message2")

    /**
     * Coroutine1 received Message1
     * Coroutine2 received Message1
     * Coroutine1 received Message2
     * Coroutine2 received Message2
     */
}
```
- HotStream이다.
  - Subscriber가 있든 없든 데이터를 Emit한다.
  - 이미 방출된 데이터는 Subscribe 할 수 없다. (Buffer, Replay 설정이 있다면 받을 수 있음)
- SharedFlow를 통해서 데이터를 내보내면, 대기중이던 수신 Coroutine 들이 수신하게 된다.
  - 일종의 BroadCasting이 가능하다.
  - 하나의 Stream을 공유한다.
- BackPressure를 제공하지 않는다.
  - Buffer와 Replay를 통해서 일정부분 달성 할 수는 있다.
- Flow와 다르게 하나의 Stream을 여러 Subscriber가 공유한다.

## [2] StateFlow
```kotlin
// MutableStateFlow 선언
val stateFlow = MutableStateFlow(0)

// 구독자가 상태를 수집
launch {
    stateFlow.collect { value ->
        println("Collected state: $value")
    }
}

// 상태 업데이트
stateFlow.value = 1
stateFlow.value = 2

/**
 * Collected state: 0 (구독 시작 시, 최신상태를 받아옴)
 * Collected state: 1
 * Collected state: 2
 */
```
- HotStream 이다.
- StateFlow는 SharedFlow와 다르게 상태를 가지고 있다. (항상 하나의 상태를 갖는다.)
  - 여러 Subscriber의 상태 동기화에 유용하다. 
  - 상태를 가지고 있기 때문에, 최신 상태를 받아온다.
  - StateFlow는 MutableStateFlow를 통해서 상태를 업데이트 할 수 있다.
- N개의 Subscriber를 지원한다.