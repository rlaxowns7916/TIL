# DNS
- IP 주소와 Domain을 이어준다.
- 전화번호부와 같은 역할이다.
- NameServer는 동의어이다.
- 하나의 Domain에 여러개의 IP가 지정 될 수 있다.
  - 기본적으로 Round-Robin으로 지정된다.
  - Weighted(가중치)-Round-Robin, Sticky-RoundRobin도 있다. 
- Tree구조의 분산 데이터베이스이다.

## Domain
- IP주소를 대신해서 사용하는 주소이다.
- 외우기 힘든 IP주소를 대신해 가독성 좋게 나타낼 수 있다.

## 동작과정 (Recursive Query)
1. Local DNS Cache에서 IP를 먼저 찾는다. 
   - Local DNS Cache에서 찾았을 경우 IP주소를 반환하고 종료한다.
2. ISP Resolver DNS서버에게 문의한다.
    - ISP에 의해 제공되며, Network환경에 기본적으로 설정된다. (DHCP)
3. ISP Resolver DNS서버에 존재하지 않을 경우, ROOT DNS 서버에게 문의한다.
4. Root DNS서버는 IP주소를 반환해 줄 수 있는 TLD(Top-Level-DNS)서버의 주소를 반환해준다.
   - Root DNS 서버는 세계에 13개만 존재한다. 
5. TLD(Top-Level-DNS)서버는 해당 Domain에 권한이 있는 **NameServer**를 반환해준다.
    - TLD서버는 .kr, .com 등등을 관리하는 서버이다.
6. NameServer는 해당 Domain에 대한 IP주소를 반환해준다.
7. Local에서 Cache에 저장하고, IP주소를 반환한다.

## 웹 사이트 접속과정
1. Local PC의 .host파일 확인
2. DHCP 프로토콜을 통해서 자신의 IP주소, 가까운 Gateway주소, 가까운 DNS 서버의 IP 주소를 받아온다.
3. ARP 프로토콜을 통해서 가장 가까운 Router의 MAC주소를 알아낸다.
4. DNS 프로토콜을 통해서 접속하려는 웹사이트의 IP주소를 얻어온다.
5. TCP 3-way handshake를 통해서 Connection을 맺는다.
6. HTTP 통신을 수행한다.

# DNS의 계층구조
- 한대의 서버로 DNS가 동작할리는 없다.
- DNS는 계층구조 (Tree구조)로 되어있으며, 상위 NS가 하위 NS의 위치를 알고있으며 root-> leaf로 내려가는 구조이다.

```text
example1   .    example2     .    com      .     (생략)
(sub)        (second-level)    (top-level)       (root)
```
- 맨 뒤에 .(root)가 생략되있는 구조이다.
- root부터 탐색해간다. 
  - root는 전세계에 13개만 존재한다.
- top-level에는 .com이나, .kr 같은 도메인이 포함된다.
- second-level부터는 그뒤의 sub-domain을 책임진다.

<img width="657" alt="스크린샷 2022-10-18 오후 10 59 00" src="https://user-images.githubusercontent.com/57896918/196460530-c44fe2fe-b409-42c1-89bc-8d864d07e397.png">

## Record Type
| Type  | Description                         |
|:------|:------------------------------------|
| A     | 도메인 이름, 호스트명을 IPV4에 매핑              |
| AAAA  | 도메인 이름, 호스트명을 IPV6에 매핑              | 
| CNAME | A레코드의 별칭을 매핑                        |
| NS    | 도메인 네임서버 매핑  (Domain Query를 하는데 사용) |
| MX    | 메일 서버 매핑                            |
| PTR   | 역방향 질의 (IP -> Domain)               |


## DNS Cache
- Domain이름, IP주소, TTL, 레코드 타입을 저장한다.
- Browser, OS, Router, DNS서버 등 여러곳에 존재한다.


## .host 파일
- **OS에서 Domain과 IP를 매핑하는 Local 설정파일**
- .host 파일에 등록된 Domain은 DNS서버를 거치지 않고, 바로 IP를 찾아간다.

## NameServer
- 특정 Domain에 대한 정보를 관리 및 제공하는 서버이다.
  - Domain 등록 시 설정하는 구체적인 서버이다.
  - Domain 소유자가 설정한 권한이 있는 서버이다.
- Domain의 IP주소 정보나 다른 레코드 (A, MX, NS) 등을 저장한다.

## DNS - TTL
- DNS서버에서 설정하고 관리하는 값이다.
- DNS 레코드의 유효시간을 나타내는 값이다. (초 단위)
- Client나 중간에 위치한 Cache서버 (혹은 클라이언트 단의 브라우저, 어플리케이션 ...) 가 얼마나 오랫동안 Cache에 저장해 놓을지 결정한다.
  - TTL이 만료되면, 캐시서버는 DNS 레코드를 제거하고, 다시 DNS서버로 쿼리를 보내서 갱신한다.
  - DNS서버의 부하를 줄이는 것과, 데이터의 실시간성에서의 트레이드오프이다.

### DNS레코드의 실시간성을 유지하고 싶다면?
1. DNS 서버의 TTL 설정을 줄인다. (내가 관리할 수 있다면)
2. Client단의 DNS Cache를 Disable 시킨다.
   - 하지만 중간에 Cache서버가 존재한다면 동작하지 않을 것이다.

# DDNS (Dynamic DNS)
- 실시간으로 자신의 변경되는 IP와 도메인을 매칭 시키는 방법이다.
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

## 동작 과정
1. IP 주소 감지
    - DDNS 클라이언트(로컬 장치 또는 라우터)가 현재 IP 주소를 감지
2. DDNS 서버에 업데이트 요청
   - IP 주소가 변경되었음을 DDNS 서버에 알리고, 새 IP 주소를 등록
3. DNS 레코드 업데이트:
   - DDNS 서버는 새 IP 주소를 기반으로 도메인 이름의 DNS 레코드를 업데이트

## 설정방법
1. Router 설정
    - Router의 DDNS 설정을 통해 사용
2. 전용 Client 사용
    - DDNS 서비스 제공자가 제공하는 전용 소프트웨어 설치
    - IP주소 변경시 자동으로 DDNS 서버를 업데이트
3. Server 또는 PC에 직접 설정
   - DDNS스크립트 작성 > API를 통해서 DDNS서버에 업데이트