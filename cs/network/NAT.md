# NAT(NetworkAddressTranslation)
- IPv4의 제한된 수 때문에 문제를 해결하기 위해서 개발되었다.
- PrivateIP를 PublicIP로 변환하여 공용망을 사용하게 할 수 있는 기술이다.
- IP 패킷의 TCP/UDP 포트 숫자와 소스 및 목적지의 IP 주소 등을 재기록하면서 라우터를 통해 네트워크 트래픽을 주고 받는 기술이다.
   - 라우터(NAT)는 포트포워딩을 통해서 사설IP의 디바이스와 외부인터넷을 연결한다.
- **NAT테이블에 매핑정보를 통해서, 단순히 IP와 PORT를 변경한채로 연결을 유지 할 수 있다**
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
