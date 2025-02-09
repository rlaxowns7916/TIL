# CPU 스케줄링
- CPU 스케줄러가 메모리에 존재하는 프로세스 들 중에서 CPU 자원을 할당해준다.
- 스케줄링을 통해서 항상 Process가 돌게만든다.
    - 핵심은 "Ready상태에 있는 Process 중 어떤 Process에게 CPU를 할당 해 줄 것인가" 이다
- 프로세스들은 실행 (CPU Burst), 입출력 (I/O Burst)로 이루어진다.
  - I/O Burst가 더 빈번하게 일어난다.
- ReadyQueue에 대기하고 있는 프로세스들에 대해서, 알고리즘을 통해서 수행할 프로세스를 결정 하는 것이다.

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

| 알고리즘                         | Network 당 Host 수                                                                                                                     |
|------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| SRT (ShortestRemainingFirst) | 현재 수행중인 프로세스의 남은 burst time 보다 짧은 burstTime을 가진 프로세스가 도착 할 경우 짧은 burstTime이 우선순위를 갖는다.                                               | 
| RR (RoundRobin)              | Queue와 유사하다 <br>시간할당량(TimeSlice) 안에 작업을 마치지 못하면 다시 ReadyQueue로 보낸다. <br/> 시간할당량의 크기가 크면 FCFS와 유사해진다. <br/> 시간할당량의 크기가 작으면 오버헤드가 커진다. |
| MultiLevel-Queue             | 우선순위에 따라 그룹마다 Ready 큐를 배치하는 것이다. (그룹마다 다른 값을 배정하여 Starvation문제를 해결하고자함)                                                              |
| Multi level Feedback Queue   | MultiLevelQueue의 방법에서, Ready Queue를 옮겨다닐수 있는 방법이다.                                                                                   |


### NonPreemptive Scheduling
- 공정하다
- 구현이 쉽다.
- 자원을 강제로 뺏을 수 없다.
- 작업이 실행중 이라면, 끝날 때 까지 계속 지속된다. (자발적으로 CPU 자원을 Release 한다.)

| 알고리즘                            | Network 당 Host 수                                                                                                                               |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| FCFS(FirstComeFirstServe)       | ReadyQueue에 도착한대로 CPU를 할당한다.                                                                                                                   | 
| SJF (ShortJobFirst)             | ReadyQueue에 대기중인 프로세스 중에서, 실행 시간이 가장 짧은 프로세스에게 할당해준다.<br/>평균 대기시간을 최소화 한다.                                                                     |
| HRN (HighestResponse-Ratio Next) | 서비스를 받을 시간과 대기중인 시간으로 결정되는 우선순위로 결정한다 <br/> 우선순위 = (대기시간 + 서비스시간) / 서비스시간 <br/> SJF 약점 보완, 대기시간이 파라미터에 들어가서 Starvation 예방에 대한 어느정도 고려가 들어가 있다. |
| Priority                        | 우선순위에 따라 CPU를 할당해준다.<br/> Aging 기법 (무기한 방치를 예방한다.)                                                                                             |



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