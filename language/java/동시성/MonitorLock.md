# MonitorLock
- Java의 동기화 기법 중 하나.
- **자바 객체에 내장된 Lock 이다.**
  - Object 객체에 구현이 되어있다.
- synchronized block을 통해서 사용한다. (Lock Acquire & Release)


## [1] wait
- synchronized 키워드가 붙은 메소드, 혹은 Block에서 사용되어야한다.
  - 호출한 Thread가 MonitorLock을 소유하고 있어야한다.
- 현재 Thread가 가진 Lock을 반납하고 WAITING 한다.
  - Thread (RUNNABLE -> WAITING)
- Lock을 반납한 Thread는 WAITING 상태로 전환된다.
- Lock을 가지고 임계영역에서 무한정 대기하는 문제를 해결 할 수 있다.

## [2] notify
- synchronized 키워드가 붙은 메소드, 혹은 Block에서 사용되어야한다.
- 대기중인 Thread를 깨운다.
  - **대기중인 Thread가 여러개라면 그 중 하나만 랜덤하게 깨우게 된다.**
  - Thread (WAITING -> RUNNABLE)

## [3] notifyAll
- synchronized 키워드가 붙은 메소드, 혹은 Block에서 사용되어야한다.
- 대기중인 Thread를 모두 깨운다.
  - 불필요한 깨움이 발생할 수 있다. 