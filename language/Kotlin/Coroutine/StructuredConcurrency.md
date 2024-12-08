# Structured Concurrency
- Coroutine은 구조화된 동시성을 제공한다. (비동기작업을 보다 안정적이고 예측가능하게 구성)
  - 자식 Coroutine이 모두 종료될 때까지 부모 Coroutine이 종료되지 않는다.
- Coroutine 코드 내에서, Exception이 유실되지 않고 적절하게 전파될 수 있도록 보장한다. (async, launch 제외)
- 여러 Coroutine들이 일관된 상태와 생명주기를 가지게 한다.

## [1] 상속
- 부모의 CoroutineContext는 자식에게로 전파된다.
  - 부모 Coroutine에서 새로운 Coroutine을 생성 (launch, async)하면, 부모의 CoroutineContext를 상속받는다.
- 자식 CoroutineContext가 있다면, 자식의 것으로 덮어씌워진다.
  - launch, async와 같은 경우는 새로운 Job을 구성하는데 부모의 Job을 이용한다. (서로의 Job을 참조로 가진다.)
- **이러한 상속 체계가 깨지게 된다면 (구조화가 깨진다면) 취소의 전파, 결과 대기와 같은 기능이 제대로 동작하지 않는다.**

## [2] 순서
- 부모 Coroutine은 자식 Coroutine의 실행에 의존적이다
  - **자식 Coroutine이 모두 완료되어야, 부모 Coroutine이 완료된다.**


## [3] 취소
- 코루틴이 취소되면 전파된다.

### 부모의 취소
- 부모의 취소는 자식 Coroutine에게 전파된다.
- 상위 레벨의 Coroutine 이 취소되면 관련된 모든 하위 레벨의 작업도 중단됨을 보장한다.

### 자식의 취소
- CancelledException이 발생하면, 자식 Coroutine은 취소된다.
  - 이때, 부모 Coroutine에게 전파되지 않으며, 다른 자식들에게도 영향을 미치지 못한다.
- 자식 Coroutine에서 Exception이 발생하면 , 부모 Coroutine에게 전파되고, 다른 자식 Coroutine에게도 전파된다.


## Coroutine LifeCycle
![CoroutineLifeCycle](https://github.com/ktj1997/TIL/assets/57896918/dc8f3357-83a7-473d-9ff3-219a0211a057)

- Completing이 있는 이유는, 자식 Coroutine의 결과를 기다리기 위함이다.  
  - 자식 Coroutine도 모두 완료가 된다면 Completed, 아니라면 Cancelling이 된다.