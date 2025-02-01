# IPC (InterProcessCommunication)
- 프로세스는 독립적인 메모리영역을 가지고 있다.
  - 서로 독립적이기 때문에, 다른 프로세스의 메모리영역에 접근 할수 없다.
- IPC를 통해서 OS에서 실행중인 프로세스간 커뮤니케이션을 진행할 수 있다.
- 프로세스간 통신하는 방법이다.

## IPC 구현의 주요 개념
1. SharedMemory
   - 공유할 수 있는 메모리 공간에 데이터를 저장한다.
   - 서로 공유된 메모리에 접근한다.
2. MessagePassing
   - OS에게 위임한다.
   - 다양한 방법으로 구현된다.
     - Direct / Indirect
     - Synchronous / Asynchronous

### 기법
1. MessagingQueue
    - **OS가 제공하는 Queue를 이용하는 방식**
    - **Kernel 내부에서 msgsnd()와 msgrcv()연산을 원자적으로 처리하여, 한 번에 하나의 프로세스만 큐를 수정할 수 있다.**
      - **대기중인 Process 중 하나만 Message를 가져갈 수 있다.**
    - 양방향 통신이 가능하다.
    - 프로세스간 메모리 공유없이 실행된다.
2. SharedMemory (RAM 기반)
   - **특정 Memory를 여러 Process가 공유하도록 설정하는 것이다.**
   - **Kernel을 거치지 않고 읽고 쓰기 때문에 매우 빠르다.**
   - **임계영역이기 떄문에, 동기화가 필요하다.**
3. Pipe (File 기반)
    - 한 Process가 Write하면 다른 Process가 읽을 수 있는 일종의 Queue
    - 단방향 (주로 부모/자식 Process 관계에서 사용된다.)
    - Anonymous Pipe와 Named Pipe로 구분된다.
4. Socket
   - Network를 통한 Process간 통신 방식
   - 파일의 일종이다.
   - OS 내부의 소켓 기술로 IPC를 구현한다.
