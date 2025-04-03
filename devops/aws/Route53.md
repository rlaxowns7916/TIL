# Route53
- 고가용성, 확장성을 갖춘 DNS
- Domain등록, Record 관리, HealthCheck, Routing기능 제공
- 고객이 직접 DNS Record를 업데이트 할 수 있다.
- 100% SLA를 보장하는 유일한 AWS 서비스이다.

## Records
- Domain에 대한 트래픽을 제어하는 방법
- 아래와 같은 정보를 갖는다.
  - Domain/SubDomain Name: ex) example.com
  - RecordType: ex) A, AAAA
  - value: Domain에 매핑된 IP 혹은 Domain
  - Routing Policy: 어떻게 Route53이 DNS-Query에 응답할지
  - TTL: DnsResolver 들이 얼마나 Record를 캐싱할지

### RecordType
- A: IPV4에 Mapping되는 HostName
- AAAA: IPV6에 Mapping되는 HostName
- CNAME: HostName에 매핑되는 또다른 HostName 
- NS: Hosted Zone에 대한 NameServer (트래픽 제어 룰을 담당한다)

## HostZone
- DNS레코드를 저장/관리 하는 저장소
- 어떤 방식으로 Routing 할지 결정되는 단위이다.
- Public HostZone: Public망 (인터넷)에서 접근 가능한 Domain
- Private HostZone: Private망 (내부)에서만 접근 가능한 Domain
  - Route53의 경우, VPC 내부에서 접근가능
- HostZone 하나당 월 0.50$의 요금이 부과된다.

### TTL
- 클라이언트가 DNS쿼리를 요청했을 때, DNS서버가 응답한 결과를 얼마나 캐싱할지 결정하는 시간
- TTL이 만료되면, 다시 DNS Query를 수행한다.
  - 너무 TTL기간을 적게 설정하면, DNS서버에 부하가 증가한다.
  - TTL기간을 너무 길게 설정하면, DNS Record의 변경이 반영되기까지 시간이 걸린다.
- alias 레코드는 제외된다.

## Health Check
- Public Resource에 대해서 수행한다.
  - Private Resource는 CloudWatch Metric/Alarm 을 통해서 판단하여야한다.
- 15개의 Region에 분산된 Global healthChecker가 주기적으로 HealthCheck를 수행한다.
  - 임계값을 설정하여 N번 연속 실패시 비정상 판단을 할지 정의한다. (default: 3)
  - Interval 설정할 수 있다. (default: 30Sec, min: 10Sec)
  - HTTP, TCP, HTTPS 프로토콜을 지원한다.
  - 전체 Health Checker 중 최소 18% 이상이 해당 리소스를 Healthy로 판단해야 해당 리소스를 **정상(Healthy)**으로 간주한다.

### Calculated HealthCheck
- 실제 HTTP/TCP HealthCheck를 수행하지 않고, 하위 HealthCheck의 상태를 기반으로 상위 HealthCheck의 상태를 결정한다.
- CloudWatch Alarm과 연동도 가능하다.
- **AND, OR, n out of m** 조건을 지원한다.
- 유료 옵샨이다. (0.5$/month)

### CNAME vs Alias
- CNAME: 다른 HostName을 가리키는 DNS 레코드
  - CNAME은 반드시 RootDomain이 아닌 SubDomain에만 사용해야 한다.
  - ex) example.com(x), www.example.com(o)
- Alias: Aws에 국한
  - DNS 기능의 확장이라고 볼 수 있다. 
  - **CNAME은 비용이 부과되지만, Alias는 비용이 부과되지 않는다. (Route53내부에서 처리되기 떄문)**
  - 특정 HostName이 AWS의 리소스에 매핑되는 레코드
    - 실제 IP를 몰라도 되고, AWS 리소스가 변경되더라도 자동으로 반영된다. 
    - 매핑 가능한 리소스
      - ELB,
      - CloudFront
      - S3 (웹사이트 한정)
      - Route53 (Same HostedZone)
      - ElasticBeanstalk
      - API Gateway
      - Global Accelerator
      - ...
    - 매핑 안되는 리소스
      - EC2 (DNS 이름)
