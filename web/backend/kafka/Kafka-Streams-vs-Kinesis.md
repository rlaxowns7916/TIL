# Kafka Streams vs Amazon Kinesis — 실무 비교 정리

## 0) 한 줄 결론
- **Kafka Streams**: Kafka 위에서 **애플리케이션 라이브러리로** 상태 기반 스트림 처리를 구현(운영 단위는 “앱 + Kafka 클러스터”).
- **Kinesis(Data Streams)**: AWS가 제공하는 **매니지드 스트리밍 서비스**(운영 단위는 “AWS 리소스 + 소비자 애플리케이션”).


## 1) 처리 모델(Programming Model)

### Kafka Streams
- Java/Scala 기반(일반적으로) 라이브러리
- `consume → transform → (state store) → produce` 패턴
- 상태(state)는 **로컬 RocksDB + changelog topic** 으로 내구성 확보

```
[Input Topic] -> (Streams App)
                  | map/filter/join/agg
                  | local state (RocksDB)
                  v
              [Output Topic]
```

### Kinesis
- 스트림은 AWS 리소스(Shards)
- 처리는 보통
  - KCL(Kinesis Client Library)
  - Lambda event source
  - (또는) Flink(Kinesis Data Analytics)
  와 결합

```
[Producers] -> [Kinesis Stream (shards)] -> [KCL Consumers / Lambda / Flink]
```


## 2) 확장 단위(Scaling Unit)와 병렬성

### Kafka Streams
- 병렬성 기본 단위: **Kafka Partition**
- Streams 애플리케이션 인스턴스 수는 파티션 수 이상으로 늘려도 효과가 제한됨(유휴 인스턴스 발생)
- 리밸런싱은 **Consumer Group 프로토콜**에 따름

### Kinesis
- 병렬성 기본 단위: **Shard**
- 처리량은 shard 개수(샤드 스케일링)로 증감
- Enhanced Fan-Out, On-Demand/Provisioned 등 옵션에 따라 소비자/처리량 모델이 달라짐


## 3) Exactly-once / 처리 보장(Guarantees)

### Kafka Streams
- Kafka 트랜잭션 기반 EOS(Exactly-Once Semantics) 지원(설정/버전/토폴로지에 따라 제약 존재)
- EOS를 쓰면 보통 **처리량/지연이 증가**(트랜잭션/커밋 비용)

### Kinesis
- 기본적으로 “at-least-once”에 가까운 패턴이 흔함(소비자 체크포인트, 재시도 등으로 인해)
- “정확히 한 번”은 애플리케이션 레벨(멱등 처리, dedup, transactional sink 등) 설계로 달성하는 경우가 많음


## 4) 운영/비용/락인(ROI 관점)

### Kafka Streams
- 장점
  - Kafka 생태계(Connect, Schema Registry 등)와 자연스럽게 결합
  - 멀티 클라우드/온프렘 전략에 유리
- 단점
  - Kafka 클러스터 운영 책임(또는 Confluent Cloud 등 관리형 선택 필요)

### Kinesis
- 장점
  - AWS 매니지드로 운영 부담 감소(특히 소규모 팀/빠른 론칭)
  - IAM/CloudWatch 등 AWS 운영 도구와 일체화
- 단점
  - AWS 종속(lock-in) 가능성
  - 샤드/트래픽 모델에 따라 비용이 예측하기 까다로운 경우가 있음


## 5) 선택 가이드(간단 체크리스트)
- 이미 Kafka가 “표준”인가?
  - Yes → Kafka Streams 우선 검토(조직의 운영/도구/스키마 관리가 이미 Kafka 중심일 가능성)
- AWS 안에서만 빠르게 끝내야 하는가?
  - Yes → Kinesis + Lambda/Flink 조합이 ROI가 높을 수 있음
- 상태 기반 연산(join/agg/window)이 핵심인가?
  - Yes → Kafka Streams(로컬 상태 + changelog 모델) 또는 Kinesis Data Analytics(Flink) 비교


## 6) 리플레이/순서/장애복구 관점(실무 포인트)
- **리플레이(재처리)**
  - Kafka: offset 기반이라 특정 시점부터 재처리가 상대적으로 단순(consumer group/seek).
  - Kinesis: consumer checkpoint(KCL) 또는 Lambda 재시도에 의해 재처리가 발생. “어디까지 처리했는지”는 애플리케이션 책임이 더 큼.
- **순서 보장**
  - Kafka: partition 단위 순서 보장(동일 key → 동일 partition 설계가 핵심).
  - Kinesis: shard 단위 순서 보장(PartitionKey → shard 매핑).
- **상태(state) 복구**
  - Kafka Streams: 로컬 state store(RocksDB) + changelog topic로 복구.
  - Kinesis: 보통 외부 상태 저장소(DynamoDB/Redis/RDB) 또는 Flink state backend로 복구.


## 7) 참고(공식)
- Apache Kafka: Kafka Streams
  - https://kafka.apache.org/documentation/streams/
- Apache Kafka: Processing Guarantees / Exactly Once Semantics
  - https://kafka.apache.org/documentation/#semantics
- Apache Kafka: Transactions
  - https://kafka.apache.org/documentation/#transactions
- AWS: Amazon Kinesis Data Streams Developer Guide
  - https://docs.aws.amazon.com/streams/latest/dev/introduction.html
- AWS: Developing consumers with KCL
  - https://docs.aws.amazon.com/streams/latest/dev/developing-consumers-with-kcl.html
- AWS: Enhanced fan-out consumers
  - https://docs.aws.amazon.com/streams/latest/dev/enhanced-consumers.html
