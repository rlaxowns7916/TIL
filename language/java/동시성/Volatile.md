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
  - **volatile 변수에 대한 Write는, 그 이전에 수행된 모든 명령어가 실행 된 후 반영된다.**
  - **volatile 변수에 대한 Read는, 그 이후에 수행될 모든 명령어가 실행되기 전에 반영된다.**
- MainMemory에 접근하게 되므로, 일관적인 값을 얻을 수 있다.

## Volatile은 동시성문제를 완벽하게 해결하는가?
- 하나의 Write, 여러개의 Read환경에서의 일관성만 보장 가능하다.
- **여러개의 Write가 유발하는 동시성문제는 해결하지 못한다.**

### Volatile의 예시
```java
/**
 * Double Check Locking
 * 
 * synchronized를 사용하였는데 왜 추가적으로 volatile를 사용하는가?
 *  - vaolatile을 사용하여, 명령어 재정렬을 바잊 할 수 있다.
 *  - valatile이 없다면, JMM이 명령어 재정렬을 수행하여, 초기화되지 않은 객체를 반환 할 수도 있다.
 *  
 * Thread 1:
 *  getInstance()를 호출하고 if (instance == null)을 통과.
 *  synchronized 블록에 들어가 instance = new Singleton() 실행.
 *  명령어 재정렬이 발생하여 instance에 메모리 참조가 먼저 저장됨.
 *  하지만 생성자가 아직 실행되지 않은 상태.
 * 
 * Thread 2:
 *  동시에 getInstance()를 호출.
 *  첫 번째 if (instance == null) 체크에서 instance가 이미 할당된 것을 확인.
 *  synchronized 블록을 건너뛰고 return instance 실행.
 *  초기화되지 않은 객체를 반환.
 *
 * [생성자 호출 이전에 참조만 먼저 저장 될 수 있는 이유]
 * 1. Memory allocation (메모리 할당): 객체 메모리 생성
 * 2. Constructor invocation (생성자 호출): 생성자를 호출하여 객체 초기화
 * 3. Reference assignment (참조 저장): instance가 객체의 메모리 주소를 가리킴
 * 
 * 
 * 재정렬이 발생하여, 위 순서가 변경되면 초기화 되지 않은 객체를 참조할 위험성이 있다.
 * /

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
