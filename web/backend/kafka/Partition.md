## Partition
- Topic을 이루는 구성요소이며, 논리적인 표현이다.
  - 하나의 Topic은 병렬처리를 위해서 N개의 Partition을 가진다.
  - Topic내의 partition끼리는 서로 독립적이다.
    - offset 조차도 독립적이다.
- 0번 부터 Indexing 된다.
- CommitLog(AppendOnly) 방식이며, 저장된 Record는 변경이 불가 (Immutable) 하다.
- Queue(FIFO) 구조이다.
    - 오래된 순서대로 Consumer가 Message를 가져간다.
    - Consumer가 Consume하여도, 데이터는 삭제되지 않는다.
        - 옵션에 따라 삭제시점을 정할 수 있다.
        - 다른 ConsumerGroup의 Consumer가 재사용 할 수 있다.
- 병렬처리를 위해서 다수의 Partition을 사용한다.
- partition에 저장된 파일들은 Immutable하다.
- 고가용성을 위해서 복제(Replica) 한다.
    - 여러 Broker에 분산된다.
    - Broker의 개수보다 많은 복제를 하는 것은 불가능하다.
- ConsumerGroup내의 하나의 Consumer에 의해서만 사용된다.
- 여러개의 Consumer를 담당 할 수 없다.

### Commit Log
- Partition의 물리적 저장 방식 (partition은 논리적인 개념)
  - Disk에 append-only 개념으로 추가된다.
  - offset 순서대로 append 된다.
- 추가만 가능하고 변경이 불가능한 DataStructure


### Segment
- Record를 저장하는 실제 물리 File이다.
- 지정된 크기가 커지거나(default 1GB), 오래되면(default 7일) 새로운 파일이 생성되고 추가된다.
- Active Segment(마지막)에 Write가 이루어진다.
  - Partition 당 Active Segment 는 하나이다.


### Consumer Lag
- Consumer가 현재 Read 하고 있는 offset을 **CurrentOffse**이라고 한다.
- Procuder가 현재 Write 하고 있는 offset을 **LogEndOffset**이라고 한다.
- **LogEndOffset과  CurrentOffset의 차이를 ConsumerLag 이라고 부른다.** 

### Partition의 추가와 삭제
- 추가만 가능하고, 삭제는 불가능하다.
  - 여러 Broker에 저장된 Data를 취합하고 정렬하는 복잡한 과정이 발생하기 때문이다.
  - Partition 삭제시에, 그 안에있던 데이터들 또한 즉시 삭제된다.
- 추가 또한 Data Rebalancing이 발생할 수 있다.
  - Producer의 관점: 보낼 때만 영향, Broker에 저장된 이후에는 영향 (X)
    - Key: 같은 Key기준 A Partiiton에 들어가던 것이, B Partition에 들어갈 수 있다.
    - RoundRobin: 애초에 지정된 규칙이 없었기 때문에 영향이 없다.
  - Conumser의 관점
    - Partiiton 단위로 Record를 가져오기 때문에 큰 상관 없다. (ConsumerGroup별 할당 전략)

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
  - **Leader가 Push해주는게 아님**
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


### HotSpot 방지 (Leader Partition 이 특정 Broker에 몰리는 것)
- Leader Parition이 특정 Broker에 몰리면 해당 Broker만 Read(버전에 따라 Follower에서도 Read 가능) /Write를 수행하므로 비효율 적이다.
- Leader Partition을 동등하게 Broker들이 나눠가져야 효율이 좋다.
- ShellScript로 재분배 수행을 제공한다.
  - kafka-reassign-partitions.sh
- Option
  - ```text
      auto.leader.rebalance.enable: (default: enable) # Rebalance 가능 여부
      leader.imbalance.check.interval.seconds: (default: 300sec) # Rebalance가 필요한지 Check 하는 주기
    ```

#### 옵션
- auto.leader.rebalance.enable (default: enable) 
  - 불균형이 있을 시 자동으로 LeaderPatition을 분배한다.
- leader.imbalance.check.interval.seconds (default: 300s)
  - 불균형이 있는지 Check하는 Interval Time이다.
- leader.imbalance.per.broker.percentage(default: 10)
  - 다른 Broker들보다 LeaderParition을 얼마나 가져가는지를 판단하는 퍼센트이다.

### Flush
- PageCache(Memory) -> Disk에 쓰는 단계이다.
  - Flush이전에 Broker의 장애가 발생하면 데이터의 유실이 일어날 수 있다.
  - 이러한 한계를 보완하기 위해서 나온게 Replication(복제) 이다.
- **성능을 위해서 Segment는 Os의 PageCache(Memory) 에 먼저 저장된다.**
  - I/O처리(Disk, Network) 에서의 성능상 이점을 누릴 수 있다.
- **Segment의 데이터 형식은 Producer로부터 수신한 것, Consumer에게 보내는 것과 정확히 일치하므로, Zero-Copy가 가능하다.**
  - Copy없이, 그대로 Network에 태울 수 있기 때문이다.
- 아래와 같은 상황에서 Flush된다.
  - Broker의 종료
  - OS의 주기적인 Flush (Kafka와 상관 없음)
  - 설정에 따른 Background에서의 FlusherThread의 실행