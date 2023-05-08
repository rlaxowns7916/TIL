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
- 버킷의 길이에 따라서, 충돌시에 LinkedList나 Tree로 변형한다.
  - 데이터 add를 통해서 개수가 8개가 되면 Tree
  - 데이터 remove를 통해서 개수가 6개가 되면 LinkedList
- Synchronized 키워드를 붙인 Wrapping한 HashMap도 존재한다.
  - Collection 수준의 Lock을 얻는다 (get, put)
  - 하나의 쓰레드가 Lock을 얻으면 다른 쓰레드는 사용 할 수 없다. (Collection 수준의 Lock)
  - 
![hashMap](https://user-images.githubusercontent.com/57896918/182023545-eab601e0-8d82-4eed-bb7b-d9145fd5abdd.png)

# ConcurrentHashMap
- HashMap이 Thread-Safe 한 것이다.
  - Segement 개수 만큼의 여러개의 Lock을 가지고 있다. 
  - default 16개의 Segment를 가지고 있다.
- 각 Bucket에 대한 LocK이 존재한다.
  - FairLock(공평한 Lock)으로 구현한다.
  - 읽기 작업에는 Lock이 걸리지 않는다.
  - 쓰기 작업시에는 특정 Segement의 Lock을 획득한다.
- 같은 Segement가 아니라면 RaceCondition이 발생하지 않는다.
- CAS(Compare & Swap)을 통해서 값을 변경한다.
  - 읽어오는 시점에 Lock이 없기 때문에, 최신화 되지 않은 값을 통해서 변경했을 가능성이 있기 떄문이다.
  - 검색(get)에는 동기화가 적용되지 않으므로 업데이트 작업(put() or remove())과 겹칠 수 있다. 
- 버전에 따른 구현방법의 차이가 있다.
  - Java7: HashMap과 유사한 방식
  - Java8: RedBlack Tree를 이용한 구현

![ConcurrentHashMap](https://user-images.githubusercontent.com/57896918/182023536-f3d6b881-c13b-4f1e-959b-af4e24ca2c77.png)


## 결론
- HashTable은 Legacy이므로 HashMap의 사용이 권장된다.
- Multi-Thread환경에서는 ConcurrentHashMap을 사용한다.
   - HashTable은 동시 사용을 막기위해서 집의 정문을 잠근다.
   - ConcurrentHashMap은 동시 사용을 막기위해서 방문을 잠근다.
- HashTable과 ConcurrentHashMap의 차이는 DB의 Table 수준의 Lock과 Row수준의 Lock의 차이정도로 이해하자.
