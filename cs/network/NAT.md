# NAT(NetworkAddressTranslation)
- IPv4의 제한된 수 때문에 문제를 해결하기 위해서 개발되었다.
- PrivateIP를 PublicIP로 변환하여 공용망을 사용하게 할 수 있는 기술이다.
  - 2개의 TCP Connection이 생기는 것이 아니라, 단일 TCP Connection으로 처리된다.
- IP 패킷의 TCP/UDP 포트 숫자와 소스 및 목적지의 IP 주소 등을 재기록하면서 라우터를 통해 네트워크 트래픽을 주고 받는 기술이다.
   - 라우터(NAT)는 포트포워딩을 통해서 사설IP의 디바이스와 외부인터넷을 연결한다.
   - **Port는 Gateway의 물리적인 제약에의한 Port 갯수가 아닌, 매핑을 하기위한 논리적인 개념이며 공인 IP하나당 65535개의 Port를 사용할 수 있다.** 
- **NAT-Table에 매핑정보를 통해서, 단순히 IP와 PORT를 변경한채로 연결을 유지 할 수 있다**

## PrivateNetwork란?
- 기업, 가정 등 한정된 공간에서 사용되는 네트워크
- PrivateNetwork에 소속된 하드웨어 끼리는 PrivateIP로 통신한다.
- 사설망에서 사용되는 IP (PrivateIP)는 사설망에서만 유효하며, 공용망에서는 사용이 불가능하다.

## PublicNetwork란?
- 일반적인 공용 인터넷 환경
- Public Network에서 제공되는 Ip를 Public Ip라고 한다.
  - ISP(InternetServiceProvider)가 제공한다.
- 중복 없이 관리된다.

## NAT 사용의 장점
1. IP 주소 절약
    - 하나의 PublicIP로 여러대의 Host가(PrivateIP를 사용하는) 인터넷에 접속 할 수 있다.
2. 보안
    - IP를 숨길 수 있다.
    - 최종목적지인 사설 IP를 숨길 수 있기 떄문에, 공격으로부터 보호할 수 있다.

## NAT 방식

### [1] Cone NAT
- Host 단위로 외부포트 지정

1. FullCone NAT
   - 한번 Private IP와 NAT IP가 매핑되면, 모든 외부 Public IP가 동일 Port로 내부 Private IP로 접근이 가능하다.
   - 가장 단순한 형태의 NAT이며, P2P 연결 친화적이다.
   - | Internal IP  | Internal Port | NAT IP      | NAT Port | Destination IP | Destination Port | Protocol | State               |
     |--------------|---------------|-------------|----------|----------------|-------------------|----------|---------------------|
     | 192.168.0.10 | 51234         | 203.0.113.5 | 40000    | Any  | Any               | TCP      | ESTABLISHED         |
2. RestrictedCone NAT
   - 한번 Private IP와 NAT IP가 매핑되면, 연결이 된 Remote IP만 접근이 가능하다. 
     - Private IP Host에 대한 식별이 가능하다면 최대한 Port는 같은걸 쓴다. (Symmetric과의 차이점)
   - 연결이 된적 없는 Host의 접근을 막을 수 있다.
   - RemoteIP만(Port는 Any) 볼 수도 있고, Remote Port까지 볼 수도 있다.
   -  | Internal IP  | Internal Port | NAT IP      | NAT Port | Destination IP | Destination Port | Protocol | State               |
       |--------------|---------------|-------------|----------|----------------|------------------|----------|---------------------|
       | 192.168.0.10 | 51234         | 203.0.113.5 | 40000    | 198.51.100.20  | 5555             | TCP      | ESTABLISHED         |
      | 192.168.0.10 | 51235         | 203.0.113.5 | 40000    | 198.51.100.20  | 7777             | TCP      | ESTABLISHED         |

### [2] Symmetric NAT
- TCP Session마다 외부 포트 지정
- 보안이 뛰어나다.
  - Inbound 트래픽이 들어올 때, NAT-Table을 참조하여 존재하지 않는다면 처리하지 않을 수 있기 때문이다.

