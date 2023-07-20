# Coroutine
- 비동기 코드를 쉽게 작성할 수 있게 한다.
  - 비동기코드를 순차적으로 실행될 수 있게 도와준다.
- I/O, Network 요청등 시간이 오래걸리는 작업에 유용하다.
- 경량화된 동시성 (Concurrency) 기법
  - Thread와 유사하지만, Thread보다 경량화 되어있다. (light-weighted Thread)
  - **운영체제의 개입 없이, 소프트웨어 레벨로 동작한다.**
  - **Thread를 사용하지않는다.**
- 가독성이 좋다.
  - callback방법이나, Promise보다 훨씬 경량화 되어있다.
- kotlinx.coroutines 라이브러리를 사용해 Coroutine을 구현할 수 있다.
  - [Git](https://github.com/Kotlin/kotlinx.coroutines)
- 프로세스가 종료되면 Coroutine의 생명주기가 살아있더라도 종료된다. 

## 원리
- Thread 하나로 동작한다.
- Coroutine 실행 도중, suspend(정지)가 발생하고  Stack, Register 정보가 메모리에 저장된다.
- 다시 resume(재개)가 될 경우 메모리를 참조하여 루틴을 시작한다. 
- Suspend(중지)가 된 시점에, 다른 로직을 실행한다.

## Coroutine 내에서 Thread.sleep
- Thread.sleep()은 Thread를 Blocking하는 것이기 때문에, 사용해서는 안된다.
- **suspend 함수인 delay()를 사용하는 것이 적절하다.**
  - delay는 Blocking이 아니라 Suspend 하면서 다른 로직에게 순서를 양보한다.
  - delay는 Coroutine내부에서의 대기일 뿐, Coroutine 외부의 순서에 관여하지 않는다.
