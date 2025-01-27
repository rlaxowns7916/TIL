# L3Switch vs Router
- 둘의 차이는 거의 없지만, 주요 목적에 따라 사용한다.

## L3Switch
- 두가지 Switching 방식을 지원한다.
  - L2 Switching: MAC 주소를 기반으로 Switching
  - L3 Switching: IP 주소를 기반으로 Switching
- 내부 네트워크(LAN)용, VLAN 분할 및 고속 스위칭/라우팅에 최적.
- WAN 기능은 미약하거나 제한적.
- 비용이 비교적 낮고, 내부 트래픽 처리 성능이 우수
- ex) 사무실 내부 네트워크 VLAN 분할

## Router
- LAN-WAN 연결 및 고급 네트워크 기능(NAT, VPN, 방화벽 등)에 최적.
- 소프트웨어 기반 라우팅, 다양한 프로토콜 지원.
- 비용이 상대적으로 높지만 WAN 트래픽 관리에 필수.
- ex) 인터넷 연결, 외부 네트워크 연결