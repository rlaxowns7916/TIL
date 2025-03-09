# Channel
- Netty에서 Network 연결을 추상화 한 것 
  - Channel의 현재 상태
  - 설정 파라미터 (송신 Buffer Size, 수신 Buffer Size)
  - I/O Operation (Read, Write, Connect, Bind)
  - I/O Event처리를 위한 PipeLine (ChannelPipeLine)
- EventLoop와 연결되어, 비동기적으로 작업을 처리한다.
  - Channel은 하나의 EventLoop에 속한다. (EventLoop는 여러개의 Channel을 가질 수 있다.)
- ChannelPipeLine을 통해서 IO를 처리한다.
  - Inboud > read(), channelActive()
  - Outbond > write(), channelInactive()
- Channel은 고유하다.
  - Comparable을 구현하고있고, 같은 hash값이 나오면 compareTo에서 Error가 발생한다.n
  

## LifeCycle
## [1] 생성 (Channel Created)
- Channel이 생성되지만 아직 연결되지 않은 상태. 
- Bootstrap이 Channel을 생성하는 시점 (connect(), bind() 호출 전)

## [2] 활성화 (Channel Active) 
- connect() 호출 후, Channel이 활성화되어 데이터 송수신이 가능한 상태.
- 
- 읽기/쓰기 (Channel Read/Write): 데이터 송수신 작업 수행.
- 비활성화 (Channel Inactive): 연결이 끊어진 상태.
- 종료 (Channel Unregistered): 리소스가 해제되고, EventLoop에서 Channel이 제거된 상태.


## 주요 구현체
- NioSocketChannel: NIO 기반 TCP 소켓.
- NioServerSocketChannel: NIO 기반 서버 소켓.
- NioDatagramChannel: NIO 기반 UDP 소켓.
- EmbeddedChannel: 테스트 용도로 사용되는 가상 채널.
- LocalChannel/LocalServerChannel: JVM 내에서만 통신하는 로컬 채널.