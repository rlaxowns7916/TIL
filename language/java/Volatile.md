# Volatile
- JDK 1.5 이상에서 사용가능하다.
- Java변수를 MainMemory에 저장하겠다고 명시하는 것이다.
- 매번 변수를 읽을 때, CacheMemory가 아니라 **MainMemory에서 읽어 오는 것이다.**

## Volatile이 왜 필요한가?
- MultiThread환경에서 일반적으로 CacheMemory에 변수가 저장되게된다.
  - 성능향상을 위한 작업이다.
  - MultiThread환경에서, 이러한 CacheMemory를 참조하게 될 때 각 Thread간의 데이터값이 상이하게 된다.
  - 각 Thread에서의 값의 변경은 다른 Thread에 즉시 전파되지 않는다.
- MainMemory에 저장하게 되므로, 일관적인 값을 얻을 수 있다.

## Volatile은 동시성문제를 완벽하게 해결하는가?
- 하나의 Write, 여러개의 Read환경에서의 일관성만 보장 가능하다.
- **여러개의 Write가 유발하는 동시성문제는 해결하지 못한다.**

## Atomic과의 차이점은?
- CAS(CompareAndSwap) 방식이다.
- 연산의 원자성을 보장하기 때문에, 경쟁조건을 해결 할 수 있다.
- 일반적인 객체에 적용이 불가능하다. (Concurrent 패키지가 지원하는 것만 사용 가능하다.)
  - AtomicInteger
  - AtomicLong
  - ...
