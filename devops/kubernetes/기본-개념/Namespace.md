# Namespace (네임스페이스)

## 개념 (Concept)
- 쿠버네티스 리소스를 논리적으로 그룹화하고 격리하는 메커니즘
- 클러스터 내 다른 팀/프로젝트/환경(Dev, Staging, Prod)을 분리
- 리소스 이름 충돌 방지 (같은 리소스 이름을 다른 Namespace에서 사용 가능)

## 역할 (Roles)
- **리소스 격리 (Resource Isolation)**: 논리적 그룹으로 리소스 분리
- **이름 공간 분리 (Name Separation)**: 리소스 이름 충돌 방지
- **리소스 할당량 (Resource Quota)**: Namespace별 리소스 사용량 제한
- **액세스 제어 (Access Control)**: RBAC로 Namespace별 권한 부여

## 구조 (Architecture)

### 기본 Namespace (Default Namespaces)
- **default**: 리소스 생성 시 지정하지 않으면 기본으로 할당되는 Namespace
- **kube-system**: 쿠버네티스 시스템 컴포넌트(Pod, Service 등)가 생성되는 Namespace
- **kube-public**: 공개적으로 접근 가능한 ConfigMap과 Secret이 저장되는 Namespace
- **kube-node-lease**: Node Heartbeat 리스 정보를 저장하는 Namespace

### Namespace 범위 (Namespace Scope)
- **범위 리소스 (Scoped Resources)**: Namespace 내에 생성 (Pod, Service, Deployment, ConfigMap, Secret 등)
- **클러스터 범위 리소스 (Cluster-scoped Resources)**: Namespace 밖에 생성 (Node, PersistentVolume, Namespace, Role/ClusterRole, RoleBinding/ClusterRoleBinding 등)

## 작동 원리 (How It Works)
1. 사용자가 Namespace YAML/JSON으로 정의
2. API Server에 Namespace 생성 요청 전송
3. Namespace Controller가 Namespace 생성 및 관리
4. 사용자가 Namespace를 지정하여 리소스 생성 (예: `kubectl create deployment -n <namespace>`)
5. Namespace 내 리소스는 서로 격리됨 (다른 Namespace의 리소스와 직접 접근 불가)

## 상호작용 (Component Interaction)
- **Namespace ↔ Pod**: Pod는 Namespace 내에서 생성
- **Namespace ↔ Service**: Service는 Namespace 내에서 생성, DNS 레코드: `<ServiceName>.<Namespace>.svc.cluster.local`
- **Namespace ↔ ResourceQuota**: ResourceQuota가 Namespace별 리소스 사용량 제한
- **Namespace ↔ RBAC**: RBAC가 Namespace별 권한 부여 (Role, RoleBinding)

## DNS 레코드 (DNS Records)
- **Service DNS**: `<ServiceName>.<Namespace>.svc.cluster.local`
- **Pod DNS (Headless Service)**: `<PodName>.<ServiceName>.<Namespace>.svc.cluster.local`

## 리소스 격리 레벨 (Resource Isolation Levels)
- **논리적 격리 (Logical Isolation)**: Namespace로 리소스 분리 (기본)
- **리소스 할당량 (Resource Quota)**: Namespace별 리소스(CPU, Memory, Storage, Pod 수 등) 제한
- **네트워크 격리 (Network Isolation)**: NetworkPolicy로 Namespace 간 네트워크 통신 제어 (옵션)
- **액세스 제어 (Access Control)**: RBAC로 Namespace별 권한 부여

## ResourceQuota (리소스 할당량)
- **개념**: Namespace별 리소스 사용량 제한
- **예시**:
  - CPU Request: `requests.cpu`
  - Memory Request: `requests.memory`
  - CPU Limit: `limits.cpu`
  - Memory Limit: `limits.memory`
  - Pod 수: `pods`
  - Storage: `requests.storage`
  - PersistentVolumeClaims: `persistentvolumeclaims`

## NetworkPolicy (네트워크 정책)
- **개념**: Namespace 간 네트워크 통신 제어
- **예시**:
  - Namespace A에서 Namespace B로의 트래픽 허용/거부
  - 특정 Pod로의 트래픽 허용/거부
  - 특정 포트로의 트래픽 허용/거부

## 장애 처리 (Failure Handling) ⭐중요
- **Namespace 삭제 중단 (Namespace Deletion Stuck)**
  - 동작 방식: Namespace가 Terminating 상태로 유지, 리소스 삭제 불가
  - 복구 메커니즘: Namespace 내 리소스 강제 삭제(`kubectl delete all -n <namespace> --all --grace-period=0 --force`), Namespace 삭제 재시도

- **ResourceQuota 초과 (ResourceQuota Exceeded)**
  - 동작 방식: Namespace 리소스 할당량 초과 시 리소스 생성 불가
  - 복구 메커니즘: ResourceQuota 조정, 불필요한 리소스 삭제, Cluster Autoscaler로 Node 확장

- **RBAC 권한 부족 (RBAC Permission Denied)**
  - 동작 방식: Namespace 액세스 권한 부족으로 리소스 조회/생성 불가
  - 복구 메커니즘: Role/RoleBinding 생성, ClusterRole/ClusterRoleBinding 생성

- **Namespace Controller 다운 (Namespace Controller Failure)**
  - 동작 방식: Namespace 관리 중단, 기존 리소스는 계속 작동, 새로운 Namespace/리소스 생성 불가
  - 복구 메커니즘: Controller Manager 재시작, Namespace Controller 재시작

- **NetworkPolicy 장애 (NetworkPolicy Failure)**
  - 동작 방식: 네트워크 통신 제어 불가, 모든 트래픽 허용 또는 거부
  - 복구 메커니즘: NetworkPolicy 수정, CNI 플러그인 재시작

## 사용 사례 (Use Cases)
- 환경 분리 (Dev, Staging, Prod)
- 팀/프로젝트 분리
- 리소스 할당량(ResourceQuota)을 통한 리소스 사용량 제어
- RBAC를 통한 Namespace별 권한 부여
- NetworkPolicy를 통한 Namespace 간 네트워크 통신 제어

## 참고자료 (References)
- [Namespaces - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)
- [Resource Quotas - Kubernetes](https://kubernetes.io/docs/concepts/policy/resource-quotas/)
- [Network Policies - Kubernetes](https://kubernetes.io/docs/concepts/services-networking/network-policies/)
- [Using RBAC Authorization - Kubernetes](https://kubernetes.io/docs/reference/access-authn-authz/rbac/)
