# In-Sync Replication (ISR)
- 정합성에 관한 문재이다.
- Leader Partition과 Follow Partition이 모두 sink 된 상태를 의미한다.
- Leader 장애시에, 새로운 Leader를 선출하는데 사용한다.
    - ISR이 아닌 Partition도 Leader로 승급가능하다.
        - unclean.leader.election.enable=true: 유실을 감수하고 복제가 안된 Follwer Partition을 Leader Partition으로 승급시킨다.
        - unclean.leader.election.enable=false: 유실을 감수하지 않는다. **해당 Broker가 복구 될 때 까지 중단**
    - 정합성과 속도사이에서 결정하면 된다.

### Leader Partition 의 이상감지
- Leader는 Follower들에게 일정 주기로 Replication요청을 보내도록 요구한다.
    - Reqeust가 오지 않을 경우, Follower에 이상이 생겼다고 간주하고 Group에서 추방시킨다.

# HighWaterMark
- 복제가 완료되었다고 판단하는 Offset 이다.
    - Consumer가 가져갈 수 있는 Offset 이다.
    - Consumer가 HighWaterMark를 읽지 않고, Leader의 Log-End-Offset을 읽었다가
      Replica가 수행되기 전에 LeaderPartition이 장애를 일으키면 문제가 생길 수 있기 때문이다.
- Follower Partiton은 LeaderPartition의 모든 것을 복제하고 있지 않다.
    - 즉가즉각 동기화 하는 것은 불가능하기 때문이다.
    - Follower Partition끼리도 동기화의 차이가 있다.
- min.insync.replicas 설정 값에 따라서 결정된다.
    - LeaderPartition과 근접한 FollowerPartition들의 유실되지 않을 **LOG-END-OFFSET**을 HighWaterMark라고 한다.
    - ```text
      min.insync.replicas = 3
    
      Leader:     0,1,2,3,4,5
      Follower1:  0,1,2,3
      Follower2:  0,1,2
      Follower3:  0,1,
      
      HighWaterMark = 2
        ```