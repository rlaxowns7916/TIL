## BootStrap
- Netty 통신의 **Client 시작점**을 추상화 한 것이다.
- BootStrap객체는 Thread-Safe하다.
- Netty의 수행할 동작과, 환경설정을 제어하는 기본이된다.
  - 초기화
    - 다양한 Event를 처리하기 위한 PipeLine을 생성한다.
  - 이벤트 처리
    - 만들어둔 PipeLine 및 Event를 Handling하는 EventHandler를 정의한다.
  - 옵션 설정
    - Network 설정 및 튜닝
  - 연결
    - 연결이 성공하면 미리정의해둔 EventHandler를 통해서 통신을 처리한다.
    - connect() 메소드는 Channel(Network연결, 데이터 교환을 대표)을 리턴헌다. 
- AbstractBootStrap이라는 추상클래스를 가진다.
  - **Client가 사용하는 BootStrap과, Server가 사용할 ServerBootStrap으로 나뉜다.**
  - Builder 패턴을 통해서 쉽게 설정이 가능하다.

### 구성요소
#### [1] BootStrap
- Client측 연결을 위한 시작점
- ThreadSafe하며, 설정과 관련된 여러가지 Method를 제공한다.

#### [2] EventLoopGroup
- I/O작업을 처리하는 EventLoop(단일 Thread) 들의 Group
- Netty에서 다양한 EventLoopGroup을 제공한다.
  - 대표적으로는 **NioEventLoppGroup** 이 있다.

#### [3] Channel
- 네트워크 연결을 추상화 한 것
- 데이터 Read/Wriite같은 I/O작업을 수행한다.
  - 실제 I/O작업을 수행하는 Thread들은 EventLoopGroup이 관리하는 Thread들이다.

#### [4] ChannelInitializer
- Channel이 생성될 때, 초기설정을 제공하는 Handler이다.
- 주로 PipeLine에 Handler를 추가하는데 많이 사용된다.

#### [5] Channel PipeLine
- Channel과 관련된 Handler들을 연결하는 목록이다.
- I/O이벤트와 데이터는 PipleLine의 Handler들을 통과하면서 처리된다.

#### [6] ChannelHandler
- I/O Event나 데이터를 처리하는 컴포넌트이다.
- Encoding / Deciding하거나, 연결 상태 변화 감지등의 역항을 한다.

### 구조
1. 전송계층 (소켓 모드 및 I/O 종류)
2. EventLoop (SingleThread / MultiThread)
3. Channel PipeLine 설정
4. Socket 주소 & 포트
5. Socket Option

### EventLoop와 Channel과의 상관관계
```text
EventLoopGroup은 I/O를 수행하는 Thread들을 관리하는 집합이다.
내부적으로 EventLoop들을 가지고 있으며, EventLoop들은 하나의 Thread를 가지고있다.

이 EventLoop들이 Channel의 I/O작업을 수행하게 된다.
하나의 EventLoop는 여러개의 Channel과 관계를 맺는다.

각 Channel마다 Thread를 할당하는 것이 아닌, 하나의 EventLoop가 여러개의 Channeel을 관리하기 때문에 자원 효율적이다.
하나의 EventLoop(Thread)에서 수행되기때문에 아래와 같은 장점이 있다.
- 각 Channel은 ThreadSafe
- ContextSwitching이 발생하지 않는다.


```

## Sample
```java
public class SampleNettyClient{
  public static void main(String[] args) {
    EventLoopGroup eventLoopGroup= new NioEventLoopGroup();
    
    try{
        BootStrap bootStrap = new BootStrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception{
                      /**
                       * 여기서 다른 eventHandler들을 Loop돌면서 초기화 할수도 있다.
                       * 
                       */
                      channelHandlers.forEach{
                          channelHandler -> 
                                    ch.pipeLine().addLast(channelHandler)
                      }
                    }
                });
        Channel channel = bootStrap.connect("localhost",8080).sync().channel();
        
        
        // Close the Connection
        channel.closeFuture().sync();
    }finally {
        // EventLoop 쓰레드를 모두 안전하게 종료하기 위함
        eventLoopGroup.shutdownGracefully();
    }
  }
}
```

### ServerBootStrap
- Server의 설정을 할 때 사용된다.

#### 예제
```java
class EchoServer{
  public static void main(String[] args) {
    /**
     * 단일 Thread로 동작하는 ParentGroup을 생성한다.
     */
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    /**
     * 생성자가 비어있으면, H/W가 가지고 있는 Core 수의 두배의 Thread를 만든다.
     * ChileGroup(Worker)를 만든다.
     */
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    
    try{
        ServerBootstrap sbs = new ServerBootstrap();
        b.group(bossGroup,workerGroup) //제어할 설정 그룹 (bossGroup)과 실제 I/O를 담당할 그룹 (workerGroup)을 할당한다.
                /**
                 * 서버가 사용할 Network 모델을 정의한다.
                 */
                .channel(NioServerSocketChannel.class) 
                .childHandler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    public void initChannel(SocketChannel ch){
                        ChannelPipeLine p = ch.pipeline();
                        p.addLast(new EchoServerHandler()); // 파이프라인에 Handler를 추가한다.
                    }
                }); // ChildGroup의 역할을 정의한다.
    }finally {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
  }
}

```