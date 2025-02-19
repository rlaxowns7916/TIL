# CriticalSection
- RaceCondition(경쟁상황)이 일어나는 영역이다.
- 여러개의 프로세스 혹은 쓰레드가 공유하는 영역을 의미한다.
- 동시에 접근하면 문제가 발생하는 영역을 의미한다.

## 해결 요구조건
**아래 3가지의 조건을 모두 만족해야 CriticalSection 문제를 해결 할 수 있다.**
### 1. MutualExclusion (상호 배제)
- 특정 프로세스 혹은 쓰레드가 CriticalSection에서 실행되는 동안 다른 프로세스 혹은 쓰레드가 접근 할  수 없다.

### 2. Progress (진행 조건)
- CriticalSection이 사용되고 있지 않다면, 접근 할 수 있어야한다.
- CriticalSection에 들어갈 Process는 공정하게 선택되어야 한다.
  - 어떠한 Process도 Starvation 상태가 유지되어서는 안된다.
  - 우선순위가 있는 경우에는 높은 우선순위의 Process가 먼저 실행될 수 있다.
- 다음 CriticalSection에 접근할 수 있는 대상은 유한시간 이내에 결정되어야 한다.

### 3. BoundedWaiting (한정 대기)
- 특정 프로세스 혹은 쓰레드가 CriticalSection을 독점해서는 안된다.
- 계속해서 우선순위에 밀려나서 Starvation 상태인 프로세스 혹은 쓰레드가 있어서는 안된다.

# Synchronization (동기화)
- 여러 Process 혹은 Thread가 안전하게 CriticalSection 에 접근가능하게 하는 방법

## H/W 기반 해결
### 1. TestAndSet (TAS)
- 단순 Lock획득을 위해서 주로 사용한다.
  - 유연성이 낮다. (0/1 로만 관리)
  - 빠르다. (Kernel의 개입이 없다.)
- XCHG 명령어 (x86)
- SpinLock

### 2. CompareAndSet
- 조건부 메모리 업데이트를 위해서 사용한다.
  - Lock-Free 알고리즘 구현에 주로 사용된다. 
  - 유연성이 높다 (모든 값 비교 및 설정 가능)
- CMPXCHG 명령어(x85)

## S/W 기반 해결

### 1. Mutex Lock
- Lock을 획득한 Thread만 해제 가능하다.
- 이미 점유된 경우, 요청 Thread는 Queue에 등록되어 슬립(sleep) 상태로 전환됩니다 (Busy Waiting 방지).
- 0 혹은 1로 상태를 관리한다.


### 2. Semaphore
- 접근제어를 Counter를 통해서 수행한다.
- Lock을 획득한 Thread와 무관하게 임의의 스레드가 해제 가능하다.
- 두가지로 나뉜다. 
  - Binary Semaphore: 이진 상태 (Mutex와 유사하나 소유권 없음). 
  - Counting Semaphore: N개의 동시 접근 허용 .


### 3. Monitor
- OS에서 직접적으로 제공하는 기능이 아닌, 동기화 원리를 활용하여 구현된 **고수준 동기화 메커니즘**
  - Mutex, ConditionVariable, Semaphore 등을 사용하여 JVM과 같은 Runtime환경에서 구현
- CriticalSection과 Synchronization을 캡슐화한다.
  - Method, Variable 등을 제공한다.
- Monitor내의 Method는 동시 실행 되지 않는다. (MutualExclusion 보장)
  - ex) Java Synchronization
- 2개의 Queue를 가지고 있다.
  - MutualExclusionQueue: 상호배제를 보장하기 위한 Queue
  - ConditionalSynchronizationQueue: 특정조건이 만족되면 대기상태의 Thread를 꺠울 Queue

## BusyWait VS Block & WakeUp
- 일반적으로는 Block & WakeUp이 CPU 소모를 줄일 수 있다.
  - 하지만 WakeUp과정, ReadyQueue에 배치하는 과정 모두 CPU 리소스를 소모한다.
- CriticalSection의 길이가 길 경우 Block & WakeUp이 유리하다.
- CriticalSection의 길이가 짧을 경우 BusyWaiting이 유리하다.