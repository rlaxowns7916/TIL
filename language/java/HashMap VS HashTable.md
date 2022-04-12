# HashTable
- CollectionFramework 가 나오기 전 부터 존재했다.
  - 이전에 나왔기 때문에 CollectionFramework의 명명법을 따르지 않는다.
  - 기존 코드의 호환성을 위해서 남겨져 있다.
- Key는 중복 불가능하고 Value는 중복 가능하다.
- Key값에 Null을 허용하지 않는다.
- Thread-Safe하다.
  - 한번에 하나의 Thread만 접근 가능하다.
    - MultiThread 환경에서 많은 병목현상이 발생한다. 
  - 모든 Method가 Synchronized 하다.

# HashMap
- Key는 중복 불가능하고 Value는 중복 가능하다.
- Key값에 Null을 허용한다.
- HashCollision이 덜 발생한다.
  - 보조 Hash함수를 사용한다.
- Thread-Safe하지 않다.
- 지속적으로 개선되고 있다.

# ConcurrentHashMap
- HashMap이 Thread-Safe 한 것이다.
  - Segement 개수 만큼의 여러개의 Lock을 가지고 있다. 
    - 읽기 작업에는 Lock이 걸리지 않는다.
    - 쓰기 작업시에는 특정 Segement의 Lock을 획득한다.
      - 같은 Segement가 아니라면 RaceCondition이 발생하지 않는다.


## 결론
- HashTable은 Legacy이므로 HashMap의 사용이 권장된다.
- Multi-Thread환경에서는 ConcurrentHashMap을 사용한다.
   - HashTable은 동시 사용을 막기위해서 집의 정문을 잠근다.
   - ConcurrentHashMap은 동시 사용을 막기위해서 방문을 잠근다.
- HashTable과 ConcurrentHashMap의 차이는 DB의 Table 수준의 Lock과 Row수준의 Lock의 차이정도로 이해하자.
