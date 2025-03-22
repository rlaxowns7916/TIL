# RDS
- AWS Managed DB
  - DB 프로비저닝 자동화
  - OS 패치 진행
  - **지속적인 백업, 특정시간에 대한 복구(Point in Time Restore)**
  - Monitoring Dashboard
  - ReadReplica
  - Multi AZ & DR Setting
  - Vertical & Horizontal Scaling
- 지원 벤더 목록
  - Aurora
  - PostgreSQL
  - MySQL
  - MariaDB
  - Oracle
  - SQLServer
  - IBM DB2
- EBS를 기반으로 구성되어있다.
- **SSH로 접속 할 수 없다.**
  - AWS가 제공하는 ManagedService이기 떄문에, Underlying EC2에 접근 할 수 없다.

## Storage Autoscaling
- RDS 최초 생성 시, 용량을 설정하게 되어있다.
- 용량의 부족이 감지되면 RDS에서 자동으로 용량을 늘린다.
- Storage 최대 임계값을 지정하는 것이 좋다. (무한정 확장되는 것을 막기 위해서)
- DB의 중단이 필요없다. 

### 발생 조건
1. 남은 공간이 10% 아래일 때
2. 조건 1이 5분이상 지속 될 때

## Read Replica
- 최대 5개까지 생성 가능하다.
- MultiAZ로 구성 가능하다.
- Async로 복제가 발생하기 때문에 복제 지연에 따른 일관성문제가 발생할 수 있다.
- Replica는 Primary로 승격(Promotion) 가능하다.
- **동일한 Region, 다른 AZ여도 비용이 발생하지 않는다.** (Managed Service에 대한 AZ간 통신비용 예외정책)
  - region간 통신에는 비용이 추가된다.

## Multi-AZ (DR)
- **Active-Standby로 구성된다.**
  - 하나의 DNSName으로 접근 가능하다.
  - Stand-by에는 읽거나 쓸 수 없으며, 장애 대기용이다.
- **Sync로 복제가 일어난다.**
  - Transaction Commit 시, Active와 Stand-by가 모두 저장되어야 완료된다.
    - TransactionLog (write-ahead log)기반
  - RTO/RPO(복구시간/복구시점)은 거의 0에 가까워진다.
  - 복제가 실패하면 장애상황으로 판단, stand-by의 복구절차를 수행한다.
    - 재시도, 지속적인 실패시에 Instance 재생성
- 장애에 따른 Stand-by의 Active 승격도 자동으로 일어난다.
- **Read Replica도 Multi-AZ 설정이 가능하다.**

### Single-AZ → Multi-AZ 전환
- **다운타임 없음 (무중단 전환)**
- 내부 절차:
    1. Active RDS 인스턴스의 **Snapshot 생성**
    2. Snapshot 기반으로 **Standby 인스턴스 Full Copy 수행**
    3. 이후 **Sync 복제 활성화**
        - Snapshot 이후의 변화는 **Write-Ahead Log**를 이용하여 동기화하므로 데이터 누락 없음

## RDS-Proxy
- 성능, 확장성, 보안성을 향상시키기 위한 Proxy 서비스
- DB Connection Pool을 관리하고 (Application의 Pool 수를 줄이는 목적), DB의 부하를 줄이며 장애 복구시간을 단축시킨다.

### 주요 기능
1. Connection Pooling
   - 여러 애플리케이션이 RDS와 연결을 맺을 때, Connection Pool을 사용하면 연결이 끊기고 재설정되는 과정이 반복되어 RDS에 부담이 될 수 있다.
   - RDS Proxy는 RDS와의 연결을 지속적으로 유지하고, 애플리케이션과의 연결을 관리하여 실제로 필요한 연결 및 지속 시간을 개선한다.
2. AutoFailover
   - DB장애시, 자동으로 장애조치를 수행하며, 가용성을 높인다.
   - 장애조치시, Connection이 유지되므로, Application 중단을 최소화 할 수 있다.
3. Security
   - IAM과 통합하여 ACL 제공
   - DB자격증명에 대한 안전한 제공
4. Performance Optimization
   - DB연결 최적화
   - Read/Write 를 효율적으로 분산하여 DB응답시간을 줄인다.

## Aurora DB
- AWS 전용 기술 (오픈소스 (X))
  - MySQL, Postgres와 호환된다.
  - MySQL 대비 5배, Postgres 대비 3배 좋은 성능을 발휘한다.
  - RDS보다 20%정도 비싸다.
  - AWS KMS를 통해서 데이터 암호화도 설정 가능하다.
  - 전송 구간에 따른 암호화 (SSL/TLS)를 기본적으로 지원한다.
  - IAM-Role 적용이 가능하다.
- **Distributed-Storage-Architecture 이다.**
- HA 및 DR에 특화되있다.
  - MultiRegion으로 구성할 수 있다. (Aurora Global Database)
  - 하나의 Region 에서는, 여러개의 AZ에 걸쳐서 총 6개의 복사본을 갖는다. (Cluster Volume)
- 최대 15개의 ReadReplica를 갖는다. (Read Traffic Distribution, 빠른 Failover)
  - Primary Node에 장애가 생기면 Replica Node가 승격(Promotion)된다.
- 자동으로 스토리지가 확장된다.
  - 10GB 단위로 확장되며, 최대 128TB 까지 확장 가능하다. (초기용량을 굳이 높게 잡을 필요가 없다.) 
  - 성능저하나 중단시간 없이 동적으로 확장 가능하다.
- Serverless 옵션을 지원한다.
  - Connection 수, 부하에 따라서 컴퓨팅자원을 ScaleUp/Down 을 수행한다.
  - DB사용량이 간헐적이고, 예측이 불가능한 워크로드에 적합하다.
- Reader와 Writer로 나누어져 있다.
  - Writer(Primary): Read와 Write 모두 수행 
  - Reader(Replica): Read만 수행
  - Endpoint 개념으로 접근을 추상화하여, 워크로드 특성별로 접속로직 구분이 가능하다.
    - 기본제공: Reader Endpoint, WriteEndpoint
    - 추가제공: CustomEndpoint

### Aurora 스토리지 계층 구조
1. Shared Storage Layer
   - DB-Instance는 Query를 처리하고, 실제 데이터는 Storage 계층이 저장된다.
   - **Storage 계층은 물리적으로 여러개의 Storage Node로 구성되며, 각각의 Node는 고성능 Network로 연결되어 있다.**
   - 복제는 Storage-Level 에서 일어난다. (Write-Ahead-Log 기반)
2. Cluster Volume
   - 하나의 Aurora DB Cluster는 ClusterVolume을 하나 갖는다.
   - 하나의 ClusterVolume은 **여러개의 AZ에 분산된 6개의 물리적 복사본을 갖는다.**
3. Quorum
   - 아래와 같은 경우 확정(Commit) 한다.
   - Write의 경우에는 (4/6)
   - Read의 경우에는 (3/6)
4. Replication
   - Log-based-Replication (WAL 을 통해서 복제)
   - 압축 및 병렬 처리 (다수의 Write 요청을 병렬로 처리해 성능을 극대화) 
   - p2p 복구 (정합성 불일치 시)