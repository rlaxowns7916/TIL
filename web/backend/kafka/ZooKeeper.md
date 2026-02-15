# ZooKeeper (Kafka 관점)

## 1) ZooKeeper가 하는 일(전통적 Kafka: ZK 모드)
ZooKeeper는 분산 시스템에서 **메타데이터/상태를 합의(consensus)로 관리**하는 코디네이터입니다.
Kafka가 ZooKeeper에 의존하던 시절(= ZK 모드)에는 대략 아래를 맡았습니다.

- **브로커 등록/발견**(broker id, 엔드포인트)
- **컨트롤러(Controller) 선출**
- **토픽/파티션 메타데이터** 일부 관리
- **ACL/쿼터 등 설정 정보** 저장(버전/구성에 따라 범위 상이)

> 주의: “ZooKeeper = Lock 서비스”로만 이해하면 Kafka에서의 역할을 과소평가하게 됩니다.


## 2) (중요) 2026 기준 팩트: Kafka는 ZooKeeper 없이도 동작한다 (KRaft)
과거 문서에는 “ZooKeeper 없이는 Kafka는 작동하지 않는다”라고 적혀 있었지만, **현재 Kafka는 KRaft(Kafka Raft) 모드로 ZooKeeper 없이 클러스터를 구성**할 수 있습니다.

- KRaft 모드에서는 **메타데이터 합의/저장**을 ZooKeeper 대신 Kafka 내부의 **컨트롤러 쿼럼(Controller Quorum)** 이 담당합니다.
- 운영 관점의 핵심 변화는 “외부 코디네이터(ZK)”를 제거하고 **Kafka 자체가 합의 레이어를 포함**한다는 점입니다.

### ZK 모드 vs KRaft 모드(ASCII)

ZK 모드(구형)

```
+-------------+      +------------------+
| Kafka Broker|<---->|  ZooKeeper Ensemble|
+-------------+      +------------------+
        ^
        | (메타데이터/컨트롤러 선출 등)
        v
+-------------+
| Kafka Broker|
+-------------+
```

KRaft 모드(현행)

```
+---------------------+
| Kafka Controller Quorum |
| (Raft 기반 합의)         |
+---------------------+
        ^        ^
        |        |
+-------------+  +-------------+
| Kafka Broker|  | Kafka Broker|
+-------------+  +-------------+
```


## 3) ZooKeeper Ensemble / Quorum(정족수) 개념
ZooKeeper는 보통 **홀수 노드(3/5/7...)** 로 구성되는 Ensemble로 운영합니다.

- 리더(Leader) 1대 + 팔로워(Follower)들로 구성
- 쓰기(write)에는 **정족수(Quorum)** 의 동의가 필요
- 네트워크 분할(partition) 상황에서 “과반이 살아있는 쪽”만 진행하도록 하여 일관성을 지킵니다.


## 4) 참고(공식)
- Apache Kafka: KRaft(메타데이터 모드 / ZooKeeper 제거)
  - https://kafka.apache.org/documentation/#kraft
- Apache ZooKeeper: 공식 문서(Quorum / ZAB 개요)
  - https://zookeeper.apache.org/documentation.html