- RootDomain, SubDomain 모두 동작한다.
- A 혹은 AAAA 타입만 지원한다.
- TTL 없이 사용 가능하다.
  - 내부적으로 TTL이 자동으로 관리된다.
  - AWS 리소스의 IP주소 변경을 자동으로 추적한다.

## Routing Policy
- Route53이 DNS Query를 어떻게 처리할지 결정한다.
- 아래와 같은 Routing Policy를 지원한다.
  - Simple (단순)
  - Weighted (가중치)
  - Failover (장애조치)
  - Latency based (지연시간 기반)
  - Geolocation (지리적 위치 기반)
  - Multi-Value Answer (다중값 응답)
  - Geoproximity (지리적 근접성 기반 / Route53 Traffic Flow 전용)

### [1] Simple
- 가장 기본적인 Routing Policy
- 특별한 조건 없이 매핑된 Route53에 등록된 레코드 셋들을 응답한다.
- 하나의 도메인에 여러개의 IP 주소를 연결 할 수 있다.
  - 여러개의 Value가 리턴되면, Client가 그중 하나를 선택한다.
- Alias를 사용하면 하나의 AWS 리소스에만사용이 가능하다.
- **HealthCheck를 지원하지 않는다.***

### [2] Weighted
- 가중치를 통해서, 트래픽을 분산할 수 있다.
- 가중치들의 합은 꼭 100이 아니어도 된다. (어차피 비율로 계산된다.)
- DNS레코드는 같은이름과 Type을 가져야만한다.
- HealthCheck를 지원한다.
  - HealthCheck가 실패한 경우, 해당 레코드는 제외한다.
- Region간 Routing, A/B Test, 인프라 단위의 Blue/Green Deployment 등등에 사용된다.


### [3] Latency
- 지연시간이 가장 짧은 리소스의 트래픽을 반환한다.
- latency가 가장 중요한 요소중 하나일 때 사용 할 수 있는 RoutingPolicy이다.
- HealthCheck를 지원한다.
- **Client <-> Region 사이의 네트워크 RTT를 기준으로 한다.**

### [4] Failover (Active-Passive)
- health-check enable은 필수적이다.
- Primary와 Secondary를 설정할 수 있다.
  - Primary가 Unhealthy라면 Secondary로 FailOver된다.

### [5] Geolocation
- 실제 사용자의 위치를 기반으로 Routing 한다.
  - latency-based와 다르다.
- **Default Record를 생성해야 한다.**
  - 일치하는 location을 찾지 못했을 때, default record로 응답한다.
- 지리적인 위치를 직접 설정
  - ex) 특정 국가/대륙/지역의 트래픽은 특정 리소스로 라우팅

### [6] Geoproximity
- 현재위치에서 가장가까운 리전으로 라우팅한다.
- Route53 Traffic flow에서만 사용가능 (GUI 기반)
- Resource의 지리적 좌표를 사용하며, 편향값을 제공한다.

### [7] Ip-based
- CIDR을 기반으로 Routing 한다.
- 성능을 향상시키고, network 비용을 낮출 수 있다.
  - 이미 알고있는 IP대역을 통해서 Public망을 거치지 않을 수 있다.

### [8] Multi-Value Answer
- 여러개의 IP주소를 리턴한다. (최대 8개)
- HealthCheck를 지원한다. (Healthy한 것만 리턴)


## Domain Registrar vs DNS Service
- Domain Registrar: 도메인 이름을 판매하고 등록하는 서비스
  - ex) GoDaddy, NameCheap, AWS Registrar
  - 이름에 대한 등록, 갱신, 소유권 관리, NS 설정을 담당
  - ICANN(국제 인터넷 주소 관리기구) 혹은 국가별 기관으로 부터 공식인증을 받은 업체
- DNS Service: 도메인 이름을 IP주소로 변환하는 서비스
  - ex) AWS Route53, Cloudflare, Google DNS
  - DNS 쿼리 처리, 레코드 관리, 트래픽 라우팅을 담당