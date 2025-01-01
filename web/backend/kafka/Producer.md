# Producer
- Kafka Topic으로 Record를 보내는 어플리케이션
  - 성공 여부를 확인 할 수 있으며, 재시도 또한 가능하다.
  - 주로 Callback을 통해서 확인한다. (비동기)
- 성능 최적화를 위해서 Batch 처리를 할 수 있다.
  - Network I/O를 줄일 수 있다.
  - ```text
        linger.ms (default: 0 --> 즉시보냄) // Message가 함께 Batch처리될 때 대기시간
        batch.size (default: 16KB) // Message를 보내기전 Batch 최대 크기
    ```
  - **linger.ms를 자주 쓴다.** (batch.size가 채워질 떄 까지 시간이 걸리면, send가 지연이 되기 떄문)
- 하나의 Connection에서 여러개의 요청(Multiple in-flight request)을 한번에 보낼 수 있다.
  - max.in.flight.requests.per.connection (default: 5)


## [1] 구성
1. Serializer
    - Message를 직렬화 한다.
    - Key / Value 형태의 Kafka가 알 수 있는 ByteArray 형태로 변환된다.
2. Partitioner
    - 어떤 Partition에 보낼지 결정한다.
    - 기본적으로 아래와 같은 Rule을 가진다.
      - Key가 null 일 때   : RoundRobin
      - Key가 null이 아닐 때: Key의 Hash 값 기반으로 선택 (동일 Key면 동일 Partition으로 가게됨) 
3. RecordAccumulator
    - Message를 모아서 효율적으로 Batch로 전송하는 역할을 한다.
    - Batch처리를 통해서 Network I/O를 줄일 수 있게 된다.
4. Sender
    - 별도의 Background Thread로 동작한다. 
    - 일정 시점마다 RecordAccumulator에 있는 것들(Batch) 을 Broker에 전송한다.
    - 전송중 에러가 발생하면, Retry로직이 발생한다. (Default: 무한대 (Integer.MAX_VALUE))
    - Buffer가 다 찼는지 확인하지 않는다.
    - **Sender의 주요속성이, 처리량에 영향을 미친다.**
      - batch.size: 한번에 보낼 Buffer의 Size이다.
      - linger.ms: Message를 Batch에 유지하는 시간이다. 이 시점이 지나면 Send한다.
      - buffer.memory: Buffer의 크기를 지정한다.
    
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
- Produce 요청이 성공할 때를 정의하는데에 사용되는 Parameter
- Producer가 Broker에 Message를 보내고 받는 리턴 값이다.
- Acks 수준을 관리함으로 해서, Message 유실 가능성과 성능사이에서 조절하게 된다.

#### 옵션
- -1: 모든 Replica에게서 ack응답을 받음
  -  -1의 경우 min.insync.replicas 옵션이 영향을 미친다. (몇개의 ack가 와야 성공인 것인가 (Leader는 필수))
  - at-least-once(최소한번 전달 보장)
- 0: 응답을 기다리지않음 (전송보장 (X) ==> 손실이 있더라도 속도를 중요시 할 경우)
- 1: Leader의 저장 여부 확인 (Leader 장애시 메세지 유실 가능 / Follower 복제 시점 이전에, Leader가 ack를 보내고 죽는다면?)

### Retry
- Network 혹은 System의 일시적인 오류를 보완하기 위해서 사용된다
- ACK를 받지못하면 재수행한다.

#### 옵션
- retries: 재시도 횟수 (default: MAX_INT)
- retry.backoff.ms: 재시도 사이의 대기시간 (default: 100)
- request.timeout.ms: Producer가 응답(ACK)를 기다리는 최대 시간 (default: 30,000 (30초))
- delivery.timeout.ms: retry를 포함하여 하나의 Record에서 성공 또는 실패륿 보고하는 시간의 상한 (default: 120,000 (2분))

**보통 retries를 조정하는 대신에 delivery.timeout.ms를 통해서 설정을 조정하는 경우가 많다.**