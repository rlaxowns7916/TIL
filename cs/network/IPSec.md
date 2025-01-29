# IPSec
- L3에서 동작
- ipv4, ipv6, G to G(Site to Site), G to E(Remote Access VPN) 모두 지원
- VPN 구현을 위해 많이 사용된다.

## Protocol
### [1] ISAKMP (Internet Security Association Key Management Protocol)
- SA (SecurityAssociation)을 생성, 관리
- **암호화 키 교환에 사용되는 프로토콜**

### [2] IPAH (Authentication Header)
- 무결성 (Integrity)와 인증(Authentication)을 제공
- 암호화 기능은 없다.
- 사용이 줄어드는 추세 (IPESP로 다 대체 가능)

### [3] IPESP (Encapsulation SecurityPayload)
- 암호화와 선택적 인증 기능(무결성 + 출처인증)을 제공
- 기밀성(Confidentiality)이 필요한 트래픽에 주로 ESP를 주로 사용
- 두가지 모드 지원
  - TunnelMode: G to G VPN에서 주로 사용, IP전체를 캡슐화
  - TransportMode: E to E || G to E VPN에 주로 사용, IP 헤더는 두고 데이터만 보호