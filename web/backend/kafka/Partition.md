## Partition
- 물리적인 표현이다.
- Queue(FIFO) 구조이다.
    - 오래된 순서대로 Consumer가 Message를 가져간다.
    - Consumer가 Consume하여도, 데이터는 삭제되지 않는다.
        - 옵션에 따라 삭제시점을 정할 수 있다.
        - 다른 ConsumerGroup의 Consumer가 재사용 할 수 있다.
- 병렬처리를 위해서 다수의 Partition을 사용한다.
- Topic내의 partition끼리는 서로 독립적이다.
    - offset 조차도 독립적이다.
- partition에 저장된 파일들은 Immutable하다.
- 고가용성을 위해서 복제(Replica) 한다.
    - 여러 Broker에 분산된다.
    - Broker의 개수보다 많은 복제를 하는 것은 불가능하다.
- ConsumerGroup내의 하나의 Consumer에 의해서만 사용된다.
- 여러개의 Consumer를 담당 할 수 없다.

### Segment
- Record를 저장하는 실제 물리 File이다.
- 지정된 크기가 커지거나(default 1GB), 오래되면(default 7일) 새로운 파일이 생성되고 추가된다.
- Active Segment(마지막)에 Write가 이루어진다.

### Commit Log
- Partition은 CommitLog 형식이다.
- 추가만 가능하고 변경이 불가능한 DataStructure
- Event들이 추가만된다.
- offset이라는 것으로 Message에 접근 가능하다.
  - offset은 계속해서 증가한다.
  - Consumer Group끼리는 다른 Offset을 갖는다.

### Partition의 추가와 삭제
- 추가만 가능하고, 삭제는 불가능하다.
  - 여러 Broker에 저장된 Data를 취합하고 정렬하는 복잡한 과정이 발생하기 때문이다.
  - Partition 삭제시에, 그 안에있던 데이터들 또한 즉시 삭제된다.
- 추가 또한 Data Rebalancing이 발생하기 때문에 신중하야 한다.
  - key값이 null일 때는 상관 없다. (RR이기 떄문에(Sticky 포함))

### Leader Partition & Follower Partition
- Leader Partition에서 Read와 Write를 담당하며, Consumer와 통신한다.
  - kafka2.4 부터는 Follower Partition도 Consumer와 통신 할 수 있다.
- Follower Partition은 복제를 담당한다.
  - Leader의 CommitLog에서 FetchRequest를 통해서 복제한다.
- Leader Parition을 적절하게 분배하는 것이 성능에 좋다.

### Replication (Partition Replication)
- 고가용성을 위한 방법이다.
  - 보통 상용환경에서는 Replication Factor를 2(1 복제) 혹은 3(2복제)를 준다.
    - 신뢰성이 중요한 경우에는 3, 보통인 경우에는 2이다.
    - 1인 경우는 Log와 같은 Metric성 정보일 때 주로 사용한다.
- Partition단위로 동작하며, Broker의 숫자보다 많이 설정 할 수 없다.
- Follower가 Leader에게 데이터를 가져오기를 요청 (Fetch Request) 한다.
- 미리 원본(Leader) 을 복사한 복제본(Follower)을 준비하여, 장애가 발생했을 때를 미리 대비한다.
- Replication Factor 옵션은 n(원본 + 복제본) 이다.
  - 최댓값은 Broker 갯수이다.
  - replication1 : 원본 1개
  - replication2 : 원본 1개 + 복제본 1개
- 데이터 처리속도에 따라 옵션을 결정하는 것이 좋다.
  - 1 (원본)이어도 되는경우: Metric과 같이 유실되도 되는 데이터
  - 복제를 많이 하는 경우: 금융정보 같이 유실되면 안되는 데이터

### 장애 발생 시
- Follower Partition 중에서 하나가 Leader Partition이 된다.
- Producer와 Consumer들 또한 새롭게 선출된 Leader로 데이터를 포워딩한다.

### Key Cardinality
- Message의 Key값의 Hash를 통해서 Partition이 결정된다.
- 균등하게 분포되어있어야지 효율성이 올라간다.

### Partition이 배치되는 방법
- 0번 Broker부터 시작하여, Round-Robin 방식으로 Leader Partition이 생성된다.
- KafkaClient는 Leader Partition과 통신을 주고 받음으로, 특정 서버에 Traffic이 몰리는 것이 아닌,
  여러 Broker가 통신을 분담하게 된다.

### HotSpot 방지 (Leader Partition 이 특정 Broker에 몰리는 것)
- Leader Parition이 특정 Broker에 몰리면 해당 Broker만 Read/Write를 수행하므로 비효율 적이다.
- Leader Partition을 동등하게 Broker들이 나눠가져야 효율이 좋다.
- ShellScript로 재분배 수행을 제공한다.
  - kafka-reassign-partitions.sh

#### 옵션
- auto.leader.rebalance.enable (default: enable) 
  - 불균형이 있을 시 자동으로 LeaderPatition을 분배한다.
- leader.imbalance.check.interval.seconds (default: 300s)
  - 불균형이 있는지 Check하는 Interval Time이다.
- leader.imbalance.per.broker.percentage(default: 10)
  - 다른 Broker들보다 LeaderParition을 얼마나 가져가는지를 판단하는 퍼센트이다.