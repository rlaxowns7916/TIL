# Redis HA: Sentinel vs Cluster (운영 관점)

> 목표: “Redis를 고가용성(HA)로 운영해야 할 때” **Sentinel**과 **Cluster** 중 무엇을 선택할지, 그리고 장애 시나리오에서 무엇이 깨질 수 있는지(운영 리스크)를 빠르게 판단할 수 있게 정리한다.

## 0) 한 줄 요약

- **Sentinel**: *"단일 Primary(쓰기 1곳) + Replica"* 구조를 **자동 Failover**로 운영하고 싶을 때. (샤딩은 애플리케이션/프록시로 별도 해결)
- **Cluster**: *"샤딩(수평 확장) + HA"*를 Redis 자체가 제공. **키 분산/멀티 노드**가 필요할 때.

---

## 1) 구조 비교

### 1.1 Sentinel (Replication + Failover)

- 데이터 분산(샤딩) 없음: 보통 **1 Primary + N Replica**
- Sentinel들이 Primary/Replica를 감시하다가, Primary 장애 시 **새 Primary를 승격**
- 클라이언트는 보통 "지금 Primary가 누구인지"를 알아야 함

ASCII 스케치:

```
      +-------------------+
      |  Sentinel quorum  |
      +-------------------+
          |        |
          v        v
+----------------+   +----------------+
|   Replica A    |   |   Replica B    |
+----------------+   +----------------+
          ^                 ^
          | (replication)   | (replication)
          +--------+--------+
                   |
                   v
             +-----------+
             |  Primary  |
             +-----------+
```

### 1.2 Cluster (Sharding + HA)

- Redis가 **16384 hash slots** 기준으로 키를 분산
- 각 샤드(마스터)에 복제본(레플리카)을 붙여서 HA 제공
- 클라이언트는 MOVED/ASK 리다이렉션을 처리(또는 cluster-aware client 사용)

ASCII 스케치:

```
slots 0..16383

+---------+      +---------+      +---------+
| M1      |      | M2      |      | M3      |
| (slots) |      | (slots) |      | (slots) |
+----+----+      +----+----+      +----+----+
     |                |                |
     v                v                v
+---------+      +---------+      +---------+
| R1      |      | R2      |      | R3      |
+---------+      +---------+      +---------+
```

---

## 2) 선택 기준 (Decision)

### 2.1 "샤딩"이 필요한가?

- **필요 없음** (메모리/트래픽이 단일 노드로 충분) → Sentinel 우선 고려
- **필요함** (용량/처리량 때문에 다수 노드로 분산 필요) → Cluster 우선 고려

### 2.2 애플리케이션 변경 비용(클라이언트/운영) 감수 가능한가?

- Sentinel은 상대적으로 단순하지만, 클라이언트가 Primary 변경을 따라가야 한다.
- Cluster는 키 분산과 리다이렉션, 멀티키 제약 등 애플리케이션/라이브러리 측 고려사항이 늘어난다.

### 2.3 멀티키/트랜잭션/스크립트 사용 패턴

- Cluster는 서로 다른 슬롯의 키에 대한 멀티키 연산(MGET, Lua, 트랜잭션 등)이 제한될 수 있다.
- 같은 슬롯에 묶고 싶다면 **hash tag**(예: `{user:123}:a`, `{user:123}:b`)를 적극적으로 설계해야 한다.

---

## 3) 장애/일관성 관점 (운영에서 자주 터지는 포인트)

## 3.1 Sentinel: Failover 동안의 “쓰기 손실” 가능성

Sentinel 기반 복제는 기본적으로 **비동기 복제**가 일반적이어서,
Primary가 죽는 순간 직전의 쓰기가 Replica로 전달되지 않았으면 **데이터 유실**이 가능하다.

간단 시나리오:

```
Client -> Primary : SET x=1   (ACK 받음)
Primary --X--> Replica : replication 못하고 죽음
Sentinel: Replica 승격
Client: 새 Primary에서 GET x => 없음(유실)
```

완화책(트레이드오프 존재):
- `min-replicas-to-write`, `min-replicas-max-lag` 등으로 “복제 지연이 크면 쓰기 거부”
  - 가용성(쓰기 성공률)과 안전성(유실 방지) 사이의 조절 장치

## 3.2 Split-brain(이중 Primary) 위험과 네트워크 파티션

네트워크 파티션이 생기면, "구 Primary"가 살아있는 것처럼 보이며 쓰기를 계속 받는 동안
Sentinel 쪽에서는 "새 Primary"를 세울 수 있다.

완화책(필수급):
- `repl-ping-replica-period`, `down-after-milliseconds`, `failover-timeout` 등 감시/판단 튜닝
- (Sentinel/Replication 공통) **fencing** 관점: 구 Primary가 다시 돌아왔을 때 쓰기를 못 받게 강제
  - 현실적으로는 애플리케이션에서 “리더만 쓰기”가 보장되도록 연결 방식을 엄격히 관리

## 3.3 Cluster: 리샤딩/노드 장애 시 클라이언트 영향

- 슬롯 이동(resharding) 시 MOVED/ASK 리다이렉션이 빈번해지고, 클라이언트가 이를 제대로 처리하지 못하면 에러/지연이 발생한다.
- 노드 장애 시에도 failover가 일어나지만, 클러스터 토폴로지 변화가 잦으면 커넥션/캐시가 흔들린다.

---

## 4) 운영 체크리스트 (실전)

### 4.1 공통

- 모니터링
  - 메모리(used_memory, fragmentation)
  - 지연(latency), slowlog
  - 연결 수, blocked clients
  - AOF/RDB 지속성 지표(사용 시)
- 백업/복구 리허설
  - RDB/AOF 복구를 **주기적으로 실제로** 해보기
- 배포/장애 대응 문서
  - “Primary 변경 시 애플리케이션이 어떻게 따라가는지”가 문서로 남아야 한다.

### 4.2 Sentinel 전용

- Sentinel은 **홀수 개** 권장(예: 3, 5) + 네트워크/장애 도메인 분리
- Sentinel과 Redis 노드의 배치(같은 머신/같은 AZ 몰빵 금지)
- Failover 이벤트 시
  - 애플리케이션 connection string/endpoint 전략(서비스 디스커버리, 프록시, 라이브러리 지원)
  - DNS TTL, 캐시 등 “바뀐 Primary를 따라가는 속도” 점검

### 4.3 Cluster 전용

- cluster-aware client 강제(라이브러리 표준화)
- 키 설계
  - 멀티키/원자성이 필요한 도메인은 hash tag로 슬롯 정렬
- 리샤딩(runbook)
  - 리샤딩 절차, 트래픽 낮은 시간대, 롤백 플랜

---

## 5) 추천 패턴 (현실적인 결론)

- **캐시(세션/조회 캐시) 중심** + 단일 노드로 충분 → Sentinel이 보통 ROI가 좋다.
- **용량/처리량 때문에 샤딩이 필수** → Cluster.
- “정말 유실이 싫다”면
  - Redis 단독으로는 한계가 있어, 시스템 전체에서 **재생성 가능 캐시**로 두거나
  - DB/로그 기반으로 원천을 보존하고 Redis는 파생 데이터로 운영하는 구조를 우선 검토한다.

---

## 참고 키워드(추가 학습)

- Redis Replication / Sentinel / Cluster 공식 문서
- `min-replicas-to-write`, `min-replicas-max-lag`
- Cluster hash slots, MOVED/ASK, hash tag
- 네트워크 파티션, split-brain, quorum
