# IP (Internet Protocol)
- 인터넷 프로토콜의 약자 이다.
- Internet Layer (L3)에서 Host를 식별하는 식별자이다.
- Network영역과, Host영역으로 나뉘어진다.
  - Network영역의 길이를 나타내는 것이 **Subnet Mask**이다.

## IPv4
- 32Bit 이다. (8Bit * 4)
  - 192.168.1.1	2
- 전송방식으로는 UniCast, MultiCast, BroadCast가 있다.
- 헤더크기가 가변이다.

## IPv6
- 128Bit이다. (16Bit * 8)
  - 2001:0db8:85a3:0000:0000:8a2e:0370:7334
  - 모두 0일경우 ::로 대체할 수 있다.
    - ex) 2001:0db8:85a3::8a2e:0370:7334 -> 2001:0db8:85a3:0:0:8a2e:0370:7334
- 기본적으로 보안을 내장하고 있다.
- 전송 방식으로는 Unicast, MultiCast, AnyCast가 있다.
- NAT가 필요하지 않다.
- 새로운 헤더 포멧을 갖는다.
  - checksum 필드가 없다.  
  - fragment 필드가 없다.
  - 고정 헤더 길이를 갖는다.
    
### AnyCast
- 1:1 통신이다.
- 가까운 하나(1)에 전송한다.
- 효율적인 라우팅 방법이다.
  - 자신의 기준 가장 가까운 노드 (지리적 x) 에 라우팅하기 때문에 효율적이다.
  - 다량의 트래픽 볼륨, 네트워크 정체, DDos 공격에 대비 할 수 있다.

## IPv4 & IPv6 변환 기술

### 듀얼 스택 (Dual Stack)
- IPv4 와 IPv6 동시 지원

### 터널링 (Tunneling)
- IPv6패킷이 IPv4 지역을 지날 때 캡슐화 된다.
- Ipv6영역으로 들어갈 때는 역캡슐화 된다.

