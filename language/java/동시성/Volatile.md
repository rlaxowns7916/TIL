# Volatile
- JDK 1.5 이상에서 사용가능하다.
- Memory 가시성 (Memory Visibility) 문제를 해결한다.
  - CPU는 처리성능을 위해서 중간에 Cache Memory 를 사용한다. (현대의 대부분 CPU는 Core단위로 CacheMemory를 각각 가지고 있다.)
    - CacheMemory가 언제 갱신되는지 정확한 시점을 알 수 없다. (CPU의 설계 방식과 종류, 실행조건에 따라서 다르다.)
    - 주로 Context Switching이 될 때 변경된다.
  - 매번 변수를 읽을 때, CacheMemory가 아니라 **MainMemory에서 읽어 오는 것이다.**
    - 성능은 당연히 저하된다.
  - volatile과 동일하게, 메모리 동기화 기법 (synchronized, ReentrantLock)을 통해서도 메모리 가시성 문제를 해결 할 수 있다.

## Volatile이 왜 필요한가?
- MultiThread환경에서 일반적으로 CacheMemory에 변수가 저장되게된다.
  - MultiThread환경에서, 이러한 CacheMemory를 참조하게 될 때 각 Thread간의 데이터값이 상이하게 된다.
  - 각 Thread에서의 값의 변경은 다른 Thread에 즉시 전파되지 않는다.
- **JMM(Java Memory Model) 최적화를 위해서 명령어의 순서를 변경하는 재정렬을 수행한다.**
  - **이러한 재정렬은 순서가 뒤바뀔 가능성이 있기 때문에, volatile을 통해서 happens-before를 보장한다.** 
- MainMemory에 접근하게 되므로, 일관적인 값을 얻을 수 있다.

## Volatile은 동시성문제를 완벽하게 해결하는가?
- 하나의 Write, 여러개의 Read환경에서의 일관성만 보장 가능하다.
- **여러개의 Write가 유발하는 동시성문제는 해결하지 못한다.**

### Volatile의 예시
```java
/**
 * Double Check Locking
 */

public class Singleton{
    private static volatile Singleton instance;
    
    public static Singleton getInstance(){
        if(instnace == null){
            synchronized (Singleton.class){
                if(instance == null){
                    instance = new Singleton();
                }
            }
        }
    }
}
```
- null check와, synchronized block을 통과하여 초기화가 된 이후에도, volatile이 없다면 CacheMemory를 참조할 수 있어 1번 이상의 초기화가 발생 할 수 있다.

## Atomic과의 차이점은?
- CAS(CompareAndSwap) 방식이다.
- 연산의 원자성을 보장하기 때문에, 경쟁조건을 해결 할 수 있다.
- 일반적인 객체에 적용이 불가능하다. (Concurrent 패키지가 지원하는 것만 사용 가능하다.)
  - AtomicInteger
  - AtomicLong
  - ...
  
<img width="692" alt="Volatile" src="https://user-images.githubusercontent.com/57896918/196179159-0550877b-b7f3-45f8-ad6a-98581fa11c18.png">
