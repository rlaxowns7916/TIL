# In-Sync Replication (ISR)
- Replication 하는 중에, Leader를 잘 따라잡고있는 Follower 들의 집합이다.
- Leader Partition과 Follow Partition이 모두 sink 된 상태를 의미한다.
- Leader Partition이 속한 Broker가 관리한다.
- Leader 장애시에, 새로운 Leader를 선출하는데 사용한다.
    - ISR이 아닌 Partition도 Leader로 승급가능하다.
        - unclean.leader.election.enable=true: 유실을 감수하고 복제가 안된 Follwer Partition을 Leader Partition으로 승급시킨다.
        - unclean.leader.election.enable=false: 유실을 감수하지 않는다. **해당 Broker가 복구 될 때 까지 중단**
    - 정합성과 속도사이에서 결정하면 된다.
- Partition의 최소 서비스 단위이다.
- min.insync.replicas 설정 값에 따라서 결정된다.
  - Write를 성공하기 위한 최소 복제본의 수
  - 높게 설정하면 메세지의 보존 가능성은 높아지나, Availabilty는 낮아진다.
  - 이 숫자를 유지하지 못하면, 서비스를 할 수 없다.

### Leader Partition 의 이상감지
- Leader는 Follower들에게 일정 주기로 Replication요청을 보내도록 요구한다.
    - Reqeust가 오지 않을 경우, Follower에 이상이 생겼다고 간주하고 Group에서 추방시킨다.

# HighWaterMark
- ISR 에서의 최소 LEO 이다.
- ISR 에서 복제가 완료되었다고 판단하는 Offset 이다.
    - Consumer가 가져갈 수 있는 Offset 이다.
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