# ByteBuf
- Netty의 ByteWrapper
  - Java NIO - ByteBuffer에 비해 풍부한 기능을 제공한다.
- 사용자 정의로 확장 가능하다.
- 최대용량을 지정 가능하다.
  - Integer.MAX_VALUE보다 크게 잡으려고 하면 Exception이 발생한다.
- ZeroCopy 사용가능 (내장 복합 버퍼형식 사용)
- Read / Write 마다 각각 고유한 Index 소유
- Method Chain 지원
- Reference Counting을 통한 메모리 관리
- Polling 지원

## Read / Write Index
- 각각 고유한 Index를 가지고 있다.
  - ReadIndex와 WriteIndex가 같아지면 데이터의 끝이다.
  - 그 이상 읽으려고 하면, IndexOutOfBoundsException이 발생한다.
- read(), write()는 index를 증가시킨다.
- get(), set()은 index를 증가시키지 않는다.
- index는 capacity를 넘겨서는 안도며, readableBytes(), writableBytes()를 통해서 남은 공간을 확인할 수 있다.



## 사용 Pattern

### [1] HeapBuff
- 보조배열 (backing-array)라고 불리는 패턴이며, 가장 많이 사용된다.
- JVM Heap공간에 할당하며, Polling을 사용하지 않는경우 빠른 할당과 해제 속도를 보여준다.
- Legacy데이터를 처리하는데 적합하다.
  - 대규모 Network I/O작업에는 불리하다.
  - Natvie Memory 자원을 접근하는 경우에 불리하다. (JVM의 도움을 받기 때문)
- hasArray가 false인데 접근하려 하면 UnsupportedOperationException이 발생한다.
- 내부적으로 Socket에 전송할 때 DirectBuffer를 사용하고 있다.


```kotlin
class HeapBufferExample {
    fun main(args: Array<String>){
        // 1. Heap Buffer 생성: Unpooled 힙 버퍼 (backing array 사용)
        val heapBuffer = Unpooled.buffer(10); // 기본적으로 heap memory에 할당

        // 2. 버퍼에 데이터 쓰기
        heapBuffer.writeByte('N'.code.toByte())
        heapBuffer.writeByte('E'.code.toByte())
        heapBuffer.writeByte('T'.code.toByte())
        heapBuffer.writeByte('T'.code.toByte())
        heapBuffer.writeByte('Y'.code.toByte())

        // 3. 현재 버퍼의 상태 출력
        println("Writer index: " + heapBuffer.writerIndex())
        println("Reader index: " + heapBuffer.readerIndex())

        // 4. 버퍼에 데이터가 저장된 Array가 있는지 확인
        if (heapBuffer.hasArray()) {
            val array = heapBuffer.array() // backing array 접근

            // 5. 읽기 시작 인덱스와 길이 계산 (arrayOfset()은 BackingArray내에서 ByteBuff가 사용하는 시작 인덱스를 반환)
            val offset = heapBuffer.arrayOffset() + heapBuffer.readerIndex()
            val length = heapBuffer.readableBytes()

            println("BackingArray contains: ")
            for (i in offset until offset + length) {
                print(array[i].toChar()) // 배열에서 직접 읽기
            }
            println()
        }

        // 6. 버퍼에서 데이터 읽기
        println("Heap Buffer contains: ")
        while (heapBuffer.isReadable) {
            print(heapBuffer.readByte().toChar())
        }

        // 7. 메모리 해제 (GC가 관리해주기 떄문에 필수는 아니다)
        heapBuffer.release() // 명시적으로 메모리를 해제
    }
}
```

### [2] DirectBuff
- BackingArray가 존재하지 않는다. (DirectBuffer -> NativeMemory이기 때문)
- 대규모 파일 I/O, 고성능 Network Server/Client 구현에 적합하다.

```kotlin
class HeapBufferExample {
    fun main(args: Array<String>) {
      // 1. Direct Buffer 생성
      val directBuffer = Unpooled.directBuffer(10); // Direct 메모리에 할당된 ByteBuf 생성

      // 2. 버퍼에 데이터 쓰기 (byte 값을 int로 자동 형변환)
      directBuffer.writeByte( 'N'.code); // 'N' 기록, writerIndex 증가
      directBuffer.writeByte( 'E'.code); // 'E' 기록, writerIndex 증가
      directBuffer.writeByte( 'T'.code); // 'T' 기록, writerIndex 증가
      directBuffer.writeByte( 'T'.code); // 'T' 기록, writerIndex 증가
      directBuffer.writeByte( 'Y'.code); // 'Y' 기록, writerIndex 증가

      // 3. 현재 버퍼의 상태 출력
      println("Writer index: " + directBuffer.writerIndex());
      println("Reader index: " + directBuffer.readerIndex());
      println("HasArray: " + directBuffer.hasArray());

      // 4. 버퍼에서 데이터 읽기
      val bytes = ByteArray(directBuffer.readableBytes()); // 10바이트 크기의 배열 생성
      println("From Copied Array: "); // 복사된 배열 출력
      directBuffer.getBytes(directBuffer.readerIndex(),bytes)
      for(byte in bytes) {
        print(byte.toChar());
      }
      println()

      println("From Direct Buffer: "); // NETTY 출력
      directBuffer.readerIndex(0); // readerIndex를 처음부터 읽도록 설정
      while (directBuffer.isReadable) { // 버퍼에 읽을 수 있는 데이터가 있는 동안
        print(directBuffer.readByte().toChar()); // 데이터 읽기
      }
      // 5. 메모리 해제
      directBuffer.release(); // 명시적으로 메모리를 해제
    }
}
```
### [3] CompositeBuff (복합 버퍼)
- 여러 Buffer를 논리적인 단일 Buffer로 관리한다.
  - Instance가 하나만 존재한다면 hasArray는 해당 Instance의 것을 따르며, 여러개일 경우는 false를 리턴한다.
  - DirectBuff 패턴을 이용하는것이 유리하다.
