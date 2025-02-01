# ContextSwitching (컨텍스트 스위칭)
- CPU는 한번에 하나의 일을 한다. (CPU를 선점하는 것은 Thread이다.)
  - 여러 일을 짧은 시간단위로 실행함으로 해서(시분할), 사용자에게는 여러가지 동작을 동시에하는 것 처럼 보이게 한다.
  - MultiTasking 환경에서는 여러 Process (or Thread)가 실행되는 것처럼 보이지만, 실제로는 빠르게 Context Switching하는 방식으로 동작한다.
- 현재 실행중인 테스크를 중지하고 다른 테스크를 실행하는 과정
- 컨텍스트 스위칭이 발생하는 동안 CPU는 아무런 일도 하지못한다. (idle 상태)
- 잦은 ContextSwitching은 많은 오버헤드를 유발한다.

## Context (컨텍스트)
- CPU가 프로세스를 실행하기위해 필요한 정보들
- PCB에 저장된다.

## 발생되는 조건
- I/O Interrupt (Process가 I/O 작업을 요청하면 전환)
- H/W Interrupt (키보드 입력, 네트워크 패킷 도착, ...)
- TimeSlice Expired (CPU 사용시간 만료)
- Interrupt 처리 대기
- 일부 SystemCall: fork(), exec(), wait() 등을 통해서 Process 변경이 수행 될 수 있음
- Preemptive Scheduling
- Process 종료

## 수행되는 과정
1. 현재 실행하고 있는 프로세스의 컨텍스트를 PCB에 저장
2. 다음 실행할 프로세스의 PCB의 정보를 읽어옴

## 프로세스와 스레드의 ContextSwitching
- 프로세스는 PCB를 교체
  - 겹치는 영역이 없기 떄문에 캐시 메모리도 모두 비운다.
- 쓰레드는 프로세스의 메모리영역을 공유하기 떄문에 Stack영역만 교체

