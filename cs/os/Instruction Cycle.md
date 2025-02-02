# Instruction Cycle (명령 주기)
- CPU가 명령어를 이해하고 실행하는 방법이다.
- Fetch - Decode - Execute의 과정을 반복한다.

## 명령어의 구성
```text
opCode | operand
```
- opCode를 통해 명령어의 종류 판단
- operand에는 명령어를 실행 할  주소

## CPU의 구성

### 1. ALU (Arithmetic & Logical Unit)
- 연산을 수행한다.
  - 산술연산 (덧셈, 뺄셈)
  - 베타적 논리합, 논리합, 논리곱

### 2. 제어장치 (Control Unit)
- 연산의 순서에 따라 차례대로 실행한다.
- 기억장치, 연산장치, 입출력 장치,에서 제어 신호를 발생시킨다.
- 신호를 수신하며, 다음 수행할 작업을 제어한다.

### 3. 레지스터 (Register)
- CPU상의 임시 기억장치를 의미한다.
- 데이터 접근속도가 매우 빠르다.

### 레지스터의 종류 및 역할

| 레지스터 이름 | 설명 |
|--------------|------------------------------------------------|
| **Memory Buffer Register (MBR)** | CPU와 메모리 간 데이터를 주고받는 범용 레지스터 (데이터 레지스터라고도 함) |
| **Memory Address Register (MAR)** | 접근할 메모리 주소를 저장하는 레지스터 |
| **Instruction Register (IR)** | 현재 실행 중인 명령어를 저장하는 레지스터 |
| **Program Counter (PC)** | 다음 실행할 명령어의 메모리 주소를 저장하는 레지스터 |
| **Accumulator (ACC)** | 연산 결과를 저장하는 레지스터 |
| **General-Purpose Registers (범용 레지스터)** | 다양한 연산 및 데이터 저장에 사용되는 레지스터 |
| **Stack Pointer (SP)** | 스택의 최상위 주소를 가리키는 레지스터 |
| **Status Register / Flags Register (FR, SR, PSW)** | 연산 결과의 상태 (Carry, Zero, Overflow 등)를 저장하는 레지스터 |
## Fetch
**명령어를 가져오는 단계**
- PC (ProgramCounter)로 부터 실행할 명령어의 메모리주소를 가져온다.
- 가져온 주소를 IR에 저장한다.

## Decode
- 명령어의 종류와 대상을 알아낸다.
- IR에 저장되어 있는 인코딩된 명령어를 Decoder에 올려서 해석 한다.

## Execute
- operand를 ALU로 보내 연산을 수행한다.
- 처리 결과는 또다른 레지스터로 보내진다.
- 이 작업이 끝나면 다시 PC로 돌아가서 위 과정을 반복한다.