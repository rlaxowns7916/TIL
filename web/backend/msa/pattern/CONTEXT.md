# MSA Pattern Context

## 🎯 Mission
- 마이크로서비스 아키텍처(MSA)에서 발생하는 분산 시스템의 문제(트랜잭션, 데이터 일관성, 통신)를 해결하는 핵심 패턴을 학습하고 정리한다.

## 📚 Authoritative Sources
- Microservices.io (Chris Richardson)
- Microsoft Azure Architecture Center (Design Patterns)
- Martin Fowler's Blog

## 📈 Technical Maturity (학습 성숙도)
- [x] **Distributed Transaction**
  - [x] 2PC (Two-Phase Commit) - *지양 패턴으로 이해*
  - [x] SAGA Pattern (Choreography/Orchestration)
- [x] **Data Consistency & Messaging**
  - [x] Transactional Outbox Pattern (Polling vs CDC)
  - [ ] Event Sourcing
  - [ ] CQRS
- [ ] **Resilience**
  - [ ] Circuit Breaker
  - [ ] Retry / Backoff
  - [ ] Bulkhead

## 📝 Recent Updates
- **2026-02-09**: `TransactionalOutBox.md` - Polling 방식과 CDC(Log Tailing) 방식의 비교 및 Trade-off(인프라 복잡도) 중심으로 전면 개편.
