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

## 생명주기
1. ChannelUnregistered: Channel이 생성됨 (EventLoop 등록 이전)
2. ChannelRegistered: Channel이 EventLoop에 등록됨
3. ChannelActive: Channel이 활성화됨 (원격 Peer와 연결 성공)
4. ChannelInactive: Channel 연결이 끊어짐 (원격 Peer와 연결 해제)

## 주요 구현체
- NioSocketChannel: NIO 기반 TCP 소켓.
- NioServerSocketChannel: NIO 기반 서버 소켓.
- NioDatagramChannel: NIO 기반 UDP 소켓.
- EmbeddedChannel: 테스트 용도로 사용되는 가상 채널.
- LocalChannel/LocalServerChannel: JVM 내에서만 통신하는 로컬 채널.

## Event
- 전체 PipeLine을 통해 전파된다.
  - ChannelHandlerContext와의 차이점 (ChannelHandlerContext는 현재 Handler를 기준으로 전파)

## Inbound 이벤트

| 이름                             | 설명                                            |
|----------------------------------|-----------------------------------------------|
| `fireChannelRegistered()`        | DownStreamHandler의 channelRegistered호출        |
| `fireChannelUnregistered()`      | DownStreamHandler의 channelUnregistered호출      |
| `fireChannelActive()`            | DownStreamHandler의 channelActive 호출 (연결 완료)   |
| `fireChannelInactive()`          | DownStreamHandler의 channelInactive 호출 (연결 종료) |
| `fireExceptionCaught(Throwable)` | DownStreamHandler의 exceptionCaught 호출         |
| `fireChannelRead(Object)`        | DownStreamHandler의 channelRead 호출             |
| `fireChannelReadComplete()`      | DownStreamHandler의 channelReadComplete d호출    |

## Outbound 이벤트

| 이름                            | 설명                                                                |
|---------------------------------|-------------------------------------------------------------------|
| `bind(SocketAddress)`           | 로컬주소에 바인딩 / DownStreamHandler의 bind()를 호출한다.                      |
| `connect(SocketAddress)`        | 원격 주소에 연결 / DownStreamHandler의 connect()를 호출한다.                   |
| `disconnect()`                  | 연결 끊기      / DownStreamHandler의의 distonnect()를 호출한다.              |
| `close()`                       | Channel 닫기, / DownStreamHandler의 close()를 호출한다.                   |
| `deregister()`                  | EventLoop에서 Channel 등록 해제 / DownStreamHandler의 deregister()를 호출한다. |
| `flush()`                       | 버퍼의 데이터를 네트워크로 전송 / DownStreamHandler의 flush()를 호출한다.             |
| `write(Object)`                 | 데이터를 버퍼에 기록 / DownStreamHandler의 write()를 호출한다.                   |
| `writeAndFlush(Object)`         | 데이터를 버퍼에 기록하고 즉시 전송                                               |
| `read()`                        | 수동으로 데이터 읽기 요청 / DownStrea,Handler의 read()를 호출한다.                 |