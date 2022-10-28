# Lock
- 벤더사 마다 Lock의 종류는 다르다.
- InnoDB를 기준으로한 정리

## 1. SharedLock (S-Lock)
- Row 레벨의 Lock 이다.
- 하나의 Row에 대한 Lock을 여러 Transaction이 가진다.
- SELECT 시점에 획득하는 Lock이다. 
  - SELECT ~ FOR SHARE
  - SELECT ~ LOCK IN SHARE MODE
- 일반적인 SELECT != S-Lock
  - 벤더마다 다르다.
  - MS-SQL의 경우, 일반 SELECT가 SharedLock이기 떄문에 FROM table WITH (nolock)을 지정해주어야만, S-Lock없이 조회된다.

## 2. ExclusiveLock (X-Lock)
- Row 레벨의 Lock 이다.
- - 하나의 트랜잭션만 선점 가능하다.
- 특정 Row를 변경하고자 할 때 Lock이 설정된다.
  - SELECT ~ FOR UPDATE
  - INSERT
  - UPDATE
  - DELETE
- X-Lock이 걸려있는 상태이면, 다른 트랜잭션이 S-Lock, X-Lock 모두 얻을 수 없다.
- 하나 이상의 S-Lock이 걸려있는 상태라면, X-Lock을 획들 할 수 없다.

## 3. Intention Lock (I-Lock)
- Row 및 Table 단위의 MGL(Multiple Granularity Locking: 다중 단위 잠금)을 제공하기 위한 Lock이다.
- 지금 당장이 아니더라도, 나중에 의도적으로 Row에 S-Lock, X-Lock을 획득하려고 하는 것이다.


## 4. Gap Lock
- 하나의 Row가 아닌 범위에 Lock을 거는 것이다.
- 해당 범위에 존재하는 비어있는 값의 **Insert 를 제한하는 것이다.**
- RepeatableRead의 일관성을 보장 할 수 있다.
  - Phantom Read의 카운터
- ~ for update로 Range를 걸 경우, 해당 Range에 GapLock(Exclusvie)이 걸린다.
- Unique한 Index의 Equal연산 (=) 경우에는 GapLock (X)