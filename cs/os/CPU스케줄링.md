# CPU 스케줄링
- CPU 스케줄러가 메모리에 존재하는 프로세스 들 중에서 CPU 자원을 할당해준다.
- 스케줄링을 통해서 항상 Process가 돌게만든다.
    - 핵심은 "Ready상태에 있는 Process 중 어떤 Process에게 CPU를 할당 해 줄 것인가" 이다
- 프로세스들은 실행 (CPU Burst), 입출력 (I/O Burst)로 이루어진다.
  - I/O Burst가 더 빈번하게 일어난다.

## 목적
- 프로세서 이용률을 높이기 위함이다.
- 시스템의 작업 처리률을 높이기 위함이다.
- 사용자 응답시간을 줄이기 위함이다.

## NonPreemptive VS Preemptive

### Preemptive Scheduling
- 자원을 강제로 뺏을 수 있다.
- 공유자원에 대한 동기화가 필요하다.
- ContextSwitching이 빈번하게 일어난다.
- 구현이 어렵다.
- 빠른 응답이 가능하다.

### NonPreemptive Scheduling
- 자원을 강제로 뺏을 수 없다.
- 작업이 실행중 이라면, 끝날 때 까지 계속 지속된다. (자발적으로 CPU 자원을 Release 한다.)
- 구현이 쉽다.
- 공정하다

## LogTerm vs MidTerm vs ShorTerm

### LongTerm Scheduler
- new -> Ready
- 시작 프로세스 중 어떤 것을 ReadyQueue에 보낼지 결정한다.
- Secondary Storage(Disk)에서 메모리로 프로세스를 로드한다.

### MidTerm Scheduler
- Memory를 빼앗는 프로세스를 SecondaryStorage(디스크)로 내린다.
- 필요 할 때 다시 Memory에 올린다 (Swap-In)
- 필요 없을 때 SecondaryStorage로 내린다 (Swap-Out)

### ShortTerm Scheduler
- Ready -> Run
- Process에게 CPU자원을 넘겨준다.

## 스케줄링 성능 평가의 기준
### 1. CPU Utilization (이용률) 
- 40 ~ 90 %가 좋다.
### 2. Throughput (단위시간 당 처리량)
- MIPS(Milion Instruction Per Second)
### 3. TurnAroundTime (총 처리시간)
- 프로세스 준비시간부터 종료시간까지 걸리는 시간  
### 4. WaitingTime (대기시간)
- ReadyQueue에서 대기한 시간
### 5. ResponseTime (응답시간)
- 요청부터 응답까지 걸리는 시간

## Dispatcher (디스패쳐)
- CPU Scheduler에 포함되어 있다.
- CPU의 제어권을 선택된 프로세스에 넘긴다.
- ContextSwitching에 관여한다.
  - StateSave (상태저장)
  - StateRestore (상태복구)
- ContextSwitching에 걸리는 시간을 DispatchLatency라고한다.