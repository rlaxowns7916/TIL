# Producer
- Kafka Topic으로 Record를 보내는 어플리케이션
  - 성공 여부를 확인 할 수 있으며, 재시도 또한 가능하다.
- Serializer로 Record를 직렬화 한다.

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


### Sticky Partitioner (default 2.4v 이상부터)
- Batch단위의 RoundRobin
- Message 단위의 RoundRobin은 여러개의 Broker에 Connection을 맺어야 하고,
  Batch의 장점을 누릴 수 없기 때문에 해당 방법을 사용한다.


### Acks
- Producer가 Broker에 Message를 보내고 받는 리턴 값이다.
- Acks 수준을 관리함으로 해서, Message 유실 가능성과 성능사이에서 조절하게 된다.

#### All
- -1: 모든 Broker 에 대한 Write 완료 (default 3.0v 이상부터)  

#### AT Most Once(최대 한번)
- 0: 무조건 신뢰하고 다음걸 보낸다. (At Most Once: 최대 한번의 발송)

#### At Least Once (최소 한번)
**중복이 발생 할 수 있기 떄문에, Consumer에서 중복처리 로직을 추가해주어야한다.**
- 1: Leader Partition에 제대로 들어갔는지만 확인 
- (N > 1): Follwer Partition들 까지 복제가 완료했을 때 acks를 받는다.

### Exactly Once
***Idempotent Producer(멱등성 보장 프로튜서)***
- 중복이 발생하지 않는다.
- PID와 SEQ를 조합하여 중복을 확인한다.
