# API Server (kube-apiserver)

## 개념 (Concept)
- 쿠버네티스 클러스터의 중앙 제어 허브로 모든 API 요청을 처리하는 컴포넌트
- etcd(Cluster State Store)와 상호작용하며 모든 컨트롤 플레인 컴포넌트의 통신 중계
- RESTful API 및 gRPC 기반으로 클러스터의 단일 진입점(Single Entry Point)

## 역할 (Roles)
- **API 게이트웨이**: 외부(kubectl, client) 및 내부(Scheduler, Controller) 요청 수신
- **인증 (Authentication)**: Client ID 확인 (X509 인증서, Service Account Token, Bearer Token 등)
- **인가 (Authorization)**: RBAC(Role-Based Access Control), ABAC 등 권한 검증
- **Admission Control**: 요청이 etcd에 저장되기 전에 유효성 검증 및 수정 (ValidatingAdmissionWebhook, MutatingAdmissionWebhook)
- **상태 조회**: etcd에서 클러스터 상태 읽기 및 컨트롤러에 제공

## 구조 (Architecture)

### Authentication(인증) 계층
- **Client Certificates**: X509 인증서 기반 인증
- **Service Account**: 각 Namespace 내부에서 사용하는 자격 증명
- **Bearer Token**: OpenID Connect, OAuth2 토큰 기반 인증
- **Webhook**: 외부 인증 서버 연동

### Authorization(인가) 계층
- **RBAC (Role-Based Access Control)**: Role(네임스페이스)과 ClusterRole(클러스터 전역) 기반 권한 부여
- **ABAC (Attribute-Based Access Control)**: 정책 기반 권한 부여 (레거시)
- **Node Authorization**: Kubelet의 Node 인증

### Admission Control
- **ValidatingWebhookConfiguration**: 요청 유효성 검증 (거리 가능)
- **MutatingWebhookConfiguration**: 요청 수정 (기본값 설정, 라벨 추가 등)
- **Built-in Plugins**: AlwaysPullImages, NamespaceExists, ResourceQuota 등

## 작동 원리 (How It Works)
1. Client가 API Server에 요청 전송 (kubectl, API call, Webhook)
2. Authentication 계층에서 클라이언트 신원 확인
3. Authorization 계층에서 요청에 대한 권한 확인 (RBAC)
4. Admission Control에서 요청 유효성 검증 및 수정 (예: DefaultImagePullPolicy)
5. etcd에 상태 저장 (Create/Update/Patch 요청) 또는 etcd에서 조회 (Get/List/Watch 요청)
6. 응답을 클라이언트에 반환

## 상호작용 (Component Interaction)
- **API Server ↔ etcd**: gRPC 통신으로 클러스터 상태 저장/조회
- **API Server ↔ Controller**: Watch API를 통해 etcd의 변경 사항 감시
- **API Server ↔ Scheduler**: 스케줄링되지 않은 Pod(Pending) 정보 전달
- **API Server ↔ Kubelet**: HTTPS 통신으로 PodSpec 전달 및 상태 수신

## 확장성 (Scalability)
- **Horizontal Scaling**: 다중 API Server 구성으로 요청 처리량 확장
- **Load Balancing**: Ingress/Load Balancer를 통한 트래픽 분산
- **Watch Cache**: List/Watch 요청 성능 최적화를 위한 in-memory 캐시

## 장애 처리 (Failure Handling) ⭐중요
- **API Server 다운 (API Server Down)**
  - 동작 방식: 새로운 요청 불가, kubectl 조회 불가, 기존 Pod/Service는 계속 작동
  - 복구 메커니즘: 다중 API Server(HA) 구성, LB(Load Balancer)를 통한 트래픽 분산

- **인증 실패 (Authentication Failure)**
  - 동작 방식: 401 Unauthorized 응답, 요청 거부
  - 복구 메커니즘: 유효한 인증서/토큰 재발행, kubeconfig 재설정

- **인가 실패 (Authorization Failure)**
  - 동작 방식: 403 Forbidden 응답, 권한 부족
  - 복구 메커니즘: RBAC Role/ClusterRole 수정, ServiceAccount 권한 부여

- **Admission Webhook 장애**
  - 동작 방식: Webhook 응답 없으면 정책에 따라 요청 거부(Fail Open/Close)
  - 복구 메커니즘: Webhook 서비스 복구, Validating/MutatingWebhookConfiguration 수정

- **etcd 연결 실패**
  - 동작 방식: 클러스터 상태 저장/조회 불가, 전체 클러스터 운영 불가
  - 복구 메커니즘: etcd 클러스터 복구, API Server 재시작

- **과부하 (Overload)**
  - 동작 방식: 응답 지연, 타임아웃, 429 Too Many Requests
  - 복구 메커니즘: Horizontal scaling, 요속 제한(Rate Limiting), Watch 캐시 최적화

## 사용 사례 (Use Cases)
- kubectl을 통한 리소스 배포 및 관리
- CI/CD 파이프라인에서의 배포 자동화
- Custom Controller 개발 (Operator 패턴)
- 외부 시스템과의 통합 (Webhook)

## 참고자료 (References)
- [kube-apiserver - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/components/#kube-apiserver)
- [Authenticating - Kubernetes](https://kubernetes.io/docs/reference/access-authn-authz/authentication/)
- [Authorization Overview - Kubernetes](https://kubernetes.io/docs/reference/access-authn-authz/authorization/)
- [Admission Controllers - Kubernetes](https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/)
