# Cache Stampede(캐시 스탬피드) 대응 + Redis HA(Sentinel/Cluster) 환경 고려사항

> 목표: **캐시가 비거나(미스) 만료되는 순간**, 동시에 몰리는 트래픽으로 인해 **DB/백엔드가 터지는 현상**을 막는다.
> 추가로 Redis가 **Sentinel/Cluster로 HA 구성**인 경우, **장애/Failover 중의 재시도/타임아웃/연결 재수립**이 스탬피드를 증폭시키는 문제까지 같이 다룬다.

---

## 1) 문제 정의

### 1.1 캐시 스탬피드란?
- 특정 키(또는 소수의 핫 키)가 만료/삭제되며 미스가 발생
- 동시에 많은 요청이 들어오면, 모두가 DB/원본 시스템으로 **동시에 폴백**
- 결과적으로 DB 부하 폭발 → 타임아웃 증가 → 더 많은 재시도 → 장애 전파

### 1.2 전형적인 타임라인

```
시간 →

t0: cache hit hit hit hit
t1: TTL 만료(또는 eviction)
    요청 A,B,C,D... 모두 miss
t2: 모두 DB 조회/계산 시작
t3: DB 병목/타임아웃 → 재시도 증가
t4: 캐시가 채워지기도 전에 시스템 전체가 느려짐
```

---

## 2) 기본 패턴(캐시 미스 시 동시성 제어)

### 2.1 Singleflight / Request Coalescing
동일 키에 대한 동시 미스 요청을 **1개의 원본 조회로 합치고**, 나머지는 결과를 기다린다.

```
[Client N개] ──(same key)──> [App]
                      ├─ (첫 요청만) -> DB/Origin
                      └─ (나머지) -> wait 결과 공유
```

- 장점: 애플리케이션 레벨에서 구현이 쉬움(특히 단일 인스턴스)
- 한계: 앱 인스턴스가 여러 개면 **프로세스 간** coalescing은 별도 장치가 필요

### 2.2 Distributed Mutex(분산 락) + Double-Check
캐시 미스 시 락을 잡고, 락 획득 성공한 1명만 DB를 조회해 캐시를 채운다.

의사코드:

```text
v = cache.get(key)
if v != null: return v

if lock.tryAcquire("lock:"+key, ttl=3s):
  try:
    v = cache.get(key)          # double-check
    if v != null: return v

    v = origin.load(key)
    cache.set(key, v, ttl=30s)
    return v
  finally:
    lock.release(...)
else:
  # 락 못 잡으면 짧게 대기/백오프 후 캐시 재시도
  sleep(jittered_backoff)
  return cache.getOrFallback(key)
```

핵심 주의사항:
- 락 TTL(lease time)을 반드시 둔다(프로세스 죽으면 영구 락 방지)
- 락 획득 실패 시 **무한 대기 금지** (짧은 대기 + 백오프 + 제한)
- 락을 잡은 상태에서 **DB 조회가 락 TTL을 초과**하면 중복 로더가 생길 수 있음 → TTL을 여유 있게

### 2.3 Soft TTL + Stale-While-Revalidate(SWR)
- 데이터에 **하드 TTL**과 별개로 “**소프트 만료 시간**”을 둔다.
- 소프트 만료 후에는 **stale(구 버전)** 을 즉시 응답하고, 백그라운드에서 갱신한다.

```
요청 -> 캐시 hit (stale)
      -> 즉시 응답
      -> (비동기) 리프레시 작업 시작
```

- 장점: 트래픽 피크에서 latency/DB 부하를 안정화
- 단점: 데이터 최신성이 중요하면 적용 범위를 제한해야 함

### 2.4 TTL Jitter(만료 시점 분산)
- 동일 TTL(예: 60초)을 쓰면 **만료가 동기화**될 수 있음
- TTL을 무작위로 흔들어 만료 시점을 분산

예:
- `ttl = baseTtl ± random(0..jitter)`

---

## 3) 캐시 붕괴/브레이크다운/펜트레이션까지 함께 고려