| Internal IP  | Internal Port | NAT IP      | NAT Port | Destination IP | Destination Port | Protocol | State               |
|--------------|---------------|-------------|----------|----------------|-------------------|----------|---------------------|
| 192.168.0.10 | 51234         | 203.0.113.5 | 40000    | 198.51.100.20  | 443               | TCP      | ESTABLISHED         |
| 192.168.0.11 | 51235         | 203.0.113.5 | 40001    | 198.51.100.30  | 80                | TCP      | ESTABLISHED         |
| 192.168.0.12 | 52800         | 203.0.113.5 | 45000    | 203.0.113.10   | 53                | UDP      | ASSURED (DNS Query) |

## NAT 과정 예시
```text
## 네트워크 구성
| 역할      | IP 주소    | MAC 주소 |
|-----------|------------|----------|
| Host A    | 1.1.1.1    | aa       |
| Host B    | 2.2.2.2    | bb       |
| Gateway   | 3.3.3.3    | cc       |
| Google    | 8.8.8.8    | 필요 없음 |

---

## 순서

### 1. Host A와 Host B가 Gateway로 요청 전송
- **Host A**:
  - **출발지 IP**: `1.1.1.1`
  - **목적지 IP**: `8.8.8.8` (Google)
  - **출발지 MAC**: `aa`
  - **목적지 MAC**: `cc` (Gateway)
  - **패킷 내용**: `Request from Host A to Google`
- **Host B**:
  - **출발지 IP**: `2.2.2.2`
  - **목적지 IP**: `8.8.8.8` (Google)
  - **출발지 MAC**: `bb`
  - **목적지 MAC**: `cc` (Gateway)
  - **패킷 내용**: `Request from Host B to Google`

---

### 2. Gateway가 Google로 요청 전달
- Gateway는 Host A와 Host B의 요청을 받아 NAT를 수행하여 **출발지 IP와 포트 번호**를 변환한 뒤 Google로 요청을 전송합니다.
- **Host A의 요청**:
  - **출발지 IP**: `3.3.3.3` (Gateway의 공인 IP)
  - **출발지 포트**: `40001` (NAT로 할당된 포트)
  - **목적지 IP**: `8.8.8.8` (Google)
- **Host B의 요청**:
  - **출발지 IP**: `3.3.3.3` (Gateway의 공인 IP)
  - **출발지 포트**: `40002` (NAT로 할당된 포트)
  - **목적지 IP**: `8.8.8.8` (Google)

---

### 3. Google이 Gateway로 응답 전송
- Google은 Gateway의 공인 IP(3.3.3.3)와 요청 시의 포트 번호를 기준으로 응답을 반환합니다.
- **Host A에 대한 응답**:
  - **출발지 IP**: `8.8.8.8` (Google)
  - **목적지 IP**: `3.3.3.3` (Gateway)
  - **목적지 포트**: `40001`
  - **패킷 내용**: `Response to Host A`
- **Host B에 대한 응답**:
  - **출발지 IP**: `8.8.8.8` (Google)
  - **목적지 IP**: `3.3.3.3` (Gateway)
  - **목적지 포트**: `40002`
  - **패킷 내용**: `Response to Host B`

---

### 4. Gateway가 Host A와 Host B로 응답 전달
- Gateway는 NAT 테이블을 참조하여 응답을 적절한 내부 호스트로 전달합니다.
- **Host A에 대한 응답**:
  - **출발지 IP**: `8.8.8.8` (Google)
  - **목적지 IP**: `1.1.1.1` (Host A)
  - **출발지 MAC**: `cc` (Gateway)
  - **목적지 MAC**: `aa` (Host A)
  - **패킷 내용**: `Response from Google to Host A`
- **Host B에 대한 응답**:
  - **출발지 IP**: `8.8.8.8` (Google)
  - **목적지 IP**: `2.2.2.2` (Host B)
  - **출발지 MAC**: `cc` (Gateway)
  - **목적지 MAC**: `bb` (Host B)
  - **패킷 내용**: `Response from Google to Host B`

---
```