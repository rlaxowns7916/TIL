# Broker
- 보통 물리적 서버와 1:1로 구성된다.
- Kafka Server라고 불린다.
- Kafka Cluster를 구성하는 요소이다.
    - 최소 3대이상의 Broker를 사용하는 것을 권장한다.
- Partition에 Record를 Read, Write 하는 소프트웨어 이다.
- Topic의 Partition 일부를 가지고 있다.
    - 일부분을 갖고있을 뿐, 전체 데이터를 갖고있지 않다.
- 하나의 Broker에만 연결해도 Cluster 전체에 접근 할 수 있다.
    - 하지만 해당 Broker가 장애가 발생하면, 접근 할 곳이 사라지므로 보통 모두 연결한다.
    - 각각의 Broker는 모든 Broker,Topic, Partition에 대해서 알고 있다. (MetaData)
### 1. 컨트롤러
- **Kafka Cluster 내부의 Broker중 하나가 Controller가 된다.**
- ZooKeeper로 부터 Broker Liveness를 모니터링 한다. (상태 체크)
- Leader와 Replica 정보를 ZooKeeper로 부터 수신하고, 해당 정보를 Cluster내의 다른 Broker에게 전달한다.
    - **LeaderPartition을 갖고 있는 Broker가 장애시 Controller가 Leader Election을 수행한다.**
    - **Controller장애시 ActiveBroker중 하나가 Controller가 된다.**
- Broker가 Cluster에서 이탈하는 경우, 해당 Broker에 존재하는 Leader Partition을 재분배한다.

### 2. 데이터 삭제
- DELETE와 COMPACT로 나뉜다.
- DELETE
  - Log Segmenet 단위로 데이터를 삭제한다.
    - 파일 단위이기 때문에, 개별 Record 단위의 삭제는 불가능하다.
  - 옵션에 따라서 삭제되는 기준을 변경 할 수 있다.
  - 디스크의 크기를 고려해야한다.
    - retention.ms(minutes,hours: 세그먼트를 보유할 최대기간 (deafult 7일)
    - retention.byres: 파티션 당 로그 적재 byte 값 (default -1 (지정하지 않음))
    - log.retention.check.interval.ms: 세그먼트가 삭제 영역에 들어왔는지 확인하는 간격 (default 5분)
- COMPACT
  - Key별로 가장 최근의 Key만 남기고 나머지는 삭제한다.
### 3. 코디네이터
- Consumer가 Commit 한 Offset을 저장하는 역할이다. (Consumer가 Topic의 어느 지점 까지 읽었는지 명시 해주는 것)
- _consumer_offsets라는 Topic에 자동으로 저장된다.
    - 기본적으로 생성되는 Topic이다.
- Consumer Group의 상태를 체크고, Rebalance(재 매칭)를 수행한다..
    - Consumer과 Partition의 정상적인 매칭을 만들어준다.
    - ex) 보통 Consumer과 Partition은 1:1 관계이지만, Consumer 에러시에 정상인 Consumer가 Partition과 1:N 관계가 될 수도 있다.

### 4. 데이터 저장
- config/server.properties의 log.dir에 명시된 디렉토리에 데이터를 저장한다.
    - Topic이름, Parition번호의 조합으로 하위디렉토리를 생성하여 데이터를 저장한다.
- Segment로 구성된다.
    - 물리적인 파일이다.
    - Active Segment에 데이터가 저장된다.
        - 가장 마지막 Segment이다.
        - 삭제 대상으로 지정되지 않는다.
    - 옵션에 따라서 다음 Segment파일로 넘어갈 수 있다.
        - Segment 파일 크기가 옵션값을 넘어 갔을 때 (기본값 1G)
        - Segment 파일이 생성된지 특정 기간을 넘겼을 때 (기본값 7일)
- 데이터 저장에 대한 Validation을 수행하지 않는다.
  - Producer와 Consumer에서 Validation 로직을 수행해야 한다.

### On-Premise 권장 설치사항
- Memory: 32GB 
- HeapMemory: 6GB(나머지는 OS의 페이지 캐시영역으로 활용)
- Disk: RAID 10(NAS[Network-Attached-Storage]는 사용해서는 안됨)
- Network: 사용하는 데이터 통신량에 따라서 결정
- FileSystem: XFS 혹은 ext4
- Broker: 10개