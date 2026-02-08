# 분산 트랜잭션 패턴 (Distributed Transaction Patterns)

## 목적
- 단일 DB 트랜잭션으로는 해결할 수 없는 **분산 환경의 데이터 정합성 문제**를 다루는 핵심 패턴/프로토콜을 정리한다.
- 특히 실무에서 자주 만나는 **SAGA(최종 일관성)** vs **2PC/XA(강한 일관성)**의 트레이드오프와 장애 복구를 중심으로 학습한다.

## 문서 구성
- [SAGA 패턴](./SAGA.md)
- [TCC 패턴](./TCC.md)
- [2PC 패턴](./2PC.md)
- [SAGA 및 2PC 심화 가이드](./분산-트랜잭션-심화.md)

## 빠른 비교
- **2PC**: 원자성(Atomicity) 보장. 대신 **Blocking 문제** 및 락 점유로 가용성/성능 저하 위험이 큼.
- **TCC**: Try-Confirm-Cancel. 2PC의 대안으로, **자원 예약(Isolation)**을 통해 정합성을 높임. 구현 복잡도가 높음.
- **SAGA**: 최종 일관성(Eventual Consistency) 기반. 보상 트랜잭션, 멱등성(Idempotency), 메시지 신뢰성(Outbox/DLQ)이 핵심.

## 권위 소스(최소 3개 교차 검증)
- Garcia-Molina & Salem, *Sagas* (1987)
- PostgreSQL Docs: Two-Phase Commit (PREPARE TRANSACTION)
- MySQL Docs: XA Transactions
- Microservices.io: Saga Pattern
- Microsoft Azure Architecture Center: Saga design pattern
