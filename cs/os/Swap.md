# Swap
- OS가 Memory를 효율적으로 관리하기 위해 사용되는 기법으로, RAM이 부족할 때 일부 Process를 Disk(Swap 공간)로 이동시키는 과정
- 이 과정을 통해서 RAM을 확보하여 우선 실행하고, 다시 Swap에서 RAM으로 불러 올 수 있다.
  - Memory가 부족한 Process가 강제종료되는 것을 방지 할 수 잇다.
- 저장공간 (SSD/ HDD)를 RAM처럼 사용할 수 있게 만드는 것이다.
    - RAM을 대체한다고 생각해서는 안된다.
    - 항상 성능을 향상시킨다고 여겨서는 안된다.
    - HDD이기 떄문에 당연히 RAM 보다는 접근속도가 느리다.
- 실제 RAM보다 더 많이 사용할 수 있다.

## 개념
### [1] Swap-In (Swap -> RAM)
- Swap에 있던 Process를 다시 RAM으로 불러들이는 과정
  - 다시 CPU의 정상 Scheduling이 가능해지는 상태가 된다.

### [2] Swap-Out (RAM -> Swap)
- RAM이 부족하면, OS가 우선순위가 낮거나 대기중인 Process (Ready, Waiting)을 Swap으로 이동시킴
  - Swap-Out당한 Process의 상태는 SuspendReady or SuspendedWaiting 이 된다.
- Process 전체를 내릴 수 있지만(과거 OS), **일부 Page만 Swap-Out(현대 OS) 시키는 경우가 많다.**
  - Process 전체 Swap-Out은 Swap-In을 할 때 비용이 너무나 많이들며, 불필요한 Memory까지 확보 될 수 있다.
  - **Page(일부 Memory) 단위로 Swap-Out 할 경우, 속도가 훨씬 빠르다.**

## 과정
1. RAM이 부족해짐
   - 새로운 Process가 실행되거나, 실행중인 Process가 더 많은 Memory를 필요로함
   - 하지만 여유의 메모리를 할당 할 수 없는 상황
2. OS가 Swap-Out을 수행
  - 실행중인 Process는 유지한다.
  - 우선순위가 낮은 Process를 Swap으로 이동시킨다.
  - RAM이 확보되었음으로, 실행중인 프로세스들은 정상 실행이 가능해진다.
3. SwapOut 되었던 Process가 다시 필요해짐
  - Swap으로 내려간 Process가 필요해지면 다시 RAM으로 불러들여야한다. (Swap-In)
  - Memory가 충분해진다면 다시 Ready or Waiting 상태로 복귀하여 Scheduling 대상이 된다.

