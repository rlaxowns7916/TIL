# DNS
- IP 주소와 Domain을 이어준다.
- 전화번호부와 같은 역할이다.
- NameServer는 동의어이다.

## Domain
- IP주소를 대신해서 사용하는 주소이다.
- 외우기 힘든 IP주소를 대신해 가독성 좋게 나타낼 수 있다.

## 동작과정 (Recursive Query)
1. Local DNS서버에서 IP를 먼저 찾는다.
   - Local DNS는 ISP의 DNS서버가 기본적으로 등록된다.
2. Local DNS서버에서 찾았을 경우 IP주소를 반환하고 종료한다.
3. Local DNS서버에 존재하지 않을 경우, 다른 DNS 서버와 통신을 시작한다.
4. Root DNS 서버에게 문의를하면 IP주소를 반환해 줄 수 있는 Top-Level DNS 서버의 주소를 반환해준다.
   - 세계에 13개만 존재한다. 
5. Top-Level DNS 서버에 문의한다.
    - .kr, .com 등등을 관리하는 서버이다.

## 웹 사이트 접속과정

1. Local PC의 .host파일 확인
2. DHCP 프로토콜을 통해서 자신의 IP주소, 가까운 Router주소, 가까운 DNS 서버의 IP 주소를 받아온다.
3. ARP 프로토콜을 통해서 가장 가까운 Router의 MAC주소를 알아낸다.
4. DNS 프로토콜을 통해서 접속하려는 웹사이트의 IP주소를 얻어온다.
5. TCP 3-way handshake를 통해서 Connection을 맺는다.
6. HTTP 통신을 수행한다.

# DDNS (Dynamic DNS)
- 실시간으로 DNS를 갱신하는 방법이다.
- Domain IP가 유동적인 경우에 사용된다.
  - ISP가 정기적으로 유동 IP를 회수 (사용하지 않을 때) 하고 다시 할당 (다시 사용할 때) 한다. 
  - IP가 바뀌어도, Domain은 변하지 않기 때문에 접속이 가능하다.
- 유동 IP를 사용하는 개인 사용자들의 개인서버, NAS 구축에 사용된다.
- 공유기를 통해 설정 할 수 있다.

## 왜 DDNS가 필요한가?
- DNS는 유동 IP가 변경되었다는 사실을 알아채지 못한다.
  - 이전 IP를 접속자에게 제공하여 엉뚱한 곳으로 트래픽이 갈 수 있다는 것이다.
- 주기적으로 자신의 IP를 DNS의 NameServer에게 자신의 IP를 갱신해주는 것이다.
  - DNS의 최신정보를 유지시켜 주는 것이다.
