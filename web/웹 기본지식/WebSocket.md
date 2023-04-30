# WebSocket
- 실시간 양방향 통신이다.
- StateFul 하다.
- Procol 자체의 Overhead가 낮다.
  - 한번 맺은 Connection은 계속해서 유지된다.
  - Message를 주고 받을 때마다 Connection을 맺고 닫을 필요가 없다.
- 표준 Web Protocol이다.
  - 모든 최신 WebBrowser들이 지원한다.
- 7계층에 존재하며, TCP(4계층)에 의존한다.
  - Connection을 맺을 때, HTTP의 헤더를 이용한다.
  - HTTP와 같이 80,443 Port를 이용하기 때문에, 방화벽을 통한 웹환경과 유사하게 사용가능하다.
- ws,wss로 URI Scheme을 표시한다.
  - 평문화된 Data를 전달하기 때문에, SSL/TLS 암호화가 필요하다.
- **Frame**이라는 단위로 전송된다.
  - 정해진 Message Format 규약은 없다.

## 과정
### 1. 연결 과정
- Client가 Server에 HTTP요청을 보내고, WebSocket 연결을 시도한다.
```text
<<Request>>
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==
Sec-WebSocket-Protocol: chat, superchat
Sec-WebSocket-Version: 13
Origin: http://example.com


<<Response>>
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=
Sec-WebSocket-Protocol: chat
```


#### 2. 데이터 전송
- Frame 단위로 데이터를 전송한다. (OSI 2Layer의 Framer과는 다르다.)
- Frame의 데이터는, Text, Binary 등 다양한 형태를 전송 할 수 있다. (정해진 Format이 없다.)
- 양방향으로 통신하고, 연결을 맺고 끊을 필요가 없기 때문에 전송지연시간이 줄어든다.

#### 3. 연결 종료 과정
- Client나 Server에서 연결을 종료하고 싶을 때 사용한다.
- **종료 Frame을 상대방ㄱ에게 전송하여 연경르 종료한다.** (code와 이유를 포함 할 수 있다.)
- 양쪽이 모두 종료 Frame을 주고받으면 정상적으로 연결이 종료된다.

#### 연결 종료 코드
1000: 정상적인 종료. 클라이언트와 서버 간에 연결이 성공적으로 종료되었습니다.   
1001: 원격 엔드포인트가 연결을 종료했습니다. 즉, 상대방이 연결을 끊었습니다.   
1002: 프로토콜 오류. 연결이 프로토콜 오류로 인해 종료되었습니다.   
1003: 데이터 형식이 지원되지 않습니다. 서버가 클라이언트로부터 받은 데이터를 처리할 수 없습니다.   
1004: 예약된 값. 이 코드는 현재 사용되지 않습니다.   
1005: 클로즈 프레임이 아닌 경우에도 연결이 종료되었습니다.   
1006: 예기치 않은 오류로 인해 연결이 종료되었습니다.   

