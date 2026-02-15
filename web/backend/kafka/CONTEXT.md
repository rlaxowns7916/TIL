# kafka Context

## 🎯 Mission
- kafka 기술의 핵심 원리를 파악하고 실무 적용 역량을 확보한다.
- 공식 문서 기반의 정확한 지식을 학습하고 기록한다.

## 📚 Authoritative Sources (공식 출처)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Blog & Engineering](https://www.confluent.io/blog/)
- [KIP (Kafka Improvement Proposals)](https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Improvement+Proposals)
- [Apache Kafka GitHub](https://github.com/apache/kafka)

## 🛠️ Subject-Specific Conventions (폴더 전용 규칙)
- **Fact-Check**: 최소 3개 이상의 공식 소스를 교차 검증한다.
- **Language**: 모든 설명은 한글로 작성하되, 기술 용어는 원문을 병기한다.
- **Auto-Update**: 작업 완료 시 해당 주제의 진행 상황과 성숙도를 이 파일에 최신화한다.

## 📈 Technical Maturity (학습 성숙도)
- [x] **Core Concepts**
  - [x] Topic, Partition, Broker, Record
  - [x] Producer & Consumer Basics
- [x] **Reliability & Consistency**
  - [x] Replication Protocol (ISR, HighWaterMark)
  - [x] Exactly-Once Semantics (EOS) - *Idempotent Producer & Transaction*
- [ ] **Ecosystem**
  - [ ] Kafka Connect
  - [ ] Kafka Streams
  - [ ] Schema Registry

## 📝 Recent Updates
- **2026-02-10**: `Consumer-리밸런싱-순서보장-실전.md` - 리밸런싱 원인/영향과 순서 보장 범위, 커밋/멱등 처리 중심의 실전 체크리스트 추가.
- **2026-02-09**: `EOS.md` - Exactly-Once Semantics의 구성 요소(Idempotency, Transaction)와 동작 원리를 운영 관점에서 재정리.
