# DHCP (Dynamic Host Configuration Protocol)
- **TCP/IP에 필요한 자동설정을 제공해주는 프로토콜이다.**
- **복잡한 인터넷 설정을 제공하는 Server와 할당 받으려는 Client로 구분된다.**
- OSI 7 Layer에서 2계층인 DataLink Layer에서 동작한다.
- IP할당의 경우 영구 할당이 아닌 **임대** 이다.

## 장점
- 자동으로 설정하기 떄문에 일일이 관리할 필요가 없어진다.
- 자동으로 설정되기 떄문에 IP 충돌 등을 방지 할 수 있다.

## 단점
- DHCP에 전적으로 의존하고 있다.

# 제공해주는 것들
- IP주소
- SubnetMask
- Gateway IP주소
- DNS 주소


## 과정
1. Client가 DHCP서버를 찾기위해서 BroadCast를 통해서 DHCP서버를 찾는다.
2. DHCP서버는 요청한 Client에게 자신이 DHCP 서버라고 ACK를 보낸다.
3. Client가 예전에 할당받은 적 있는 IP주소를 사용해도 되는지 DHCP에게 다시 Request를 보낸다.
4. Server는 사용해도 되면 ACK를 보내주고, 이미 사용중이라면 새로운 IP주소를 포함한 설정을 넘겨준다.