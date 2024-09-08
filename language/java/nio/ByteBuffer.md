# ByteBuffer
- Java NIO 라이브러리 (1.4버전부터 포함)에 의해서 사용 가능하다.
- 특정 Position (Index)에서 Read와 Write를 수행한다.
  - capacity (Buffer의 총 크기)
  - limit(Buffer를 Read/Write 할 수 있는 범위)
  - position(현재 Read/Write가 발생하는 위치)
  - mark(편의를 위해서 임의로 마킹 할 수 있는 위치)
- File을 Read하거나 Write하는 시점에 임시로 데이터를 들고 있다.
- File을 Memory에서 바로 읽어들인다. (효율적)
  - 읽어들이는 부분에 Lock을 걸어 다른 파일의 접근을 제한 할 수 있다.
  - DataLoss를 방지하기 위해서, File에 즉시 Write를 할 수 있다.


## ByteOrder
- ByteBuffer에서 Byte의 순서(endian)을 지정하는데 사용된다.
  - 다중바이트 데이터타입 (int, long, float, double)을 바이트 배열에 저장하여 읽을 때, 순서를 결정하는데 중요한 역할을 한다.
- 기본적으로 BigEndian 이다.
  - Network에서 데이터는 일반적으로 BigEndian으로 전송된다.
- 플랫폼간 독립성에 유용하다.
  - 플랫폼에 따라서, 알맞은 ByteOrder를 지정하여 사용할 수 있다.

### BigEndian
- ByteOrder.BIG_ENDIAN
- 32비트 정수 0x12345678은 메모리에 [12][34][56][78] 순서로 저장
### LittleEndian
- ByteOrder.LITTLE_ENDIAN
- 32비트 정수 0x12345678은 메모리에 [78][56][34][12] 순서로 저장

## HeapByteBuffer
- 파일의 내용을 Heap Memory에 Load하고 Read / Write를 수행한다.
- HeapMemory에 저장하는 것은 결과적으로 Copy가 필요로 하기 때문에, DirectByteBuffer에 비해서 성능이 좋지않다.
- allocate()를 통해서 HeapByteBuffer를 생성한다.
  - 기본구현 내부적으로는 DirectBuffer를 사용한다고 한다 (아래 블로그 글 참조)

## DirectByteBuffer
- MappedByteBuffer도 DirectByteBuffer에 포함된다.
- **파일의 내용을 NativeMemory에 적재한다.**
  - I/O 작업을 수행하는데에 있어서 FileChannel이 필요하지 않다.
  - **파일의 일부를 NativeMemory 에 적재하여, 실제 메모리의 일부처럼 동작하는 것으로 보이게 하며 성능이 좋다.**
- allocateDirect()를 통해서 DirectByteBuffer를 생성한다.
- DirectByteBuffer의 생성 빈도가 많으면 성능에 악영향을 미칠 수 있다. (비용이 더 크므로, 내부적으로 Cache를 사용한다.)
- 회수가 되지 않는다면 OOM의 원인이된다.
- Native 메모리를 참조하는 객체는 결국 JVM Heap 안에 생성되며, 이 객체가 JVM의 GC에 의해 회수되면 이 객체가 참조하는 Native 메모리는 JVM이 아닌 다른 메커니즘에 의해 어쨌든 회수된다.
  - 혹은 강제회수
  - ```java
        ((DirectBuffer)directBuffer).cleaner().clean()
    ```

### HeapBuffer와 DirectBuffer 분석 블로그 글
https://homoefficio.github.io/2020/08/10/Java-NIO-FileChannel-%EA%B3%BC-DirectByteBuffer/

---
### DirectByteBuffer가 HeapByteBuffer 보다 성능이 좋은 이유
1. NativeMemory 접근
   - OS Library와 직접 상호작용이 가능하다.
   - 데이터가 Memory에서 이동하는 횟수 자체가 줄어든다.
   - NativeMemory에서 FileSystem으로의 전송이 더 빠르게 전송되어 성능상의 이점을 누릴 수 있다.
2. Memory Copy 오버헤드 감소
   - HeapByteBuffer의 경우, NativeMemory에서 HeapMemory로 Copy가 일어나는 과정이 존재한다. (추가적인 Memory Overhead)
3. GC 부담 감소
   - HeapByteBuffer 사용시, HeapMemory에서 관리되기 때문에 GC의 도움을 받지만 추가적인 Overhead를 야기한다.