### 3.1 Cache Breakdown(핫키 1개가 터짐)
- 해결책: 락, SWR, 보호용 로컬 캐시(짧은 TTL)

### 3.2 Cache Avalanche(대량 키가 동시에 만료)
- 해결책: TTL jitter, 워밍업(배치 프리로드), 중요 키는 soft TTL로 보호

### 3.3 Cache Penetration(존재하지 않는 키로 DB를 두들김)
- 해결책: Negative caching(“없음”도 TTL로 캐싱), bloom filter(대규모 키셋)

---

## 4) Redis HA 환경에서 스탬피드가 증폭되는 지점

Redis 장애/Failover는 “캐시 미스”와 다른 차원의 이벤트지만, 관측상 둘은 결합되어 **훨씬 큰 스탬피드**로 나타난다.

### 4.1 Sentinel
- 장애 시:
  - master down 감지
  - leader sentinel 선출
  - replica 승격
  - clients가 **새 master로 재연결**

스탬피드 증폭 포인트:
- failover 동안 `GET/SET` 타임아웃 증가
- 클라이언트가 공격적으로 retry → Redis에 추가 부하
- Redis가 불안정하니 애플리케이션이 DB로 폴백 → DB 스탬피드

### 4.2 Cluster
- 장애 시:
  - 특정 hash slot을 담당하는 node 장애
  - `MOVED/ASK` 리다이렉트 처리, topology refresh

증폭 포인트:
- topology refresh가 잦거나, 클라이언트가 리다이렉트를 잘못 처리하면 재시도 폭발
- 멀티키 연산/파이프라인이 slot 제약에 의해 실패/재시도 반복

---

## 5) 운영 체크리스트(스탬피드 + HA 함께 잡기)

### 5.1 Timeout/Retry Budget(재시도 예산)
- **Redis 타임아웃을 짧게**(예: 50~200ms) 두되, 무조건 DB 폴백을 하지 말고
- 재시도는 **작게, 제한적으로**

권장 규칙 예:
- Redis: 1회 + 짧은 백오프 1회(총 2회 이내)
- DB: 별도의 보호(서킷브레이커, rate limit)

### 5.2 Circuit Breaker + Fallback 정책
- Redis가 불안정할 때 “모든 요청을 DB로”는 최악
- fallback을 단계화:
  1) stale cache(가능하면)로 응답
  2) 부분 기능 제한(예: 추천/랭킹 off)
  3) 마지막으로 DB 폴백(동시성 제한)

### 5.3 Bulkhead(격벽)
- “캐시 미스 로더” 작업을 별도 thread pool/큐로 격리
- 로더 풀 saturation 시:
  - 즉시 실패(또는 stale 반환)
  - 무한 대기 금지

### 5.4 락 키/캐시 키 네이밍 표준
- `cache:{domain}:{id}`
- `lock:cache:{domain}:{id}`
- 만료/삭제/무효화 프로세스가 많아질수록 규약이 안정성에 직결

### 5.5 관측(Observability)
다음 지표를 같이 본다:
- Redis: latency p95/p99, timeouts, reconnect 횟수, failover 이벤트
- 앱: cache hit ratio, miss burst(초당 miss), loader concurrency
- DB: QPS, connection pool saturation, slow query 증가

---

## 6) “실전” 구성 예시(권장 조합)

### 6.1 핫키(강한 최신성 요구 X)
- SWR + background refresh
- TTL jitter
- Redis 장애 시 stale 우선

### 6.2 핫키(강한 최신성 요구 O)
- distributed mutex + double-check
- 로더 pool 제한 + fail-fast
- Redis 장애 시 DB 폴백은 **동시성 제한**

### 6.3 존재하지 않는 키가 자주 요청됨
- negative caching(짧은 TTL)
- (대규모 키셋) bloom filter 고려

---

## 7) 참고(추가로 읽을 것)
- Redis Sentinel/Cluster 공식 문서(클라이언트 동작, failover, redirection)
- (일반) cache stampede / dogpile effect, stale-while-revalidate 패턴
