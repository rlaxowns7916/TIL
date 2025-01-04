# Channel
- Java NIO(1.4부터 포함)에 의해서 제공된다.
- 실제 I/O 대상(File, Socket)과의 연결을 표현한다.
- ThreadUnsafe
- 일반적인 I/O와 다르게 Buffer를 사용하여 비동기적으로 파일에 접근한다.
  - File의 특정부분에 대한 Read / Write
  - File을 다른 File로 복사
  - Memory에 매핑
- 일반적인 I/O에 비해서, 큰 파일을 읽을 때 장점이 있다.
- 회수되어야 하는 자원이다. (try-with-resources)
  - Network, FileHandler, Socket 등에 연결되어 있는 경우가 많기 때문이다.
- 양방향 Read/Write를 지원한다.
  - inputStream(), outputStream()처럼 별도로 필요하지않다.
- BlockingI/O NonBlocking I/O 모두지원한다.
  - NonBlockingI/O는 Selector와 함께 사용되며, Data Read / Write 를 할 준비가 되어있지 않다면 바로 반환한다.
- 데이터를 ByteBuffer 단위로 주고받는다.

## 특징

### [1] Buffer 기반 I/O
- ByteBuffer라는 메모리 데이터블록을 가지고 있다.
- ByteBuffer에 먼저 기록한 후, Buffer <--> File로 데이터를 전송한다.

### [2] Async I/O
- File을 Read하거나 Write하는 순간에 CPU가 다른 작업을 수행할 수 있게하여 I/O의 병렬성과 효율성을 향상시킨다.

### [3] RandomAccess
- File의 임의의 위치에 데이터를 읽거나 쓸 수 있다.
- RandomAccess를 통해서, File의 특정부분을 빠르게 읽거나 쓸 수 있다.

### [4] File Locking
- File에 대한 잠금기능을 제공한다.
- File 전체 / 일부에 대한 Lock이 가능하다.

### [5] (Native) Memory Mapping
- DirectByteBuffer를 통해서 NativeMemory에 매핑 가능하다.
- File을 Disk에서 읽지않고 데이터를 Memory처럼 다룰 수 있게 한다.

### [6] ZeroCopy
- transferTo(), transferFrom()을 통해서 ZeroCopy기능을 제공한다.
- 사용자공간이 아닌, 커널 공간에서만 직접 전송하여 성능을 극대화한다.


## 생성법

### [1] Read 모드
```java
class ReadMode{
    public static void main(String[] args) {
        try(
            FileInputStream fis = new FileInputStream("test.txt");
            FileChannel channel = fis.getChannel();
        ){
            ByteBuffer 
        } 
    }
}
```

### [2] Write 모드
```java
class WriteMode{
    public static void main(String[] args) {
        try(
            FileOutputStream fos = new FileOutputStream("test.txt");
            FileChannel channel = fis.getChannel();
        ){
            
        }
    }
}

```

### [3] Read & Write 모드
```java
class ReadAndWriteMode{
    public static void main(String[] args) {
        try(
                RandomAccessFile raf = new RandomAccessFile("test.txt");
                FileChannel channel = raf.getChannel();
        ){

        }
    }
}
```

## ZeroCopy
- Channel간의 데이터를 전송할 때 사용한다.
- transferTo(), transferFrom()을 사용하여 zeroCopy를 구현할 수 있다.
- OS Level에서 데이터를 직접 SocketBuffer로 전송하기 때문에, Kernel 공간에서만 전송이 이루어진다. (UserMode로 복사 (X))
```java
public class ClientExample {
    public static void main(String[] args) {
        try (
            // 클라이언트 소켓 채널 생성
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9000));
            FileInputStream fis = new FileInputStream("source.txt");
            FileChannel fileChannel = fis.getChannel();
        ) {
            System.out.println("Connected to server. Sending file...");

            // transferTo 메서드를 사용하여 데이터를 파일 채널에서 소켓 채널로 전송
            long transferredBytes = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
            System.out.println("Sent " + transferredBytes + " bytes to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```