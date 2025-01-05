# ChannelFuture
- Java의 Future가 Blocking만을 지원하기 때문에, Netty에서 구현한 구현체
- ChannelFutureListener를 통해서 비동기 Callback 수행을 가능하게 해준다.

## 주요 Method
### [1] addListener(ChannelFutureListener listener)
- ChannelFutureListener를 등록한다.
- ChannelFutureListener는 등록된 작업이 끝날 때 호출된다.

### [2] sync
- ChannelFuture가 완료될 때까지 대기한다. (Blocking)

### [3] isDone
- ChannelFuture가 완료되었는지 확인한다.

### [4] isSuccess
- ChannelFuture가 성공적으로 완료되었는지 확인한다.

## 예제
```java
public static void main(String[] args) {
    ChannelFuture future = channel.connect(new InetSocketAddress("localhost", 8080));
    future.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
                ByteBuf buffer = Unpooled.copiedBuffer("Hello World", Charset.defaultCharset());
                future.channel().writeAndFlush(buffer);
            } else {
                Throwable cause = future.cause();
                cause.printStackTrace();
            }
        }
    });
}
```