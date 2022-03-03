# Aggregate에서의 트랜잭션

- 두 개의 트랜잭션에서 하나의 Aggregate에 동시에 수정이 가해진다면?
- 여러가지 Lock 기법을 통해서 해결하기

### 예시 1
- 쓰레드A: 운영자
- 쓰레드B: 소비자
```text
쓰레드A Aggregate Search

                            쓰레드B Order Aggregate Search

쓰레드A 배송대기중인 상품
배송중으로 변경                                            

                            쓰레드B Order Aggregate 배송지 변경
쓰레드A 트랜잭션 커밋 

                            쓰레드B 트랜잭션 커밋 
```
- 배송중으로 변경되면, 소비자는 배송지를 변경 할 수 없다.
- 하지만 트랜잭션 시작 시점에서 배송중이 아니었으므로 변경이된다.
- 결과적으로 두개의 트랜잭션이 모두 반영되어, 배송지가 변경된 상태로 배송중이라는 정보가 입력된다.
  - 하지만 트랜잭션 A는 이전 배송지인 상태에서 배송중상태로 변경했으므로, 트랜잭션 일관성이 깨지게 된다. 




## 선점 잠금(Pessimistic Lock)
- **먼저 애그리거트를 구한 스레드가 애그리거트 사용이 끝날 때까지 다른 스레드가 해당 애그리거트를 수정하는 것을 막는 방식이다.**
- 먼저 자원을 선점한 스레드가 Lock을 해제하기전까지 수정작업은 블로킹된다.

```sql
SELECT order where id = ? FOR UPDATE
```
- 수정하기 위해 SELECT를 하며, Lock을 획득한다는 의미

### 예시 2
```text
쓰레드A Aggregate Search
Lock 선점

                                        쓰레드B Aggregate Search
                                        Lock으로인한 대기

쓰레드A Aggregate Update



쓰레드A 트랜잭션 커밋
Lock  해제
                                        Blocking 해제
                                        쓰레드B Aggregate Search
                                        Lock 선점
                                        
                                        
                                        쓰레드B Aggregate Update
                                        
                                        
                                        쓰레드B 트랜잭션 커밋
                                        Lock 해제
```
- 트랜잭션이 시작할 때 선점한 쪽이 Lock을 획득
- 트랜잭션이 완료 될때 Lock 해제
- Lock이 걸린상태에서는 다른 트랜잭션 접근시 블로킹
- 예시 1의 경우를 해결 가능하다.
  - 쓰레드A 수정시 Lock을 선점 + 쓰레드B는 완료까지 대기
  - 배송중으로 바뀌었기 때문에, 쓰레드B는 배송지를 변경불가능하게 되는 것이다.


### 선점잠금의 문제점
**교착상태(DeadLock)가 발생 할 수 있다.**

### 예시3
```text
쓰레드A Aggregate1 Lock
                        
                           쓰레드B Aggregate2 Lock
                           
쓰레드A Aggregaet2 접근

                           쓰레드B Aggregate1 접근
```
- 양쪽 모두 자원을 하나 선점한 상태로 트랜잭션이 끝나지 않은 상태
- 다음단계를 진행 할 수 없는 두 쓰레드는 **교착상태**에 빠지게 된다.

### 선점잠금에서의 DeadLock 해결법
- Lock에 대한 만료시간을 걸어둔다.
- Connection자체에 만료시간을 걸어두는 DBMS도 존재한다.


## 비선점 잠금(OptimisticLock)
- Lock을 통해서 문제를 해결 하는 것이 아닌, <br>**실제 DBMS적용 시점에 변경가능 여부를 확인하는 방식이다.**
- 선점 잠금(Pessimistic Lock)으로 해결 못하는 문제를 해결 가능하다.
  - 락을 무한적으로 선점할 수 없기 때문이다. (Search와 Update사이에 많은 간격이있다면?)

```sql
UPDATE aggregate SET version = version+1, colx = ?,coly = ?
WHERE id = ?and  version = [현재버전]
```

- Table의 현재값과, 현재 Aggregate의 버전값이 동일한 경우에만 데이터를 수정시킨다.
- 데이터 수정시에 테이블의 버전값을 1 증가시킨다.
- 다른 트랜잭션이 이전에 먼저 데이터를 수정했으면, 버전 불일치로 수정에 실패한다.
- 트랜잭션 충돌이 일어나면 Application에서 예외처리를 적용해주면 된다.


### 예시 4

```text
쓰레드A Aggregate Search
(version = 5)

                                        쓰레드B Aggregate Search
                                        (Version = 5)
                                        
쓰레드A Aggregate Update 
(Version = 5)

                                        쓰레드B Aggregate Search
                                        (Version = 5)

쓰레드A 트랜잭션 커밋 성공
(Version 5 - > 6)
                   
                                        쓰레드B 트랜잭션 커밋 실패
                                        (Table = 6  & Aggregate 5)      
```

### 강제 버전 증가 (JPA 가정)
- DDD에서 Aggregate의 완전함은 AggregateRoot뿐만 아니라, 다른 엔티티까지 포함이다.
- **연관된 엔티티의 값이 변하더라도 JPA에서 Root의 버전은 변화하지 않는다.**
  - 연관된 엔티티가 변화하면, Root의 버전도 변화하여야한다.
  - 트랜잭션 커밋 시에 강제적인 Version 증가 (SELECT일지라도)
```java
public interface UserRepository extends JpaRepository<User, Long> {

  @Lock(LockModeType.OPTIMISTIC_FORCE_FOR_INCREMENT)
  Optional<User> findWithOptimisticLockById(Long id);
}
```