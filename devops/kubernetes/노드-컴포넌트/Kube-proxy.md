# Kube-proxy

## 개념 (Concept)
- 각 Node에서 실행되는 네트워크 프록시로 Service의 네트워킹 규칙을 관리
- Service(Kubernetes Service)를 통한 Service Discovery와 Load Balancing 구현
- iptables/IPVS/ipvs 규칙을 통해 트래픽 라우팅

## 역할 (Roles)
- **서비스 디스커버리 (Service Discovery)**: DNS 또는 환경 변수를 통해 Service에 접근
- **로드 밸런싱 (Load Balancing)**: Service의 Endpoints로 트래픽 분산
- **네트워크 규칙 관리**: iptables/IPVS 규칙을 업데이트하여 트래픽 라우팅
- **Health Check**: Endpoint Controller가 제공하는 Endpoints 업데이트

## 구조 (Architecture)

### 프록시 모드 (Proxy Modes)
- **iptables (기본 모드)**: iptables 규칙을 사용하여 트래픽 라우팅
  - 장점: 단순, 안정적
  - 단점: 규칙 수가 많을 때 성능 저하

- **IPVS (IP Virtual Server)**: IPVS 규칙을 사용하여 트래픽 라우팅
  - 장점: 대규모 서비스(수천 개의 Pod)에서 높은 성능
  - 단점: 복잡, iptables보다 설정 어려움

- **userspace (레거시)**: userspace 프로그램으로 트래픽 프록시
  - 장점: 유연한 로직
  - 단점: 성능 저하 (현재 거의 사용되지 않음)

### 네트워킹 흐름
1. 클라이언트가 Service VIP(Virtual IP)로 요청 전송
2. kube-proxy의 iptables/IPVS 규칙에 의해 실제 Pod IP로 트래픽 라우팅
3. Load Balancing 알고리즘(Round Robin, Least Connection)으로 Pod 선택
4. 요청이 선택된 Pod로 전송

## 작동 원리 (How It Works)
1. API Server에서 Service 및 Endpoints 감시(Watch)
2. Service 생성/업데이트 시 iptables/IPVS 규칙 업데이트
3. 클라이언트가 Service VIP로 요청 전송
4. kube-proxy의 규칙에 의해 실제 Pod IP로 라우팅
5. Load Balancing 알고리즘으로 Pod 선택 및 트래픽 전송

## 상호작용 (Component Interaction)
- **Kube-proxy ↔ API Server**: Watch API로 Service/Endpoints 감시
- **Kube-proxy ↔ iptables/IPVS**: 네트워킹 규칙 업데이트
- **Kube-proxy ↔ Kubelet**: Service Endpoints 동기화

## Service 타입별 동작 (Service Type Behavior)
- **ClusterIP (기본)**: 클러스터 내부에서만 접근 가능한 VIP 할당
- **NodePort**: 각 Node의 특정 포트(30000-32767)를 통해 외부 접근 가능
- **LoadBalancer**: 클라우드 로드 밸런서(ELB, ALB)와 통합하여 외부 접근
- **ExternalName**: DNS CNAME을 통해 외부 서비스 매핑

## Session Affinity (세션 유지)
- **Client IP**: 동일한 클라이언트 IP는 동일한 Pod로 라우팅 (기본: None)
- **Cookie**: 쿠키 기반 세션 유지 (Headless Service에서 사용)

## 장애 처리 (Failure Handling) ⭐중요
- **kube-proxy 다운 (kube-proxy Down)**
  - 동작 방식: 새로운 Service Endpoints 업데이트 불가, 기존 규칙은 계속 작동
  - 복구 메커니즘: kube-proxy 재시작, Service Endpoints 재동기화

- **iptables 규칙 충돌 (iptables Rules Conflict)**
  - 동작 방식: 트래픽 라우팅 실패, Service 접근 불가
  - 복구 메커니즘: kube-proxy 재시작, iptables 규칙 재구성

- **Endpoint 장애 (Endpoint Failure)**
  - 동작 방식: 해당 Pod로 트래픽 라우팅 불가, 다른 Pod로 라우팅 (자동 복구)
  - 복구 메커니즘: Endpoint Controller가 Endpoints 업데이트, kube-proxy 규칙 업데이트

- **IPVS 장애 (IPVS Failure)**
  - 동작 방식: IPVS 규칙 업데이트 불가, 트래픽 라우팅 실패
  - 복구 메커니즘: kube-proxy 재시작, IPVS 모드 해제 및 iptables 모드로 전환

- **네트워크 파티션 (Network Partition)**
  - 동작 방식: Service Discovery 불가, DNS 조회 실패
  - 복구 메커니즘: 파티션 해제 후 kube-proxy 재시작, Endpoints 재동기화

## 사용 사례 (Use Cases)
- Service Discovery (DNS 또는 환경 변수를 통한 서비스 검색)
- Load Balancing (Service를 통한 트래픽 분산)
- Session Affinity (동일 클라이언트 요청을 동일 Pod로 라우팅)
- External Traffic (NodePort, LoadBalancer를 통한 외부 접근)

## 참고자료 (References)
- [kube-proxy - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/components/#kube-proxy)
- [Services - Kubernetes](https://kubernetes.io/docs/concepts/services-networking/service/)
- [The kube-proxy - How it works](https://kubernetes.io/docs/reference/networking/virtual-ips/)
- [Using Source IP for Load Balancers - Kubernetes](https://kubernetes.io/docs/tutorials/services/source-ip/)
