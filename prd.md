# PRD: 쿠버네티스 핵심 개념 및 아키텍처 (Kubernetes Core Concepts & Architecture) 문서화

## 📋 개요 (Overview)

### 목표 (Objective)
쿠버네티스의 핵심 구성 요소(Control Plane, Node Components)와 기본 개념(Pods, Services 등)을 체계적으로 연구하여 스케줄링 학습을 위한 견고한 기초 지식을 확립한다.

### 대상 영역 (Target Areas)
- **Control Plane 컴포넌트**: API Server, etcd, Scheduler, Controller Manager, Cloud Controller Manager
- **Node 컴포넌트**: Kubelet, Kube-proxy, Container Runtime (CRI)
- **기본 개념**: Pods, Services, Deployments, Namespaces, ConfigMaps, Secrets

---

## 🎯 요구사항 (Requirements)

### 1. 문서 구조 (Document Structure)
**기존 TIL 패턴 준수:**
- 제목: 한국어 (영어 병기)
- 구조: 개요 → 개념 → 구성 요소 → 작동 원리 → 장애 처리 → 사용 사례 → 참고자료
- 길이: 15-50줄, 간결한 불릿 포인트 중심
- 언어: 한국어 설명 + 영어 기술 용어 병기
- 레벨 구조: 명확한 헤딩 계층 (##, ###)

**권장 템플릿:**
```markdown
# [주제명] (English Name)

## 개념 (Concept)
[2-3문장 핵심 정의]

## 구조 (Architecture)
### [구성 요소 1]
- 역할 및 책임

### [구성 요소 2]
- 역할 및 책임

## 작동 원리 (How It Works)
- 불릿 포인트

## 상호작용 (Component Interaction)
- 컴포넌트 간 통신 방식

## 장애 처리 (Failure Handling) ⭐중요
- [장애 시나리오 1]
  - 동작 방식
  - 복구 메커니즘
- [장애 시나리오 2]
  - 동작 방식
  - 복구 메커니즘

## 사용 사례 (Use Cases)
- 활용 시나리오

## 참고자료 (References)
- [공식 문서](URL)
```

---

## 📚 공식 출처 (Authoritative Sources)

### 쿠버네티스 공식 문서 (Official Kubernetes Documentation)

**Architecture Overview:**
1. **Kubernetes Architecture Overview**
   - URL: https://kubernetes.io/docs/concepts/architecture/
   - 범위: Control Plane, Node 컴포넌트, 상호작용

2. **The Kubernetes Control Plane**
   - URL: https://kubernetes.io/docs/concepts/architecture/control-plane/
   - 범위: API Server, etcd, Scheduler, Controller Manager

**Core Concepts:**
3. **Pods**
   - URL: https://kubernetes.io/docs/concepts/workloads/pods/
   - 범위: Pod 생명주기, Multi-container 패턴

4. **Services**
   - URL: https://kubernetes.io/docs/concepts/services-networking/service/
   - 범위: Service 타입(ClusterIP, NodePort, LoadBalancer, ExternalName)

5. **Deployments**
   - URL: https://kubernetes.io/docs/concepts/workloads/controllers/deployment/
   - 범위: Rolling updates, Rollbacks, ReplicaSet 관계

**Components Detail:**
6. **kube-apiserver**
   - URL: https://kubernetes.io/docs/concepts/overview/components/#kube-apiserver
   - 범위: REST API, Authentication, Authorization, Admission

7. **etcd**
   - URL: https://kubernetes.io/docs/concepts/overview/components/#etcd
   - 범위: Key-value store, 데이터 일관성, 백업 전략

8. **kube-scheduler**
   - URL: https://kubernetes.io/docs/concepts/overview/components/#kube-scheduler
   - 범위: Scheduling 알고리즘, Predicate, Priority, Preemption

9. **kube-controller-manager**
   - URL: https://kubernetes.io/docs/concepts/overview/components/#kube-controller-manager
   - 범위: Node Controller, Replication Controller, Endpoint Controller

10. **cloud-controller-manager**
    - URL: https://kubernetes.io/docs/concepts/overview/components/#cloud-controller-manager
    - 범위: Cloud 제공자별 컨트롤러(Node, Route, Service)

11. **kubelet**
    - URL: https://kubernetes.io/docs/concepts/overview/components/#kubelet
    - 범위: PodSpec 실행, 상태 보고, cAdvisor

12. **kube-proxy**
    - URL: https://kubernetes.io/docs/concepts/overview/components/#kube-proxy
    - 범위: Network proxy, iptables/IPVS, Service discovery

13. **Container Runtime Interface (CRI)**
    - URL: https://kubernetes.io/docs/concepts/architecture/#container-runtime
    - 범위: CRI 정의, 지원 런타임(containerd, CRI-O)

### CNCF (Cloud Native Computing Foundation) 자료

14. **CNCF Certified Kubernetes Conformance**
    - URL: https://www.cncf.io/certification/kconformance/
    - 범위: Kubernetes 일관성 테스트, 컴플라이언스

### 클라우드 제공자 문서 (Cloud Provider Guides)

15. **AWS EKS Documentation - Kubernetes Concepts**
    - URL: https://docs.aws.amazon.com/eks/latest/userguide/kubernetes-concepts.html
    - 범위: AWS EKS 구조, EKS Control Plane

16. **Google Cloud GKE - Understanding Kubernetes Architecture**
    - URL: https://cloud.google.com/kubernetes-engine/docs/concepts/kubernetes-engine-architecture
    - 범위: GKE Control Plane, Node 구성, Auto-scaling

17. **Azure AKS - Kubernetes Core Concepts**
    - URL: https://docs.microsoft.com/en-us/azure/aks/concepts-clusters-workloads
    - 범위: AKS Control Plane, Node pool, Workloads

### 실무 사례 및 모범 사례 (Industry Case Studies & Best Practices)

18. **Kubernetes Best Practices - Patterns for Cloud Native**
    - URL: https://www.cncf.io/blog/2022/12/19/kubernetes-best-practices/
    - 범위: Production 운영 패턴, 모니터링, 보안

19. **Google SRE Book - Managing Critical State**
    - URL: https://sre.google/sre-book/production-environment/
    - 범위: Kubernetes 환경에서의 SRE 실무

20. **Kubernetes Failure Stories (k8s.gcr.io)**
    - URL: https://k8s.gcr.io/
    - 범위: 실제 장애 사례 및 교훈

### 학술 자료 (Academic Resources)

21. **Kubernetes: Container Orchestration System**
    - URL: https://dl.acm.org/doi/10.1145/3342195.3387527
    - 설명: ACM SIGMOD 2020, Kubernetes 아키텍처 심층 분석

22. **Borg, Omega, and Kubernetes (Google)**
    - URL: https://research.google/pubs/pub43438/
    - 설명: Kubernetes의 기원인 Borg 시스템 연구

### 비교 및 의사결정 가이드 (Comparison & Decision Frameworks)

23. **Kubernetes vs Docker Swarm vs Mesos**
    - URL: https://www.cncf.io/blog/2022/12/19/kubernetes-vs-docker-swarm-vs-mesos/
    - 범위: Container 오케스트레이션 도구 비교

24. **Kubernetes Scheduler Deep Dive**
    - URL: https://github.com/kubernetes/community/blob/master/contributors/design-proposals/scheduling/scheduling-framework.md
    - 범위: Scheduling Framework 디자인 문서

---

## 🗂️ 파일 구조 (File Structure)

### 생성 위치 (Location)
```
devops/kubernetes/
├── CONTEXT.md (신규 파일, Mission 및 Maturity 정의)
├── 개요/
│   ├── 아키텍처-개요.md (Architecture Overview)
│   └── 쿠버네티스란.md (What is Kubernetes)
├── 제어-평면/
│   ├── API-Server.md (kube-apiserver)
│   ├── etcd.md
│   ├── Scheduler.md (kube-scheduler)
│   ├── Controller-Manager.md (kube-controller-manager)
│   └── Cloud-Controller-Manager.md
├── 노드-컴포넌트/
│   ├── Kubelet.md
│   ├── Kube-proxy.md
│   └── Container-Runtime.md (CRI)
└── 기본-개념/
    ├── Pod.md
    ├── Service.md
    ├── Deployment.md
    ├── ReplicaSet.md
    └── Namespace.md
```

---

## ✅ 검증 기준 (Validation Criteria)

### Fact-Check
- 최소 3개 이상의 공식 소스 교차 검증
- 쿠버네티스 공식 문서, CNCF 자료, 클라우드 제공자 문서 포함

### Failure-focus
- 정상 작동(Happy Path)보다는 장애 상황(Failure Modes) 분석 우선
- 컴포넌트 장애, 네트워크 파티션, 리소스 부족 등 다양한 시나리오

### Language Policy
- 모든 설명은 한글로 작성
- 핵심 기술 용어는 영어 원문 병기
  - 예: "Control Plane (제어 평면)", "Pod (파드)", "etcd"

### 콘텐츠 기준 (Content Standards)
- 각 문서: 15-50줄 (간결함 유지)
- 불릿 포인트 위주 설명
- 코드 예시: 최소화 (개념 이해에 집중)
- 다이어그램: 필요시 ASCII 또는 텍스트 설명으로 대체

---

## 📊 성공 지표 (Success Metrics)

### 완료 기준 (Completion Criteria)
- [ ] CONTEXT.md 생성 (Mission 및 Technical Maturity 정의)
- [ ] 아키텍처 개요 문서 생성 (최소 3개 공식 출처 인용)
- [ ] 제어 평면 컴포넌트 문서 5개 생성 (각 최소 3개 공식 출처 인용)
- [ ] 노드 컴포넌트 문서 3개 생성 (각 최소 3개 공식 출처 인용)
- [ ] 기본 개념 문서 5개 생성 (각 최소 3개 공식 출처 인용)
- [ ] 장애 상황 분석 섹션 포함 (각 컴포넌트별 최소 2개 시나리오)

### 품질 기준 (Quality Metrics)
- 모든 기술 용어에 영어 병기
- 최소 3개의 다양한 출처 유형 사용 (공식, CNCF, 클라우드, 실무)
- 장애 상황 분석 섹션의 상세도 (복구 메커니즘 명시)
- 기존 TIL 문서 스타일과의 일관성 유지

---

## 🔄 이후 단계 (Future Phases - 승인 후 진행)

### Phase 3: 문서 작성 (Documentation)
1. **아키텍처 개요**
   - Kubernetes 아키텍처 전체 구조
   - Control Plane과 Node의 통신 방식
   - 클러스터의 확장성 가용성 설계

2. **제어 평면 컴포넌트**
   - API Server: 인증, 인가, Admission Control
   - etcd: 데이터 저장, 일관성 모델, 백업/복구
   - Scheduler: 스케줄링 결정 프로세스, Preemption
   - Controller Manager: 각 컨트롤러의 역할
   - Cloud Controller Manager: 클라우드 제공자 통합

3. **노드 컴포넌트**
   - Kubelet: PodSpec 실행, 상태 보고, cAdvisor
   - Kube-proxy: iptables/IPVS, Service 구현
   - Container Runtime: CRI, containerd, CRI-O

4. **기본 개념**
   - Pods: 생명주기, Multi-container 패턴
   - Services: 타입별 동작, Discovery
   - Deployments: Rolling update 전략, ReplicaSet 관리
   - Namespaces: 리소스 격리
   - ConfigMaps/Secrets: 설정 관리

### Phase 4: 검증 및 리뷰 (Verification & Review)
1. LSP 진단 실행 (오류/경고 확인)
2. CONTEXT.md Technical Maturity 체크리스트 업데이트
3. 기존 자료와 중복 여부 확인
4. Progress.md에 업데이트

---

## ⚠️ 제약사항 (Constraints)

- 기존 TIL 문서 스타일 유지 (간결, 불릿 포인트 중심)
- 공식 문서 기반의 정확한 정보만 사용
- 코드 예시 최소화 (개념 이해에 집중)
- PRD.md 작성 완료 후 **Phase 3 진행 승인 필수**
- 스케줄링 심화 학습을 위한 기초 문서 역할에 초점

---

## 📝 작성 기록 (Creation Log)

- **생성일자**: 2026-02-03
- **작업 디렉토리**: `/home/tj-rp-1/Code/TIL/k8s-core-concepts-20260203` (worktree)
- **브랜치**: `feat/k8s-core-concepts-20260203`
- **다음 단계**: PRD 승인 대기 중 (승인 후 Phase 3 진행)
