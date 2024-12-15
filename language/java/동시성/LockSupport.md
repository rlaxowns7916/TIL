# LockSupport
- java 1.5 > java.util.concurrent 부터 패키지가 추가되었다.
- synchronized / wait(), notify()를 보완하기위해 추가되었다.
- 저수준 동기화 도구이며, Thread의 상태를 변경하는 기능을 제공한다.
- **natvie method를 통해서 구현되었기 때문에 성능상 유리하다**

## 기존 동기화 기법 (synchronized / wait(), notify())와의 차이점
1. LockSupport는 synchronized block(동기화 블록) 없이도 사용 가능하다.
2. Thread의 상태를 BLOCKED로 만들지 않는다.
3. notify()나 notifyAll()과 같이 임의의 Thread를 꺠우는 것이 아닌, 특정 Thread를 깨울 수 있다. (세밀한 제어 가능)
4. **synchronized와 다르게 interrupt 발생 시, InterruptedException이 발생하지 않으며 바로 반환된다.**

## 신호 보존 메커니즘 (Signal Preservation)
- SIGNAL을 저장한다.
- 그에 따라 순서에 상관없이 동작 할 수 있게 된다.
- **unpark(thread)가 호출되면 Signal을 그 이후 park() 호출을 즉시 반환할 수 있도록 한다.**

## [1] park
- 현재 Thread를 WAITING 상태로 만든다.
  - 다른 Thread가 깨워줄 때 까지 상태가 유지되며, CPU의 Scheduling 대상이 되지 않는다.
- unpark() 호출 시 바로 반환된다.

## [2] partNanos
- park와 동일하게 동작하지만, 시간을 지정할 수 있다. (nano 초)
  - TIME_WAITING 상태에서 시간이 지나면 RUNNABLE 상태로 변경된다.
- unpark() 호출 시 바로 반환된다.

## [3] unpark
- park된 Thread를 깨운다.
  - park 상태였던 Thread를 깨우면, RUNNABLE 상태로 변경된다. 


## 샘플 코드
```java
public class LockSupportExample {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Thread is parking...");
            LockSupport.park();
            System.out.println("Thread has been unparked!");
        });

        thread.start();

        try {
            Thread.sleep(1000); // 스레드가 park 상태로 들어가도록 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LockSupport.unpark(thread); // 스레드를 깨움
    }
}
```