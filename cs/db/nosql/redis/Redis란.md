# Redis
- 영속성을 지원하는 In-Memory 저장소이다.
  - String, List, Hash, Set, SortedSet의 자료구조를 지원한다.
  - Disk에 데이터를 저장 할 수 있다.
- 읽기 성능 증대를 위한 Replication을 지원한다.
  - Replication을 통한 FailOver도 가능하다.
- 쓰기 성능 증대를 위한 Sharding을 지원한다.
- ANSI C로 작성되어 있다. (ANSI C Compiler가 있는 곳에서는 동작가능)
- 대부분의 언어로 포팅 되어 있다.

## 주요 사용처
- 캐시
- pub/sub 모델
  - channel 에 publish, subscribe
  - 이벤트를 저장하지 않는다. 
  - 바로 subscriber에게 메세지를 전송한다.
    - 실시간으로 subscribe하지 않으면 메세지를 받을 수 없다.
- 세션관리
  - WAS 외부에서 세션을 관리하는 방식이다.
  - Redis에 세션정보가 존재한다면, 어느 서버로 요청이 가던간에 세션을 통해서 Stateful한 작업이 가능하다.
## MemCached와의 차이

### 공통점
- 캐시 기능을 제공한다.
- In-Memory 이다.
- 세션, 캐시 등 기한이 있지만 빠른성능을 요구하는 역할로 사용된다.

### 차이점
- MemCached는 LRU알고리즘을 사용, Redis는 6가지의 알고리즘이 존재한다.
- MemCached는 String 만 사용가능, Redis는 5가지의 자료구조 사용이가능하다.
- MemCached는 고성능 분산 메모리 객체 캐싱 시스템으로, 영속성을 보장하지 않는다.
- Redis는 SingleThread, Memcacheds는 MultiThread로 동작한다.