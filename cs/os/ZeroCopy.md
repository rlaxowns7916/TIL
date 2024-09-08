# ZeroCopy
- Data 전송을 최적화하여, CPU사용량을 줄이고 복사를 최소화 하는 기술
  - 불필요한 중간단계를 생략한다.
  - Kernel 내에서만 데이터를 전송하여, 효율과 속도를 올린다.
- Network, File I/O에 주로 사용된다.
- DirectMemoryAccess(DMA)가 이용된다.

## 전통적인 I/O
1. Disk --> Kernel 데이터 복사
2. Kernel --> User 데이터 복사 (사용자 프로그램이 사용 할 수 있도록)
3. User --> Kernel 데이터 복사 (Network, Disk에 전송하기 위해서)
4. Kernel --> Network 데이터 복사 

## ZeroCopy I/O
1. Disk --> Kernel 데이터 복사
2. MemoryMapping (Kernel 공간의 Memory를 매핑하여, 사용자 프로그램이 접근 가능하도록 함)
3. Kernel --> Network 데이터 복사 (MemoryMapping된 데이터를 Network로 전송)