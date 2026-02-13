# BACKLOG.md - Learning Queue & Ideas

## 🚀 Active Queue (최우선 순위)
| Priority | Topic | Context | Status |
| :--- | :--- | :--- | :--- |
| **P0** | **Kafka Streams & Kinesis 비교** | 책 7장. Kafka 학습의 완성(PR #12, #13 후속). 실시간 처리 패턴 확립. | Done |
| **P1** | **일관된 해싱 (Consistent Hashing)** | 책 3.5장. Kafka/Redis의 데이터 분산 핵심 원리. | Pending |
| **P1** | **CAP & PACELC 이론** | 책 3.1~3.2장. 분산 시스템 트레이드오프 설계의 이론적 근거. | Pending |
| **P1** | **Kafka KRaft(=ZooKeeper 제거) 운영/마이그레이션 개요** | ZooKeeper 문서 FIX와 연결. KRaft 용어/구성/마이그레이션 체크리스트. | Ready |

## 📅 Candidates (Prioritized)
| Priority | Category | Topic | Why? |
| :--- | :--- | :--- | :--- |
| **High** | Data Structure | **Bloom Filter & HyperLogLog** | (책 3.6, 3.8) 대규모 데이터 효율적 처리/중복 제거. |
| **Mid** | Cache | **분산 캐싱 전략 (Redis)** | (책 6장) DB 부하 분산의 핵심. |
| **Mid** | Database | **Key-Value Store (DynamoDB)** | (책 5장) NoSQL 설계 패턴 이해. |
| **Low** | Network | **DNS & Load Balancer** | (책 4장) 인프라 기초. |

## 🧠 Insight Dump (아이디어 저장소)
- *Kafka 학습 중 발견*: Zero-Copy 매커니즘과 OS PageCache의 관계 (성능 최적화 관점)
- *트랜잭션 학습 중 발견*: 2PC(2-Phase Commit)의 실제 구현체(XA)와 SAGA 패턴의 현실적 복잡도 비교

---
**Note**: 이 파일은 Agent의 '작업 큐' 역할을 하며, 학습 도중 발견된 새로운 주제나 사용자의 제안을 수시로 업데이트합니다.
