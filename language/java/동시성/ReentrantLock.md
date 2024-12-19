# ReentrantLock
- java 1.5 > java.util.concurrent 부터 패키지가 추가되었다.
- Java에서 제공하는 Lock이다.
- 하나의 Thread만 Lock을 잡을 수 있다.
  - lock()과 unlock()메소드를 제공한다.
  - fairLock()과 unfairLock()을 제공하여 starvation에 대응하게 해준다.
    - fair: 들어온 순서대로의 실행을 보장한다.
    - unfair: 우수한 성능을 보이나, 공정성이 보장되지 않는다.
- Lock을 획득하지 못했다면 Blocking이 되며, Sleep & Wake 방식이다.
- **내부적으로 LockSupport를 사용한다.**
- 내부적으로 Queue(AbstractQueuedSynchronizer) 를 사용한다.
  - UnFairMode여도 사용된다,
  - Lock획득을 시도하는 사이에, 새로운 요청이 Lock을 가로 챌 수도 있다.
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
1. Lock 미 획득시 로직 정의 가능 (synchronized는 획득 하기 전까지 Blocking) 
2. FairMode 사용 시, 공정성의 보장 (synchronized는 보장되지 않는다.)
3. Lock의 해제 (ReentrantLock은 Lock의 소유자만 unLock이 가능하나, Synchronized는 아니어도 가능하다.)

## Condition
- ReentrantLock을 통해서 사용되며, 보다 세밀하게 Thread 동시성을 제어 가능하게 한다.
  - 일종의 Thread 대기열이다.
  - **하나의 ReentrantLock이 여러개의 Condition을 생성 할 수 있다.**
  - 특정 조건에 따른 Condition을 만들어서, 해당 조건에 맞는 Thread만 깨울 수 있다.
- **wait(), notify()가 불특정한 Thread를 깨우는 것과 달리, Condition은 특정 Thread를 깨울 수 있다.**

1. await()
- Condition을 만족하지 않으면, 현재 Thread를 대기 상태로 만든다.
- Lock을 반납하고, 대기 상태로 전환된다.

2. signal()
- 대기 중인 Thread를 깨운다.
- **대기 중인 Thread가 여러개라면 그 중 하나만 랜덤하게 깨우게 된다.**
- ReentrantLock을 획득한 Thread만 signal()을 호출 할 수 있다.

### 샘플 코드
```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockConditionExample {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int MAX_CAPACITY = 5;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();  // 큐가 비어있지 않음을 알리는 Condition
    private final Condition notFull = lock.newCondition();   // 큐가 꽉 차있지 않음을 알리는 Condition

    public static void main(String[] args) {
        ReentrantLockConditionExample example = new ReentrantLockConditionExample();

        Thread producer = new Thread(example.new Producer(), "Producer");
        Thread consumer = new Thread(example.new Consumer(), "Consumer");

        producer.start();
        consumer.start();
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            int value = 0;
            while (true) {
                lock.lock();
                try {
                    // 큐가 꽉 찼다면 생산자 대기
                    while (queue.size() == MAX_CAPACITY) {
                        System.out.println(Thread.currentThread().getName() + " 대기: 큐가 가득 참");
                        notFull.await();
                    }
                    
                    // 아이템 생산 후 큐에 추가
                    queue.offer(value);
                    System.out.println(Thread.currentThread().getName() + " 생산: " + value);
                    value++;

                    // 아이템을 넣었으니 소비자에게 알림
                    notEmpty.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                // 예제를 위해 잠시 대기
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    // 큐가 비어있다면 소비자 대기
                    while (queue.isEmpty()) {
                        System.out.println(Thread.currentThread().getName() + " 대기: 큐가 비었음");
                        notEmpty.await();
                    }
                    
                    // 큐에서 아이템 소비
                    int value = queue.poll();
                    System.out.println(Thread.currentThread().getName() + " 소비: " + value);

                    // 아이템을 빼냈으니 생산자에게 알림
                    notFull.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                // 예제를 위해 잠시 대기
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
```
