# EOS (Exception Once Semantics)
- Kafka에서 Exactly-Once를 보장하기 위한 방법
- Broker로 Record를 정확하게 한번 전달 하는 것을 목표로 한다.

### Idempotent
- Exactly-Once-Delivery(정확하게 1번) 를 지원한다. (default: false, Kafka 3.0.0 부터는 true)
    - **true로 변경 시, acks=all로 변경된다.**
    - enable.idempotence=true로 설정한다.
- 제한적으로나마 중복 메세지 전송이나 메세지 순서변경과 같은 일을 방지하기 위해서 있는 Producer이다.
    - Partition 마다 생성된다.
    - Network오류로 인해서 Message의 중복발송이나, 순서가 변경되는 것을 방지 할 수 있다.
- 성능에는 크게 영향을 미치지 않는다. (MessageHeader 부만 추가)
- 여러번 전송하더라도, KafkaCluster에서는 단 한번만 저장된다.
- 데이터를 Broker로 전달 할 때, PID(Producer 고유 ID)와 SequenceNumber를 전달한다.
    - PID는 동일한 Session에서만 유효하다.
        - Broker가 Producer와 최초로 연결 될 때, Broker가 고유한 값을 채번하여 Producer에게 전달한다.
        - 즉, Producer가 새롭게시작된다면 의미없다.
    - SequenceNumber는 Producer에서 고유하게 관리한다.
      - **SequenceNumber는 PID-Partition마다 관리한다.**
- **Sequence는 순서가 역전되거나 꼬이는 현상이 있으면 OutOfOrderSequenceException이 발생한다.**
    - 이전 Sequence의 Record가 도착하지 않았다면, 이후의 것들이 정상적으로 도착하였어도 Exception을 던져서 Retry를 유도한다.
- Producer의 Data가 정확하게 한번 Broker에 저장되도록 동작한다.
- 이미 저장되어있는 것을 또 저장하려고해도 acks를 보내준다.

## Transaction
- IdempotentProducer의 확장
- Broker의 경우 대부분 default 설정을 그대로 사용한다.
- **Producer에서 TransactionAPI를 사용해야 한다.**
  - idempotent 기능 (enable.idempotence)가 enable 되어있어야 한다.
  - transaction.id값에 대한 할당이 필요하다.
    - **이 값이 할당되어 있지 않다면, IdempotentProducer로 동작한다.**
- **Consumer에서 isolation_level (read_commited) 을 통한 설정이 필요하다.**
  - Transaction의 상태는 Broker가 아닌 Consumer에 의해서 이루어진다.
    - Broker(Transaction Coordinator)는 Message 등록 시, offset을 기존과 동일하게 Consumer에 전달한다.
    - Consumer는 IsolationLevel에 따라서 읽을지 안읽을지 결정한다.
    - **READ_COMMITED가 설정되지 않은 ConsumerGroup은 Abort된 Transaction을 읽을 수 있다.**
- 성능에 어느정도 영향을 미친다.
    - Commit, Rollback에 대한 부분이 추가되기 때문이다.
- 다수의 Data를 하나의 Transaction으로 묶어, Atomic하게 처리하는 것을 의미한다.
    - 여러 Topic 혹은 Partition에 Write 할 때, Transaction을 단위로 "all or nothing" 을 보장한다.
- Producer별로, 고유한 ID값을 사용해야한다.
    - 기존 idempotentProducer의 문제였던 휘발성(instance 재 시작시 pid, sequence 초기화)를 해결한다.
    - **transactional.id에 매핑되는 pid-sequence는 Broker의 TransactionCoordinator가 관리한다.**
        - TransactionCoordinator -> TransactionLog를 관리하는 Broker의 Thread
        - ID(ProducerId, SequenceNumber, TransactionId)를 할당하고, Client가 이 정보를 Message Header에 포함하여, 고유하게 식별
    - init -> begin -> commit 순서대로 동작한다.
    - 이전 트랜잭션을 승계 가능하다. (이전 Producer들의 진행내역)
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


### 과정
1. Transaction Coordinator 찾기
   - initTransactions()를 호출하여, Broker에게 Request를 보내고 Coordinator의 위치를 찾는다.
   - Coordinator는 Producer에게 PID를 할당한다.
2. TransactionalID 할당
   - Producer가 initPidRequest()를 통해서 PID를 갸ㅏ지고 온다.
   - TransactionCoordinator는 PID할당 이력을 TransactionLog에 기록한다.
3. BeginTransaction
   - Producer가 새로운 Transaction의 시작을 알린다.
   - Coordinator 관점에서는 Transaction이 시작된 것은 아니고, Producer Local 관점에서의 수행이다.
   - Coordinator 관점에서의 Transaction 수행은 Send()를 통해서 이루어진다.
4. AddPartitionToTxnRequest()
   - Coordinator는 새로운 TopicPartition에 처음 기록 될 떼 TransactionLog에 기록한다.
5. ProduceRequest
   - Producer가 Broker에게 Message를 보낸다.
6. AddOffsetCommitToTxnRequest
   - 특정 ConsumerGroup의 Offset을 Transation에 포함시킨다.
   - Commit 준비 단계이다.
7. TxnOffsetCommitRequest
    - AddOffsetCommitToTxnRequest로 추가된 Offset을 ConsumerGroup에 실제로 Commit 한다.
    - Commit 단계이다.
8. EndTxnRequest
   - 최종적으로 commit 혹은 abort 한다.
   - 여태까지 동작했던 Transaction을 반영할지, 버릴지 결정하는 단계이다.