# Structured Concurrency
- Coroutine은 구조화된 동시성을 제공한다.
  - 자식 Coroutine이 모두 종료될 때까지 부모 Coroutine이 종료되지 않는다.
- Coroutine 코드 내에서, Exception이 유실되지 않고 적절하게 전파될 수 있도록 보장한다. (async, launch 제외)
- 여러 Coroutine들이 일관된 상태와 생명주기를 가지게 한다.

## 부모의 취소
- 부모의 취소는 자식 Coroutine에게 전파된다.
- 상위 레벨의 Coroutine 이 취소되면 관련된 모든 하위 레벨의 작업도 중단됨을 보장한다.

## 자식의 취소
- CancelledException이 발생하면, 자식 Coroutine은 취소된다.
  - 이때, 부모 Coroutine에게 전파되지 않으며, 다른 자식들에게도 영향을 미치지 못한다.
- 자식 Coroutine에서 Exception이 발생하면 , 부모 Coroutine에게 전파되고, 다른 자식 Coroutine에게도 전파된다.


## Coroutine LifeCycle
![CoroutineLifeCycle](https://github.com/ktj1997/TIL/assets/57896918/dc8f3357-83a7-473d-9ff3-219a0211a057)

- Completing이 있는 이유는, 자식 Coroutine의 결과를 기다리기 위함이다.  
  - 자식 Coroutine도 모두 완료가 된다면 Completed, 아니라면 Cancelling이 된다.