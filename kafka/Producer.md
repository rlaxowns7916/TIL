# Producer
- Kafka Topic으로 Record를 보내는 어플리케이션
  - 성공 여부를 확인 할 수 있으며, 재시도 또한 가능하다.
- Serializer로 Record를 직렬화 한다.

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
- ex)
  - -1: 모든 Broker 에 대한 Write 완료 (default 3.0v 이상부터)  
  - 0: 무조건 신뢰하고 다음걸 보낸다. 
  - 1: Leader Partition에 제대로 들어갔는지만 확인 
  - N > 1: Follwer Partition들 까지 복제가 완료했을 때 acks를 받는다.

### 중복
- At Most Once가 아닐 때 (ack N (N>=1)) 중복이 일어날 수 있다.
  - TimeOut등의 문제와 더불어서 재전송으로 인한 중복
- Consumer가 중복에 대한 처리를 해주어야 한다.

### Idempotent Producer
- Exactly Once를 제공한다.
  - 중복이 발생하지 않는다.
