# DHCP
- OSI 7 Layer에서 7계층인 ApplicationLayer에서 동작한다.
- Dynamic Host Configuration Protocol의 약자이다.
- Host의 IP주소와 TCP/IP 각종 설정을 자동으로 제공해주는 프로토콜을 의미한다.
  - 구성: IP주소, 서브넷 마스크, 가까운 라우터, DNS의 IP 주소
  - 할당: IP주소를 제한적으로 임시적 할당
- IP할당의 경우 영구 할당이 아닌 **임대** 이다.

## 장점
- 자동으로 설정하기 떄문에 일일이 관리할 필요가 없어진다.
- 자동으로 설정되기 떄문에 IP 충돌 등을 방지 할 수 있다.


## 단점
- DHCP에 전적으로 의존하고 있다.


## 과정
1. Client가 DHCP서버를 찾기위해서 BroadCast를 통해서 DHCP서버를 찾는다.
2. DHCP서버는 요청한 Client에게 할당할 IP주소를 반환하여 준다.
3. Client가 응답받은 IP주소를 사용해도 되는지 DHCP에게 다시 Request를 보낸다.
4. Request받은 IP주소가 사용 가능하면 DHCP서버가 ACK를 넘겨준다.