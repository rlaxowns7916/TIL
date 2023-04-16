## Private Network란?
- 외부의 접근을 차단하는 Network이다.
- 매우 비싸다.

## VPN (Virtual Private Network)
- 저렴하다.
- PrivateNetwork의 기능을 소프트웨어적으로 (Virtual) 풀어낸 것이다.
- Router가 VPN기능을 제공해야하며, 이 기능을 제공하는 Rotuer를 SG(Secure Gateway)라고 부른다.
- **사용자는 VPN Client를 사용하며, VPN Router를 통해 터널링이 된다.**
  - VPN을 사용할시 총 2개의 IP를 사용하게 된다.
    - VPN으로부터 할당받은 IP
    - ISP로부터 할당받은 IP

### 왜 Virtual인가?
- Virtual NIC(Network Interface Card)가 생긴다.
- VPN으로부터 할당받은 새로운 IP가 이 NIC를 통해서 나간다.