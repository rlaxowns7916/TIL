# Redis High Availability: Sentinel vs Cluster

## 1. Overview
Redis의 고가용성(HA)을 보장하는 두 가지 핵심 아키텍처인 **Sentinel**과 **Cluster**의 차이점을 정리한다. Sentinel은 **Failover(장애 복구)**에 집중하며, Cluster는 **Sharding(데이터 분산)**과 Failover를 동시에 제공한다.

## 2. Redis Sentinel
**목적:** Master-Replica 구조에서 Master 장애 시 자동으로 Replica를 Master로 승격시키는 모니터링/관리 시스템.

### Architecture
```
+--------+       +----------+
| Client | <---> | Sentinel |
+--------+       +----------+
                      |
      +---------------|---------------+
      |               |               |
      v               v               v
+----------+    +----------+    +----------+
|  Master  | -> | Replica1 |    | Replica2 |
+----------+    +----------+    +----------+
```

### 특징
- **Failover:** Quorum(정족수) 합의를 통해 장애 판단 후 자동 승격.
- **Configuration Provider:** 클라이언트는 Sentinel에 접속해 현재 Master 주소를 질의(Service Discovery).
- **Sharding:** 지원 안 함. (단일 Master의 쓰기 성능 한계).

## 3. Redis Cluster
**목적:** 데이터를 여러 노드에 자동 분산(Sharding)하고, 각 샤드별로 HA를 구성하여 확장성(Scale-out) 확보.

### Architecture
```
      +-------+        +-------+        +-------+
      | Node A| <----> | Node B| <----> | Node C|
      | (0-5k)|        | (5k-10k)|      | (10k+)|
      +-------+        +-------+        +-------+
         |                |                |
      +-----+          +-----+          +-----+
      |Rep A|          |Rep B|          |Rep C|
      +-----+          +-----+          +-----+
```

### 특징
- **Sharding:** 16384개의 Hash Slot을 노드에 분배.
- **Failover:** Sentinel 없이 노드 간 Gossip 프로토콜로 장애 감지 및 승격.
- **Smart Client:** 클라이언트가 Slot 매핑 정보를 캐싱하고, `MOVED/ASK` 에러를 처리.

## 4. Comparison

| Feature | Sentinel | Cluster |
|---|---|---|
| **Primary Goal** | High Availability (Failover) | Scalability (Sharding) + HA |
| **Failover** | Sentinel 프로세스가 주도 | 노드 간 Gossip 합의 |
| **Sharding** | X (Client-side sharding 필요) | O (Native Sharding) |
| **Client** | Sentinel 접속 지원 필요 | Cluster Protocol 지원 필요 |
| **Complexity** | 상대적으로 낮음 | 높음 |

## 5. Conclusion
- 데이터 용량이 작고(단일 노드 충분) **안정성**이 중요하다면 **Sentinel**.
- 대규모 데이터/트래픽 처리가 필요해 **확장성**이 중요하다면 **Cluster**.
