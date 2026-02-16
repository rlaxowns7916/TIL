# Redis High Availability: Sentinel vs Cluster

## 1. Overview
Redis의 고가용성(HA)을 보장하는 두 가지 핵심 아키텍처인 **Sentinel**과 **Cluster**의 차이점을 심층 분석한다. 단순한 기능 비교를 넘어, 장애 감지(Failure Detection), 리더 선출(Leader Election), 그리고 클라이언트 동작 방식의 차이에 집중한다.

## 2. Redis Sentinel: 안정성 중심의 HA
**목적:** Master-Replica 구조의 모니터링 및 자동 Failover. 데이터 샤딩은 지원하지 않음.

### 2.1 Architecture & Quorum
Sentinel은 별도의 프로세스로 실행되며, **Quorum(정족수)** 기반의 합의 알고리즘을 사용하여 Split Brain 현상을 방지한다.

```
[Network Partition Scenario]

Group A (Majority)          |   Group B (Minority)
                            |
+--------+   +----------+   |   +----------+
| Client |---| Sentinel1|   |   | Sentinel3|
+--------+   +----------+   |   +----------+
     |            |         |         |
+----------+ +----------+   |   +----------+
|  Master  |-| Sentinel2|   |   | Replica1 |
+----------+ +----------+   |   +----------+
```
- **SDOWN (Subjective Down):** 하나의 Sentinel이 Master에 접속 불가라고 판단.
- **ODOWN (Objective Down):** 설정된 Quorum(예: 3대 중 2대) 이상의 Sentinel이 SDOWN에 동의하면 ODOWN으로 확정하고 Failover를 시작.
- **최소 구성:** Split Brain 방지를 위해 **최소 3개의 Sentinel 인스턴스** 권장 (홀수 구성).

### 2.2 Failover Process
1. **Detection:** Master ODOWN 확정.
2. **Election:** Sentinel 프로세스들 중 Failover를 주도할 'Leader Sentinel' 선출 (Raft 알고리즘 유사).
3. **Promotion:** Leader가 적절한 Replica(데이터 최신성, 우선순위 고려)를 골라 `SLAVEOF NO ONE` 명령 전달.
4. **Reconfiguration:** 나머지 Replica들이 새 Master를 바라보도록 설정 변경.
5. **Notification:** Pub/Sub 채널을 통해 클라이언트에게 새 Master 정보 전파.

### 2.3 Client Complexity (단점)
클라이언트는 Master의 IP를 직접 알면 안 되며, **반드시 Sentinel을 통해 Master 주소를 질의(Discovery)**해야 한다.
- **연결 과정:** Client -> Sentinel 접속 -> `get-master-addr-by-name` -> Master IP 획득 -> Master 접속.
- **장애 시:** 연결이 끊어지면 다시 Sentinel에 질의하여 새 Master 주소를 받아야 함.

---

## 3. Redis Cluster: 확장성 중심의 Sharding + HA
**목적:** 데이터 분산(Sharding)과 HA를 동시에 제공. 별도의 Sentinel 프로세스 없이 노드끼리 통신.

### 3.1 Architecture & Hash Slots
전체 데이터를 **16,384개의 Hash Slot**으로 나누어 노드에 분배한다.
- Key의 할당: `CRC16(key) % 16384`

```
Node A (Master) Contains: Slots 0-5460
Node B (Master) Contains: Slots 5461-10922
Node C (Master) Contains: Slots 10923-16383
```

### 3.2 Gossip Protocol & Failover
Cluster의 모든 노드는 **Gossip Protocol**을 통해 서로의 상태를 지속적으로 교환한다.
- **PFAIL (Probable Fail):** A노드가 B노드에 ping을 보냈는데 응답이 없으면 PFAIL 마킹.
- **FAIL:** 클러스터 내 과반수의 Master 노드가 B를 PFAIL로 마킹하면 B는 FAIL 상태가 됨.
- **승격:** B의 Replica가 FAIL 상태를 인지하면, 과반수 Master의 투표를 얻어 Master로 승격.

### 3.3 Smart Client (Redirect)
클러스터는 프록시가 아니며, 클라이언트가 **어떤 Key가 어떤 노드에 있는지 알아야 한다.**
- **MOVED Error:** 클라이언트가 엉뚱한 노드에 요청을 보내면, 올바른 노드 IP와 함께 `MOVED` 에러 리턴 (영구 이동).
- **ASK Error:** 리샤딩(마이그레이션) 중에 임시로 해당 노드에 질의하라는 신호.
- **Client Library:** Jedis, Lettuce 같은 라이브러리는 Slot-Node 매핑 정보를 내부적으로 캐싱하여 리다이렉트를 최소화함.

---

## 4. Comparison & Decision Guide

| Feature | Sentinel | Cluster |
|---|---|---|
| **핵심 가치** | **HA (안정성)** | **Scale-out (확장성)** + HA |
| **Failover 주체** | Sentinel 프로세스 (외부 감시자) | Master 노드들 (Gossip 합의) |
| **최소 노드 수** | 3개 (M1+R1+S1 불가, S는 3개 권장) | 6개 (Master 3 + Replica 3) |
| **데이터 분산** | 지원 안 함 (Client-side sharding 필요) | **Native Sharding (16384 Slots)** |
| **클라이언트 요구사항** | Sentinel 접속 및 주소 Discovery 지원 | Hash Slot 매핑 캐싱, MOVED/ASK 처리 |
| **트랜잭션/Multi-key** | 단일 노드라 자유로움 | **같은 Slot 내의 Key만 가능** (Hash Tag `{user:1}` 필요) |

### 5. Conclusion
- **Sentinel 선택:** 데이터셋이 메모리 한계(예: 32GB~64GB)를 넘지 않으며, 관리 복잡도를 낮추고 안정적인 Failover가 최우선일 때.
- **Cluster 선택:** 데이터가 단일 노드 메모리를 초과하거나, 쓰기 트래픽(Write Throughput)을 여러 노드로 분산해야 할 때.
