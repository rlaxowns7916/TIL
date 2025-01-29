# Private Network란?
- 외부의 접근을 차단하는 Network이다.
- 물리적으로 격리되어있다.
- 매우 비싸다.

# VPN (Virtual Private Network)
- PublicNetwork를 통해서 안전한 Network 통신 (like PrivateNetwork)을 가능하게 한다.
- PrivateNetwork의 기능을 소프트웨어적으로 (Virtual) 풀어낸 것이다.
- **실제 IP를 숨기고, 데이터를 암호화한다.**
  - 인증, 암호화, 무결성 검사 등을 수행한다.
- Router가 VPN기능을 제공해야하며, 이 기능을 제공하는 Rotuer를 SG(Secure Gateway)라고 부른다.
- **사용자는 VPN Client를 사용하며, VPN Router를 통해 터널링이 된다.**
  - Tunnel(Session)이 확립되면 VPN Server는 가상 IP를 할당한다. 
  - VPN을 사용할시 총 2개의 IP를 사용하게 된다.
    - VPN으로부터 할당받은 IP
    - ISP로부터 할당받은 IP

### 왜 Virtual인가?
- Virtual NIC(Network Interface Card)가 생긴다.
- VPN으로부터 할당받은 새로운 IP가 이 NIC를 통해서 나간다.
- 실제 전용선을 깔아서 통신하는 것이 아닌, PublicNetwork를 통해 논리적인 전용선 통신을 하기 때문이다.

## 과정

### [1] 인증
- VPN Client와 VPN 서버간 상호 인증 절차를 거친다.
- IP/Password || PSK (Pre-Shared Key)

### [2] 암호화
- Tunneling 된 패킷에 암호화 과정을 적용
- 외부에서 패킷을 도청하더라도, 내용을 쉽게 알아 볼 수 없도록 보호

### [3] 무결성 검사
- 전송 중에 데이터가 변조되지 않았음을 검증
- HMAC, SHA, MD5등을 사용하여 패킷 Hash를 비교

### [4] 재전송 및 세션 관리
- 패킷 손실 시, 재전송을 통한 세션 유지와 관리
- VPN서버와 Client간 암호화키 갱신

```text
1. 사용자 장비 (예: 노트북)
   │
   ▼
2. VPN 클라이언트
   │  - 사용자 인증 (ID/PW, 인증서)
   │  - 데이터 암호화 시작
   ▼
3. 터널링 프로세스
   │  - 원본 패킷 → [암호화 + VPN 헤더 추가] → 캡슐화된 패킷
   ▼
4. 인터넷 (공용 네트워크)
   │  - 캡슐화된 패킷 전송
   ▼
5. VPN 서버
   │  - 헤더 제거 및 복호화
   │  - 원본 패킷 추출
   ▼
6. 목적지 서버 (예: 회사 내부 시스템)
   │  - 평문 데이터 수신
   ▼
7. 응답 데이터 → 역과정으로 사용자에게 전달
```

### VPN Client <-> VPN Server 통신과정
```text
VPN Server Public IP: 3.3.3.3
Client의 원본 Public IP: 1.1.1.1 (ISP 할당)
VPN Server가 클라이언트에 할당한 사설 IP: 192.168.0.x
사내 서버 내부망 IP: 10.0.0.x

원본 패킷 (VPN 캡슐화 전)
목적지IP: 10.0.0.x (사내 서버)
출발지IP: 192.168.0.x (VPN Server가 할당해준 private IP)

VPN 터널링 적용 후 (원본 패킷은 암호화)
목적지IP: 3.3.3.3 (VPN Server Public IP)
출발지IP: 1.1.1.1 (Client 원본 Public IP)

```

### Proxy와의 차이점
```text
둘다 경유지를 거쳐서 목적지로 간다는 점은 동일
암호화 및 캡술화 여부가 큰 차이라고 볼 수 있다.
```

## Tunneling
- 데이터 패킷을 캡슐화 및 암호화해서 전송하는 기술
  - 원본 패킷을 추가적인 Header로 감싸 암호화한다.
  - VPN 서버가 패킷을 복호화하고, 최종목적지로 전달한다.
- 암호화된 데이터를 주고 받을 수 있는 세션(Tunnel)을 의미한다.
- **PrivateNetwork에서 사용하는 패킷을 PublicNetwork를 통해서 전송할 때, 내부 패킷을 암호화 및 캡슐화 해서 전송한다.**
- 대표적인 프로토콜은 아래와 같다.
  - IPSec
  - OpenVPN(SSL/TLS 기반)
  - GRE
  - ...
