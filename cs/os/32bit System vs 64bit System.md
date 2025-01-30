# 32bit System
- x86, x86-32라고 불리며, 32bit 데이터를 처리할 수 있는 Processor
- **4GB의 메모리를 관리 할 수 있따.** (실제 사용가능한 메모리는 3.2GB)
  - 사용가능한 Memory의 양 떄문에 연산 속도가 느리다.
- **32bit 소프트웨어 어플리케이션과만 호환된다.**
- 대표적인 OS는 아래와 같다.
  - Windows95
  - Windows98
  - WindowsXP

# 64bit System
- x64, x86-64라고 불리며, 64bit 데이터를 처리할 수 있는 Processor (실제로는 48~57 bit를 사용)
- 4GB이상의 메모리를 관리 가능하다. (최대 17TB)
- 32bit, 64bit 소프트웨어 어플리케이션이 모두 호환된다.
- 현대의 대부분 OS가 64bit시스템이다.


## 어떤 차이가 있는가
```text
가장 큰 차이는 주소공간 (Address Space) 이며, CPU 레지스터의 크기를 의미한다. (고속도로 차선으로 비유된다)
주소공간이 클 수록, 한번에 더 많은 데이터를 처리 할 수 있다.
Processor가 한번에 처리 할 수 있는 주소의 크기에 따라서 접근 할 수 있는 Memory의 범위가 달라진다.

32bit Processor의 경우, 주소를 나타내는데 32bit를 사용하고, 이론상 최대 43억(2^32)주소를 지정 가능하며 4GB이다.
이마저도 KernelLevel과 UserLevel이 나누어 갖는다. (보통 2GB 씩)
32bit Processor에서 4GB이상의 물리 Memory를 장착해도, 그 이상 사용하기 어렵다.

64bit Processor의 경우 주소를 나타내는데 64bit를 사용하고, 이론상 최대 2⁶⁴개의 주소를 지정할 수 있고 16EB(Exabyte)에 해당한다.
물리Memory를 모두 사용하는게 가능하다.
 

```