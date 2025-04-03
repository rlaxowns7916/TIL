# VPC (Virtual Private Cloud)
- 사용자가 정의한 가상 사설 Network
- Region 단위로 구성된다.
  - 기본적으로 하나의 Region에 최대 5개가 설정 가능하다.
- VPC CIDR: IP Range(CIDR)를 가진다.


## 구성요소

### [1] Subnet
- VPC를 특정단위로 나눈 범위 (CIDR)
- AZ(Availability Zone) 단위로 나뉘며, 각 AZ는 서로 다른 물리적 위치에 존재한다. (변경 불가)
- public 또는 private을 구분할 수 있으며, 보안 정책 및 라우팅 제어를 세분화하여 설정할 수 있다.

### [2] Route table
- 트래픽을 전달할 위치가 명시된 테이블
- 각 Subnet은 하나의 RoutingTable을 사용하며, 해당 규칙에 따라 트래픽이 라우팅 된다.

### [3] Internet Gateway
- VPC 단위로 하나만 생성할 수 있다.
- VPC 리소스가 인터넷과 양방향 통신을 가능하개 해주는 Gateway
- IGW에 연결되어 있더라도, RoutingTable과 SecurityGroup에 따라 인터넷과 연결되지 않을 수 있다.
- **NAT를 수행하는 장치가 아니다.**
  - **VPC 내의 Resource가 Public IP 또는 Elastic IP를 가지고 있어야만 인터넷과 연결된다.**
  - PublicIP가 어떤 VPC 내부 Resource에 연결되어 있는지를 확인한 후 Routing해줄 뿐이다.

### [4] NAT Gateway
- Public Subnet에 위치한다.
  - Public IP를 가져야하며, 이 IP를 통해서 IGW와 연결된다.
- private subnet은 RouteTable을 통해서 NAT Gateway로 Routing되고, 그 후 IGW로 Routing되어 인터넷에 연결한다.
- private subnet에서 인터넷으로 나가는 것을 가능하게 하지만, 외부에서 해당 리소스에 직접 접근 하는 것은 막는다. (Outbound 통신만 가능)

### [5] VPC Endpoint
- VPC Endpoint Gateway가 필요하다.
- 인터넷, NAT, IGW를 거치지 않고 VPC 내부에서 AWS 서비스를 프라이빗 하게 접근할 수 있게 해준다.

### [6] NACL (Network ACL)
- subnet 단위로 설정되는 방화벽
- Inbound, Outbound 모두 설정 가능하다.
  - Allow/Deny가 가능하다. 
  - Default NACL은 Inbound/Outbound 모두 Allow all
- Rule의 순서대로 적용된다. (first match)

| 항목                   | Security Group (SG)                         | Network ACL (NACL)                          |
|------------------------|---------------------------------------------|---------------------------------------------|
| **적용 대상**          | 인스턴스 (ENI 등 리소스 단위)               | 서브넷 단위                                 |
| **상태성**             | 상태 저장 (Stateful)                        | 상태 비저장 (Stateless)                     |
| **허용/거부 여부**     | 허용만 가능 (Allow only)                    | 허용 및 거부 모두 가능 (Allow & Deny)      |
| **규칙 방향**          | 인바운드 / 아웃바운드 별도 설정             | 인바운드 / 아웃바운드 별도 설정             |
| **룰 평가 방식**       | 모든 규칙을 평가                            | 규칙 번호 순서대로 첫 매치 적용             |
| **기본 동작**          | 모든 트래픽 기본 차단, 명시된 것만 허용     | 기본적으로 모든 트래픽 허용 (기본 NACL 기준)|
| **응답 트래픽 처리**   | 자동 허용 (Stateful 특성)                   | 명시적으로 허용 규칙 추가 필요              |
| **적용 우선 순위**     | 인스턴스에 직접 적용                        | 서브넷에 적용되어 그 안의 인스턴스에 영향  |
| **로그 기능**          | VPC Flow Logs 사용 가능                     | VPC Flow Logs + NACL 자체 로깅 가능         |
| **구성 복잡도**        | 상대적으로 간단                            | 상대적으로 복잡                             |

### [7] VCP Flow Logs
- ENI 단위로 생성되는 로그
- Inbound/Outbound의 트래픽을 모두 캡쳐한다.
  - 기본적으로는 disable
- Log를 CloudWatchLogs 또는 S3에 저장 할 수 있다.
- Default 포맷을 가진다.
  - ```text
      version account-id interface-id srcaddr dstaddr srcport dstport protocol packets bytes start end action log-status
    ```
- 아래와 같은 Usecase에 주로 사용한다.
  - 보안 분석
  - 트러블 슈팅
  - 규정 준수
  - 최적화
---

## VCP Peering
- VPC 간의 연결을 가능하게 해주는 서비스
- IP 대역이 겹치면 안된다.
  - IP 대역이 겹치면, 누구와 통신해야 하는지 모르게되기 때문이다.
- 연결은 양방향이지만, 전이되지 않는다.
  - ex) A <-> B, A <-> C 일 경우, B<->C (X)

## Site-to-Site VPN && Direct Connect
### [1] Site-to-Site VPN
- 온프레미스와 AWS VPC를 연결하는 VPN
- Public망 에서 암호화된 연결로 이루어진다.
- OnPresmise와 AWS VPC간 IP대역이 겹치면 안된다.

### [2] Direct Connect
- 온프레미스와 AWS VPC를 연결하는 전용선
- Private망에서 이루어진다.
- 구축하는데 몇달이 걸릴 수 있다.