# ChannelHandler
- Inbound / Outbound를 처리하는 Handler이다.
  - Inbound: channelRead(), channelActive(), exceptionCaught()
  - Outbound: write(), flush()
- 데이터에 수정을 가하는 등의 이벤트를 처리할  수 있으며, ChannelPipeline에 있는 다음 Handler에게 넘길 수 있다.
- 같은 PipeLine에 InboundHandler, OutboundHandler가 포함 될 수 있으나, 내부적인 구현으로 구분하여 슌서대로 전달한다. (같은 타입일 때만)
- **Unix의 PipeLine과 같이 ChannelPipeLine에 있는 선행 Handler의 Output은 후행 Handler의 Input이 된다.**

## Inbound
```java
public class MyInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Inbound: " + msg);
        // 데이터 수정 후 다음 핸들러로 전달
        String modifiedMsg = ((String) msg).toUpperCase();
        ctx.fireChannelRead(modifiedMsg);
    }
}
```

## Outbound
```java
public class MyOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("Outbound: " + msg);
        // 데이터 수정 후 다음 핸들러로 전달
        String modifiedMsg = ((String) msg).toLowerCase();
        ctx.write(modifiedMsg, promise);
    }
}
```

## 예외처리
- exceptionCaught를 통해서 예외처리가 가능하다.
- Netty는 Exception이 발생하면, 현재 Pipeline에서 예외를 처리하기 위해서 exceptionCaught를 호출한다.
- **현재 Handler에서 exceptionCaught를 구현하지 않거나, Method내부에서 ctx.fireExceptionCaught(cause)를 호출하지 않으면, 다음 Handler로 전파된다**
- **Exception이 PipeLine 끝가지 전달되었음에도 처리가 되지 않으면 Netty의 기본 구현으로 처리된다.**
  - Exception을 Logging
  - Channel을 Close

### 예시
```text
[Handler A] -> [Handler B] -> [Handler C]
```
- Handler B에서 ExceptionCaught --> Exception을 전파하지않음

```text
[Handler A] -> [Handler B] -> [Handler C]
[exceptionCaught X]
```
- Logging 및 Channel Close

# ChannelPipeLine
- Channel이 생성되면 자동으로 할당된다.
- Inbound, Outbound에서 Event를 처리하는 Handler를 체이닝 한 구조
  - **InboundHandler는 Chain 앞 -> 뒤로 흐른다**
  - **OutboundHandler는 Chain 뒤 -> 앞으로 흐른다**
- intercepting-filter 구조를 통해서 개발자가 자유롭게 Handler를 추가 가능하다.
