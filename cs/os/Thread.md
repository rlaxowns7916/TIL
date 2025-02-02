# Thread
- 경량화 프로세스(LightWeightProcess)라고 불린다.
  - Process에 비해서 생성하는데 조금의 자원밖에 안필요하다.
- 프로세스내에서 작업을 실행하는 주체 (작업의 흐름)
- 모든 프로세스는 최소 한개 이상의 쓰레드를 가진다.
- 두개 이상의 쓰레드 부터는 멀티쓰레드라고 한다.
- Context Switching 에서 프로세스보다 빠르다.
  - Context Switching 시 Stack만 교체하면 된다.
- 부모의 메모리 영역 (Code,Data,Heap)을 공유하고 독립적으로 PC,RegisterSet,Stack 영역만 가진다.
  - 쓰레드 끼리의 Interaction에 비용이 들어가지 않는다.

# MultiThreading
- 두개 이상의 쓰레드를 사용하여 프로그래밍 하는 것
- 응답성이 좋다.
  - Blocking 작업 (I/O)에서 유리하다.
- 경제적이다.
  - Resource Sharing이 필요 없다.
  - ContextSwitching 비용이 적다.
- 확장성이 좋다.
  - 병렬처리가 가능하다.
  

# Thread의 종류

## UserThread
- UserMode에서 동작
- 라이브러리 형태로 제공된다.
- 동일한 메모리에서 쓰레드가 관리되므로 빠르다.
- 하나의 쓰레드가 SystemCall등으로 호출되면 나머지 쓰레드도 중단된다.

## KernelThread
- KernelMode에서 동작
- 하나의 쓰레드가 I/O로 Blocking되어도, 다른 쓰레드는 동작 가능하다.
- 유저 스레드에 비해서 생성 및 관리하는 것이 느리다.

## TCB (Thread Control Block)
- OS가 Thread를 관리하기 위해서 사용하는 데이터 구조
- ContextSwitching 시 TCB 정보를 로드하여 상태를 복구한다.

### 구성요소
| 항목 | 설명 |
|------|------|
| **Thread ID** | 스레드를 구별하기 위한 고유 ID |
| **Thread State** | 현재 스레드의 상태 (Running, Ready, Blocked 등) |
| **Program Counter (PC)** | 스레드가 마지막으로 실행한 명령어의 메모리 주소 |
| **CPU Register** | 스레드가 사용 중인 CPU 레지스터 값 |
| **Stack Pointer (SP)** | 현재 스레드의 스택 주소 |
| **Priority (우선순위)** | 스레드의 실행 우선순위 (높을수록 빠르게 실행) |
| **Memory Management Info** | 스레드가 접근하는 메모리 영역 정보 (Stack, Heap, Code) |
| **Thread-specific Data** | 특정 스레드에 대한 OS 내부 관리 데이터 |
| **Parent Process ID (PPID)** | 해당 스레드를 포함하는 프로세스의 ID |
| **Resource List** | 스레드가 사용하는 자원 (파일, 네트워크, 동기화 객체 등) |
