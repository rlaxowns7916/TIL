# ChannelHandler
- **Network 이벤트에 의해서 Trigger 된다.**
- Inbound / Outbound를 처리하는 Handler이다.
  - Inbound: channelRead(), channelActive(), exceptionCaught()
  - Outbound: write(), flush()
- 데이터에 수정을 가하는 등의 이벤트를 처리할  수 있으며, ChannelPipeline에 있는 다음 Handler에게 넘길 수 있다.
- 같은 PipeLine에 InboundHandler, OutboundHandler가 포함 될 수 있으나, 내부적인 구현으로 구분하여 슌서대로 전달한다. (같은 타입일 때만)
- **Unix의 PipeLine과 같이 ChannelPipeLine에 있는 선행 Handler의 Output은 후행 Handler의 Input이 된다.**

## 생명주기
1. handlerAdded: ChannelHandler가 pipeLine에 추가될 때 호출된다.
2. handlerRemoved: ChannelHandler가 ChannelPipeLine에서 제거될 때 호출됨
3. exceptionCaught: ChannelPipeLine에서 처리중에 오류가 발생하면 호출됨

## Inbound
```java
public class MyInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Inbound: " + msg);
        // 데이터 수정 후 다음 핸들러로 전달
        String modifiedMsg = ((String) msg).toUpperCase();
        
        // 다음 Handler가 있다면
        ctx.fireChannelRead(modifiedMsg);
        
        // 마지막이라면
        ReferenceCountingUtil.release(msg);
    }
}
```
- channelRead()를 재정의하는 경우에는 ByteBuf를 release() 하는 책임을 갖는다.
  - ReferenceCountingUtil.release()를 통해 해제 가능하다. 
  - 혹은 다음 Handler로 넘긴다 (ctx.fireChannelRead())
    - super.channelRead()는 내부적으로 fireChannelRead()를 호출하나, 명시적인 의도를 드러내기 위해서 fireChannelRead()를 권장한다.

## Outbound
```java
public class MyOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("Outbound: " + msg);
        // 데이터 수정 후 다음 핸들러로 전달
        String modifiedMsg = ((String) msg).toLowerCase();
        ctx.write(modifiedMsg, promise);
        
        // 이 Handler에서 msg를 폐기하고 메세지를 제거할 것 이라면
        ReferenceCountingUtil.release(msg);
        promise.setSuccess();
    }
}
```
- outboundHandler에서 write를 처리하고 Message를 해제하려면 ReferenceCount를 조절해주면 된다.
- DownstreamHandler로 write를 넘기기만 할 뿐이다.
  - 실제 Network를 통한 Socket통신은 flush 시점에 발생한다. (writeAndFlush도 가능)

## Encoder & Decoder
- **Decoder, Encoder 모두 ChannelHandler의 구현체**
- Decoder: Inbound의 ByteArray -> Java객체로 Decoding (ex: ByteToMessageDecoder)
- Encoder: Outbound의 Java객체 -> ByteArray로 Encoding (ex: MessageToByteEncoder)

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
- ServerBootStrap은 주로 ChannelInitializer (init()을 override) 를 통해서 pipeLine에 등록한다.
  - ChannelInitializer도 ChannelHandler이기 떄문에, initChannel에서 handler들을 등록한 후 자신을 제거한다.

## 수정 Method
1. add계열: pipeLine에 새로운 Handler를 추가한다.
   - addFirst()
   - addBefore()
   - addAfter()
   - addLast()
2. remove(): pipeLine에서 handler를 제거한다.
3. replace(): handler를 다른 handler로 교체한다.

# ChannelHandlerContext
- **ChannelHandler와 ChannelPipeline 사이의 연결고리를 제공한다.**
- **Runtime에 pipeLine에 대한 조작이 가능해진다.**
- 각 Handler에 주어지는 Context 객체이며, Handler가 다양한 적업을 수행할 수 있도록 돕는다.
- 각 Handler가 PipeLine에 추가될 때마다 고유한 ChannelHandlerContext가 생성된다.
- channelHandlerContext에서 사용할 수 있는 API는 현재 Handler를 기준으로 전파된다.