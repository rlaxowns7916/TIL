## Partition
- Queue(FIFO) 구조이다.
    - 오래된 순서대로 Consumer가 Message를 가져간다.
    - Consumer가 Consume하여도, 데이터는 삭제되지 않는다.
        - 옵션에 따라 삭제시점을 정할 수 있다.
        - 다른 ConsumerGroup의 Consumer가 재사용 할 수 있다.
- CommitLog이다.
    - Event들이 추가된다.
    - offset이라는 것으로 Message에 접근 가능하다.
        - offset은 계속해서 증가한다.
        - Consumer Group끼리는 다른 Offset을 갖는다.
    - Producer와 Consumer의 Offset 차이를 Consumer Lag라고 부른다.
        - Consumer Lag는 성능을 모니터링 하는 중요 지표이다.
- 병렬처리를 위해서 다수의 Partition을 사용한다.
- **Segement**라는 실제 Message가 저장되는 물리 File로 구성된다.
    - 지정된 크기가 커지거나(default 1GB), 오래되면(default 168 시간) 새로운 파일이 생성되고 추가된다.
- partition끼리는 서로 독립적이다.
    - offset 조차도 독립적이다.
- partition에 저장된 파일들은 Immutable하다.
- 고가용성을 위해서 복제(Replica) 한다.
    - 여러 Broker에 분산된다.
- ConsumerGroup내의 하나의 Consumer에 의해서만 사용된다.
- 여러개의 Consumer를 담당 할 수 없다.

### Partition의 추가와 삭제
- 추가만 가능하고, 삭제는 불가능하다.
  - 여러 Broker에 저장된 Data를 취합하고 정렬하는 복잡한 과정이 발생하기 때문이다.
  - 추가 또한 Global Relocation이 발생하기 때문에 신중하야 한다.

### Leader Partition & Follower Partition
- Leader Partition에서 Read와 Write를 담당하며, Consumer와 통신한다.
  - kafka2.4 부터는 Follower Partition도 Consumer와 통신 할 수 있다.
- Follower Partition은 복제를 담당한다.
  - Leader의 CommitLog에서 FetchRequest를 통해서 복제한다.
- Leader Parition을 적절하게 분배하는 것이 성능에 좋다.

### Replication (Partition Replication)
- 고가용성을 위한 방법이다.
- Broker의 숫자보다 많이 설정 할 수 없다.
- 미리 원본(Leader) 을 복사한 복제본(Follower)을 준비하여, 장애가 발생했을 때를 미리 대비한다.
- Replication Factor 옵션은 n(원본 + 복제본) 이다.
    - replication1 : 원본 1개
    - replication2 : 원본 1개 + 복제본 1개

### 장애 발생 시
- Follower Partition 중에서 하나가 Leader Partition이 된다.
- Producer와 Consumer들 또한 새롭게 선출된 Leader로 데이터를 포워딩한다.

### Key Cardinality
- Message의 Key값의 Hash를 통해서 Partition이 결정된다.
- 균등하게 분포되어있어야지 효율성이 올라간다.

### Partition이 배치되는 방법
- 0번 Broker부터 시잫가여, Round-Robin 방식으로 Leader Partition이 생성된다.
- KafkaClient는 Leader Partition과 통신을 주고 받음으로, 특정 서버에 Traffic이 몰리는 것이 아닌,
  여러 Broker가 통신을 분담하게 된다.