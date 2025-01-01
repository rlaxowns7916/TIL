# In-Sync Replication (ISR)
- ISR 정의
  - Replication 과정에서 Leader Partition을 따라잡고 있는 Follower Partition들의 집합.
  - ISR은 Leader와 동기화(synced)된 상태를 유지하는 Follower들만 포함
    - Follwer가 복제를 따라가지 못하거나, 오랜 시간 응답이 없을 경우 ISR에서 제외된다.
- Controller(Producer) 가 관리하며, 주기적으로 Orchestrator(ZooKeeper, KRaft)에게 상태를 전달한다.
- **Leader 장애시에, 새로운 Leader를 선출하는데 사용한다.**
    - ISR이 아닌 Partition도 Leader로 승급가능하다.
      - Topic 단위로 설정이 가능하다.
          - unclean.leader.election.enable=true: 유실을 감수하고 복제가 안된 Follwer Partition을 Leader Partition으로 승급시킨다.
          - unclean.leader.election.enable=false: 유실을 감수하지 않는다. **해당 Broker가 복구 될 때 까지 중단**
    - 정합성과 속도사이에서 결정하면 된다.
- Kafka Cluster에서의 Partition의 최소 서비스 단위를 의미한다.


## In-Sync Replication 판단 옵션
- 아래의 설정들은 모두 Broker의 설정

### [1] replica.lag.max.messages
- Message의 Lag 갯수를 보고 판단 한다.
- **사용하지 않는 것이 좋다.**
  - 갑자기 Traffic이 튀어서, 한번에 Record가 빠르게 인입되면 순간적으로 OSR(OutOfSync-Replica)가 될 수 있다.

### [2] replica.lag.time.max.ms
- Follower가 Leader에게 Fetch 요청을 보내는 interval을 체크하여, ISR을 판단한다.
- Fetch 요청을 보내지 않는 시간이 이 값보다 길어지면, ISR에서 제외한다.

## min.insync.replicas
- 서비스를 실행하기 위한 최소 Broker 수
  - 이 정도 Replication이 되어야, 서비스가 제대로 동작이 된다는 것을 믿겠다라는 의미이다.
- 높게 설정하면 메세지의 보존 가능성은 높아지나, Availabilty는 낮아진다.
- 이 숫자를 유지하지 못하면, 서비스를 할 수 없다.
  - Producer의 Produce 요청을 Broker가 거부한다.
- replicationFactor와 같은 수준을 유지할 필요는 없다. (도메인의 성격에 따라) 

### Leader Partition 의 이상감지
- Leader는 Follower들에게 일정 주기로 Replication요청을 보내도록 요구한다.
    - Reqeust가 오지 않을 경우, Follower에 이상이 생겼다고 간주하고 Group에서 추방시킨다.

# HighWaterMark
- **Consumer가 안전하게 읽을 수 있는 데이터의 오프셋 위치를 나타낸다.**
  - Kafka의 최소 데이터 정합성을 보장한다.
- ISR 에서 복제가 완료되었다고 판단하는 Offset 이다.
    - Consumer가 가져갈 수 있는 Offset 이다.
    - ISR에 포함된 Follower Partition들 중에서 MinOffset을 의미한다.
    - Consumer가 HighWaterMark를 읽지 않고, Leader의 Log-End-Offset을 읽었다가
      Replica가 수행되기 전에 LeaderPartition이 장애를 일으키면 문제가 생길 수 있기 때문이다.
- Follower Partiton은 LeaderPartition의 모든 것을 복제하고 있지 않다.
    - 즉가즉각 동기화 하는 것은 불가능하기 때문이다.
    - Follower Partition끼리도 동기화의 차이가 있다.
    - LeaderPartition과 근접한 FollowerPartition들의 유실되지 않을 **LOG-END-OFFSET**을 HighWaterMark라고 한다.
    - ```text
      min.insync.replicas = 3
      
      Leader:     0,1,2,3,4,5
      Follower1:  0,1,2,3
      Follower2:  0,1,2
      --- ISR (Follower2 까지 잘 따라잡고 있다고 가정) ---
      Follower3:  0,1,
      
      HighWaterMark = 2
        ```