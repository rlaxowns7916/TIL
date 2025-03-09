# ELB (Elastic LoadBalancer)
- managed LB이다.
  - AWS가 동작할 것이라는걸 보장한다.
  - 업그레이드, 관리, HA를 보장한다.
- 자체 LB를 가지는 것보다 저렴하고 적은 노력이 들기떄문에 사용하는것이 권장된다.
- EC2, ASG, ECS,CloudWatch, Route53 등 다른 AWS 서비스와 통합 될 수 있다.
- TargetGroup을 통해서 AWS 서비스들에 대한 LoadBalancing이 가능하다.

## 종류
### [1] CLB (Classic LoadBalancer)
- 구 버전이다. (2009)
  - 더이상 사용할 수 없다.
- HTTP, HTTPS, TCP, SSL

### [2] ALB (Application LoadBalancer)
- 신 버전이다. (2016)
- IP주소가 변동된다. (DNS기반)
- HA(고가용성)을 위해서 AZ를 선택할 수 있다. (최소 2개)
- L7 LoadBalancer
    - 여러대의 Application의 부하를 분산 할 수 있다.
    - HTTP, HTTPS, WebSocket
        - client의 실제 IP는 X-Fowarded-For를 통해 전달된다. (port: X-Forwarded-Port, protocol: X-Forwarded-Proto)
- 다양한 Routing, Redirection을 지원한다.
  - Port, Path 기반 라우팅

  

### [3] NLB (Network LoadBalancer)
- 신 버전이다. (2017)
- AZ마다 고정 IP하나를 가지고 있으며, ElasticIP를 할당 할 수 있다. (DNS기반, IP기반 모두 사용가능)
- L4 LoadBalancer
  - TCP, TLS, UDP
  - 초당 수백만건의 요청을 처리할 수 있다.(고성능)
  - **HealthCheck는 HTTP, HTTPS, TCP를 지원한다.**
- private ip를 등록하여 Routing 가능하다. (하드코딩)
- EC2Instance 뿐만 아니라, ALB도 LB대상으로 둘 수 있다.
- Port 기반으로 Routing 가능하다.

### [4] GWLB (Gateway LoadBalancer)
- Layer3에서 동작한다. (IP Protocol)
- 주로 보안, 침입감지, 방화벽에 사용한다.