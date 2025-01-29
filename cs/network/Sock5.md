# SOCK5 (SOCKet Secure version 5)
- 서버 간에 프록시 역할을 하는 인터넷 프로토콜
  - Sock4를 UDP에서도 동작 가능하게 확장한 버전
- **TCP Payload에 SOCK5 Header를 추가하여 전송한다.**
  - SOCK5헤더의 길이는 가변이다.
- 클라이언트와 서버 사이에 직접적인 연결을 설정하지 않고도 데이터를 교환할 수 있게 한다.
- OSI 5계층 프로토콜이다.
  - TCP / UDP 모두 지원 (Layer4위에서 동작하며, Application의 Network트래픽을 라우팅 하는 역할을 수행)
  - 네트워크 트래픽을 구분하지 않기 떄문에, 유연하다. (HTTP, FTP, SMTP 등)
- 암호화를 하지 않는다. (VPN 보다 빠르다.)
- **IP를 변경하거나, 지리적으로 차단된 컨텐츠를 우회하는데 유리하다.**

## 순서

### [1] 초기화 및 인증방식 협상
1. Client와 Proxy서버간 연결을 수행한다.
2. 사용할 인증방식에 대해서 협상한다.
```text
[Version][Number of Methods][Methods...]
Version: 
    SOCKS 버전(5)
Methods: 
    00 NO AUTHENTICATION REQUIRED
    01 GSSAPI
    02 USERNAME/PASSWORD
    03-07 IANA ASSIGNED
    80-FE RESERVED FOR PRIVATE METHODS
    FF NO ACCEPTABLE METHODS
```

### [2] 인증
1. 클라이언트가 협상에 성공한 인증방식을 통해서 인증정보를 제공한다.
2. Proxy서버는 인증정보를 검증한다.`

### [3] 요청
1. 클라이언트는 Proxy서버에게 요청을 보낸다.
```text
[Version][Command][Reserved][Address Type][Address][Port]
Command:
    0x01: CONNECT
    0x02: BIND
    0x03: UDP ASSOCIATE

Address Type:
    0x01: IPv4
    0x03: 도메인 이름
    0x04: IPv6

Address: 목적지 주소

Port: 목적지 포트
```

### [4] 응답
1. Proxy서버는 요청에 대한 응답을 클라이언트에게 보낸다.
```text
[Version][Reply][Reserved][Address Type][Address][Port]
Reply:
    0x00: 성공
    0x01: 일반적인 실패
    0x02: 허용되지 않은 연결
    기타...
```