### ByteBuffer 의 한계
```text
position(index) 하나만으로 Read와 Write를 모두 관리한다.
읽기와 쓰기를 동시에 수행하는 경우, Position이 꼬이면서 데이터를 잘못읽거나 쓰는 문제가 발생할 수 있다.
또한 일부분에 대한 읽기 / 쓰기를 수행해야 하는 경우 직접 읽기와 쓰기에 대한 Position을 관리해야한다.

ByteBuffer는 기본적으로 Overwrite이다.
즉, Clear로 Position을 초기화 시켰다고 ByteBuffer에 있는 기존 데이터가 삭제되는 것이 아니라, Position에 의해서 Overwrite된다.
```

### ByteBuffer 명령어

1. flip()
   - WriteMode -> ReadMode로 전환한다.
   - position을 0으로 설정하고 limit을 데이터의 끝(position) 으로 설정한다.
2. get()
   - 상대적 get
   - 현재 position에 있는 데이터를 읽어온다.
   - position을 1 증가시킨다.
3. get(index)
   - 절대적 get
   - index에 있는 데이터를 읽어온다.
   - position은 변하지 않는다.
4. put()
   - 상대적 put
   - 현재 position에 데이터를 쓴다.
   - position을 1 증가시킨다.
5. put(index)
   - 절대적 put
   - index에 데이터를 쓴다.
   - position은 변하지 않는다.
6. clear()
   - position을 0으로 설정하고 limit을 capacity로 설정한다. 
   - 기존 Buffer에 있던 데이터가 삭제되는 것이 아닌, Position에 의해서 Overwrite된다.
7. compact()
    - position과 limit 사이에 있는 데이터를 앞으로 당긴다. (아직 읽지 않은 데이터를 앞으로 땡긴다.)
    - position은 limit - position 만큼 증가한다.
8. wrap()
   - byte[]를 ByteBuffer로 만든다.
   - capacity와 limit은 byte[]의 크기를 따라간다.

### SampleCode
```java
public class NIoPlayground {
    
    public static void main(String[] args) throws IOException {
        try(
                //Helloworld
                FileInputStream fis = new FileInputStream("/Users/kimtaejun/IdeaProjects/Algorithm/src/javaplayground/input.txt");
                FileChannel inputChannel = fis.getChannel();
        ){
            ByteBuffer buffer = ByteBuffer.allocate(3);

            /**
                ------------------------------
                BeforeRead --> Position :0 Limit :3 Capacity :3
                Read --> Position :0 Limit :3 Capacity :3 ||  [H]
                Read --> Position :1 Limit :3 Capacity :3 ||  [e]
                AfterRead --> Position :2 Limit :3 Capacity :3
                ------------------------------
                BeforeRead --> Position :0 Limit :3 Capacity :3
                Read --> Position :0 Limit :3 Capacity :3 ||  [l]
                Read --> Position :1 Limit :3 Capacity :3 ||  [l]
                AfterRead --> Position :2 Limit :3 Capacity :3
                ------------------------------
                BeforeRead --> Position :0 Limit :3 Capacity :3
                Read --> Position :0 Limit :3 Capacity :3 ||  [o]
                Read --> Position :1 Limit :3 Capacity :3 ||  [W]
                AfterRead --> Position :2 Limit :3 Capacity :3
                ------------------------------
                BeforeRead --> Position :0 Limit :3 Capacity :3
                Read --> Position :0 Limit :3 Capacity :3 ||  [o]
                Read --> Position :1 Limit :3 Capacity :3 ||  [r]
                AfterRead --> Position :2 Limit :3 Capacity :3
                ------------------------------
                BeforeRead --> Position :0 Limit :2 Capacity :3
                Read --> Position :0 Limit :2 Capacity :3 ||  [l]
                Read --> Position :1 Limit :2 Capacity :3 ||  [d]
                AfterRead --> Position :2 Limit :2 Capacity :3
             */

            // InputChannel에서 ByteBuffer에 데이터를 읽어옴 (끝에 도달하면 -1을 전달한다.)
            while(inputChannel.read(buffer) > 0){
                System.out.println("------------------------------");
                // WriteMode -> ReadMode
                buffer.flip();
                System.out.println("BeforeRead --> Position :"+ buffer.position() + " Limit :"+buffer.limit());
                // Write To Channel
                while(buffer.position() < 2){
                    System.out.println("Read -->" + " Position :"+ buffer.position() + " Limit :" + buffer.limit()+ " ||  [" + new String(new byte[]{buffer.get()}) + "]") ;
                }

                // ReadMode -> WriteMode
                System.out.println("AfterRead --> Position :"+ buffer.position() + " Limit :"+buffer.limit());
                buffer.compact();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

```