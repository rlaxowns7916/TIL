# Java에서의 동시성보장
- JVM에서 Thread들은 Static영역과 Heap영역을 공유하게된다.
  - 이 두가지영역에 동시접근 하게 됨으로 해서 동시성문제가 발생하게 된다.

## 0. Singleton
- Singleton 패턴을 통해 생성된 객체는 상태를 갖지 않아야 한다.
- 상태를 갖지않기 때문에 Read만이 일어나며, Write를 하지 않으므로 동시성 문제는 발생하지 않는다.

## 1. Synchronized
- 객체단위의 Lock을 건다.
- blocking을 사용하여 Thread-Safe를 보장한다.
- 성능상의 저하를 유발한다.
  - Thread가 block 에 최초 접근시에 lock을 걸게된다.
  - lock이 걸린 block에 다른 Thread들이 접근하면 Blocking이 된다.
    - Blocking 되는 동안 아무런 작업을 수행하지 않는다.
  - Blokcing에서 Thread의 상태를 바꿔주는 것에도 System Resource가 소모된다.

## 2. Concurrent
- Java5에 포함된 패키지, 다양한 유틸리티 클래스를 제공한다.
- Synchronized 보다 훨씬 속도,성능 상에서 유리하다.
- 다양한 Collection들이 존재한다.

## 3. Atomic
- NonBlocking 방식을 사용한다.
- CAS(Compare And Swap) 알고리즘을 기반으로 한다.
  - (기존 값, 변경할 값)을 인자로 변경 요청을 보낸다.
  - 기존 값이 메모리에 있는 값과 일치한다면 true와 함께 변경한다.
  - 기존 값이 메모리에 있는 값과 일치하지 않는다면 false와 함께 변경하지 않는다.
- Synchronized보다는 성능이 좋다.
  - 무한 루프로 Checking을 한다면 CPU사용률은 증가하겠지만, Lock으로 Thread의 동작을 Blocking 하는 것이나,     
    Thread의 상태를 바꿔주는 것이 훨씬 비싼 연산이다.