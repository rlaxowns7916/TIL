# Selector
- **하나의 Thread로 여러개의 Channel을 관리한다.**
- **Selector는 Channel들을 등록하여, 각 채널에서 발생하는 I/O 이벤트(읽기, 쓰기, 연결, 수락 등)를 모니터링한다.**
- **Event가 준비되면 Thread를 깨워서 처리한다.**

## 어떻게 Selector는 Event를 Monitoring 하는가
```text
 Java NIO의 Selector는 내부적으로 각 OS에서 제공하는 I/O Multiplexing 기능을 사용하여 Event를 Monitoring 한다. 
 
 - epoll(Linux) & EPollSelectorImpl, 
 - kqueue(macOS / BSD) & KQueueSelectorImpl, 
 - poll / select(기타 Unix 계열) & PollSelectorImpl, 
 - IOCP(Windows) * WindowsSelectorImpl

```

## SelectionKey
- Channel을 Selector에 등록하면 반환되는 객체이다.
- 해당 Channel에 대한 어떤 Event를 Monitoring 할 것인지의 정보가 들어있다.
- Selector에게 Monitoring을 취소하게 할 수 있다.

### Register Option
1. OP_ACCEPT: 연결이 수락되고, Channel이 생성되면 알린다.
2. OP_CONNECT: 연결이 생성되면 알린다.
3. OP_READ: Channel에서 데이터를 읽을 수 있으면 알린다.
4. OP_WRITE: Channel에 데이터를 쓸 수 있으면 알린다.

## 주요 메소드
1. select()
    - 이벤트가 발생하지 않으면, Thread는 대기한다.
      - interrupt, wakeUp()등을 통해서 깨어나면서 0을 리턴 할 수도 있다.
    - 이벤트 처리가 가능한 Channel의 숫자를 리턴한다.
2. selectNow()
    - 즉시 반환하며, 없다면 0을 반환한다.
3. select(long timeout)
   - 일정 시간 동안만 select()를 수행한다.
4. wakeUp()
   - 다른 Thread에 의한 wakeUp은 select를 끝내고 바로 반환하게 한다.

## 예제
```java
public class SimpleNioServer {
    public static void main(String[] args) throws IOException {
        // 1. Selector & ServerSocketChannel 생성
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);

        // 2. Channel 등록
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port 8080...");

        while (true) {
            // 3. 이벤트 대기
            int readyCount = selector.select();
            if (readyCount == 0) continue;

            // 4. 준비된 키들 처리
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                // (1) 새 연결 수락
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    // 클라이언트는 읽기 이벤트로 등록
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Client connected: " + client.getRemoteAddress());
                }
                // (2) 데이터 읽기
                else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    int bytesRead = client.read(buffer);
                    if (bytesRead == -1) {
                        client.close();
                        System.out.println("Client disconnected.");
                    } else {
                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        String msg = new String(data);
                        System.out.println("Received: " + msg);

                        // 에코: 다시 쓰기
                        buffer.clear();
                        buffer.put(("Echo: " + msg).getBytes());
                        buffer.flip();
                        client.write(buffer);
                    }
                }
            }
        }
    }
}
```