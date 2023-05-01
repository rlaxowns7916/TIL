# URL 입력 시, 벌어지는 일

## 1. Web Browser가 도메인 명의 IP 조회
1. 캐시를 통한 조회를 수행한다.
   1. 웹 브라우저 캐시
   2. OS 캐시
   3. Router의 로컬 네트워크 캐시
   4. 회사 네트워크 Or ISP의 캐시조회


2. ISP의 DNS서버에 DNS 쿼리를 통한 IP 조회
3. 캐시에 저장

## 2. ARP를 통한 Router MAC 주소 조회
- ARP: IP(3계층)을 MAC(2계층)으로 대응시킬 떄 사용하는 프로토콜
- Router에게 요청을 보낼 때, MAC주소는 Roueter의 것, IP는 DNS로부터 받아온 것으로 요청을 보낸다.

## 3. Web Broswer가 서버와 TCP 연결 시작
- 3way-handshake를 통하 연결
- HTTPS라면 TLS가 추가된다.

## 4. WebBrower가 HTTP 요청을 서버로 전송

## 5. Server로부터 HTTP 응답을 받고 랜더링