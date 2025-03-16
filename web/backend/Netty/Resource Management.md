# ResourceManagement
- Netty는 내부적으로 DirectByteBuf를 사용하기 떄문에, 적절하게 release 해주지 못하면 memoryLeak이 발생할 수 있다.
- ReferenceCount를 잘 관리해야 발생하지 않는다.

## ResourceLeakDetector
```text
# MemoryLeak Detect Enable 옵션
java -Dio.netty.leakDetectionLevel=[LEVEL]

# 결과
Running io.netty.handler.codec.xml.XmlFrameDecoderTest
15:03:36.885 [main] ERROR io.netty.util.ResourceLeakDetector - LEAK:
    ByteBuf.release() was not called before it's garbage-collected.
Recent access records: 1
```
- ReferenceCount가 0이되지 않는 상황을 감지하기 위한 매커니즘
  - 객체의 ReferenceCount가 0이 아닌데, GC가 발생하려 할 떄를 감지한다. (with ReferenceQueue + PhantomReference)
- 잠재적인 문제진단을 돕기위해서 Application 버퍼 할당의 1%를 샘플링하여, MemoryLeak을 탐지한다 
- LEVEL
  - DISABLED: MemoryLeak 감지 비활성화
  - SIMPLE: 기본 샘플링 비율 1%를 이용해 누출을 보고한다.
  - ADVANCED: 발견된 MemoryLeak과 메세지에 접근한 위치를 보고한다. (SIMPLE의 샘플링 비율을 이용한다)
  - PARANOID: ADVANCED + 비율 100% (**성능에 크게 영향을 미치기 떄문에, 디버깅 시점에만 이용해야 한다.**)