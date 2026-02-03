# Service (서비스)

## 개념 (Concept)
- 동적인 Pod 집합(Pod Set)에 대한 안정적인 네트워크 추상화
- Service Discovery(서비스 검색) 및 Load Balancing(로드 밸런싱) 제공
- Pod IP가 변경되어도 Service VIP로 접근 가능

## 역할 (Roles)
- **서비스 디스커버리 (Service Discovery)**: DNS 또는 환경 변수를 통해 서비스 검색
- **로드 밸런싱 (Load Balancing)**: 여러 Pod로 트래픽 분산
- **안정적 엔드포인트 (Stable Endpoint)**: Pod IP 변경에도 안정적인 서비스 접근점 제공
- **세션 유지 (Session Affinity)**: 동일 클라이언트 요청을 동일 Pod로 라우팅

## 구조 (Architecture)

### Service 타입 (Service Types)
- **ClusterIP (기본)**: 클러스터 내부에서만 접근 가능한 VIP 할당
  - IP 범위: 10.0.0.0/24 (기본)
  - 장점: 단순, 내부 통신에 적합
  - 단점: 외부 접근 불가

- **NodePort**: 각 Node의 특정 포트(30000-32767)를 통해 외부 접근 가능
  - 접근 방법: `<NodeIP>:<NodePort>`
  - 장점: 외부 접근 가능, 추가 로드 밸런서 불필요
  - 단점: 포트 제약, 보안 위험

- **LoadBalancer**: 클라우드 로드 밸런서(ELB, ALB)와 통합하여 외부 접근
  - 접근 방법: `<LoadBalancerIP>`
  - 장점: 자동으로 로드 밸런서 생성, 고가용성
  - 단점: 클라우드 제공자 종속, 비용 발생

- **ExternalName**: DNS CNAME을 통해 외부 서비스 매핑
  - 접근 방법: `<ServiceName>.<Namespace>.svc.cluster.local`
  - 장점: 외부 서비스 추상화
  - 단점: 포트 매핑 불가

### Endpoints
- **Endpoints**: Service와 Pod 매칭 결과 (Pod IP + Port 리스트)
- **EndpointSlice**: 대규모 서비스(1000+ Endpoints) 최적화
- **Endpoint Controller**: Service Selector에 매칭되는 Pod 감시, Endpoints 업데이트

## 작동 원리 (How It Works)
1. 사용자가 Service YAML/JSON으로 정의
2. API Server에 Service 생성 요청 전송
3. Endpoint Controller가 Service Selector에 매칭되는 Pod 감시
4. Endpoints 업데이트 (Pod IP + Port 리스트)
5. kube-proxy가 Service VIP와 Endpoints로 iptables/IPVS 규칙 업데이트
6. 클라이언트가 Service VIP로 요청 전송
7. kube-proxy 규칙에 의해 실제 Pod IP로 트래픽 라우팅

## 상호작용 (Component Interaction)
- **Service ↔ kube-proxy**: Service VIP와 Endpoints로 iptables/IPVS 규칙 업데이트
- **Service ↔ Endpoint Controller**: Service Selector에 매칭되는 Pod 감시
- **Service ↔ CoreDNS**: Service DNS 레코드 등록 (`<ServiceName>.<Namespace>.svc.cluster.local`)

## Service Discovery
- **DNS (권장)**: CoreDNS가 Service DNS 레코드 제공
- **환경 변수**: Pod 시작 시 환경 변수로 Service 정보 주입
  - `<SERVICE_NAME>_SERVICE_HOST`: Service VIP
  - `<SERVICE_NAME>_SERVICE_PORT`: Service Port

## Session Affinity (세션 유지)
- **ClientIP**: 동일한 클라이언트 IP는 동일한 Pod로 라우팅 (기본: None)
  - `sessionAffinity: ClientIP`
  - `sessionAffinityConfig.clientIP.timeoutSeconds`: 세션 유지 시간 (초)
- **Cookie**: 쿠키 기반 세션 유지 (Headless Service에서 사용)

## Headless Service
- **개념**: ClusterIP를 할당하지 않고 DNS 레코드로 Pod IP 직접 노출
- **용도**: StatefulSet, Stateful Application (Database, Kafka)
- **DNS 레코드**: Pod IP로 DNS 레코드 생성 (`<PodName>.<ServiceName>.<Namespace>.svc.cluster.local`)

## 장애 처리 (Failure Handling) ⭐중요
- **Endpoints 장애 (Endpoints Failure)**
  - 동작 방식: Endpoints가 비어있으면 트래픽 라우팅 불가
  - 복구 메커니즘: Endpoint Controller가 Pod 매칭, Endpoints 재동기화

- **kube-proxy 다운 (kube-proxy Down)**
  - 동작 방식: 새로운 Endpoints 업데이트 불가, 기존 규칙은 계속 작동
  - 복구 메커니즘: kube-proxy 재시작, Endpoints 재동기화

- **ClusterIP 충돌 (ClusterIP Conflict)**
  - 동작 방식: 기존 Service와 IP 충돌 시 생성 불가
  - 복구 메커니즘: Service 재생성, IP 할당 제한 확인

- **LoadBalancer 생성 실패 (LoadBalancer Creation Failure)**
  - 동작 방식: 클라우드 API 장애 시 로드 밸런서 생성 불가
  - 복구 메커니즘: 클라우드 API 복구, Service Controller 재시작, 로드 밸런서 수동 생성

- **DNS 장애 (DNS Failure)**
  - 동작 방식: CoreDNS 다운 시 Service Discovery 불가, Service VIP로는 접근 가능
  - 복구 메커니즘: CoreDNS 재시작, Service IP 직접 사용

## 사용 사례 (Use Cases)
- 마이크로서비스 간 통신 (ClusterIP)
- 웹 애플리케이션 외부 노출 (NodePort, LoadBalancer)
- 외부 서비스 추상화 (ExternalName)
- Stateful Application (Headless Service)
- 세션 유지 필요한 애플리케이션 (Session Affinity)

## 참고자료 (References)
- [Services - Kubernetes Documentation](https://kubernetes.io/docs/concepts/services-networking/service/)
- [Service Networking - Kubernetes](https://kubernetes.io/docs/concepts/services-networking/)
- [Connecting Applications with Services - Kubernetes](https://kubernetes.io/docs/tutorials/services/connect-applications-service/)
- [CoreDNS - Kubernetes](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/)
