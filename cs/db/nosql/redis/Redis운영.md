# Redis 운영

## [1] Memory 관리를 잘하자

### 1. Max-Memory는 완벽하지 않다.
- Max-Memory를 설정하더라도 더 많이 사용하게 될 가능성이 높다.
  - JMalloc이라는 Memory-Allocator를 사용한다.
  - 이 Max-Memory값을 넘어가면 데이터를 지우는 등의 작업을 수행한다.
  - 하지만 확실하게 지워진다고 단언 할 수 없다. (실제 Memory보다 많이 사용하게 될 수도 있는 이유)
- **메모리 파편화**의 사유로 더 많은 메모리를 잡고 있을 수도 있다.
  - EX) 4097 byte를 사용해도, OS는 8K의 MemoryPage를 할당하기 떄문이다.
- usedMemory가 2GB로 보고되지만, 실제로는 11GB의 사이즈를 가지는 경우도 존재했다.

### 2. Swap에 주의하자
- Swap이 존재한다면?
  - Swap을 사용하게 되면 성능이 확연하게 저하된다.
- Swap이 존재하지 않는다면?
  - OOM으로 Redis가 죽을 수 있다.

### 3. Copy-On-Write
- 설정해 놓은 Memory의 2배까지도 사용하게 될 수 있다.
- 큰 메모리의 Instance 한대보다, 작은 메모리 Instance 여러대가 유리하다.
- 

## [2] Collection 주의 사항
1. 10000개 이하로 Element를 유지하는게 좋다.
2. Collection의 Expire은 전체에 걸린다.
    - Collection 내부 데이터 대상으로 Expire를 걸 수 없다.
### Memory 사용을 줄이는 Collection 사용
- 아래의 Collection들은 추가적인 자료구조를 사용한다.
  - Hash -> HashTable을 하나 더 사용한다.
  - Sorted Set -> SkipList와 HashTable을 사용한다.
  - Set -> HashTable을 사용한다.

### ZipList사용하기
- 속도는 느려지지만, 메모리 관리에는 훨씬 유리하다.
- In-Memory 특성 상, 적은 개수의 경우 선형탐색을 하는게 훨씬 유리할 수 있다.
  - 선형으로 저장 한다.
  - Memory 관리에서는 훨씬 유리하다.
- 설정에 따라서 ZipList사용을 할 수있다.
  - value의 갯수가 n개를 넘어가기 전까지 ziplist사용
  - ...

## [3] O(N) 명령어 사용에 주의하자
- Redis는 SingleThread이다.
  - 한번에 하나의 작업밖에 못한다는 것이다.
  - **한개의 작업이 Heavy하게 될 경우에 뒷 작업들이 Blocking되게 된다.**
- 아래는 대표적인 O(N) 명령어이다.
  - KEYS
    - SCAN 명령어로 대체
  - FLUSHALL, FLUSHDB
  - DELETE COLLECTIONS
    - Collection의 일부영역만 가져오기
    - Colelction을 여러개로 나눠서 저장
  - GET ALL COLLECTIONS