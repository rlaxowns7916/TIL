# Channel
- Coroutine간 데이터를 전달하는 방법이다.
  - 한쪽에서 Send하고, 반대편에서 Receive 한다.
  - send, receive 모두 Suspend Point이다.
- 일종의 Pipe 이다.
  - 항상 대기상태가 아니다.
  - 실제 Call이 발생 했을 떄 동작한다.

## Channel의 사용 이유
1. PubSub 패턴
2. BackPressure
3. 순서보장
4. Coroutine간의 동기화

## 위험한 코드
```kotlin
fun main() = runBlocking{
    val channel = Channel<Int>()
    launch{
        for (x in 1..10){
            channel.send(x) // Data를 보낼 때, Receive측이 없다면 보내지 않는다. (Sleep)
        }
    }
    
    repeat(10){
        println(channel.receive()) // Data를 보낼 때, Send측이 없다면 받지 않는다. (Sleep)
    }
}
```
- 무한루프가 발생한다.
- send와 receive는 상호의존적이다.
  - send는 최초에 receive가 없으므로 Blocking된다.
  - Blocking된 Coroutine이기 떄문에, receive가 실행될 기회가 없다.
  - 그렇기 떄문에 재게될 기회가 없어서 무한 루프에 걸리게 된다.
- 너무 오래걸리면 Exception이 발생한다.
  - Evaluation stopped while it's taking too long

## 올바른 코드
```kotlin
fun main() = runBlocking{
    val channel = Channel<Int>()
    launch{
        for (x in 1..10){
            channel.send(x) // Data를 보낼 때, Receive측이 없다면 보내지 않는다. (Sleep)
        }
    }
    
    launch{
      repeat(10){
        println(channel.receive()) // Data를 보낼 때, Send측이 없다면 받지 않는다. (Sleep)
      } 
    }   
}
```
- Channel을 사용할 떄는 Coroutine을 분리해야 한다.

## Channel Close
- 더 이상 보낼 것이 없으면 Channel을 닫아주어야 한다.
- close()를 통해서 channel을 닫아 줄 수 있다.
- close()는 Sender의 관점에서 처리한다.
  - send할 데이터가 모두 처리됐으면 close()한다.
```kotlin
fun main() = runBlocking<Unit>{
    val channel = Channel<Int>()
  launch{
      for (x in 1..10){
          channel.send(x)
      }
      channel.close() // 모두 send하고 close 한다.
  }
  
  for (x in channel){ // 이미 close된 channel이라면, for-in 문을 통해서 접근 가능하다.
      println(x)
  }
}
```