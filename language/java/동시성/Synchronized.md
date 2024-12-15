# Synchronized
- Java의 동기화기법
  - JVM 레벨에서 동작한다.
  - Reentrant를 지원한다.
    - 동일한 스레드가 이미 획득한 Monitor Lock을 다시 요청하면, Deadlock 없이 계속 실행 가능
- Unfair Lock이다.
- 한번에 하나의 Thread만 임계영역에 접근 가능하게 한다.
- **객체의 Monitor Lock을 획득하여, 임계영역에 접근하는 방식**
  - 모든 객체들은 내부적으로 자신의 Lock을 가지고 있다. (Monitor Lock)
  - synchronized가 진입하기 위해서는 해당 Lock이 존재해야한다.
  - 해당 MonitorLock은 한번에 하나의 Thread밖에 점유 할 수 없다.
- **Lock을 획득하지 못하면, CPU상태가 BLOCKED가 된다.**
  - TIME_WAITING이 아닌 BLOCKED상태는, Lock을 획득하기 위해 무한정 대기하며, CPU Scheduling 대상에서 제외된다.
  - BLOCKED 상태에서는 Interrupt가 발생해도 아무런 반응이 없다.
- Lock을 획득하면 BLOCKED -> RUNNABLE 상태가 되며 코드를 실행한다.
- synchonized의 범위가 모두 끝나면 자동적으로 Lock이 해제된다.


## 단점
1. 무한정 대기: synchronized는 무한정 대기이다. (TIME_WAITING이 아닌 BLOCKED)
2. 공정성: Lock을 획득한 순서대로 실행되지 않는다. (공정성이 보장되지 않는다.)


## 적용범위

### [1] instance method
- method를 한번에 하나의 Thread만 실행 가능하다.
- 다른 Thread들은 BLOCKED 상태로 무한정 대기한다.
```java
public synchronized void synchronizedMethod() {
    // 동기화된 메서드
}
```

### [2] static method
- Class레벨로 동작한다.
  - Class객체에 있는 Monitor Lock을 획득한다.
- 모든 Instance에게 Lock이 공유된다.

```java
public static synchronized void staticSynchronizedMethod() {
    // 클래스 수준 동기화
}
```

### [3] code block
```java
synchronized(object) {
    // 동기화된 코드
}
```
- 특정 객체에 대한 동기화를 수행한다.
- this를 통해서도 자기자신에 대한 monitor lock을 획득 할 수 있다.