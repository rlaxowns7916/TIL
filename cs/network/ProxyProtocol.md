# ProxyProtocol
- TCP에서 원본 IP/Port 정보를 전달하기 위해서 사용되는 방법
  - NAT를 거치면서, 원본의 TCP 연결 정보를 잃어버리는 문제를 해결하기 위해 사용된다.
- LB와 NAT같이, 원본 Client의 IP/PORT가 유실 될 수 있는 환경에서 적합하다.
- TCP Payload에 추가되는 Header이다.
  - 최초 연결이 생성될 때만 전달된다.
  - 연결이 된 이후에는 데이터부만 전달된다.
- HTTP의 X-Forwarded-For와 같은 역할을 한다.


## Message 구조
```markdown
PROXY TCP4 [CLIENT ORIGIN IP] [NAT IP] [CLIENT ORIGIN PORT] [NAT PORT]

PROXY TCP4 192.168.0.1 203.0.113.1 12345 54321
```
- PROXY: Proxy Protocol의 시작을 알리는 키워드
- TCP4: TCP 버전 4
- CLIENT ORIGIN IP: 원본 Client의 IP
- NAT IP: NAT를 거친 후의 IP
- CLIENT ORIGIN PORT: 원본 Client의 PORT
- NAT PORT: NAT를 거친 후의 PORT

## 구조
- Client는 PROXY프로토콜을 인지하지 않아도 된다.
  - PROXY 프로토콜을 지원하는 NAT나 LB에서 PROXY프로토콜로 자동 설정이 가능하다.
  - ex) nginx 설정
    - ```text
          stream {
            server {
            # 8080 포트 오픈 , (listen 8080 proxy_protocol;) 8080포트 LISTEN, 모든 패킷이 PROXY 프로토콜로 들어올 것이라고 판단 (없다면 에러)
            listen 8080;
            
            # 트래픽 전달
            proxy_pass host.docker.internal:9090;
            
            # Proxy Protocol 헤더 자동 추가
            proxy_protocol on;        
          }
      ```

```markdown
+------------------------+
|  클라이언트            
|  Client IP: 192.168.0.1
|  Client Port: 12345    
+------------------------+
           |
           | TCP 요청
           v
+------------------------+
|  NAT 라우터            
|  NAT 변환 후           
|  Client IP: 203.0.113.1
|  Client Port: 54321    
+------------------------+
           |
           | Proxy Protocol 헤더 추가:
           | - 원본(Client) IP: 192.168.0.1
           | - 원본(Client) Port: 12345
           v
+------------------------+
|  백엔드 서버                     
|  - Client IP: 203.0.113.1     
|  - Client Port: 54321         
|  Proxy 헤더로 원본 정보 확인       
|  - 원본(Client) IP: 192.168.0.1
|  - 원본(Client) Port: 12345    
+------------------------+
```