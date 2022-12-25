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
- 참조: http://redisgate.kr/redis/configuration/copy-on-write.php
- 설정해 놓은 Memory의 2배까지도 사용하게 될 수 있다.
- 큰 메모리의 Instance 한대보다, 작은 메모리 Instance 여러대가 유리하다.
#### 2배의 메모리가 사용 될 수 있는 이유
```text
자식 프로세스(child process)를 생성(fork)하면 같은 메모리 공간을 공유하게 된다. 
메모리 공간이 공유된 시점 이후 부모 프로세스가 데이터를 새로 넣거나, 수정하거나, 지우게 되면 같은 메모리 공간을 공유할 수 없게 된다. 
이때 부모 프로세스는 해당 페이지를 복사한 다음 수정한다. 

만약 자식 프로세스가 없었다면 페이지를 복사하지 않고 바로 수정했을 것이다. 
자식 프로세스가 생성되어 작업을 하는 동안 데이터 입력/수정/삭제가 발생하면 해당 메모리 페이지를 복사해야 되기 때문에 평소보다 더 많은 메모리가 필요해진다. 
```

#### 언제 발생할 수 있나?
```text
[1] save 파라미터 [RDB] (설정 o, 명령어 x)

redis.conf 파일에 디폴트로 "save 60 10000" 이런 파라미터가 활성화(enable) 되어있다.   
의미는 60초 동안 1만 개의 키가 새로 입력되면(바뀌면) RDB 파일을 새로 쓰라는 것이다.   
이 파라미터로 RDB 파일을 새로 쓸 때 자식 프로세스가 생성되어 작업하는데 이때 COW가 발생한다.
예를 들어, 물리적 메모리가 32gB인 시스템에서 레디스 서버 인스턴스가 30gB를 사용하고 있었고,
RDB 파일을 새로 쓰는 동안 COW가 발생해서 3gB의 메모리가 추가로 필요했다면, Real 메모리가 부족하므로 스왑(swap)이 발생해서 처리가 늦어져서 문제가 발생할 것이다.   
그러므로 COW에 대비해서 여유 메모리가 필요하다.

[2] BGSAVE 명령 [RDB]

BGSAVE 명령을 수행하면 자식 프로세스가 생성되어 RDB 파일을 새로 쓴다.   
이때도 똑같이 COW가 발생한다.   
SAVE 명령은 레디스 프로세스가 직접 수행하므로 COW가 발생하지 않는다.

[3] 복제 Replication [RDB]

save 파라미터나 BGSAVE 명령을 실행하지 않아도 자신이 마스터이고 슬레이브가 연결되면, 
전체 데이터 동기(full resync)가 발생하여 이때도 RDB 파일을 만들게 되므로 COW가 발생한다.   
이것은 복제 시 RDB 파일을 디스크에 쓰지 않고 바로 소켓(네트워크)로 주는 옵션(repl-diskless-sync yes)을 사용해도 마찬가지다.   
왜냐하면 대상(target)이 디스크인지 소켓인지만 다를 뿐 자식 프로세스가 생성되어 RDB 데이터를 만드는 것은 동일하기 때문이다.

[4] auto-aof-rewrite-percentage 파라미터 [AOF]

redis.conf 파일에 디폴트로 "auto-aof-rewrite-percentage 100" 이런 파라미터가 있다.   
의미는 appendonly 파일이 100% 커지면 appendonly 파일을 다시 쓰라는 것이다.   
다시 쓰기는 AOF 자식 프로세스가 생성되어 작업하는데, 이때도 COW가 발생한다.

[5] BGREWRITEAOF 명령 [AOF]

BGREWRITEAOF 명령을 수행하면 자식 프로세스가 생성되어 appendonly 파일을 다시 쓴다.   
이때도 COW가 발생한다.
```

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