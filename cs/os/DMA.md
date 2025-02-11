# DMA (Direct Memory Access)
- **CPU의 개입 없이, 주변장치와 메모리(RAM)간의 데이터 전송을 수행하는 기능이다.**
  - 전용 DMA Controller가 수행한다.
- CPU의 부담을 줄이고, 데이터 전송 속도를 높이기 위해 사용한다.
  - Interrupt발생 회수를 최소하여 효율성을 높인다.
    - CPU에 의해서만 작업이 이루어질 경우, 주변장치가 메모리접근을 할 떄마다 Interrupt가 발생
- 누가 Disk에서 kernel memory buffer로 올리냐가 차이이다.
   - cpu: 일반적인 I/O
   - DMAcontroller: DMA
- KernelSpace에서 UserSpace로의 복사를 줄이는 방법은 zeroCopy
## 과정
1. CPU는 DMA Controller에게 어떤 Address로 데이터를 보내라는 명령을 전달
2. DMA Controller는 MemoryBus를 통해 메모리에 접근하여 데이터를 전송
3. 전송이 완료되면 CPU에게 Interrupt를 발생시킨다. (전송완료 Interrupt)
4. CPU는 Interrupt를 수신한 후 후처리를 수행