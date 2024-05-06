# I/O Stream
- Java에서는 I/O Stream을 통해 데이터를 주고 받는다.
  - 데이터가 이동하는 통로이다.
- Data를 읽고 쓰는 표준화된 방법을 제공
  - 순차적으로 Data를 순차적으로 읽는 추상화된 방법
- **단방향 흐름이며, 일회용이다.**

## 주의 해야 할 점

### [1] Close
- Stream을 사용한 후에는 닫아야 한다.
  - 외부자원 (Network, File)을 사용하는 Stream을 닫아주지 않으면 Leak이 발생 할 수 있다.
  - Memory내부에서 사용되는 Stream (ex: ByteArrayOutputStream)은 close()메소드가 override 되어있지만 아무런 동작을 하지 않는다.
- Stream을 닫지 않으면 Memory Leak, File Lock 등의 문제가 발생할 수 있다.
- try-with-resources, try-catch-finally를 사용하여 닫을 수 있다.

### [2] Buffer 사용
- Buffer를 사용하면 I/O작업의 성능을 높일 수 있다.
  - Buffer는 데이터를 한 곳에서 다른 곳으로 전송하는 동안 일시적으로 그 데이터를 보관하는 메모리 영역이다.
- Buffer를 사용하면, 데이터를 일정 크기만큼 한번에 읽어들이거나 쓸 수 있다. (한 Byte씩 읽어들이는 것 (X))
- ex)
  - BufferedReader (read())
  - BufferedWriter (write())

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