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
| 2026-02-08 | Kubernetes Core Concepts & Architecture | [NEW] | [CLOSED] | - | https://github.com/rlaxowns7916/TIL/pull/7 | K8s 핵심 컴포넌트/아키텍처 문서화(PR 진행중) |
| 2026-02-08 | MCP Architecture & Custom Server | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/8 | Model Context Protocol 구조 및 구현 |
| 2026-02-09 | Transactional Outbox + CDC(Debezium) | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/9 | DB-메시징 원자성 문제를 Outbox+CDC로 해결하는 패턴 정리 |
| 2026-02-09 | Kafka Exactly-Once Semantics(EOS) | [FIX] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/9 | 용어/흐름/운영 관점 중심으로 문서 리라이트(ASCII 다이어그램 포함) |
| 2026-02-10 | MCP 문서 보강: Tool Calling 체크리스트 | [FIX] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/10 | mcp-architecture.md에 운영/보안/에러모델 체크리스트 섹션 추가 |
| 2026-02-10 | Kafka Consumer 리밸런싱/순서 보장 실전 | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/10 | 리밸런싱 원인/영향, 커밋 전략, 멱등 처리, 순서 보장 규칙 |
| 2026-02-11 | Kafka Consumer Group 리밸런싱(Rebalancing) | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/11 | Eager vs Cooperative(Incremental), static membership, 운영 체크리스트 |
| 2026-02-11 | 멱등성(Idempotency) 설계: Web API + 메시징 | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/11 | Idempotency-Key, dedup store, upsert, Outbox 결합 |
| 2026-02-11 | Kafka offset commit & processing guarantees | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/12 | auto/manual commit, delivery semantics 정리 |
| 2026-02-12 | Kafka backpressure(pause/resume) + MCP production hardening | [NEW] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/13 | pause/resume 기반 intake 제어 + MCP 운영 체크리스트 |
| 2026-02-14 | Kafka Streams vs Kinesis + ZooKeeper(KRaft) correction | [NEW/FIX] | [CLOSED] | - | https://github.com/rlaxowns7916/TIL/pull/15 | 스트리밍 프레임워크 비교 + KRaft 전환 관련 정정 |
| 2026-02-16 | Progress.md row break + PR status sync | [FIX] | [MERGED] | Done | https://github.com/rlaxowns7916/TIL/pull/18 | Progress.md 테이블 깨짐 수정 + PR #10~#13 상태 동기화 |
| 2026-02-16 | Redis HA: Sentinel vs Cluster (Retry V2) | [NEW] | [CLOSED] | - | https://github.com/rlaxowns7916/TIL/pull/19 | 문서/구성 재시도(PR 종료) |
| 2026-02-17 | Redis HA: Sentinel vs Cluster (운영 관점 재정리) | [NEW] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/20 | Sentinel/Cluster 선택 기준 + 장애 시나리오 + 운영 체크리스트 |
| 2026-02-17 | Progress.md PR status sync (2026-02-17) | [FIX] | [OPEN] | - | https://github.com/rlaxowns7916/TIL/pull/20 | Progress.md에서 PR #7/#15/#18/#19 상태 동기화 |
