# VirtualThread
- JDK 21에 정식으로 포함된 경량 스레드(Virtual Thread) 모델이다.
- 여러 개의 Virtual Thread가 적은 수의 Platform Thread에 매핑되어 실행된다.
- JVM이 Scheduling에 관여한다.
- Virtual Thread는 소프트웨어(S/W)에서 관리하는 스레드이므로, 컨텍스트 스위칭(Context Switching) 비용이 낮다.
- Virtual Thread가 블로킹될 경우, 해당 Platform Thread는 다른 Virtual Thread의 작업을 수행할 수 있다. 따라서 Platform Thread가 불필요하게 대기하지 않는다.

## CarrierThread
- CarrierThread는 PlatformThread(Kernel Thread)와 매핑되는 JVM의 Thread이다.
- 하나의 CarrierThread는 다수의 VirtualThread와 매핑될 수 있으며, VirtualThread가 실행될 때 CarrierThread에서 처리된다.
- VirtualThread가 어떤 CarrierThread에서 실행될지는 알 수 없다.
- VirtualThread는 직접 OS 커널 스레드와 연결되지 않으며, CarrierThread를 통해 실행된다.

## Scheduling

### 1. **ForkJoinPool과 Carrier Thread**
- Virtual Thread의 기본 실행 환경은 **ForkJoinPool**을 기반으로 한다.
- ForkJoinPool 내에서 Virtual Thread는 여러 개의 **Carrier Thread**에 의해 실행된다.
- Carrier Thread의 개수는 일반적으로 **CPU 코어 수와 유사한 수준**으로 유지된다.

### 2. **Work-Stealing Algorithm**
- JVM은 Work-Stealing 방식을 활용하여 Virtual Thread의 부하를 균등하게 분산한다.
- 만약 특정 Carrier Thread가 Virtual Thread 실행을 마쳤다면, 다른 Carrier Thread에서 실행 대기 중인 Virtual Thread를 가져와 실행할 수 있다.
- 이를 통해 특정 Carrier Thread가 과부하되는 것을 방지하고, 전체적인 처리량을 최적화한다.

### 3. **Non-Preemptive Scheduling**
- Virtual Thread는 **Non-Preemptive 방식**으로 스케줄링된다.
- OS 스레드와 달리, JVM이 강제적으로 Virtual Thread를 중단하지 않으며, Virtual Thread가 자발적으로 실행을 종료하거나 블로킹될 때만 컨텍스트 스위칭이 발생한다.
- 따라서 CPU-Bound 작업을 수행하는 Virtual Thread는 장시간 실행될 수 있으며, 이는 비효율적인 스케줄링을 초래할 수 있다.

### 4. **Blocking 시 자동 재배치**
- Virtual Thread가 `Thread.sleep()`, `wait()`, `IO 호출` 등으로 블로킹되면, JVM은 해당 Virtual Thread를 Carrier Thread에서 분리하고 다른 Virtual Thread를 실행한다.
- 이를 통해 **Platform Thread가 블로킹되지 않고 다른 작업을 수행할 수 있도록 최적화된다**.
- 하지만, `synchronized` 블록이나 JNI 호출과 같은 특정 상황에서는 Virtual Thread가 Carrier Thread에 고정(Pinned)될 수 있어 성능 저하가 발생할 수 있다.

### 5. **Carrier Thread와 Virtual Thread 간의 관계**
- Virtual Thread는 특정 Carrier Thread에 고정되지 않으며, JVM이 적절한 Carrier Thread에서 실행되도록 조정한다.
- Virtual Thread의 실행 위치는 예측할 수 없으며, 필요할 경우 다른 Carrier Thread로 이동할 수 있다.
- 다만, 특정 연산(`synchronized` 블록, 네이티브 코드 호출 등)이 포함되면 Carrier Thread가 Blocking 될 위험이 있으므로 주의가 필요하다.

### 6. Blocking 시의 동작
- JVM은 기본적으로 Blocking 시 `LockSupport.park()` 를 호출
- VirtualThread가 아니라면 VirtualThreads.park()를, 아니라면 Unsafe.park(JNI)를 호출한다. 
  - VirtualThread가 PlatformThread와 분리된다.
  - unpark()호출 시, VirtualThread는 다시 CarrierThread에서 수행될 자격을 얻는다.
```java
public static void park(Object blocker) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        try {
            if (t.isVirtual()) {
                VirtualThreads.park();
            } else {
                U.park(false, 0L);
            }
        } finally {
            setBlocker(t, null);
        }
    }
```

## Continuation
- Continuation은 **Virtual Thread의 실행을 중단하고, 이후 다시 재개할 수 있도록 하는 JVM 내부 메커니즘**이다.
- JVM의 Scheduling과정에서 Continuation을 사용하여 실행을 제어한다.
- 아래와 같은 흐름이다.
  1. Virtual Thread가 시작되면, JVM은 해당 Virtual Thread를 위한 `Continuation`을 생성.
  2. Virtual Thread가 `park()` 호출 시, 현재 실행 상태를 Continuation에 저장한 후 Carrier Thread에서 해제됨.
  3. Virtual Thread가 `unpark()`되면, Continuation을 통해 이전 상태에서 다시 실행됨.
  4. Virtual Thread는 Carrier Thread에 다시 매핑되어 실행을 재개함.

    
## 주의 할 점
1. Pinned 상태 주의
    - `synchronized` 블록이나 네이티브 코드에서 Virtual Thread가 Platform Thread에 고정(Pinned)될 수 있음.
    - 이렇게 되면 Virtual Thread의 경량성 장점이 사라지고, Platform Thread를 점유하게 되어 성능 저하 가능.
    - JDK 25부터 일부 개선될 예정.
2. Virtual Thread를 풀링(pooling)하지 말 것
    - Virtual Thread는 생성 비용이 거의 없고, 자동으로 스케줄링되므로 재사용할 필요가 없음.
    - 오히려 풀링하면 성능이 저하될 수 있으며, 무한대로 생성하는 것이 더 효율적임.
3. ThreadLocal 사용 최소화
    - Virtual Thread는 매우 빠르게 생성/소멸되므로, ThreadLocal을 많이 사용하면 관리가 어렵고 메모리 누수가 발생할 가능성이 있음.
    - 대신 JDK 21에서 도입된 `ScopedValue`를 사용하면 Virtual Thread 환경에서 더 효율적으로 상태를 관리할 수 있음.
4. CPU-Bound 작업에는 Virtual Thread를 사용하지 말 것
   - Virtual Thread는 I/O-Bound 작업에 최적화되어 있다.
     - 낮은 컨텍스트 스위칭 비용과 Platform Thread를 블로킹하지 않는 특성 덕분에 I/O 대기 시간 동안 다른 작업을 수행할 수 있음.
   - Virtual Thread가 CPU-Bound 작업을 수행하면 하나의 Platform Thread를 독점하게 됨.