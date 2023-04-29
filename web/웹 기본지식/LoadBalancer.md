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

## GSLB (Global Server Load Balancing)
- 각 회사나 집단에서 자사 서비스에 대한 LoadBalancing을 하기 위해서 사용한다.
  - 단일 Instance들에 대한 LoadBalancing이라기 보다는, Region, DataCenter 같은 더 큰 단위의 LoadBalancing 이다.
- DNS 기반의 LoadBalancing 이다.
  - DNS와 다르게 상태에 따른 모니터링이 가능하다. (Health-Check)
  - DNS와 다르게 지역별 서버에 따른 Latency를 갖고있기 떄문에, Latency가 적은 IP를 리턴해준다.
- DNS의 프록시 형태로 동작한다.
- H/W일수도 있고, S/W일 수도 있다.

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