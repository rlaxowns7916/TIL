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
- 다수의 Data를 하나의 Transaction으로 묶어, Atomic하게 처리하는 것을 의미한다.
- 사용자가 보낸 Record를 저장할 뿐만 아니라, Transaction의 시작과 끝을 알리는 Record 또한 전송한다.
- Producer별로, 고유한 ID값을 사용해야한다. 
  - init -> begin -> commit 순서대로 동작한다.
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
- 데이터를 Broker로 전달 할 때, PID(Producer 고유 ID)와 SID(Record Id)를 전달한다.
  - PID는 동일한 Session에서만 유효하다.
    - 즉, Producer가 새롭게시작된다면 의미없다.
  - SID는 순서를 의미하는데, 순서가 역전되거나 꼬이는 현상이 있으면 OutOfOrderSequenceException이 발생한다.
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
- 0: 응답을 기다리지않음 (전송보장 (X))
- 1: Leader의 저장 여부 확인 (Leader 장애시 메세지 유실 가능 / Follower 복제 시점 이전에, Leader가 ack를 보내고 죽는다면?)

