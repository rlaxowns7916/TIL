# JIT(Just-In-Time) Compiler
- 일종의 Java의 캐시이며, 성능을 향상시키는 방법이다.
- Java의 인터프리터(JVM -> 기계어) 방식을 보완하는 방식이다.
- **Method 단위로 컴파일 하고, 캐싱한다.**
- 기본적으로 자동 실행된다.
  - JVM이 시작 될 때, JIT Compiler도 같이 실행된다.

## Java의 동작과정
- Java는 컴파일 + 인터프리터 방식이 결합된 Hybrid 언어이다.
  - 컴파일 방식: Java코드 -> .Class파일 (Byte Code)
  - 인터프리터 방식: .Class파일 -> 기계어파일 (Machine Code)
- Compile이 된 이후, JVM은 소스코드에 대한 추가적인 해석 없이 .class파일을 바로 호출한다.
  - .class파일을 다시 한줄한줄 읽어서 기계어로 변환하기 때문에 느리다.
  - **JIT Compiler는 자주 사용되는 코드를 기계어파일로 만든 후, 캐싱해서 재사용한다.**
  - Compile을 동적으로 그 때(Just-In-Time) 컴파일 하기 때문에 JIT Compiler 인 것이다.

## JVM WarmUp
- JIT Compiler도 일종의 Cache이기 떄문에, Application이 시작 되었을 때는 아무 것도 존재하지 않는다.
- 의도적으로 로직을 실행시켜서, JIT Compiler를 동작하게 하여 최적화를 수행하게 한다.
- WarmUp에 따라서 최적화의 강도를 조절 할 수 있다.

### Interpreting
- Level 0 이다.
- 기본 동작이며, 단순한 인터프리팅 방식이다.
- 특정 임계치를 넘으면, C1 Compiler의 최적화를 받을 수 있는 큐로 적재한다.

### [1] C1 Compiler
- Level1, Level2, Level3 가 포함된다.
- **ByteCode를 컴파일 하는 역할을 수행한다.**
  - 기계어로 Compile하여, 빠르게 실행 될 수 있도록한다.
- Level3 + 특정 임계치를 넘으면 C2 Compiler의 최적화를 받을 수 있는 큐로 적재한다.
  - 큐가 차있으면, Level2로 다시 내린다.
  - Level2는 임계치를 넘으면 Level3가 되고, 다시 큐로 들어가려는 역할을 수행한다.

### [2] C2 Compiler
- Level4 이다.
- **ByteCode를 컴파일 하고, Caching 한다.**
  - C1 Compiler의 최적화와 더불어, 추가적인 최적화를 수행한다.
  - Cache에 저장 한 후, 동일한 코드가 실행 될 시에 Cache에 있는 기계어를 다시 수행한다.
- 최적화된 코드를 제공하여, 전반적인 성능향상을 제공한다.

