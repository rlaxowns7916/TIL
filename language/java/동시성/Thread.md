# Thread
- Thread는 프로세스 내에서 실행되는 흐름의 단위이다.
  - 하나의 Process에는 최소 한개의 Thread가 필요하다.
- UserThread와 DaemonThread로 나뉜다.
  - | **특징**                 | **User Thread (Non-Daemon)**          | **Daemon Thread**                  |
    |--------------------------|---------------------------------------|------------------------------------|
    | **JVM 종료**             | 종료되지 않은 User Thread가 있다면 JVM은 종료되지 않음 | User Thread가 없으면 JVM이 종료됨 |
    | **기본값**               | 기본적으로 모든 스레드는 User Thread | `setDaemon(true)`를 호출해야 Daemon |
    | **목적**                 | 메인 작업 수행                       | 백그라운드 작업 수행              |
    | **예**                   | 애플리케이션 로직, 메인 메서드        | 가비지 컬렉터, JVM 모니터링       |


## 생성법
1. Thread 클래스 상속
```java

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread is running");
    }
}
```

2. Runnable 인터페이스 구현
```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable is running");
    }
}

Thread t = new Thread(new MyRunnable());
```
- start()를 통해서 실행한다.
  - start()를 호출하면, Thread가 생성되고, run()이 호출된다.
  - run()을 직접 호출하면, 새로운 Thread가 생성되지 않고, 현재 Thread에서 실행된다.
- Runnable을 구현해서 사용하는게 더 좋은 방식이다.
  - Interface가 더 유연하고, 상속의 제한이 없기 때문이다.
  - Thread의 불필요한 기능을 사용하는 것을 방지 할 수 있다.


## 상태
### [1] NEW
  - Thread가 생성된 상태
  - 아직 start()가 호출되지 않은 상태
### [2] RUNNABLE
  - 실행중이거나 실행가능한 상태 (Java에서는 실제 CPU에서 실행되고있는 상태와, 실행 대기중인 상태를 구분 할 수 없다.)
  - start()가 호출되면, 해당 상태로 들어간다.
  - 실제로 CPU에서 실행 가능한 상태이며, OS의 Scheduler에 의해 실행된다. 
### [3] BLOCKED
  - Thread가 동기화 Lock을 기다리는 상태
    - synchronized를 통해서 Lock을 획득하지 못한 상태 (다른 Lock 객체는 WATING 상태에서 대기한다.)
  - CPU를 사용하지 않는 상태
  - interrupt가 발생해도 꺠어나지 않는다.
### [4] WAITING
  - Thread가 무기한으로 다른 Thread의 작업을 기다리는 상태
  - CPU를 사용하지 않는 상태
  - wait(), join() 메소드를 통해서 발생한다.
  - interrupt를 통해서 깨울 수 있다.
  - notify(), notifyAll() 메소드를 통해서 깨우거나, join()이 끝날 때 까지 디라니다. 
### [5] TIMED_WAITING
  - Thread가 일정 시간동안 다른 Thread의 작업을 기다리는 상태
    - interrupt를 통해서 깨울 수 있다.
    - sleep(mills), wait(mills), join(mills) 메소드를 통해서 발생한다.
    - CPU를 사용하지 않는 상태
### [6] TERMINATED
  - Thread의 실행이 완료 된 상태
    - 한번 실행이 종료되면, 다시 시작 될 수 없다.
  - CPU를 사용하지 않는 상태


## Method

### [0] start
- Thread를 실행한다.
- run()이 호출된다.
  - run()은 별도의  Thread에서 실행된다.

### [1] join()
- caller느 해당 Thread의 작업이 완료될 때 까지 기다린다.
  - 이 때 상태는 WAITING이 된다.
  - **start()를 일괄적으로 호출 한 후, 마지막에 join()을 일괄 호출하는 것이 좋다.**
- 무한정 대기 할 수 없기 때문에, 기다릴 시간을 지정할 수 있다.
  - join(1000) : 1초 기다린다.
  - 이 때 상태는 TIMED_WAITING이 된다.
- join()이 완료되면 다시 RUNNABLE 상태가 된다.

### [2] interrupt()
- 특정 Thread의 작업을 중간에 작업의 끼워 넣는 역할을 한다.
- 해당 Thread는 InterruptedException이 발생하게된다.
  - Method를 호출한다고 바로 Exception이 발생하는 것이 아니다.
  - 해당 Thread가 InterruptedException을 던지는 Method를 호출 할 때 발생한다. 
  - Exception이 발생한 후, Interrupted 상태는 다시 초기화 된다. (isInterrupted = false)
    - 초기화 하지 않으면, 유지된 Interrupted 상태로 다음 작업에 영향을 줄 수 있다. (예: Thread.sleep()에서 또다시 Exception 발생)
    - 상태를 아래의 Method들로 확인 할 수 있다.
      - isInterrupted()를 통해서 확인 할 수 있다. (상태를 false로 돌리지 않음)
      - interrupted()를 통해서 확인 할 수 있다. (상태를 false로 돌림)

### [3] yield
- 다른 Thread에게 CPU를 양보한다.
  - **OS의 Scheudler에게 힌트를 줄 뿐, 강제적인 실행 순서를 지정하거나 양보 하는 것이 아니다.** 
  - Thread.sleep()을 통해서도 목적을 달성할 수 있으나, 상태 변경과 그 시간만큼 Thread가 실행되지 않는 단점을 보완 가능하다.
- **Runnable 상태는 그대로 유지된다.**