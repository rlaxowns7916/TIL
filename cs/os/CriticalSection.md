# CriticalSection
- RaceCondition(경쟁상황)이 일어나는 영역이다.
- 여러개의 프로세스 혹은 쓰레드가 공유하는 영역을 의미한다.
- 동시에 접근 할 수 없다.

## 해결 요구조건
### 1. MutualExclusion (상호 배제)
- 특정 프로세스 혹은 쓰레드가 CriticalSection에서 실행되는 동안 다른 프로세스 혹은 쓰레드가 접근 할  수 없다.

### 2. Progress (진행)
- CriticalSection이 사용되고 있지 않다면, 접근 할 수 있어야한다.
- 다음 CriticalSection에 접근할 수 있는 대상은 유한시간 이내에 결정되어야 한다.

### 3. BoundedWaiting (한정 대기)
- 특정 프로세스 혹은 쓰레드가 CriticalSection을 독점해서는 안된다.
- 계속해서 우선순위에 밀려나서 Starvation 상태인 프로세스 혹은 쓰레드가 있어서는 안된다.

**위 3가지의 조건을 모두 만족해야 유효한 알고리즘이 된다.**


## 해결법

### 1. Mutex Lock
#### 특징
- 동기화 대상이 하나이다.
- CriticalSection에 들어가는 시점에 Lock을 획득
- CriticalSection에서 나가는 시점에 Lock을 반환
- Lock이 걸려있기 때문에, MutalExclustion을 만족한다.
- Lock을 소유한 대상만이 Release가 가능하다.

#### 한계점
- BusyWaiting (SpinLock)
  - CriticalSection의 Lock을 획득하기 전까지 계속 진입하려고 시도한다.
  - CPU 자원을 낭비한다.
- Lock 방식이기 떄문에 동시성이 결여된다.

### 2. Semaphore
#### 특징
- 동기화 대상이 하나 이상이다. (Counting Semaphore)
  - Mutex처럼 하나의 동기화 대상만 가질 수 도 있다. (Binary Semaphore)
- 자원에 접근할 때 Semaphore-- , 나갈 떄 Semaphore++ 연산을 진행한다.
  - Semaphore가 음수면 접근 할 수 없다.
- Mutext의 BusyWaiting을 **Block & WakeUp**으로 해결하였다.
  - CriticalSection에 진입하려 했던 프로세스는 Block 시킨 후 PCB를 Waiting Queue에 넣고, 자리가 생기면 다시 WakeUp시키고, ReadyQueue에 넣는다.
- Starvation을 유의해야한다.
  - Semaphore WaitingQueue에서 빠져나가지 못하는 경우가 존재한다.
  - FIFO나 오래기다린 순으로 우선순위를 주는 방법을 고려해야 한다.
- Semaphore를 가진 대상이 아니어도 Release가 가능하다.
## BusyWait VS Block & WakeUp
- 일반적으로는 Block & WakeUp이 CPU 소모를 줄일 수 있다.
  - 하지만 WakeUp과정, ReadyQueue에 배치하는 과정 모두 CPU 리소스를 소모한다.
- CriticalSection의 길이가 길 경우 Block & WakeUp이 유리하다.
- CriticalSection의 길이가 짧을 경우 BusyWaiting이 유리하다.