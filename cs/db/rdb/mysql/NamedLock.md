# Named Lock
- 보통 MySQL에서 분산 락 (Distributed Lock)을 구현할 때 사용한다.
  - Redis, Zookeeper 등 다른 솔루션이 있지만 추가적인 관리포인트이다.
  - MySQL 한대를 Main RDB로 사용하고 있다면 선택지로 나쁘지 않다.
    - MySQL Cluster(Master(Active) - Master(Active)) 로 이용중이라면, 사용 할 수 없다. 
- 데이터가 아닌 **사용자 지정 문자열**에 Lock을 거는 것이다.
- 사용자가 이름을 직접 정의할 수 있는 **USER-LEVEL Lock** 이다.
- ExclusiveLock 과의 차이점은 TimeOut처리가 쉽다는 것과, 데이터가 Lock대상이 아니라는 것이다.
- **5.7 이전에는 Lock을 중복으로 잡게되면 이전의 Lock들은 풀린다.**
- **5.7 이후에는 동시에 여러개의 Lock을 잡을 수 있다.**
- 다른 DataSource를 사용하는 것이좋다.
  - Connection이 부족 할 수 있기 때문이다.
  - NamedLock은 Lock을 획득하는 Connection, 로직을 수행하는 Connection 2가지를 필요로한다.
    - 같은 DataSource를 사용하게 된다면, Lock을 얻기위해 Connection을 물고있는채로 대기 할 것이다.
- Lock을 명시적으로 풀어주어야 한다.
  - 당연히 트랜잭션과 같은 생명주기를 갖지않는다.
  - @Transacional을 사용하게되면, 하나의 Connection으로 묶이게 된다. (DataSource분리가 안된 상황)
    - Connection을 직접 관리하는 별도의 방법이 필요하다.

## 사용법

### 1. GET_LOCK (Name, TimeOut)
- Name에 해당하는 Lock을 얻기위한 명령어
- 1: Lock획득 성공
- 0: TimeOut동안 Lock획득 실패
- null: 에러 발생

### 2. RELEASE_LOCK (Name)
- 1: Lock 해제
- null: 해제할 Lock이 없음 (Lock점유상태가 이미 아님)

### 3. RELEASE_ALL_LOCKS()
- 현재 점유된 모든 NamedLock Release
- n: Release된 Lock의 개수

### 4. IS_FREE_LOCK (Name)
- 1: Lock이 비어있는 겨우
- 0: Lock이 점유 되어있는 경우