# LoadBalancer

## LoadBalancing이란?

**분산처리된 서버에서, 서버의 부하량, 속도저하 등을 고려해서 적절하게 분산 처리해주는 서비스**

- DownTime감소
- 확장성 증가
- 유연성 증가
- 처리속도 증가

### LoadBalancing 기법

#### 1. RR (Round Robin)
- 요청이 들어온대로 순서대로 라우팅 하는 방식이다.

#### 2. WeightedRR
- 서버마다 가중치를 주고, 가중치가 높은 서버부터 우선적으로 라우팅한다.

#### 3. IP Hash

- IP주소를 해시해서, 해당하는 Server에 라우팅 한다.

#### 4. Least Connection
- 가장 Connection이 적은 서버에게 라우팅한다.

#### 5. Least Response Time
- 서버의 연결 상태 + 응답시간 까지 고려하여 트래픽을 배분한다.
- 가장 짧은 응답시간을 가지는 서버에게 우선적으로 라우팅 한다.

## LoadBalancer 종류

### 1. L4 LoadBalancer

- IP & TCP/UDP 포트 정보를 기반으로 한다.
- Packet 레벨에서만 로드밸런싱을 하기 떄문에 속도가 빠르다.
- 데이터를 복호화할 필요가 없기 때문에, 안전하다.
- 저렴하다

### 2. L7 LoadBalancer

- TCP/UDP 는 물론, HTTP URL, FTP 파일명, 쿠키정보 등을 바탕으로 로드밸런싱이 가능하다.
    - 섬세한 라우팅이 가능
- 캐싱 기능을 제공한다.
- 특정한 비정상 패턴을 지닌 트래픽을 걸러낼 수 있다.
    - DoS/DDoS 같은 것들
- 비싸다.

## GSLB(Global Server Load Balancing)

### 개념
- 물리적으로 분산된 여러 데이터센터나 클라우드 리소스에 걸쳐 **트래픽을 분산**하여, 고가용성(HA)과 최적 성능을 달성하는 기술이다.
- 단일 서버나 단일 IDC 수준이 아니라, **Region, DataCenter** 같은 **더 큰 단위**로 트래픽을 조정한다.

### 동작 방식
- 주로 **DNS 레벨**에서 **도메인 요청에 대한 응답(IP)을 동적으로 제어**하여, **사용자의 위치(Geolocation)**, **네트워크 상태**, **서버 상태(HealthCheck)** 등을 바탕으로 최적의 데이터센터 IP를 반환한다.
- GSLB 솔루션은 각 IDC/서버 상태를 모니터링(HealthCheck)하고, 장애 시 해당 리소스를 DNS 응답에서 제외한다.

### 특성 및 이점
1. **HealthCheck & Failover**: 실시간 헬스 체크로 장애 지점 우회.
2. **지연시간(레이턴시) 최적화**: 사용자에게 가장 가까운 IDC로 라우팅.
3. **부하 분산**: 특정 IDC에 트래픽이 몰리지 않도록 균형 분산.
4. **DR(Disaster Recovery)**: 한 지역에 재해 발생 시 다른 지역으로 즉시 전환.

### 콘텐츠 동기화
- 정적 콘텐츠(이미지, JS 등)는 보통 **CDN**을 사용하여 전 세계 PoP로 배포한다.
- 동적 데이터(세션, DB)는 **별도의 동기화/복제**가 필요할 수 있다.

### 주의 사항
- **DNS TTL** 설정에 따른 트래픽 재분배 속도 vs. DNS 요청 증가의 트레이드오프.
- **Session Stickiness** 필요 시, GSLB와 로컬 LB의 세션 관리 전략 고려.
- **보안**: DNS 공격, DDoS 대응, 인증서 관리 등.

--- 

### DR (Disaster Recovery)
- 서버의 상태를 지속적으로 모니터링 한다.
  - DNS는 서버의 IP만 넘겨줄 뿐, 서버의 상태는 알지 못한다.
  - 실패한 서버의 IP는 응답에서 제외하기 때문에, 사용자는 서비스를 안정적으로 사용할 수 있다.

### Load Balancing
- 서버의 Load를 모니터링 한다.
  - DNS는 RR방식을 사용한다. (DNS LoadBalancing)
  - GSLB는 LoadTime이 적은 서버의 IP를 반환하는 것을 최우선적으로 한다.

### Latency
- 지리적으로 가까운 서버를 이욯한다.
  - DNS는 RR방식이기 떄문에, 먼 거리의 서버를 배정해줄 때도 있다.

#### 구성 방식
1. Private DNS서버를 구성
2. Public DNS서버에서 DNS Query를 내 Private DNS서버로 보내도록 설정
3. GSLB 소프트웨어를 통해서 적합한 IP를 반환한다.

***AWS Route53, Azure Traffic Manager는 따로 구성안해도 된다.**

#### 동작방식
1. 사용자가 DNS에 Domain 질의
2. DNS는 Local 부터 Root까지 순차적인 질의를 수행
3. 해당 Domain이 GSLB에게 위임된 도메인일 경우 GSLB에게 질의 (GSLB는 NameServer에 등록되어있다.)
4. GSLB는 프록시 방식이기 떄문에 담당 도메인에 DNS Query
5. GSLB는 정책에 따라 (HealthCheck, Latency, Custom ...)에 따라서 최적의 IP를 리턴해준다.