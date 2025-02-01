# Interrupt (인터럽트)
- CPU가 특정기능을 수행하는 도중에 급하게 다른일을 처리하고자 할 때 사용하는 기능이다.
  - 한개의 CPU는 한번에 하나의 일만 할 수 있기 떄문이다.
  - **CPU에게 급하게 일을 처리해달라고 요청하는 것**
- Interrupt가 발생하면 InterruptHandler로 넘어간다.
  - InterruptVector(테이블)에 저장된 InterruptHandler의 주소를 통해 찾아간다. 
  - 수행중이던 작업은 PCB에 기록한다. 
  - Interrupt Handler에는 서비스루틴이 존재한다.
- Interrupt Handler의 서비스루틴이 종료되면 다시 CPU 연산을 재개한다.

## Interrupt의 종류

### 1. External Interrupt
- PoserFail: 전원 이상
- Timer : 일정한 시간 간격으로 Interrupt
- I/O: I/O 장치가 입출력이 완료되었음을 알림
### 2. Internal Interrupt
- 잘못된 명령이나 잘못된 데이터를 사용하려고 할 때 발생
  - Division By Zero
  - OverFlow/UnderFlow
  - 기타 Exception
### 3. S/W Interrupt (= Exception, = Trap)
- 프로그램 처리도중 오는 요청

### Interrupt의 구성요소
1. Source : 누가 Interrupt를 호출 했는가?
2. Priority: 2개 이상의 Interrupt가 존재할 때, 무엇을 먼저 처리 할 것인가?
3. InterruptVector: InterruptServiceRoutine의 시작주소


## 동작순서
1. IRQ (인터럽트 요청)
2. Program 실행 중단 (현재 실행중이던 Mirco Operation 까지는 수행한다.)
3. 현재의 Program 보존 (PCB, PC 등)
4. Interrupt 처리 루틴 실행
5. Interrupt Service 루틴 실행
   - Interrupt 원인을 파악하고 실질적인 작업을 수행한다.
   - Service루틴 수행중, 우선수위가 더 높은 Interrupt 발생 시, 재귀적으로 1~5 수행
6. 상태복구 (PCB, PC 등)
7. 중단된 Program 재개

## Interrupt 우선순위
**일반적으로 H/W Interrupt가 S/W Interrupt보다 우선수위가 높고, External Interrupt가 Internal Interrupt보다 우선순위가 높다.**
1. PowerFail (전원 이상)
2. Machine Check (하드웨어 오류)
3. External (외부신호)
4. I/O (입출력)
5. 명령어 잘못입력
6. S/W Interrupt(프로그램 오류)
7. SVC (Supervisor Call)

### Sysetm Call과의 차이
```text
System Call은 프로그램이 Kernel Layer에서 할 수있는 작업을 가능하도록 OS에서 제공해주는 것이며,
Interrupt는 CPU가 특정한 작업을 하도록 지시 하는 것이다.
```