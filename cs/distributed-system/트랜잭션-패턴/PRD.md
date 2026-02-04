# PRD: 분산 트랜잭션 패턴 (Distributed Transaction Patterns) 문서화

## 📋 개요 (Overview)

### 목표 (Objective)
마이크로서비스 및 분산 환경에서의 트랜잭션 일관성 문제를 해결하는 패턴들을 공식 문서 수준으로 정밀 연구 및 한글화하여 실무 적용 역량을 확보한다.

### 대상 패턴 (Target Patterns)
- **SAGA Pattern**: 긴 트랜잭션을 보상 트랜잭션(Compensating Transactions)으로 분할하여 처리
- **Two-Phase Commit (2PC)**: 분산 트랜잭션의 원자성 보장을 위한 코디네이터 기반 프로토콜

---

## 🎯 요구사항 (Requirements)

### 1. 문서 구조 (Document Structure)
**기존 TIL 패턴 준수:**
- 제목: 한국어 (영어 병기)
- 구조: 개요 → 개념 → 장단점 → 사용 사례 → 참고자료
- 길이: 15-50줄, 간결한 불릿 포인트 중심
- 언어: 한국어 설명 + 영어 기술 용어 병기
- 레벨 구조: 명확한 헤딩 계층 (##, ###)

**권장 템플릿:**
```markdown
# [패턴명] (English Name)

## 개념 (Concept)
[2-3문장 핵심 정의]

## 문제 (Problem)
- 해결하려는 문제
- 불릿 포인트

## 해결방안 (Solution)
### [주요 측면 1]
- 설명

### [주요 측면 2]
- 설명

## 장점 (Pros)
- 불릿 포인트

## 단점 (Cons)
- 불릿 포인트

## 장애 상황 분석 (Failure Modes) ⭐중요
- [장애 시나리오 1]
  - 동작 방식
  - 복구 메커니즘
- [장애 시나리오 2]
  - 동작 방식
  - 복구 메커니즘

## 사용 사례 (Use Cases)
- 사용해야 하는 상황

## 관련 패턴 (Related Patterns)
- [패턴명] (링크)

## 참고자료 (References)
- [공식 문서](URL)
- [학술 논문](URL)
```

---

## 📚 공식 출처 (Authoritative Sources)

### SAGA Pattern Sources

**학술 논문 (Academic Papers):**
1. **Sagas (Garcia-Molina & Salem, 1987)**
   - URL: https://www.cs.cornell.edu/andru/cs711/2002fa/reading/sagas.pdf
   - 설명: SAGA 패턴의 원본 학술 논문

2. **ACM SIGMOD Paper: "Sagas"**
   - URL: https://dl.acm.org/doi/10.1145/38713.38742
   - 설명: SAGA 패턴의 공식 ACM 출판

**클라우드 제공자 문서 (Cloud Provider Guides):**
3. **AWS Prescriptive Guidance: Serverless Saga Pattern**
   - URL: https://docs.aws.amazon.com/prescriptive-guidance/latest/patterns/implement-the-serverless-saga-pattern-by-using-aws-step-functions.html
   - 범위: Step Functions 활용 실무 구현, 장애 처리

4. **Google Cloud: Implementing Saga Pattern in Workflows**
   - URL: https://cloud.google.com/blog/topics/developers-practitioners/implementing-saga-pattern-workflows
   - 범위: 교차 서비스 트랜잭션 전략, 이벤트 기반 아키텍처

5. **Azure Architecture Center: Saga Design Pattern**
   - URL: https://learn.microsoft.com/en-us/azure/architecture/patterns/saga
   - 범위: 분산 트랜잭션 코디네이션, 데이터 일관성

**프레임워크 문서 (Framework Documentation):**
6. **AxonIQ: Sagas Documentation**
   - URL: https://docs.axoniq.io/axon-framework-reference/5.0/sagas/
   - 범위: Event Sourcing 통합, CQRS 패턴

7. **Temporal: Mastering Saga Patterns**
   - URL: https://temporal.io/blog/mastering-saga-patterns-for-distributed-transactions-in-microservices
   - 범위: 워크플로우 오케스트레이션, 장애 처리

**실무 사례 (Industry Case Studies):**
8. **Netflix, Amazon & Uber Workflow Patterns**
   - URL: https://blog.stackademic.com/how-netflix-amazon-and-uber-orchestrate-billions-of-workflows-daily-43496fd70c99
   - 범위: 대규모 실무 구현 사례

**오픈소스 구현 (Open Source Implementations):**
9. **Temporal Order Saga**
   - URL: https://github.com/temporal-sa/temporal-order-saga
10. **Axon Framework SAGA Example**
    - URL: https://github.com/dashsaurabh/saga-pattern-axon-spring-boot-sample

### Two-Phase Commit Sources

**데이터베이스 공식 문서 (Database Documentation):**
1. **PostgreSQL Two-Phase Commit**
   - URL: https://www.postgresql.org/docs/current/two-phase.html
   - 범위: X/Open XA 표준 준수 구현, 복구 메커니즘

2. **MySQL XA Transactions**
   - URL: https://dev.mysql.com/doc/refman/8.1/en/xa.html
   - 범위: XA 트랜잭션 SQL 인터페이스, 상태 라이프사이클

**학술 논문 (Academic Papers):**
3. **"Consensus on Transaction Commit" (Jim Gray & Leslie Lamport, 2006)**
   - URL: https://lamport.azurewebsites.net/video/consensus-on-transaction-commit.pdf
   - 설명: ACM Transactions on Database Systems, 2PC의 이론적 기초

**산업 표준 (Industry Standards):**
4. **X/Open XA Specification**
   - URL: https://pubs.opengroup.org/onlinepubs/009680699/toc.pdf
   - 설명: 분산 트랜잭션 처리의 정의적 명세

5. **Oracle White Paper: XA and Oracle Controlled Distributed Transactions**
   - URL: https://www.oracle.com/technetwork/products/clustering/overview/distributed-transactions-and-xa-163941.pdf
   - 범위: In-doubt 트랜잭션 처리, 엔터프라이즈급 고려사항

**오픈소스 구현 (Open Source Implementations):**
6. **Seata**
   - URL: https://github.com/zhaohaoh/seata
   - 설명: XA, TCC, SAGA 모드 지원 프레임워크

7. **gosql2pc**
   - URL: https://github.com/gosom/gosql2pc
8. **committer**
   - URL: https://github.com/vadiminshakov/committer

### 비교 및 의사결정 가이드 (Comparison & Decision Frameworks)

1. **Martin Fowler - Patterns of Distributed Systems**
   - URL: https://martinfowler.com/articles/patterns-of-distributed-systems/
   - 범위: 2PC, SAGA 비교, 패턴 선택 기준

2. **Microsoft Azure Architecture Center - Saga Pattern**
   - URL: https://learn.microsoft.com/en-us/azure/architecture/patterns/saga
   - 범위: 패턴 비교, 의사결정 프레임워크

3. **AWS Prescriptive Guidance - Saga Pattern**
   - URL: https://docs.aws.amazon.com/prescriptive-guidance/latest/modernization-data-persistence/saga-pattern.html
   - 범위: 클라우드 환경 구현, 성능/확장성 고려사항

4. **"Designing Data-Intensive Applications" (Martin Kleppmann)**
   - URL: https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/
   - 범위: 분산 트랜잭션 이론, CAP 정리 영향

5. **System Design School - Distributed Transactions**
   - URL: https://systemdesignschool.io/domain-knowledge/distributed-transactions
   - 범위: 실무 의사결정, 안티패턴

---

## 🗂️ 파일 구조 (File Structure)

### 생성 위치 (Location)
```
cs/distributed-system/
├── CONTEXT.md (기존 파일, 업데이트 필요)
├── 트랜잭션-패턴/ (신규 폴더)
│   ├── SAGA.md
│   ├── 2PC.md (Two-Phase Commit)
│   ├── TCC.md (Try-Confirm-Cancel, 선택 사항)
│   ├── Outbox-Pattern.md (선택 사항)
│   └── 패턴-비교.md (Patterns Comparison, 선택 사항)
```

### 기존 자료 통합 (Existing Content Integration)
- `web/backend/msa/pattern/SAGA.md` → `cs/distributed-system/트랜잭션-패턴/SAGA.md`로 마이그레이션 고려
- `web/backend/msa/pattern/2PhaseCommit.md` → `cs/distributed-system/트랜잭션-패턴/2PC.md`로 마이그레이션 고려

---

## ✅ 검증 기준 (Validation Criteria)

### Fact-Check
- 최소 3개 이상의 공식 소스 교차 검증
- 학술 논문, 클라우드 제공자 문서, 프레임워크 문서, 실무 사례를 포함

### Fail-safe Focus
- 행복 회로(Happy Path)보다는 장애 상황(Failure Modes) 분석 우선
- 네트워크 파티션, 코디네이터 실패, 참여자 실패 등 다양한 시나리오 다루기

### Language Policy
- 모든 설명은 한글로 작성
- 핵심 학술 용어는 영어 원문 병기
  - 예: "Compensating Transactions (보상 트랜잭션)", "Atomicity (원자성)"

### 콘텐츠 기준 (Content Standards)
- 각 문서: 15-50줄 (간결함 유지)
- 불릿 포인트 위주 설명
- 코드 예시: 최소화 (패턴 이해에 집중)
- 다이어그램: 필요시 ASCII 또는 텍스트 설명으로 대체

---

## 📊 성공 지표 (Success Metrics)

### 완료 기준 (Completion Criteria)
- [ ] SAGA.md 생성 (최소 3개 공식 출처 인용)
- [ ] 2PC.md 생성 (최소 3개 공식 출처 인용)
- [ ] 장애 상황 분석 섹션 포함 (각 패턴별 최소 3개 시나리오)
- [ ] CONTEXT.md Technical Maturity 업데이트

### 품질 기준 (Quality Metrics)
- 모든 기술 용어에 영어 병기
- 최소 3개의 다양한 출처 유형 사용 (논문, 클라우드, 실무, 오픈소스)
- 장애 상황 분석 섹션의 상세도 (복구 메커니즘 명시)
- 기존 TIL 패턴과의 일관성 유지

---

## 🔄 이후 단계 (Future Phases - 승인 후 진행)

### Phase 3: 문서 작성 (Documentation)
1. **SAGA 패턴**
   - 개념, 문제, 해결방안
   - Choreography vs Orchestration 비교
   - 보상 트랜잭션 설계
   - 장애 상황 복구 메커니즘

2. **Two-Phase Commit**
   - 프로토콜 단계 (Prepare, Commit, Abort)
   - 코디네이터/참여자 역할
   - XA 스펙과의 관계
   - 병목 및 단일 실패점(Single Point of Failure) 분석

### Phase 4: 검증 및 리뷰 (Verification & Review)
1. LSP 진단 실행 (오류/경고 확인)
2. CONTEXT.md Technical Maturity 체크리스트 업데이트
3. 기존 자료와 중복 여부 확인
4. Progress.md에 업데이트

---

## ⚠️ 제약사항 (Constraints)

- AGENTS.md의 지침 준수
- 기존 TIL 문서 스타일 유지 (간결, 불릿 포인트 중심)
- 공식 문서 기반의 정확한 정보만 사용
- 코드 예시 최소화 (개념 이해에 집중)
- PRD.md 작성 완료 후 **Phase 3 진행 승인 필수**

---

## 📝 작성 기록 (Creation Log)

- **생성일자**: 2026-02-02
- **작업 디렉토리**: `/home/tj-rp-1/Code/TIL/main` (worktree)
- **다음 단계**: PRD 승인 대기 중 (승인 후 Phase 3 진행)
