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