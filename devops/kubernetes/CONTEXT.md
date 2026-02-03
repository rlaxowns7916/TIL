# kubernetes Context

## 🎯 Mission
- 쿠버네티스(Kubernetes)의 핵심 개념과 아키텍처를 체계적으로 이해하고 스케줄링 학습을 위한 견고한 기초를 확립한다.
- Control Plane, Node Components, 기본 개념(Pods, Services 등)의 상호작용을 심층 분석한다.
- 장애 상황(Failure Modes)에 대한 이해를 통해 실무 환경에서의 신뢰성 있는 운영 역량을 확보한다.

## 📚 Authoritative Sources (공식 출처)
- **Kubernetes 공식 문서**: https://kubernetes.io/docs/
- **CNCF (Cloud Native Computing Foundation)**: https://www.cncf.io/
- **클라우드 제공자 가이드**: AWS EKS, Google GKE, Azure AKS 문서

## 🛠️ Subject-Specific Conventions (폴더 전용 규칙)
- **Fact-Check**: 최소 3개 이상의 공식 소스(공식 문서, CNCF, 클라우드 제공자)를 교차 검증한다.
- **Language**: 모든 설명은 한글로 작성하되, 기술 용어는 영어 원문을 병기한다 (예: Control Plane (제어 평면), Pod (파드)).
- **Failure-First**: 정상 작동(Happy Path)보다는 장애 상황(Failure Modes) 분석을 우선한다.
- **Auto-Update**: 작업 완료 시 해당 주제의 진행 상황과 성숙도를 이 파일에 최신화한다.

## 📈 Technical Maturity (학습 성숙도)
- [x] 기초 개념 파악 (Overview, Control Plane, Node Components, Basic Concepts)
- [ ] 스케줄링 원리 이해 (Scheduler, Scheduling Algorithm)
- [x] 실무 장애 시나리오 분석 (Failure Handling, Recovery) - 각 컴포넌트별 장애 처리 섹션 완료
- [ ] 고급 주제 및 내부 메커니즘 심화 (Preemption, CRI, Custom Controllers)

## 📝 완료 문서 (Completed Documents)
- **개요 (Overview)**: 쿠버네티스란, 아키텍처-개요 (2 docs)
- **제어 평면 (Control Plane)**: API-Server, etcd, Scheduler, Controller-Manager, Cloud-Controller-Manager (5 docs)
- **노드 컴포넌트 (Node Components)**: Kubelet, Kube-proxy, Container-Runtime (3 docs)
- **기본 개념 (Basic Concepts)**: Pod, Service, Deployment, ReplicaSet, Namespace (5 docs)
- **총계**: 15 docs (CONTEXT.md 포함)
