# Distributed System Context

## 🎯 Mission
- 분산 환경에서 발생하는 복잡한 데이터 정합성 및 가용성 문제를 해결하기 위한 아키텍처 역량을 확보한다.
- 실무에서 즉시 활용 가능한 분산 락, 합의 알고리즘, 분산 트랜잭션 패턴을 공식 문서 수준으로 정밀 연구한다.

## 📚 Authoritative Sources (공식 출처)
- [Redis Documentation](https://redis.io/docs/): Distributed Locking (Redlock) 등 참조.
- [PostgreSQL Documentation](https://www.postgresql.org/docs/): Advisory Locks 및 Isolation Levels.
- [MySQL Documentation](https://dev.mysql.com/doc/): Named Locks 및 InnoDB Locking.
- [MIT 6.824: Distributed Systems](https://pdos.csail.mit.edu/6.824/): 이론적 배경 및 논문 소스.
- [Microservices.io](https://microservices.io): SAGA 패턴 등 마이크로서비스 패턴 표준 문서.
- [Microsoft Azure Architecture Center](https://learn.microsoft.com/en-us/azure/architecture/patterns/): 클라우드 환경 아키텍처 패턴 가이드.

## 🛠️ Subject-Specific Conventions (폴더 전용 규칙)
- **Fact-Check First**: 이론(CAP 정리, PACELC 등)과 실제 구현체(Redis, DB 등)의 차이점을 반드시 대조하여 기술한다.
- **Fail-safe Focus**: 행복 회로(Happy Path)보다는 장애 상황(Failure Modes)에서의 동작 방식을 우선적으로 분석한다.
- **Language Policy**: 모든 설명은 한글로 작성하되, 핵심 학술 용어는 원문을 병기한다.

## 📈 Technical Maturity (학습 성숙도)
- [x] 분산 락 전략 (Redis vs DB)
- [ ] 합의 알고리즘 (Paxos, Raft) 기초
- [x] 분산 트랜잭션 패턴 (SAGA, 2PC)
- [ ] 메시지 브로커 정합성 (Kafka, RabbitMQ)
