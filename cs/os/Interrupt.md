# Interrupt (인터럽트)
- **작업을 일시 중단하고 요청을 처리한 후, 원래 작업으로 돌아가는 과정**
- CPU가 특정기능을 수행하는 도중에 급하게 다른일을 처리하고자 할 때 사용하는 기능이다.
  - 한개의 CPU는 한번에 하나의 일만 할 수 있기 떄문이다.
  - **CPU에게 급하게 일을 처리해달라고 요청하는 것**


## Interrupt 판별 방식

### [1] S/W에 의한 Interrupt Polling
- CPU가 모든 장치를 순차적으로 검사하여, Interrupt 요청을 감지하고 처리하는 방식
- 각 장치에는 Interrupt 요청을 나타내는 Flag가 존재하고, CPU가 이것을 감시한다.
- Poling방식은 속도가느리지만, 단순하고 비용이 절며하다.
  - 우선순위 변경이 어렵고, 모든 장치를 확인해야 하므로 성능이 떨어진다.

### [2] H/W vector에 따른 판별 (IVR)
- IVR방식은 각 장치가 고유한 Vector를 가지고 있고, CPU가 Interrupt를 받을 때 해당 Vector를 참조하여 ISR를 수행하는 방식이다.
- **CPU와 Interrupt 장치 사이에, DataBus 또는 AddressBus가 존재하여 IVR을 전달 받을 수 있게 한다**
- Polling보다 속도가 빠르며, 우선순위 관리도 용이하지만 H/W 비용이 증가한다.

### [3] 데이지 체인 (Daisy Chain)
- 모든 장치가 하나의 Interrupt 요청 선을 공유하며 Chain 형태로 연결하는 방식
- CPU와 가까운 장치가 우선순위가 더 높아진다.
- H/W가 간단하고 비용이 저렴하지만, 우선순위가 낮은 장치가 Starvation이 발생할 수 있다.

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


## 동작순서 (IVR)
1. 외부 장치가 IRQ (인터럽트 요청)
2. Program 실행 중단 (현재 실행중이던 Mirco Operation 까지는 수행한다.)
3. 상태저장 - Context Save (특정 Memory 영역에 저장 // PCB 아님)
   - Register 값, Stack, ...
4. Interrupt Service  Routine (ISR) 실행
   - Interrupt 원인을 파악하고 실질적인 작업을 수행한다.
   - ISR 수행중에 우선수위가 더 높은 Interrupt 발생 시, 재귀적으로 1~5 수행
5. 상태복구 (Context Restore) 
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