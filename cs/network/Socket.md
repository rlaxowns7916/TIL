# Socket
- TCP/IP를 통한 논리적 연결시, 사용되는 소프트웨어적인 인터페이스
- Network 상의 다른 프로그램과 데이터를 송/수신 할 수 있게 해주는 소프트웨어 구조
  - Socket에 데이터를 쓴다. (송신) / Socket에서 데이터를 읽는다. (수신)
- 하나의 Port에 여러개의 Socket을 열 수 있다.
  - 아래의 4개를 조합하여 사용하기 때문에 유일성 확인이 가능하다.
    - srcIp
    - srcPort
    - dstIp
    - dstPort
- ServerSocket과 ClientSocket 모두 자신의 Socket만 관리하면 된다.
  - read() 시, OS가 Socket의 수신버퍼에 저장해둔 데이터를 읽어갈 수 있다.
  - write()시, Osrk Socket의 송신버퍼에 데이터를 저장해두고, NIC를 통해서 패킷으로 분할되어 전송된다.

## Server Socket
(Netty에서의 bossGroup 역할은 OS가 수행한다.)
1. Socket을 열고, Bind를 통해서 IP와 Port를 할당한다.
2. Listen을 통해서 클라이언트의 접속을 기다린다. (다수의 클라이언트를 받을 수 있다.)
3. accept를 통해서 클라이언트의 접속을 받아들인다.

**Server NIC -> OS TCP 수신 버퍼 -> Application**

## Client Socket
1. Socket을 열고(연결 당 새로운 Port 할당), Connect를 통해서 Server에 접속한다.
2. Server와 통신을 한다. (하나의 ClientSocket은 하나의 ServerSocket과 연결된다.)
3. 통신이 끝나면 Socket을 닫는다.

**Application -> OS TCP 송신 버퍼 -> Client NIC**

### BackLog Queue
- Server의 OS에서 관리하는 Connection 관리 Queue
  - SYN Queue
    - Client로부터 Syn 패킷을 받게되면 이 Queue에 Append 된다. (추적 목적)
    - 이 대기열이 꽉차게 되면, (e.g: SYN Flood Attack) 이후의 연결이 Drop 될 수 있다.
  - Accept Queue
    - 3Way HandShake가 완료되면, Accept Queue에 Append 된다.
    - ServerSocket이 Accept를 할 경우, Queue에서 제거된다.

## File
- Socket은 File이다. (File과 일관된 인터페이스를 통해서 사용이 가능하다.)
  - File Descriptor를 통해서 File을 읽고 쓴다.
  - read(), write()를 통해서 데이터를 읽고 쓴다.
    - Blcking I/O
    - read()는 Blocking이다. (per Thread)
  - select(), poll(), epoll() 등을 통해서 File의 상태를 모니터링 할 수 있다.
    - Non-Blocking I/O (이벤트를 감지 한 후, NonBlocking Mode read(), write() ==> 바로반환 (있으면 처리, 없으면 에러코드)
- ```text
    A way to speak to other programs using standard Unix file descriptors
  ```

## Kernel Level
- socket 자체는 KernelLevel 이다.
  - bind(), listen(), accept()와 같은 SystemCall을 통해서 UserLevel에서 접근이 가능해진다.
- Kernel은 NIC와 상호작용하며, 무결성 검증 및 실제 데이터를 주고받는다.


## Socket 상태
- CLOSED: 초기상태, Socket이 열려있지 않거나 닫힌 상태
- LISTEN: ServerSocket이 클라이언트의 연결 요청 (Syn 패킷)을 기다리는 상태
- SYN-SENT: ClientSocket이 ServerSocket에게 Syn 패킷을 보내고 응답을 기다리는 상태
- SYN-RECEIVED: ServerSocket이 ClientSocket에게 Syn + Ack 패킷을 보내고 응답을 기다리는 상태
- ESTABLISHED: 3Way HandShake가 완료되어 데이터를 주고받을 수 있는 상태 (데이터 통신이 가능한 상태)
- FIN-WAIT-1: 연결 종료 요청을 보내고 응답을 대기하는 상태
- FIN-WAIT-2: 연결 종료 요청을 보낸 후 상대방의 ACK를 수신 후, FIN 요청을 대기하는 상태
- CLOSE-WAIT: 상대방의 FIN 응답을 받고, 자신의 Socket이 닫히기를 기다리는  상태