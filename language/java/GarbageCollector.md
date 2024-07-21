# Garbage Collector
**메모리 관리 기법으로, 사용하지않는 동적할당 메모리 영역(Heap) 을 회수 하는 것**

## GC의 원리가 되는 가설
- Weak Generational Hypothesis
  - 대부분의 객체는 금방 접근 불가능 상태가 된다.
  - 오래된 객체에서 새로운 객체로의 참조는 매우 적다.

## Mark & Sweep

### 동작 순서
1. Heap 영역에 존재하는 객체들의 접근 가능 여부 탐색
2. GC Root 에서 부터 참조값을 따라가며, 접근 가능객체에 **Mark**하는 과정을 가진다.
3. Mark 되지 않은 객체 **(접근 할 수 없는 객체)** 를 **Sweep**(제거)한다.

#### Mark 
- 접근가능 객체에 표시를 하는 것

#### Sweep
- Mark 되지 않은 객체를 제거하는 과정

#### Compact
- Sweep 과정에서 발생하는 메모리 단편화를 Compact 과정을 통해서 매워줌

#### 문제점
- 어떠한 객체를 회수해야할지 모르게된다. --> 주기적으로 체크해야한다.

## Refrence Counting
1. 객체 자체에 자신을 참조하는 Refrence의 개수를 기록하는 변수를 만든다.
2. 자신을 참조할 때 ++, 참조가 끊길 때 --
3. 0이되면 메모리를 회수한다.

#### 장점
-  구현이 간단하다.

#### 문제점
- 순환참조시에 메모리 해제가 불가능하다.

***
## Stop The World
**메모리가 관리되는 동안 JavaApplication 이 멈추는 현상**
- GC를 실행하는 스레드를 제외한 모든 스레드가 멈춘다.
- StopTheWorld 발생 시간을 줄이기 위해 JVM 튜닝을 한다.
***
## 일반적인 GC 구조
- **G1GC(9+ default)는 다른 구조를 가진다**
- YoungGeneration과 OldGeneration으로 나뉜다.
- 원래는 YoungGeneration + OldGeneration + PermanenetGeneration이었지만
  8버전 부터, PermenantGeneration이 삭제되었다.

***

### Minor GC
- YoungGeneration(Eden, Survivor 포함) 에서 발생하는 GC
- **Eden영역이 가득 차 있을 때 발생한다.**
- **MinorGC도 StopTheWorld를 발생시킨다.**
- MinorGC의 횟수와 지속시간을 적절하게 최적화하여 StopTheWorld최적화가 목표이다.

### Major GC (Full GC)
- **Old Generation이 꽉 찼을 때 발생한다.**
- **Heap전체를 대상으로 발생하는 GC**
  - YoungGeneration (Eden, Survivor2개) + OldGeneration
- MajorGC의 발생 횟수를 줄이는 것이 GC튜닝의 목적이다.

***

### YoungGeneration
- Eden영역 1개와 Survivor영역 2개로 나뉜다.
- Young영역에서 발생하는 GC를 **Minor GC** 라고한다.

#### Eden 영역
- 새롭게 생성되는 객체가 들어가는 공간
- Eden영역이 꽉차면 GC가 발생한다.
- GC과정에서 살아남은 객체는 Survivor영역으로 넘어가고 Eden영역은 비워진다.

#### Survivor 영역
- Eden 영역이 다시 꽉 차게되면 GC가 발생한다.
- Survivor영역에서 살아남은 객체는 또 다른 Survivor영역으로 넘어간다.
  - Copy & Delete
- 특정 Age값이 넘어가는 경우에는 OldGeneration으로 넘어간다.

#### Promotion
**Survivor영역 사이를 이동하고, Age가 찼을 때 Old Generation으로 이동하는 것을 의미한다.**

***

## Old Generation
- **Young Generation보다 크기가 크며, GC가 적게발생하지만 시간이 오래걸린다.**
- **Young Generation의 Survivor에서 살아남은 객체가 위치하게 된다.**
- **Old Generation에서 발생하는 GC를 **Major GC**라고한다.**

***

## GC 종류
### 1. Serial GC (-XX:+UseSerialGC)
- 가장 단순한 GC, 싱글 스레드로 동작, StopTheWorld가 다른 GC에 비해 길다
- Mark&Sweep&Compact 알고리즘 사용 
- 실무에선 거의 사용 (x)

### 2. Parallel GC (-XX:+UseParallelGC)
- Java8의 Default GC
- YoungGeneartion을 멀티 스레드 방식 (Old Generation은 아님)
- SerialGC에 비해서 상대적으로 StopTheWorld가 짧다.*

### 3. Parallel Old GC(-XX:+UseParallelOldGC / -XX:ParallelGCThreads=n)
- Parallel Old GC는 Old Generation에도 멀티 스레드 적용
-XX:+ParallelGCThreads=n 옵션으로 멀티 스레드 개수를 지정할 수 있음

### 4. CMS GC(ConCurrent Mark Sweep GC)
- Java14에서 제거됨
- StopTheWorld로 JavaApplication이 멈추는 현상을 줄이고자 만든 GC
- 접근 가능한 객체를 한번에 찾는게 아닌, 4번의 과정을 나눠서 한다.
    1. InitialMark : GCRoot가 참조하는 객체만 Mark
    2. ConcurrentMark: 참조하는 객체를 따라가면서 지속적인 Mark
    3. Remark: ConcurrentMark과정에서 변경된점이 없는지 다시 체크
    4. ConcurrentSweep: StopTheWorld 없이 접근 할수 없는 객체를 제거
**StopTheWorld를 최대한 줄이고자 함**

### 5. G1GC(Garbage-First GC)
- Java9+의 Default GC
  - 현존 GC중 stop-the-world의 시간이 제일 짧음
  - CMS GC를 개선한 GC
- Heap을 **Region**이라는 부분으로 나눠서 메모리 관리 (고정된 크기)
  - Region단위로 탐색하고 **각각의 Region에서 GC가 발생한다.**
  - RegionsSize: **startingHeapSize/2048** (1~32MB 사이의 값 위치)
  - Humonogous (Region 크기의 50% 이상을 차지하는 객체를 저장하기 위한 공간) / Available/Unused: 아직 사용되지 않은 공간
- GC
  - MinorGC
    - 기존 MinorGC와 유사하다.
    - MultiThread로 동작한다.
  - MixedGC
    - YoungGeneration 뿐만 아니라, OldGeneration도 수집
    - MajorGC의 비용이 크기 때문에 수행한다.
    - ConcurrentMark가 완료 된 후 실행
    - OldGeneration의 사용률이 임계치 (default: 45%)를 넘으면 수행 // -XX:InitiatingHeapOccupancyPercent
  - MajorGC
    - OldGeneration을 수집한다.
- 아래의 순서를 가진다.
  1. Initial Mark
     - STW 벌생
     - OldGeneration 에서 참조되는 YoungGeneration 객체를 식별
  2. Root Region Scan
     - Application Thread와 병행하며 실행
     - YoungGeneration에서 참조되는 OldGeneration 객체 식별
  3. Concurrent Mark
     - Application Thread와 병행하며 실행
     - Heap을 순회하며 Marking
  4. Remark
     - STW 발생
     - Concurrent Marking과정에서 변경된 객체를 다시 확인
  5. Cleanup
     - Garbage로 식별된 객체를 제거
     - Application Thread와 병행하며 실행
     - OldGeneration 영역중 Garbage가 많은 영역을 대상으로 실행
  6. Evacuation
     - STW 발생
     - 살아있는 객체는 다른 Region으로 이동하고, Garbage로 마킹된 것은 비운다.
