## Consumer
- Partition에서 Record를 가져와서 사용하는 어플리케이션
    - 순서대로 Read(Poll)를 수행한다.
    - Record를 읽은 위치를 Commit하여 다시 읽는 것을 방지한다.
    - Commit 과정에서의 Offset 증가를 통해서 중복 Read를 방지한다.
- 다른 ConsumerGroup에 속한 Consumer들은 서로 연관이 없다.
    - 하나의 Topic에 여러 ConsumerGroup이 동시에 Read할 수 있다.
- 하나의 Consumer가 0개이상의 Partition 사용이 가능하다.
  - 여러개의 Partition의 Record를 가지고 올 수 있다.
  - Consumer의 갯수가 Partition의 갯수보다 크다면 특정 Consumer는 놀고있을 수 있다.
- Commited된 Record만 Read 할 수 있다.
- **ThreadSafe하지 않기떄문에, MultiThread로 구성해서는 안된다.**

### ConsumerGroup
- 동일한 group.id로 구성된 Consumer들이다.
- Offset을 공유한다.
- ConsumerGroup에 속한 Consumer들은 Partition을 분배하여 Consume한다.
    - ConsumerGroup이 동일한 Consumer는 동시에 Partition에 접근 할 수 없다.
    - ConsumerGroup에 4개의 Consumer, Topic에 4개의 파티션이 있다면?
        - Consumer : Partition = 1 : 1
    - = Partition은 최대 1개의 Consumer를 가질 수 있다.
      - Consumer의 갯수가 더 많다면 노는 Consumer가 존재 할 수 있다.
### Consumer Rebalancing
- Consumer Group에 새로운 Consumer가 투입되거나 제외되면 발생
  - Consumer는 지속적으로 HeartBeat를 전송하는데, 일정시간 이상 응답이 없으면(Session TimeOut) Group에서 제외하고 Rebalancing한다.
  - Partition의 제어권이 다른 Consumer에게 넘어 가는 것이다.
- Group의 관리는 Coordinator(Broker)가, Partition의 분배는 Leader Consumer가 수행한다.
- 자주일어나면 장애의 원인이 될 수 있다.
  - Rebalancing이 일어나는 과정중에 Consume이 중지된다.
  - Topic에 해당하는 Consumer의 연결을 모두 끊고 새롭게 할당하는 과정이다.
  - Partition 갯수가 많을수록 더 오래걸린다.
- **RebalanceListener를 통해서 Rebalancing이 일어날 때, 로직 처리를 할 수 있다.**

### Consumer Partition Assigner 
- Leader Consumer의 수행
- Consumer와 Partition을 매칭시킨다.
- ConsumerGroup 단위로 설정된다.

#### 1. Range Assigner
- 파티션을 숫자기준으로 정렬, Consumer의 이름을 사전순으로 정렬
- 반반씩 나눠 가진다. (반으로 안나뉘면 앞에 위치한 Consumer가 더 가져간다.)
- Consumer가 Partition보다 수가 많다면? 뒷 번호 Consumer들은 계속해서 처리를 하지 못할 것이다.
  - 새로운 Consumer를 추가하여 Re-Assign된다해도 똑같다.

#### 2. RoundRobin Assigner
- 파티션을 Consumer에 번갈아가며 할당한다.

#### 3. Sticky Assigner
- Round Robin의 개선된 방법
- 최대한 기존의 Consumer와 매칭을 시킨다.

### ConsumerLag
- Producer와 Consumer의 Offset 차이를 Consumer Lag라고 부른다.
  - Consumer Lag는 성능을 모니터링 하는 중요 지표이다.
  - Produce되는 양보다, Consumer가 Consume하는 양이적은 것이 문제이다.
  - Partition과 Consumer의 숫자를 늘려 병렬처리 양을 늘리는 것이 중요하다.

### Consumer TimeOut
- 2가지의 TimeOut이 존재한다.
- TimeOut이 발생한 Broker는 해당 Consumer Group에서 제외되며, 이로인한 Rebalancing이 일어난다.

#### HeartBeat
- Broker로 전송하는 주기적인 HealthCheck 실패로 인한 TimeOut은 SessionTimeOut이라고 부른다.
- session.timeout.ms: TimeOut 기준 값
- heartbeat.interval.ms: HealthCheck 주기

#### Polling Interval
- 메세지를 Polling한 후, 다음 Polling 때 까지의 시간 만료로 인한 TimeOut
- max.poll.records: 한번에 땡겨올 메세지의 수 (수가 채워져야 Polling하는 것은 아니고, 시간이 다 되면 미달이어도 땡겨온다.)
- max.poll.interval.ms: Poll 주기 (이 설정 안에 Poll을 안떙겨가면 장애이다.)


### Consumer 관련 Position
- Last Committed Offset(Current Offset): Consumer가 최종 Commit한 Offset
- Current Position: Consumer가 현재 읽어간 위치 (Commit 전)

### Commit
- 파티션으로부터 메세지를 어디까지 가져갔는지 기록하는 과정
  - **Commit 정보는 _consumer_offsets 이라는 내부 토픽을 통해 관리한다.**
- Commit을 제대로 실행하지 못했다면, Consumer가 다시 실행되었을 때, 재처리 될 수 있다.

#### 자동 Commit
- enable.auto.commit으로 설정한다.
- auto.commit.interval.ms를 체크하여 poll 작업(이전) 마다 자동 Commit을 실행한다.
- poll(), close() 메소드 호출 시 자동 호출된다.

#### 수동 Commit
- Async, Sync를 지원한다.


### Consumer 주요 옵션
1. bootstrap.servers
   - Broker의 주소 (host + port)
   - 2개이상의 Broker주소를 입력하여, 장애상황에 대비하는 것이 좋다.
2. key.desrializer
   - Consumer가 Record의 Key를 직렬화 할 때 사용
3. value.desrializer
   - Consumer가 Record의 Value를 직렬화 할 때 사용
4. group.id
   - ConsumerGroup을 지정
   - default는 null이다.
5. auto.offset.reset
   - ConsumerGroup이 특정 Partition을 읽을 때, 저장된 Offset이 없으면 어디서부터 읽을지에 대한 설정 (새로 떴을 때)
   - latest(default): 최신 Record부터 읽는다.
   - earliest: 가장 오래된 Record부터 읽는다.
   - none: 저장된 Offset이 없으면 Exception을 발생시킨다.
6. enable.auto.commit
   - 자동 Commit을 사용할지 여부
   - default는 true이다.
7. auto.commit.interval.ms
    - 자동 Commit 주기
    - default는 5000ms이다.
8. max.poll.records
    - Polling할 때, 최대로 가져올 Record의 수
    - default는 500이다.
9. session.timeout.ms
    - Consumer와 Broker가 연결이 끊기는 최대 시간
      - 이 시간내에 heartBeat가 오지않으면 ConsumerGroup에서 제외된다.
    - default는 10000ms이다.
10. heartbeat.interval.ms
    - HeartBeat 주기
    - default는 3000ms이다.
11. max.poll.interval.ms
    - Polling을 호출하는 최대 간격 시간
      - 이 시간동안 Polling을 하지않으면 ConsumerGroup에서 제외한다.
    - default는 300000ms이다
12. isolation.level
    - Producer가 Record를 트랜잭션 단위로 보낼 때 사용한다.