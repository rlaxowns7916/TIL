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


## [2] 종류

## Transaction Producer
- IdempotentProducer의 확장
- 다수의 Data를 하나의 Transaction으로 묶어, Atomic하게 처리하는 것을 의미한다.
  - 여러 Topic 혹은 Partition에 Write 할 때, Transaction을 단위로 "all or nothing" 을 보장한다.
- Producer별로, 고유한 ID값을 사용해야한다. 
  - 기존 idempotentProducer의 문제였던 휘발성(indtance 재 시작시 pid, sequence 초기화)를 해결한다.
  - **transactional.id에 매핑되는 pid-sequence는 Broker의 TransactionCoordinator가 관리한다.**
  - init -> begin -> commit 순서대로 동작한다.
- consumer도 isolation_level (read_commited) 을 통한 설정이 필요하다.
- 여러 파티션에 걸친 Write가 성공하면 Commit, 아니라면 Abort
   - read_commited기 때문에 Consumer는 commit된 시점에 한번에 해당 데이터 들을 볼 수 있다.
```java
class Example{
    public static void main(String[] args) {
        configs,put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID());
        Producer<String,String> producer =new kafkaProducer<>(configs);

        producer.initTransactions();

        producer.beginTransaction();
        for (int i = 0; i < 100; i++){
            producer.send(new ProducerRecord<>("my-topic", Integer.toString(i),Integer.toString(i)));
        }   
    }
}
```

## Idempotent Producer
- Exactly-Once-Delivery(정확하게 1번) 를 지원한다. (default: false, Kafka 3.0.0 부터는 true)
  - **true로 변경 시, acks=all로 변경된다.**
  - enable.idempotence=true로 설정한다. 
- 제한적으로나마 중복 메세지 전송이나 메세지 순서변경과 같은 일을 방지하기 위해서 있는 Producer이다.
  - Partition 마다 생성된다. 
  - Network오류로 인해서 Message의 중복발송이나, 순서가 변경되는 것을 방지 할 수 있다.
- 여러번 전송하더라도, KafkaCluster에서는 단 한번만 저장된다.
- 데이터를 Broker로 전달 할 때, PID(Producer 고유 ID)와 Sequence를 전달한다.
  - PID는 동일한 Session에서만 유효하다.
    - 즉, Producer가 새롭게시작된다면 의미없다.
  - Sequence는 순서가 역전되거나 꼬이는 현상이 있으면 OutOfOrderSequenceException이 발생한다.
  - Producer의 Data가 정확하게 한번 Broker에 저장되도록 동작한다.
  - 이미 저장되어있는 것을 또 저장하려고해도 acks를 보내준다.

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
  - at-least-once(최소한번 전달 보장)
- 0: 응답을 기다리지않음 (전송보장 (X) ==> 손실이 있더라도 속도를 중요시 할 경우)
- 1: Leader의 저장 여부 확인 (Leader 장애시 메세지 유실 가능 / Follower 복제 시점 이전에, Leader가 ack를 보내고 죽는다면?)

