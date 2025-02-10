# Process
**운영체제로 부터 메모리 및 자원을 할당 받아 실행되고 있는 프로그램**
- CPU를 점유하고 실행되는 단위이다.
- Process간의 통신은 IPC (InterProcessComunicaiton)
- 하나의 Process안에 여러개의 쓰레드 생성 가능
- Process마다 최소 1개의 쓰레드(Main Thread)를 소유

## Process의 메모리영역
- **code**: 실행될 기계어 코드가 저장된 공간
- **data**: 전역,static변수등이 저장된 공간(CompileTime에 결정)
- **stack**: 함수안에 선언된 지역변수, 매개변수 등이 저장
- **heap**: 프로그래머가 관리하는 메모리영역, 동적을 할당된 메모리들이 존재한다.

## MultiProgramming
- 하나의 CPU가 시분할과 같은 기법으로 여러개의 Process를 실행하는 것.
  - 하나의 CPU는 한번에 하나의 일만 할 수 있으나, 시간을 나누어 빠르게 실행하기 떄문에, 동시에 실행되는 것 처럼 보임
- **한 Program의 I/O가 수행되면 ContextSwitching을 한다.**
  - Starvation이 발생 할 수 있다.
- 병렬성은 없으나, CPU의 활용도는 높일 수 있다.
  - ContextSwitching이 발생한다.

## MultiTasking
- **하나의 CPU에서 여러개의 작업(Task)를 빠르게 전환(Time Slice)하여 실행하는 방식**
- CPU가 빠르게 ContextSwitching을 하여 동시에 실행되는 것처럼 보이게 한다.

## MultiProcessing
- 여러개의 CPU로 각자 독립적으로 여러개의 Process를 실행하는 것
- 각각의 Process는 독립적이기 때문에, 한개의 Process에서 일어난 문제점이 다른 Process로 전이 되지 않는다.

***

## Process의 생명주기

### 1. New
- Process가 생성된 상태이다.
- OS가 Process 실행을 준비 중

### 2. Ready
- CPU 점유 대기 상태 이다.
- 변수 초기화 등 실행될 준비 작업을 마치고 실행 대기상태이다.
- ReadyQueue에서 CPU 할당을 대기한다.
- Running으로 부터 전이 가능하다.
  - Timer Interrupt (시분할에 의한 CPU 스케줄링)
  - 우선순위 높은 Process의 대기 (Preemptive 스케줄링)
  - ...
### 2-1. SuspendedReady
- 실행 대기중이던 Process가 OS에 의해서 중단됨
- **CPU의 스케줄링 대상에서 제외됨**
- 변경 사유
  1. OS에서 Memory가 부족하면 Ready상태의 Process를 Disk로 Swap시킴
  2. Process는 SuspendedReady 상태가 됨
  3. Memory (!= SwapMemory)가 확보되면 다시 Ready 상태가됨

### 3. Running
- CPU를 점유하여 실행되고 있는 상태이다.

### 4. Waiting
- Event가 완료를 대기하는 상태
  - I/O 요청
  - 자식 Process 종료 대기 (wait())
  - Semaphore, Mutex를 사용한 동기화 대기
  - Timer 및 Delay (sleep())
  - 이벤트 기반 작업 (select(), poll(), epoll_wait())
- 작업이 모두 끝나면 다시 Ready 상태가 된다.

### 4-1. SuspendedWaiting
- 대기 상태였던 Process가 OS에 의해서 정지됨
- CPU 스케줄링에서 제외
- 변경 사유
  1. OS에서 Memory가 부족하면 Waiting 상태의 Process를 Disk로 Swap시킴
  2. Process는 SuspendedWaiting 상태가 됨
  3. Memory(!= SwapMemory)가 확보되면 Ready(Complete 이벤트 수신 시)로 변경되거나, Waiting 상태를 유지한다.

### 5. Terminated
- Process가 완전히 종료된 상태이다.
- 사용하던 메모리 영역이 해제된다.

![Process 생명주기](https://user-images.githubusercontent.com/57896918/158165830-203bc68d-a277-4e36-bc22-b3d2a571271e.png)

***
## PCB (Process Control Block)
- Memory에 유지하고 있으며, Process 종료 시 소멸
- Process의 메타데이터를 가지고 있다.
  - Process가 가지고 있어야 할 정보를 저장한 블록
- OS는 PCB를 통해서 Process를 구분하고 관리한다.

### 가지고 있는 정보
1. ProcessID
   - Process 식별자
2. Program State
    - New
    - Ready
    - Running
    - Waiting
    - Terminated
3. Program Counter
    - Process가 다음 실행할 명령어의 주소를 가르킨다.
4. CPU Register
    - Accumulator, Index Register, 범용 레지스터 등 
5. CPU Scheduling Information
    - 스케줄링 우선순위
    - CPU 점유 시간
6. Memory-Management Information
    - Process의 주소 공간
7. I/O Status
    - Process에 할당된 입출력장치
    - 열린 파일 목록 등
8. 계정 정보
    - CPU 사용시간
    - 계정 번호 
## Process Scheduling (Process 스케줄링)
- Process의 생성 및 실행에 필요한 시스템의 자원을 해당 Process에 할당하는 작업을 의미한다.
- 비선점 기법과 선점 기법으로 나뉠 수 있다.

### 선점 기법 (Preemptive)
- 한 Process가 CPU를 할당 받아 실행중이더라도, 우선순위가 높은 Process가 CPU를 강제로 뱃을 수 있다.
- 긴급하고 우선순위 높은 Process들이 빠르게 실행 될 수 있다.
FCFS(FirstComeFirstServe), SJF(ShortJobFirst) 등의 알고리즘이 사용된다.

### 비선점 기법 (NonPreemptive)
- CPU를 할당 받으면 다른 Process가 CPU를 강제로 뺏을 수 없다.
- 모든 Process에 대한 공정한 처리가 가능하다.
- RoundRobin, SRT(ShortRemainingFirst) 등의 알고리즘이 사용된다.
***