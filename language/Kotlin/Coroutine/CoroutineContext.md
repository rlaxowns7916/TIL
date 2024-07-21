# CoroutineContext
- Map + Set을 합쳐놓은 형태
- **Coroutine이 실행되는 환경을 의미한다.**
  - MetaData를 저장하거나, 자식 Coroutine에 넘길 정보를 저장하는 저장소의 용도로 볼 수 있다.
  - 임의의 값이 아닌 CoroutineContext.Element 인터페이스를 구현하는 값만 들어갈 수 있다.
- Job,Dispatcher와 CoroutineExceptionHandler,ThreadLocl 모두 환경의 일부이다.
- Interface가 존재한다.
  - Map과 같은 형식으로, get을 통해서 CoroutineContext를 가져오거나 없다면 null을 반환한다.
  - plus()를 통해서 Context를 합칠 수 있다.

## CoroutineContext의 전파
- 부모 Coroutine의 CoroutineContext는 자식Coroutine으로 전파된다.
  - 자식 Coroutine을 새로운 CoroutineContext를 지정하면서 생성하면, 부모 Context를 오버라이드하여 생성된다.
- suspend 함수 호출 시, Caller의 Context가 Callee 에게로 전파된다.
- withContext를 사용 시, 해당 Scope에서 별도의 CoroutineContext의 사용이 가능해진다.

## CoroutineContext의 구성요소
1. Job
2. Name
3. Dispatcher
4. CoroutineExceptionHandler
5. ContinuationInterceptor

### 접근하는 방법
1. CoroutineScope 내부
```text
runBlocking, launch, async와 같은 CoroutineScope 내부에 있다면,

CoroutineScope.coroutineContext로 접근 가능하다.
Scope내에서는 CoroutineScope가 this로 접근이 가능하므로, this.coroutineContext로 접근 가능하다.
```

2. Continuation
```text
Continuation.coroutineContext로 접근이 가능하다.
```

3. suspend 함수 내부
```text
suspend함수 내부에서, coroutineContext접근이 가능하다.
```
***

## Element
- CoroutineContext의 요소이다.
  - CoroutineDispatcher
  - Job
  - CoroutineName
  - CoroutineExceptionHandler
  - ThreadLocl
  - ... Custom Coroutine Context Element
- coroutineContext[Element이름] 으로 조회 가능하다.
  - \+ 연산으로 Elemet 끼리의 결합이 가능하다.
    - 새로운 CoroutineContext를 생성한다.
    - 이미 같은 Key값을 갖고있는 Elemnt가 있다면 Override한다.
    - 리턴 값은 더해진 이후의 CoroutineContext
  - minusKey 연산을 제공한다.
    - 해당 Key값의 Element를 coroutineContext에서 제외한다.
  - \- 연산은 Element를 제거한다.
  - 리턴 값은 은 - 연산이 진행된 후의 결과이다.
- CoroutineContext Interface의 구현체이다.


```kotlin
import kotlin.concurrent.thread
import kotlinx.coroutines.*

fun main() = runBlocking {
  /**
   * launch가 CoroutineContext를 인자로 받고
   * plus 연산을 통한 Element의 결합으로 새로운 Coroutine Context를 생성한다.
   * 
   *  CoroutineDispatcher, CoroutineName은 Element를 검색하는 Key값이다.
   */
  val threadLocal = ThreadLocal<String>()
  threadLocal.set("hello")
  val job1 = launch {
    launch(Dispatchers.IO + CoroutineName("launch1") + threadLocal.asContextElement()) {
      println(coroutineContext)
      println(coroutineContext[CoroutineName])
    }
  }

  val job2 = launch(Dispatchers.Default + CoroutineName("launch2")) {
    println(coroutineContext[CoroutineDispatcher])
    println(coroutineContext[CoroutineName])
  }

  job1.join()
  job2.join()
}
/**
 * Dispatchers.Default
 * CoroutineName(launch2)
 * [CoroutineName(launch1), ThreadLocal(value=hello, threadLocal = java.lang.ThreadLocal@1f763139), CoroutineId(4), "launch1#4":StandaloneCoroutine{Active}@504ad22b, Dispatchers.IO]
 * CoroutineName(launch1)
 */
```

### CoroutineContext에서 ThreadLocal 사용하기
- Coroutine은 Dispatcher에 따라서, 별도의 Thrad에서 동작 가능하다. (예측 할 수 없다.)
- TheadLocalElement를 사용한다면, ThreadLocal의 정보를 전파시킬 수 있다.
  - Element를 구현했다. (ThreadLocalElement -> ThreadContextElement -> Element)
  - **ThreadLocal에서, asContextElement를 통해서 생성 가능하다.**
  - ThreadContextElement는 조금 더 상위 추상화 인턴페이스이다.
- Log의 TraceId(MDC에 넣어져있음)은 Coroutine에서 아래와 같이 전파된다.
  - MDCContext -> ThreadContextElement<MdcContextMap>
  - MDCContext(kotlinx.coroutines.slf4j)는 CoroutineContext가 전파 될 때마다, MDC를 Restore한다.
  - slf4j는 MDC에 저장되어있는 traceId, spanId를 통해서 logging하기 때문에, Coroutine이 여러번 suspend되어도 전파가 가능하다.
  - CoroutineContext 내부 -> restoreThreadContext -> MDCContext -> ThreadContextElement (MDC 값 Restore)

### Custom CoroutineContext 만들기

```kotlin

import kotlin.coroutines.AbstractCoroutineContextElement

class CustomContext(
      val property: String
) : AbstractCoroutineContextElement(CustomContext) {
  companion object Key : CoroutineContext.Key<CustomContext>
}


fun main() = runBlocking {
  launch(Dispatchers.Default + CustomContext("Starting coroutine!")) {
    val customContext = coroutineContext[CustomContext] as CustomContext
    println(customContext.property)
  }
}
```