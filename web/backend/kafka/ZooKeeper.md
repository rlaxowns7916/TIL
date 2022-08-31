# ZooKeeper
- **분산 코디네이터** 이다.
    - 분산 시스템 간의 데이터 동기화 및 Lock의 기능을 수행한다.
    - 분산 시스템 내의 중요한 상태, 설정 정보등을 저장한다.
    - 이중화를 통한 고가용성을 보장해야 한다.
- Broker를 관리하는 소프트웨어 이다.
    - 분산 Configuration 정보 유지
    - 분산 동기화 서비스 제공
    - 네이밍 레지스트리 제공
- 분산작업을 제어하기위해서 Tree형태로 구성되어 있다.
- ZooKeeper 없이는 Kafka는 작동하지 않는다.
    - ZooKeeper를 제외한 버전 출시예정(2022)
    - 3.0 부터는 ZooKeeper가 없이도 동작하지만 아직 완전하지 않다.
- Cluster로 구성된다.
  - 홀수로 구성한다. 
  - ZooKeeper Emsemble 이라고 부른다.
  - Leader와 Follower로 나뉜다.
- KafkaCluster과 1:N 관계를 유지 할 수 있다.
  - 하나의 Zookeeper Emsemble은 여러개의 KafkaCluster를 관리 할 수 있다.
        
    
## 정족수 기반 (Quorum 알고리즘)
- Zookeeper의 의사결정 알고리즘
- 분산코디네이션 환경에서 예상치 못한 장애가 발생하였을 때, 분산시스템의 일관성을 유지시키기 위해서 사용