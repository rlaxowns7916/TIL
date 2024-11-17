# I/O Stream
- Java에서는 I/O Stream을 통해 데이터를 주고 받는다.
  - 데이터가 이동하는 통로이다.
- Data를 읽고 쓰는 표준화된 방법을 제공
  - 순차적으로 Data를 순차적으로 읽는 추상화된 방법
- **단방향 흐름이며, 일회용이다.**

### InputStream
- 단방향 ReadStream
- Byte를 읽어드리는 Java 추상 클래스
  - 파일 저장, 네트워크 전송 모두 Byte 단위로 이루어진다.
  - FileInputStream, ByteArrayInputStream, SocketInputStream 등의 구현체가 있다.
  - read(), read(byte[]), readAllBytes() 제공

### OutputStream
- 단방향 WriteStream
- Byte를 쓰는 Java 추상 클래스
  - 파일 저장, 네트워크 전송 모두 Byte 단위로 이루어진다.
  - FileOutputStream, ByteArrayOutputStream, SocketOutputStream 등의 구현체가 있다.
  - write(), write(byte[]) 제공

### 기본 Stream
- FileOutputStream, FileInputStream과 같이 단독으로 사용 될 수 있는 Stream을 나타낸다.

### 보조 Stream
- BufferedInputStream, BufferedOutputStream과 같이 기본 Stream을 보조하여 사용하는 Stream을 나타낸다.
  - 보조 Stream을 Close()하면 내부적으로 flush()가 먼저 호출되며, 연쇄적으로 내부 Stream이 Close()된다.
  - 보조하는 대상의 원본 (FileOutputStream,... )이 먼저 Close 된다면 Flush되지 못하고 데이터가 손실 될 수 있다.

## 주의 해야 할 점

### [1] Close
- Stream을 사용한 후에는 닫아야 한다.
  - 외부자원 (Network, File)을 사용하는 Stream을 닫아주지 않으면 Leak이 발생 할 수 있다.
    - 외부자원은 GC의 대상이 아니다.
  - Memory내부에서 사용되는 Stream (ex: ByteArrayOutputStream)은 close()메소드가 override 되어있지만 아무런 동작을 하지 않는다.
- Stream을 닫지 않으면 Memory Leak, File Lock 등의 문제가 발생할 수 있다.
- try-with-resources, try-catch-finally를 사용하여 닫을 수 있다.

### [2] Buffer 사용은 필수
- Buffer를 사용하면 I/O작업의 성능을 높일 수 있다.
  - Buffer는 데이터를 한 곳에서 다른 곳으로 전송하는 동안 일시적으로 그 데이터를 보관하는 메모리 영역이다.
- Buffer를 사용하면, 데이터를 일정 크기만큼 한번에 읽어들이거나 쓸 수 있다. (한 Byte씩 읽어들이는 것 (X))
  - BufferedInputStream의 경우, Buffer의 크기만큼 원본에서 읽어온 후, read() 호출 시, 1Byte 씩 리턴한다.
  - BufferedOutputStream의 경우, Buffer의 크기만큼 채워지면, 원본에 write() 한다.
- DISK에서 Read/ Write의 기본단위가 보통 4KB, 8KB이기 때문에, 그 이상 늘린다고 성능이 비례하지는 않는다.
- ex)
  - BufferedInputStream, BufferedOutputStream  
  - BufferedReader (read())
  - BufferedWriter (write())

### [3] Read
- 하나의 Byte 씩 읽어온다.
- 파일의 끝에 도달하거나, 읽어올 Byte가 없다면 -1을 리턴한다. (EOF)
  - java의 read()가 int를 반환하는 이유 -> Java의 Byte는 -128 ~ +127의 값을 표현 가능(EOF를 위한 특별한 값 할당이 어려움) // Int로 표현하면 0~255로 표현하고, 표현할 것이 없으면 -1로 표현하면됨
- readBytes vs readAllBytes
  - readBytes: 1M단위로 나누어서 데이터를 읽어온다. (대용량에 적합) // read() 한번 == SystemCall 한번 이기 떄문에, 최적화가 필요하다
    - Buffer를 통해서 최적화 할 수 있다.
  - readAllBytes: 한번에 모두 읽어오기 때문에, OOM이 발생 할 수 있다. (한번에 모든 내용을 데이터에 올려서 사용해야 할 때)
    - 읽어 들일 때는 내부적으로 4KB, 혹은 8KB로 읽어들여 최적화한다.

## example
```java
class Main{
    public static void main(String[] args) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             InputStream inputStream = new FileInputStream("example.txt")) {
            byte[] dataBuffer = new byte[1024];
            int length;
            while ((length = inputStream.read(dataBuffer)) != -1) {
                buffer.write(dataBuffer, 0, length);
            }
            byte[] data = buffer.toByteArray();
            // 데이터 사용
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```