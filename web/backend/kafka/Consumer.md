## Consumer
- Partition에서 Record를 가져와서 사용하는 어플리케이션
    - 순서대로 Read(Poll)를 수행한다.
    - Record를 읽은 위치를 Commit하여 다시 읽는 것을 방지한다.
    - Commit 과정에서의 Offset 증가글 통해서 중복 Read를 방지한다.
- 다른 ConsumerGroup에 속한 Consumer들은 서로 연관이 없다.
    - 하나의 Topic에 여러 ConsumerGroup이 동시에 Read할 수 있다.
- 하나의 Consumer가 0개이상의 Partition 사용이 가능하다.
  - 여러개의 Partition의 Record를 가지고 올 수 있다.
- Commited된 Record만 Read 할 수 있다.

### ConsumerGroup
- 동일한 group.id로 구성된 Consumer들이다.
- ConsumerGroup에 속한 Consumer들은 Partition을 분배하여 Consume한다.
    - ConsumerGroup이 동일한 Consumer는 동시에 Partition에 접근 할 수 없다.
    - ConsumerGroup에 4개의 Consumer, Topic에 4개의 파티션이 있다면?
        - Consumer : Partition = 1 : 1 

### ConsumerLag
- Producer와 Consumer의 Offset 차이를 Consumer Lag라고 부른다.
  - Consumer Lag는 성능을 모니터링 하는 중요 지표이다.

### Consumer ReBalancing
- Consumer가 장애가 났을 떄의 대응이다.
- Consumer Group내의 다른 정상동작하는 Consumer가 장애가 발생한 Consumer가 Consume하던 Partition의 Record를 대신 처리한다.


### Consumer 관련 Position
- Last Committed Offset(Current Offset): Consumer가 최종 Commit한 Offset
- Current Position: Consumer가 현재 읽어간 위치 (Commit 전)