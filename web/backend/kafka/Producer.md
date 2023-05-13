# Producer
- Kafka Topic으로 Record를 보내는 어플리케이션
  - 성공 여부를 확인 할 수 있으며, 재시도 또한 가능하다.
  - 주로 Callback을 통해서 확인한다. (비동기)


## [1] 구성
1. Serializer
    - Message를 직렬화 한다.
2. Partitioner
    - 어떤 Partition에 보낼지 결정한다.
    - Batch에 쌓는다.
3. Sender
    - 별도의 Thread로 동작한다.
    - 일정 시점마다 Batch에 있는 것들을 Broker에 전송한다.
    - Buffer가 다 찼는지 확인하지 않는다.
    - **Sender의 주요속성이, 처리량에 영향을 미친다.**
      - batch.size: 한번에 보낼 Buffer의 Size이다.
      - linger.ms: Message를 Batch에 유지하는 시간이다. 이 시점이 지나면 Send한다.
      - buffer.memory: Buffer의 크기를 지정한다.


## [2] 종류

## Transaction Producer
- 한번의 로직으로 atomic하게 여러 Partition에 모두 데이터를 저장해야 하는 경우가 있다.
- Partition에 데이터를 기록 할 때, 부가적으로 Commit 여부를 함께 기록한다.

### 과정
1. Partitioner를 통해서 어느 Partition으로 보낼지 결정한다.
    - key가 있으면 Hash, 없으면 RoundRobin
    - Custom Partitioner도 생성 가능하다.
    - Partition 추가를 신중하게 고려해야 하는 이유는 Partition 추가 시에, Key값에 따른 Hash가 깨지기 때문에
      이전의 Key와 파티션과의 매칭의 동일성을 보장 할 수 없기 때문이다. (Global Relocation)
2. Compress(Optional)을 통해서 Message를 압축한다.
    - 권장하는 추세이다.
    - CPU Usage는 늘지만, Latency를 낮추고 성능상 유리하다.
3. RecordAccumulator에서 Message를 모아서 Batch로 전송한다.
4. Sender가 Batch단위로 모은 Message를 전송한다.


## Idempotent Producer
- 중복 메세지 전송이나, 메세지 순서변경과 같은 일을 방지하기 위해서 있는 Producer이다.
  - Network오류로 인해서 Message의 중복발송이나, 순서가 변경되는 것을 방지 할 수 있다.

### 과정
1. 각 메시지에 고유한 일련번호(PID, Sequence Number)를 할당한다.
2. Kafka Broker는 이 일련번호를 이용하여 중복 메시지를 감지하고 거부한다.
3. 메시지 순서가 변경되면 Broker가 이를 감지하고, 올바른 순서로 메시지를 처리한다.


### Sticky Partitioner (default 2.4v 이상부터)
- Batch단위의 RoundRobin
- Message 단위의 RoundRobin은 여러개의 Broker에 Connection을 맺어야 하고,
  Batch의 장점을 누릴 수 없기 때문에 해당 방법을 사용한다.

***

### Acks
- Producer가 Broker에 Message를 보내고 받는 리턴 값이다.
- Acks 수준을 관리함으로 해서, Message 유실 가능성과 성능사이에서 조절하게 된다.

#### 옵션
- -1: 모든 Replica에게서 ack응답을 받음
  -  -1의 경우 min.insync.replicas 옵션이 영향을 미친다. (몇개의 ack가 와야 성공인 것인가 (Leader는 필수))
- 0: 응답을 기다리지않음 (전송보장 (X))
- 1: Leader의 저장 여부 확인 (Leader 장애시 메세지 유실 가능 / Follower 복제 시점 이전에, Leader가 ack를 보내고 죽는다면?)

