# CoroutineScope
- Coroutine이 실행되는 범위를 의미한다.
- Interface로 구현되어있다.
  - **내부적으로 CoroutineContext를 감싸고 있는 Wrapper이다.**
- 각각의 CoroutineScope는 별개이며, 순서에 영향을 미치지 않는다.
- 같은 CoroutineScope에서는 순차적으로 실행된다.


## Global Scope
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