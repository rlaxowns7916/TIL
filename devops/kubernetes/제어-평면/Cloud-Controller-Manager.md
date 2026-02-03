# Cloud Controller Manager

## 개념 (Concept)
- 클라우드 제공자별로 특화된 컨트롤러를 분리하여 실행하는 컴포넌트
- 쿠버네티스 코어와 클라우드 제공자 API의 결합을 느슨하게 하여 유지보수성 향상
- 하이브리드 클라우드/멀티 클라우드 환경 지원

## 역할 (Roles)
- **Node Controller**: 클라우드 VM(Node) 상태 동기화, Node 삭제
- **Route Controller**: 클라우드 라우팅 테이블 설정으로 Pod 통신 지원
- **Service Controller**: 클라우드 로드 밸런서(Load Balancer) 생성/관리
- **Volume Controller**: 클라우드 볼륨(Storage) 생성/연결

## 구조 (Architecture)

### 클라우드 제공자별 컨트롤러 (Cloud Provider Controllers)
- **AWS**: AWS Cloud Provider (EC2, ELB, EBS, Route53)
- **GCP**: GKE Cloud Provider (Compute Engine, Cloud Load Balancing, Cloud Storage)
- **Azure**: Azure Cloud Provider (Azure VM, Azure Load Balancer, Azure Disk)
- **OpenStack**: OpenStack Cloud Provider
- **vSphere**: vSphere Cloud Provider

### 컨트롤러 세부 기능
- **Node Controller**:
  - 클라우드 VM 상태를 쿠버네티스 Node 객체로 동기화
  - 클라우드에서 삭제된 VM을 쿠버네티스에서 제거 (taints: node.kubernetes.io/unreachable)
- **Route Controller**:
  - 클라우드 라우팅 테이블 업데이트로 Node 간 Pod 통신 지원
  - 특히 CNI 플러그인이 라우팅을 관리하지 않는 경우
- **Service Controller**:
  - Service 타입이 LoadBalancer일 때 클라우드 로드 밸런서 생성
  - 로드 밸런서 설정(NodePort, Health Check, SSL 등)
- **Volume Controller**:
  - PersistentVolumeClaim(PVC)에 매핑되는 클라우드 볼륨 생성/삭제
  - 볼륨 Attach/Detach 관리

## 작동 원리 (How It Works)
1. 사용자가 Service 타입 LoadBalancer 생성
2. Cloud Controller Manager의 Service Controller가 이 감시(Watch)
3. 클라우드 제공자 API를 호출하여 로드 밸런서 생성
4. 로드 밸런서의 외부 IP(External IP)를 Service에 업데이트
5. 클라우드 라우팅 테이블을 업데이트하여 트래픽 라우팅

## 상호작용 (Component Interaction)
- **Cloud Controller Manager ↔ API Server**: Watch API로 리소스 감시, 상태 업데이트
- **Cloud Controller Manager ↔ Cloud Provider API**: 클라우드 리소스 생성/관리
- **Cloud Controller Manager ↔ Controller Manager**: Node, Service, Volume 관련 이벤트 협조

## 클라우드 제공자별 예시 (Cloud Provider Examples)
- **AWS EKS**: AWS Cloud Provider를 통해 ELB(Elastic Load Balancer), EBS(Elastic Block Store), Route53 관리
- **Google GKE**: Google Cloud Provider를 통해 Cloud Load Balancing, Cloud Disk, Cloud Router 관리
- **Azure AKS**: Azure Cloud Provider를 통해 Azure Load Balancer, Azure Disk, Azure DNS 관리

## 사용 여부 결정 (When to Use)
- **Managed Kubernetes (EKS, GKE, AKS)**: 클라우드 제공자가 관리하므로 사용 불필요
- **Self-hosted Kubernetes**: 클라우드에서 직접 구축 시 필요 (예: EC2에서 kubeadm으로 구축)
- **On-premises**: 외부 클라우드와 통합이 필요 없으면 사용 불필요

## 장애 처리 (Failure Handling) ⭐중요
- **Cloud Controller Manager 다운 (CCM Down)**
  - 동작 방식: 새로운 클라우드 리소스 생성 불가, 기존 리소스는 계속 작동
  - 복구 메커니즘: 다중 CCM 구성(HA), Leader Election

- **Node Controller 장애 (Node Controller Failure)**
  - 동작 방식: 클라우드 VM 삭제 감지 불가, Orphan Node 문제 발생 가능
  - 복구 메커니즘: CCM 재시작, 수동으로 Node 제거

- **Service Controller 장애 (Service Controller Failure)**
  - 동작 방식: 새로운 Load Balancer 생성 불가, 기존 LB는 계속 작동
  - 복구 메커니즘: CCM 재시작, 수동으로 LB 생성

- **클라우드 API 장애 (Cloud API Outage)**
  - 동작 방식: 클라우드 리소스 생성/삭제 불가, 쿠버네티스 내부는 계속 작동
  - 복구 메커니즘: 클라우드 API 복구 대기, 재시도 메커니즘 작동

- **Volume Controller 장애 (Volume Controller Failure)**
  - 동작 방식: 클라우드 볼륨 생성/연결 불가, PVC가 Pending 상태로 유지
  - 복구 메커니즘: CCM 재시작, 수동으로 볼륨 생성 및 연결

## 사용 사례 (Use Cases)
- AWS EC2에서 쿠버네티스 셀프 호스팅 시 ELB, EBS, Route53 연동
- Google Cloud에서 셀프 호스팅 시 Cloud Load Balancing 연동
- 하이브리드 클라우드 환경에서의 클라우드 리소스 관리

## 참고자료 (References)
- [cloud-controller-manager - Kubernetes Documentation](https://kubernetes.io/docs/concepts/architecture/cloud-controller/)
- [AWS Cloud Controller Manager](https://github.com/kubernetes/cloud-provider-aws)
- [Google Cloud Controller Manager](https://github.com/kubernetes/cloud-provider-gcp)
- [Azure Cloud Controller Manager](https://github.com/kubernetes/cloud-provider-azure)
