# ReentrantLock
- Java에서 제공하는 Lock이다.
- 하나의 Thread만 Lock을 잡을 수 있다.
  - lock()과 unlock()메소드를 제공한다.
  - fairLock()과 unfairLock()을 제공하여 starvation에 대응하게 해준다.
    - fair: 들어온 순서대로의 실행을 보장한다.
    - unfair: 우수한 성능을 보이나, 공정성이 보장되지 않는다.
- Lock을 획득하지 못했다면 Blocking이 되며, Sleep & Wake 방식이다.

## Reentrant
```text
하나의 Thread가 이미 Lock을 소유한 상태에서, 다시 Lock을 요청하면
다른 Thread가 Lock을 요청한 것 과 똑같이 동작하며, Critical Session에 접근하게 해준다.

Lock을 얻은 후 다시 Lock을 요청하면 Lock을 잃지 않고 그대로 사용가능하다.
lock()을 통해서 Lock의 소유권을 얻지만 이미 소유권을 가지고있기 떄문에 바로 반환된다.
(이를 통해 synchronized 보다 더 좋은 성능을 가질 수 있다.)
```

## 샘플 코드
```java
import java.util.concurrent.locks.ReentrantLock;

public class Example {
  private ReentrantLock lock = new ReentrantLock();

  public void doSomething() {
    boolean isLocked = lock.tryLock(); // lock을 시도하고, 얻을 수 있는지 여부를 반환
    if (isLocked) {
      try {
        // lock을 얻은 경우, 임계 구역(critical section)을 실행
      } finally {
        lock.unlock(); // lock을 해제
      }
    } else {
      // lock을 얻지 못한 경우, 다른 작업을 수행
    }
  }
}
```


## Vs Synchronized
1. 락 미 획득시 로직 정의 가능 (synchronized는 획득 하기 전까지 Blocking) 
2. 공정성의 보장 (synchronized는 보장되지 않는다.)
3. Lock의 해제 (ReentrantLock은 Lock의 소유자만 unLock이 가능하나, Synchronized는 아니어도 가능하다.)
4.