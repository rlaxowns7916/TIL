# Interrupt (인터럽트)
- **작업을 일시 중단하고 요청을 처리한 후, 원래 작업으로 돌아가는 과정**
- CPU가 특정기능을 수행하는 도중에 급하게 다른일을 처리하고자 할 때 사용하는 기능이다.
  - 한개의 CPU는 한번에 하나의 일만 할 수 있기 떄문이다.
  - **CPU에게 급하게 일을 처리해달라고 요청하는 것**


## Interrupt 판별 과정
1. Interrupt 발생 (IRQ)
2. InterruptController가 관리
   - Priority에 따라 가장 중요한 Interrupt 먼저 처리
3. Interrupt Vector Table (IVT)에서 Handler 검색
4. ISR(Handler의 Interrupt Service Routine) 수행
5. 처리 완료 후 원래 작업으로 복귀

## Interrupt의 종류
### 1. External Interrupt (=H/W Interrupt)
- PowerFail: 기계적 문제
- Timer : 일정한 시간 간격으로 Interrupt
- I/O: I/O 장치가 입출력이 완료되었음을 알림

### 2. Internal Interrupt (=S/W Interrupt)
- 잘못된 명령이나 잘못된 데이터를 사용하려고 할 때 발생
  - Division By Zero
  - OverFlow/UnderFlow
  - 기타 Exception
- System Call

### Interrupt의 구성요소
1. Source : 누가 Interrupt를 호출 했는가?
2. Priority: 2개 이상의 Interrupt가 존재할 때, 무엇을 먼저 처리 할 것인가?
3. InterruptVector: InterruptServiceRoutine의 시작주소


## 동작순서
1. IRQ (인터럽트 요청)
2. Program 실행 중단 (현재 실행중이던 Mirco Operation 까지는 수행한다.)
3. 현재의 Program 보존 (PCB, PC 등)
4. Interrupt Service  Routine (ISR) 실행
   - Interrupt 원인을 파악하고 실질적인 작업을 수행한다.
   - ISR 수행중에 우선수위가 더 높은 Interrupt 발생 시, 재귀적으로 1~5 수행
5. 상태복구 (PCB, PC 등)
6. 중단된 Program 재개

## Interrupt 우선순위
**일반적으로 H/W Interrupt가 S/W Interrupt보다 우선수위가 높고, External Interrupt가 Internal Interrupt보다 우선순위가 높다.**
1. PowerFail (전원 이상)
2. Machine Check (하드웨어 오류)
3. External (외부신호)
4. I/O (입출력)
5. 명령어 잘못입력
6. S/W Interrupt(프로그램 오류)
7. SVC (Supervisor Call)

### System Call과의 차이
```text
System Call은 프로그램이 Kernel Layer에서 할 수있는 작업을 가능하도록 OS에서 제공해주는 것이며,
Interrupt는 CPU가 특정한 작업을 하도록 지시 하는 것이다.
```