- Memory복사를 줄이는 효율성을 제공한다.
- JDK의 기능이 아닌 Netty의 기능이다.
- DataFrame 조합, Message 조립등을 할 때 유용하다.
- CompositeBuff 단의 rIndex, wIndex와 각각의 rIndex,wIndex 모두 존재한다.

```kotlin
/**
    HEADRBODY-DATA1
    All component: CompositeByteBuf(ridx: 0, widx: 15, cap: 15, components=2)
    First component: UnpooledDuplicatedByteBuf(ridx: 0, widx: 5, cap: 5, unwrapped: UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf(ridx: 0, widx: 5, cap: 5))
    Second component: UnpooledDuplicatedByteBuf(ridx: 0, widx: 10, cap: 10, unwrapped: UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf(ridx: 0, widx: 10, cap: 10))
 */
class HeapBufferExample {
    fun main(args: Array<String>) {
        // 1. CompositeByteBuf 생성
        val compositeBuffer = Unpooled.compositeBuffer();

        // 2. 개별 ByteBuf 생성
        val headerBuf = Unpooled.buffer(5);
        headerBuf.writeBytes(byteArrayOf('H'.code.toByte(), 'E'.code.toByte(), 'A'.code.toByte(), 'D'.code.toByte(), 'R'.code.toByte()));

        val bodyBuf = Unpooled.buffer(10);
        bodyBuf.writeBytes(byteArrayOf('B'.code.toByte(), 'O'.code.toByte(), 'D'.code.toByte(), 'Y'.code.toByte(), '-'.code.toByte(), 'D'.code.toByte(), 'A'.code.toByte(), 'T'.code.toByte(), 'A'.code.toByte(), '1'.code.toByte()))

        // 3. CompositeByteBuf에 ByteBuf 추가
        compositeBuffer.addComponents(true, headerBuf, bodyBuf); // true는 읽기 포인터를 자동으로 설정함
        println("All component" + compositeBuffer.toString())
        // 4. CompositeByteBuf에서 전체 데이터 읽기

        for (i in 0 until compositeBuffer.readableBytes()) {
            print(compositeBuffer.getByte(i).toChar()) // 전체 데이터를 하나의 버퍼처럼 읽음
        }
        println()

        // 5. 개별 ByteBuf에 대한 접근
        println("First component: " + compositeBuffer.component(0).toString())
        println("Second component: " + compositeBuffer.component(1).toString())

        // 6. 메모리 해제
        compositeBuffer.release(); // 모든 구성 요소의 참조 카운트를 감소시키고 필요 시 메모리 해제
    }
}
```

## Byte 폐기
```text
+---+---+---+---+---+---+---+---+---+---+
| X | X | X |   |   |   |   |   |   |   |
+---+---+---+---+---+---+---+---+---+---+
  0       ^           ^               ^
          |           |               |
       rIndex       wIndex         capacity
```
- discardBytes()를 통해서 이미 읽은 Byte를 폐기하여 공간을 확보 할 수 있다.
- **자주 호출하여 충분한 공간을 확보하는 것이 좋을 것 같지만, 위 동작은 Memory Copy를 유발하므로 성능상 좋지 않다.** 

## 주의 할점

### [1] ByteBuff간의 Read/Write
```kotlin
    val sourceBuffer = Unpooled.buffer(10)
    val targetBuffer = Unpooled.buffer(10)


    targetBuffer.writeBytes(sourceBuffer) // readerIndex에서부터 데이터를 복사
```
- Index를 지정하지 않으면, 복사 크기만큼 sourceBuffer의 rIndex가 증가한다.
- Index를 지정해주면 해당 Index부터 복사가 시작된다. (rIndex는 증가하지 않는다.)

### [2] 파생 Buff
- 아래와 같은 명령어로 파생 ByteBuff 생성이 가능하다.
  - duplicate()
  - slice()
  - Unpooled.unmodifiableBuffer()
  - order(ByteOrder)
  - readSlice()
- 파생 Buffer는 rIndex, wIndex, markIndex를 포함하는 새로운 ByteBuf 인스턴스를 반환한다.
- **생성비용이 저렴하지만 내부의 모든 정보는 공유되기 떄문에, 파생Buffer의 변경은 원본에 영향을 미친다.**
- **기존 Buff의 복사본이 필요하다면, copy()를 사용해야 한다.**
