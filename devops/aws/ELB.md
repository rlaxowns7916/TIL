# ELB (Elastic LoadBalancer)
- managed LB이다.
  - AWS가 동작할 것이라는걸 보장한다.
  - 업그레이드, 관리, HA를 보장한다.
- **static DNS 이름을 제공한다.**
- 자체 LB를 가지는 것보다 저렴하고 적은 노력이 들기떄문에 사용하는것이 권장된다.
- EC2, ASG, ECS,CloudWatch, Route53 등 다른 AWS 서비스와 통합 될 수 있다.
- TargetGroup을 통해서 AWS 서비스들에 대한 LoadBalancing이 가능하다.

## 종류
### [1] CLB (Classic LoadBalancer)
- 구 버전이다. (2009)
  - 더이상 사용할 수 없다.
- HTTP, HTTPS, TCP, SSL
- 하나의 SSL 인증만을 지원한다.
  - ACM (AmazonCertificateManager)와 연동 가능하다.

### [2] ALB (Application LoadBalancer)
- 신 버전이다. (2016)
- IP주소가 변동된다. (DNS기반)
- HA(고가용성)을 위해서 AZ를 선택할 수 있다. (최소 2개)
- Labmda 함수, 
- L7 LoadBalancer
    - 여러대의 Application의 부하를 분산 할 수 있다.
    - HTTP, HTTPS, WebSocket
        - client의 실제 IP는 X-Fowarded-For를 통해 전달된다. (port: X-Forwarded-Port, protocol: X-Forwarded-Proto)
- 다양한 Routing, Redirection을 지원한다.
  - Port, Path 기반 라우팅
- 여러개의 SSL인증서를 제공할 수 있다.
  - SNI 덕분에 특정 도메인에 해당하는 인증서를 식별 가능하다.
  - ACM (AmazonCertificateManager)와 연동 가능하다.

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
- GENEVE Protocol(port: 6081)을 사용한다. / 최대 8500 MTU


### Sticky Session
- ALB, CLB에서만 지원 (NLB는 L4이기 떄문)
- Cookie를 사용해서 구현
- Application-based
  - custom
    - Taget(EC2, ECS)에서 자체 생성
    - Custom하게 Applciation에서 필요한 데이터를 담을 수 있음
    - 예약어 (AWSALB, AWSALBAPP, AWSALBTG)를 사용해서는 안됨
  - application
    - LB에 의해서 만들어짐
    - Cookie의 이름은 AWSALBAPP
- Duration-based
  - LB에 의해서 만들어짐
  - Cookie의 이름은 AWSALB(ALB), AWSELB(CLB)
  - 세션의 유지시간을 설정하기 떄문에 일정시간이 지나면 해제된다.

## CrossZone LoadBalancing
- AZ간의 인스턴스 갯수 불균형을 해소한다.
- AZ마다 공평하게 분배가 아니라 Instance별로 비율을 분배한다.
- **ALB는 기본적으로 Enable 되어있다.**
  - **AZ간의 통신이어도 비용이 부과되지 않는다. (보통 AWS는 AZ간의 통신에 비용을 부과한다.)**
  - CLB는 기본적으로 disable이며, enable 가능하고 AZ간 통신에 비용이 부과되지 않는다.
- NLB와 GWLB는 기본적으로 Disable 되어잇다.
  - AZ간의 통신에 요금이 부과된다.
```text
/**
  * LB는 AZ마다 공평하게 트래픽을 분배
  * AZ에 있는 인스턴스의 갯수가 차이나면 부하가 제대로 분산되지 않음
  * CrossZone LoadBalancing은 이 문제를 해결
  */
        ┌───────────────────┐
        │   Load Balancer   │
        └────────▲──────────┘
                │
        ┌───────┴──────────────┐
        │                      │
┌──────────────┐   ┌──────────────┐
│      AZ1     │   │      AZ2     │
├──────────────┤   ├──────────────┤
│   EC2        │   │  ️ EC2       │
│  ️EC2        │   │  ️ EC2       │
└──────────────┘   │  ️ EC2       │
                   │  ️ EC2       │
                   │  ️ EC2       │
                   └──────────────┘                        │
```
### Before CrossZone LoadBalancing
| AZ  | EC2 개수 | AZ별 트래픽 분배(%) | 1개 인스턴스당 트래픽(%) |
|-----|---------|-------------------|--------------------|
| AZ1 | 2개     | 50%               | 25%               |
| AZ2 | 5개     | 50%               | 10%               |

### After CrossZone LoadBalancing
| AZ  | EC2 개수 | AZ별 트래픽 분배(%) | 1개 인스턴스당 트래픽(%) |
|-----|---------|-------------------|--------------------|
| AZ1 | 2개     | 28.6%             | 14.3%              |
| AZ2 | 5개     | 71.4%             | 14.3%              |


## Connection Draining
- ELB 버전에 따라서 이름이 달라진다.
  - CLB       -> ConnectionDraining
  - ALB & NLB -> Deregistration Delay  
- In-Flight Request (활성 요청)을 완료할 수 있도록 하는 기능이다.
  - 기존 요청은 처리하게 둔다.
  - 새로운 요청은 Drained 된 서버로 가지 않는다.
- 1 ~ 3600초로 처리 가능하다. (default: 300초)