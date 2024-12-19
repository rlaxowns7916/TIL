# Memory Barrier
- **Memory Barrier는 H/W와 S/W 레벨에서 CPU와 메모리 간의 동작을 제어하여 명령어 재정렬과 메모리 가시성 문제를 방지하는 도구이다.**
  - 다중쓰레드 환경에서의 동작 동기화 및 가시성 문제 해결
- MainMemory 로 강제 Flush 하거나, 다른 코어가 캐시된 데이터를 최신 상태로 유지하도록 보장합니다.
- CPU 수준의 MemoryBarrier 명령이다.
- CPU 아키텍쳐에 따라 차이가 있다.
  - x86 아키텍처
        MFENCE, LFENCE, SFENCE
  - ARM 아키텍처
        DMB(Data Memory Barrier), DSB(Data Synchronization Barrier)

    
## 종류
1.  LoadLoad Barrier
   - 이전 읽기 작업이 완료된 후에 다음 읽기 작업이 시작되도록 보장.
   - ex) A = x를 읽은 후에 B = y를 읽을 때, x의 읽기가 끝나기 전에 y를 읽지 않음. 
2. LoadStore Barrier
   - 읽기 작업이 완료된 후에 쓰기 작업이 시작되도록 보장.
   - ex) A = x를 읽은 후 y = B를 쓰는 경우, x 읽기가 끝난 뒤에야 B가 쓰여짐. 
3. StoreStore Barrier
   - 이전 쓰기 작업이 완료된 후에 다음 쓰기 작업이 시작되도록 보장.
   - ex) x = A를 쓰고 난 뒤 y = B를 쓸 때, A 쓰기가 완료된 후에 B가 쓰여짐. 
4. StoreLoad Barrier
   - 쓰기 작업이 완료된 후에 읽기 작업이 시작되도록 보장.
   - 가장 비용이 큰 배리어로, 쓰기 작업의 결과가 전파된 후 다음 읽기를 보장.
   - ex) x = A를 쓰고 난 뒤, B = y를 읽을 때, A 쓰기가 완료된 뒤에야 y가 읽혀짐.

## Java에서의 동작
- JMM(JavaMemoryModel)에서의 happens-before 관계는 실제 CPU의 MemoryBarrier 명령어를 사용하여 구현된다.
- **Java에서는 volatile, synchronized, final을 통해서 Memory Barrier를 사용한다.**
- **JIT 컴파일러가 생성한 바이트코드는 CPU 메모리 배리어 명령어를 포함한다.**

### MemoryBarrier가 사용되는 명령어
1. volatile
2. synchronized
3. final
4. Lock (java.util.concurrent)
5. UnsafeAPI (LowLevel Memory 제어 API)