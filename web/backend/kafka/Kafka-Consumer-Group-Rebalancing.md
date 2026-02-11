# Kafka Consumer Group 리밸런싱(Rebalancing)과 순서/처리 보장

## 목적
- Consumer Group의 **리밸런싱이 언제/왜 발생**하는지, 어떤 프로토콜로 진행되는지 이해한다.
- 리밸런싱이 **처리 지연/중복 처리/순서 보장**에 미치는 영향을 정리하고, 운영에서의 방지 전략(설정/패턴)을 제시한다.

---

## 1) 리밸런싱이란?
Consumer Group은 파티션을 컨슈머 인스턴스에 할당(assign)하여 병렬 처리한다. 그런데 아래 이벤트가 발생하면 **파티션 할당을 다시 계산**해야 한다.

### 리밸런싱 트리거 (대표)
- 컨슈머 인스턴스 수 변화
  - scale-out / scale-in
  - 컨슈머 프로세스 크래시/재시작
- 컨슈머가 그룹에서 “죽었다”고 판단
  - heartbeat 실패(`session.timeout.ms`)
- 폴링이 너무 오래 멈춤
  - 처리 시간이 길어 `max.poll.interval.ms` 초과
- 토픽 파티션 수 변화(토픽 확장)

---

## 2) (클래식) 리밸런싱 프로토콜 개요
Kafka의 그룹 관리 프로토콜은 대략 다음 흐름으로 “재조합”된다.

```
Consumers (group)                 Coordinator (broker)

C1 ---- JoinGroup -------------------->
C2 ---- JoinGroup -------------------->
...
Coordinator chooses leader
Leader computes assignment (assignor)
Leader ---- SyncGroup(assignment) ---->
Coordinator ---- SyncGroupResponse ----> C1/C2/...

(이후) 각 컨슈머는 assigned partition을 consume
```

### 핵심 용어
- **Group Coordinator**: 특정 consumer group을 관리하는 브로커 역할(모듈).
- **Leader Consumer**: 그룹 내 1개 컨슈머가 leader로 선출되어 할당 계산을 수행(클래식 프로토콜).
- **Partition Assignor**: 파티션 할당 알고리즘. 예) Range, RoundRobin, Sticky, CooperativeSticky

---

## 3) Eager vs Cooperative 리밸런싱 (중요)

### Eager(Stop-the-world) 리밸런싱
전통적인 방식은 리밸런싱 시점에 **기존 파티션을 모두 revoke**하고, 그 다음에 새로 assign한다.

- 장점: 단순
- 단점: 리밸런싱 동안 **전 파티션 소비가 멈출 수 있음**(processing stop)

```
Time →

C1: [consume P0,P1] -- revoke all --> [stop] ---- assign new --> [resume]
C2: [consume P2,P3] -- revoke all --> [stop] ---- assign new --> [resume]
```

### Cooperative(Incremental) 리밸런싱
Incremental cooperative 방식은 “전부 회수 후 재할당”이 아니라 **점진적으로 파티션을 이동**한다.

- 장점: 리밸런싱 중에도 일부 파티션은 계속 처리 → 다운타임 감소
- 단점: 설정/운영 이해 필요(특히 assignor)

```
Time →

C1: [consume P0,P1] -- revoke P1 only --> [consume P0] -- assign P2 --> [consume P0,P2]
C2: [consume P2,P3] ------------------> [consume P2,P3] -- assign P1 --> [consume P1,P2,P3]
```

> 실무적으로는 `CooperativeStickyAssignor`(또는 해당하는 cooperative 전략) 채택 여부가 리밸런싱 체감 품질을 크게 좌우한다.

---

## 4) 순서 보장과 리밸런싱

### Kafka가 보장하는 “순서”의 범위
- **파티션 단위 순서**: 한 파티션 내 레코드 순서는 유지된다.
- “토픽 전체 순서”는 보장되지 않는다(파티션이 여러 개라면 자연스럽게 병렬 소비됨).

### 리밸런싱이 순서/중복에 미치는 영향
리밸런싱 자체가 파티션 내부 순서를 깨지는 않지만, **중복 처리(duplicate processing)** 를 유발할 수 있다.

대표 케이스: 오프셋 커밋 전에 처리 완료 → 리밸런싱/장애 → 다른 컨슈머가 같은 오프셋부터 재처리.

```
P0 log: ... [offset=10] [11] [12] ...

C1 처리:
- offset 10 처리 완료
- (커밋 전) 리밸런싱 발생 → P0가 C2로 이동

C2:
- 마지막 커밋이 9라면 offset 10부터 재처리 (중복)
```

### 결론
- 리밸런싱은 **at-least-once 소비에서는 중복 처리 가능성**을 높인다.
- 중복을 “없애려는” 접근보다, **중복을 전제로 멱등 처리(consumer side idempotency)** 를 설계하는 것이 현실적이다.

---

## 5) 운영에서 리밸런싱을 줄이는 설정/전략

### (1) Static Membership: 재시작으로 인한 불필요한 리밸런싱 감소
- 컨슈머 인스턴스가 재시작/재연결하더라도 동일 멤버로 간주되도록 하는 기능.
- `group.instance.id`를 사용.

효과:
- “잠깐 죽었다 살아난” 컨슈머로 인해 전체 그룹이 요동치는 상황 감소.

### (2) 처리 시간이 긴 경우: `max.poll.interval.ms` 튜닝
- 메시지 처리 시간이 길면 poll이 늦어지고, **컨슈머가 살아있어도 그룹에서 제거**될 수 있다.
- 해결:
  - 배치 크기(`max.poll.records`)를 낮춰 poll 주기를 안정화
  - 처리 파이프라인 병렬화/비동기화
  - `max.poll.interval.ms`를 처리 시간에 맞춰 상향(단, 너무 키우면 장애 감지가 늦어짐)

### (3) Cooperative assignor 사용 검토
- `partition.assignment.strategy`에 cooperative 계열을 적용(환경/버전에 따라 클래스명 상이).

### (4) 리밸런스 리스너로 안전한 revoke 처리
- 리밸런싱 시점에 in-flight 작업을 정리하지 않으면 중복/유실(외부 시스템 관점)이 커질 수 있다.
- `ConsumerRebalanceListener`에서 revoke 시점에:
  - 처리 중인 작업 중단/플러시
  - 필요한 경우 오프셋을 **동기 커밋**(지연을 줄이되 안전성 요구에 맞게)

---

## 6) 체크리스트 (현장형)
- [ ] 컨슈머 처리 로직이 **멱등**인가? (중복 처리 대비)
- [ ] `max.poll.interval.ms`, `max.poll.records`가 처리 시간과 일치하는가?
- [ ] 리밸런싱 리스너에서 revoke 시 안전하게 정리하는가?
- [ ] scale-out/rolling deploy가 잦다면 `group.instance.id`(static membership) 또는 cooperative 리밸런싱을 검토했는가?

---

## 참고 문헌 (공식/준공식)
- Apache Kafka Documentation
  - Consumer Configs / Group Management / Rebalance 관련 설정 문서
  - https://kafka.apache.org/documentation/
- KIP-345: Static Membership
  - https://cwiki.apache.org/confluence/display/KAFKA/KIP-345%3A+Static+Membership
- KIP-429: Incremental Cooperative Rebalancing
  - https://cwiki.apache.org/confluence/display/KAFKA/KIP-429%3A+Incremental+Cooperative+Rebalancing
- Confluent Documentation: Rebalancing, cooperative sticky assignor 개념 정리
  - https://docs.confluent.io/
