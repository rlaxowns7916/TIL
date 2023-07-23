# Flow
- Kotlin에서 사용할 수 있는 순차적 + 비동기 Stream
  - StreamAPI는 동기로 동작한다.
- emit을 통해서 Stream에 Data를 흘려보낸다.
- Coroutine내부에서만 호출되어야 한다.

## Flow의 특징
1. 비동기적으로 데이터를 생성한다.
2. Flow 연산자 제공
3. Cold Stream
   - 종결연산이 선언됐을 떄만 시작한다.
4. 취소 가능 (Coroutine이 취소되면 같이 취소된다.)

```kotlin
fun flowSomething() : Flow<Int> = flow{
    repeat{
        emit(Random.nextInt(0,500))
        delay(10L)
    }
}

fun main() = runBlocking {
    flowSomething().collect {value -> println(value)}
}
```

## Flow Builder
- builder를 통해서 Flow를 만들어낼 수 있다.
```kotlin
//flow

flow{
    emit(1)
    emit(2)
    emit(3)
    emit(4)
    emit(5)
}.collect{println(it)}

//flowOf
flowOf(1,2,3,4,5).collect{ println(it) }

//asFlow
listOf(1,2,3,4,5).asFlow().collect{ println(it) }

```

## Flow Operator
- Flow Stream 내부에서 연산을 시켜주는 연산자들이다.

### [1] map
- 가공연산

### [2] filter
- 필터링 연산

### [3] transform
- Stream 변형 연산
- 기존 중간연산(map,filter) 들은 1:1이지만, transform은 1:N이 결과가 될 수 있다.
```kotlin
fun main() = runBlocking{
    (1..20).asFlow().transform{
        emit(it)
        emit(it * 2)
    }.collect{
        println(it)
    } // 1 2 2 4 ...20 40
}
```

### [4] zip
- 여러개의 flow를 하나의 flow로 합쳐주는 연산이다.
```kotlin
fun main() = runBlocking {
    val nums = (1..3).asFlow()
    val strs = flowOf("a","b","c")
    
    nums.zip(strs) {a,b -> "num:${a} str: ${b}"}.collect{println(it)}
}
```

### [5] take
- Stream에서 몇개만 뽑는다.
- 앞에서부터 순차적으로 가져온다.
```kotlin
fun main() = runBlocking{
    (1..20).asFlow().transform{
        emit(it)
        emit(it * 2)
    }.take(5)
     .collect{
        println(it)
    } // 1 2 2 4 3
}
```

### [6] drop
- Stream에서 몇개만 버린다.
- 앞에서부터 순차적으로 버린다.
```kotlin
fun main() = runBlocking{
    (1..20).asFlow().transform{
        emit(it)
        emit(it * 2)
    }.drop(5)
     .collect{
        println(it)
    } // 6 4 8 ...
}
```

### [7] reduce
- 모든 값을 누적하여 최종연산을 하는 것이다.
```kotlin
fun main() = runBlocking{
    (1..20).asFlow().transform{
        emit(it)
        emit(it * 2)
    }.drop(5)
     .reduce{a,b ->
        a+b
    } 
}
```

## Flow Context
- Flow는 현재 진행되는 Coroutine Context에서 호출된다.
- Flow내에서는 Context를 변경 할 수 없다.

### [1] 일반적인 Flow
```kotlin
fun simple(): Flow<Int> = flow{
    for(i in 1..10){
        emit(i)
    }
}

fun main() = runBlocking{
    /**
     * IO Dispatchers가 실행한 Coroutine에서 Flow 연산을 수행한다.
     */
    launch(Dispatchers.IO){
        simple()
                .collect{ println (it) }
    }
}

```

### [2] Flow내에서의 Coroutine Context 전환
- Flow내에서는 CoroutineBuilder를 통한 Context 전환을 할 수 없다.
  - IllegalArguementException이 발생한다. (Flow invariant is violated)
- **flowOn 연산자를 통해서 전환 가능하다.**
```kotlin
/**
 * 불가능 한 경우
 */
fun simple(): Flow<Int> = flow{
    withContext(Dispatchers.Default){ //Context 변경 불가
        for(i in 1..10){
            delay(100L)
            emit(i)
        }
    }
}

fun main() = runBlocking<Unit>{
    launch(Dispatchers.IO){
        simple()
                .collect{ println(it) }
    }
}

/**
 * 가능한 경우
 */
fun simple(): Flow<Int> = flow{
    for(i in 1..10){
        delay(100L)
        emit(i)
    }
}.flowOn(Dispatchers.Default) // flowOn을 통한 Coroutine Context 변경

fun main() = runBlocking<Unit>{
    launch(Dispatchers.IO){
        simple()
                .collect{ println(it) }
    }
}
```

## Flow BackPressure
- BackPressure(백프레셔) 문제를 다루는 방식이다.
  - 수신측이 바쁘면, 송신측도 Blocking이 걸리게 된다.
  - 송신측의 publish속도가, 수신측에 subscribe 속도보다 빠를 때 발생한다.


### [1] Buffer
- Buffer를 추가하여, 수신자를 대기하지 않고 바로바로 보낼 수 있게 한다.
```kotlin
fun main() = runBlocking<Unit>{
    launch(Dispatchers.IO){
        simple().buffer()
                .collect{ 
                    delay(500L)
                    println(it) 
                }
    }
}
```

### [2] Conflate
- 생산자의 속도가 소비자의 속도보다 빠를 때, 아이템들을 모두 누락시킨다.
- 소비자가 처리할 수 있을 때만 처리하는 것이다.
- 최신의 데이터만 유효 할 때 (중간에 있는 값들은 무시해도 될 때) 유용하다.
```kotlin
fun main() = runBlocking<Unit>{
    launch(Dispatchers.IO){
        simple().conflate()
                .collect{
                    delay(500L)
                    println(it)
                }
    }
}
```

## Flow 예외처리
- collect 측에서 try-catch 하는 것도 가능하다.
- .catch()하는 것이 더욱 추천되는데, flow측에서 발생 할 수 있는 에러를 명확하게 처리 가능하기 때문이다.
  - catch()에서는 새로운 emit을 하거나, 다른 Exception을 던질 수도 있다.
  - catch()는 UpstreamFlow에만 영향을 미친다. (catch 하위에 있는 Flow의 Exception은 catch하지 못한다.)
```kotlin
fun simple(): Flow<Int> = flow{
    for(i in 1..10){
        delay(100L)
        emit(i)
    }
}.catch{e -> println(e)}

```

## Flow 종결처리
- collect하는 측에서, finally를 통한 종결처리도 가능하다.
- onCompletion() 블록을 제공한다.
  - 예외 여부를 알 수 있게 파라미터를 제공한다.
```kotlin
fun simple(): Flow<Int> = flow{
    for(i in 1..10){
        delay(100L)
        emit(i)
    }
}.onCompletion{ cause -> if (cause != null) println("Exception!")  else println("Complete")  }
```
