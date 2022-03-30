# Interrupt (인터럽트)
- CPU가 특정기능을 수행하는 도중에 급하게 다른일을 처리하고자 할 때 사용하는 기능이다.
  - 한개의 CPU는 한번에 하나의 일만 할 수 있기 떄문이다.
- Interrupt가 발생하면 InterruptHandler로 넘어간다.
  - InterruptVector(테이블)에 저장된 InterruptHandler의 주소를 통해 찾아간다. 
  - 수행중이던 작업은 PCB에 기록한다. 
  - Interrupt Handler에는 서비스루틴이 존재한다.
- Interrupt Handler의 서비스루틴이 종료되면 다시 CPU 연산을 재개한다.

## Interrupt의 종류

### 1. External Interrupt
- Timer : 일정한 시간 간격으로 Interrupt
- I/O: I/O 장치가 입출력 준비가 완료되었음을 알림
### 2. Internal Interrupt
- 잘못된 명령이나 잘못된 데이터를 사용하려고 할 때 발생
  - Division By Zero
  - OverFlow/UnderFlow
  - ...
### 3. S/W Interrupt (= Exception, = Trap)
- 프로그램 처리도중 오는 요청
- SystemCall

### Interrupt의 구성요소
1. Source : 누가 Interrupt를 호출 했는가?
2. Priority: 2개 이상의 Interrupt가 존재할 때, 무엇을 먼저 처리 할 것인가?
3. InterruptVector: InterruptServiceRoutine의 시작주소