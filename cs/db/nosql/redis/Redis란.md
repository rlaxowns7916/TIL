# Redis
- 영속성을 지원하는 In-Memory 저장소이다.
  - String, List, Hash, Set, SortedSet의 자료구조를 지원한다. 
- 읽기 성능 증대를 위한 Replication을 지원한다.
- 쓰기 성능 증대를 위한 Sharding을 지원한다.
- ANSI C로 작성되어 있다. (ANSI C Compiler가 있는 곳에서는 동작가능)
- 대부분의 언어로 포팅 되어 있다.

## MemCached와의 차이

### 공통점
- 캐시 기능을 제공한다.
- In-Memory 이다.

### 차이점
- MemCached는 LRU알고리즘을 사용, Redis는 6가지의 알고리즘이 존재한다.
- MemCached는 String 만 사용가능, Redis는 5가지의 자료구조 사용이가능하다.
- MemCached는 고성능 분산 메모리 객체 캐싱 시스템으로, 영속성을 보장하지 않는다.