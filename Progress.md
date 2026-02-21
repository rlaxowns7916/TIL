# TIL Study Progress

| Date | Subject | Type | PR Link/Status | Notification | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 2026-02-01 | Project Infrastructure | [NEW] | Initial Setup | Completed | Bare + Worktree, AGENTS.md, Progress.md |
| 2026-02-02 | 분산 락 전략 한글화 | [REWORK] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/3 | 지침에 따른 한글화 및 구조 개선 |
| 2026-02-02 | Distributed Locking Strategies | [NEW] | [CLOSED] | - | https://github.com/rlaxowns7916/TIL/pull/2 | 한글화 작업으로 대체됨 |
| 2026-02-02 | Docker Build Audit | [FIX] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/1 | BuildKit & Multi-stage optimizations |
| 2026-02-03 | 분산 트랜잭션 패턴 심화 | [REWORK] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/5 | SAGA, 2PC 메커니즘 및 장애 복구 전략 |
| 2026-02-03 | Kafka 트랜잭션 및 EOS | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/6 | Idempotent Producer, Transaction API 상세 분석 |
| 2026-02-04 | 분산 트랜잭션 패턴 심화 | [FIX] | [MERGED] | - | https://github.com/rlaxowns7916/TIL/pull/5 | CONTEXT/README/prd 정리, 참고문헌 기준 강화 |
| 2026-02-04 | Kafka 트랜잭션 및 EOS | [FIX] | [MERGED] | - | https://github.com/rlaxowns7916/TIL/pull/6 | 참고문헌을 Kafka 공식 문서/KIP 중심으로 재정렬 |
| 2026-02-08 | Kubernetes Core Concepts & Architecture | [NEW] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/7 | K8s 핵심 컴포넌트/아키텍처 문서화(PR 진행중) |
| 2026-02-08 | MCP Architecture & Custom Server | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/8 | Model Context Protocol 구조 및 구현 |
| 2026-02-09 | Transactional Outbox + CDC(Debezium) | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/9 | DB-메시징 원자성 문제를 Outbox+CDC로 해결하는 패턴 정리 |
| 2026-02-09 | Kafka Exactly-Once Semantics(EOS) | [FIX] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/9 | 용어/흐름/운영 관점 중심으로 문서 리라이트(ASCII 다이어그램 포함) |
| 2026-02-10 | MCP 문서 보강: Tool Calling 체크리스트 | [FIX] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/10 | mcp-architecture.md에 운영/보안/에러모델 체크리스트 섹션 추가 |
| 2026-02-10 | Kafka Consumer 리밸런싱/순서 보장 실전 | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/10 | 리밸런싱 원인/영향, 커밋 전략, 멱등 처리, 순서 보장 규칙 |
| 2026-02-11 | Kafka Consumer Group 리밸런싱(Rebalancing) | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/11 | Eager vs Cooperative(Incremental), static membership, 운영 체크리스트 |
| 2026-02-11 | 멱등성(Idempotency) 설계: Web API + 메시징 | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/11 | Idempotency-Key, dedup store, upsert, Outbox 결합 |
| 2026-02-11 | Kafka offset commit & processing guarantees | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/12 | auto/manual commit, delivery semantics 정리 |
| 2026-02-12 | Kafka backpressure(pause/resume) + MCP production hardening | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/13 | pause/resume 기반 intake 제어 + MCP 운영 체크리스트 |
| 2026-02-14 | Kafka Streams vs Kinesis + ZooKeeper(KRaft) correction | [NEW/FIX] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/15 | 스트리밍 프레임워크 비교 + KRaft 전환 관련 정정 |
| 2026-02-16 | Progress.md row break + PR status sync | [FIX] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/18 | Progress.md 테이블 깨짐 수정 + PR #10~#13 상태 동기화 |
| 2026-02-17 | Redis HA Sentinel vs Cluster + Progress sync | [NEW/FIX] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/20 | Redis HA 개요 + Progress 동기화 |
| 2026-02-18 | Cache Stampede 대응 + Redis HA 고려사항 | [NEW] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/21 | Stampede 대응(락/확률/예열) + HA 운영 관점 |
| 2026-02-19 | RDB 트랜잭션/락 정리 보강 | [REFINE] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/22 | 트랜잭션/락 개념 정리 품질 개선 |
| 2026-02-19 | RDB 트랜잭션/락 - 내용 보강 및 품질 개선 | [REFINE] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/23 | 심화/예시/주의사항 보강 |
| 2026-02-20 | QueryDSL 벌크연산 & JPA 경로표현식 - 내용 보강 및 품질 개선 | [REFINE] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/24 | 벌크 연산/영속성 컨텍스트/경로표현식 보강 |
| 2026-02-20 | JPA Lock - 내용 보강 및 품질 개선 | [REFINE] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/25 | 낙관/비관 락, 데드락, 실무 팁 보강 |
| 2026-02-20 | OAuth2 - 내용 보강 및 품질 개선 | [REFINE] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/26 | Grant/Token/보안/실무 흐름 보강 |
| 2026-02-21 | Progress update (2026-02-21) | [FIX] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/27 | 2026-02-21 Refinement 반영 + Progress 최신화 |
| 2026-02-22 | Worker Thread Pattern + Transfer-Encoding(chunked) - 내용 보강 및 품질 개선 | [REFINE] | [OPEN] | - | (PR 생성 후 링크 기입) | 멀티스레드 워커 패턴 심화 + HTTP/1.1 chunked 프레이밍/실무 주의사항 보강 |
