# WebSocket
## 개요
웹소켓(WebSocket)은 단일 TCP 연결을 통한 실시간 양방향 전이중(full-duplex) 통신을 제공하는 통신 프로토콜
- 실시간 양방향 통신이다.
- StateFul 하다. (연결 상태를 유지)
- Procol 자체의 Overhead가 낮다.
  - 한번 맺은 Connection은 계속해서 유지된다.
  - Message를 주고 받을 때마다 Connection을 맺고 닫을 필요가 없다.
- 표준 Web Protocol이다. (RFC 6455에 정의)
  - 모든 최신 WebBrowser들이 지원한다.
  - 지원안하는 경우도있는데, SockJS를 통해서 비슷하게 사용가능하게 한다.
- 7계층에 존재하며, TCP(4계층)에 의존한다.
  - Connection을 맺을 때, HTTP의 헤더를 이용한다.
  - HTTP와 같이 80,443 Port를 이용하기 때문에, 방화벽을 통한 웹환경과 유사하게 사용가능하다.
- ws,wss로 URI Scheme을 표시한다.
  - 평문화된 Data를 전달하기 때문에, SSL/TLS 암호화가 필요하다.
- **Frame**이라는 단위로 전송된다.
  - 정해진 Message Format 규약은 없다.
- Stomp(Simple-Text-Oriented-Messaging-Protocol) 프로토콜도 존재한다.
  - 미리 정의된 프로토콜 형식이다. 
  - Message에 대한 가공 및 처리가 쉽다.
  - 여러 Client간의 Message 전달이 쉽다.

## 웹소켓 스펙 핵심 정리

### 핵심 특징
1. **단일 TCP 연결**: HTTP와 달리 한 번 연결을 맺으면 계속 유지
2. **낮은 지연시간**: 헤더 오버헤드가 적어 실시간 애플리케이션에 적합
3. **양방향 통신**: 클라이언트와 서버 모두 언제든지 메시지 전송 가능
4. **크로스 도메인 통신**: 웹소켓은 기본적으로 Same-Origin 정책에서 벗어나 있음
5. **텍스트/바이너리 데이터 지원**: 다양한 형식의 데이터 전송 가능

### 웹소켓 프레임 구조
웹소켓 프로토콜은 데이터를 프레임(Frame)이라는 단위로 전송

```
0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-------+-+-------------+-------------------------------+
|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
|N|V|V|V|       |S|             |   (if payload len==126/127)   |
| |1|2|3|       |K|             |                               |
+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
|     Extended payload length continued, if payload len == 127  |
+ - - - - - - - - - - - - - - - +-------------------------------+
|                               |Masking-key, if MASK set to 1  |
+-------------------------------+-------------------------------+
| Masking-key (continued)       |          Payload Data         |
+-------------------------------- - - - - - - - - - - - - - - - +
:                     Payload Data continued ...                :
+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
|                     Payload Data continued ...                |
+---------------------------------------------------------------+
```

- **FIN(1비트)**: 현재 프레임이 메시지의 마지막 부분인지 여부
- **RSV1,2,3(3비트)**: 예약 비트, 확장에 사용
- **Opcode(4비트)**: 프레임의 종류
  - 0x0: 연속 프레임
  - 0x1: 텍스트 프레임 
  - 0x2: 바이너리 프레임
  - 0x8: 연결 종료
  - 0x9: Ping
  - 0xA: Pong
- **MASK(1비트)**: 페이로드 데이터가 마스킹 되었는지 여부
- **Payload length(7bit)**: 페이로드 데이터의 길이
- **Masking-key(4)**: 마스킹된 데이터를 해독하는 키
- **Payload Data**: 실제 데이터

### Packet 예시
```text
00000000  81 A4 12 34 56 78 41 71  18 3C 18 50 33 0B 66 5D  |...4VxAq.<.P3.f]|
00000010  38 19 66 5D 39 16 28 1B  22 17 62 5D 35 57 71 5C  |8.f]9.(."b]5Wq\|
00000020  37 0C 18 3E 1E 1D 7E 58  39 78                     |7..>..~X9x    |


81 -> 10000001 (FIN=1, RSV=000, Opcode=0001)
A4 -> 10100100 (Mask=1, Payload length=36 )
12 34 56 78 -> Masking-key
나머지 -> MaskingPayload (Stomp  등등)
```

### 보안 고려사항
1. **항상 wss:// 사용**: 민감한 데이터는 반드시 TLS/SSL을 통해 암호화
2. **Origin 검증**: 서버에서 연결 요청의 Origin 헤더를 확인하여 신뢰할 수 있는 출처인지 검증
3. **인증**: 웹소켓 연결 전 사용자 인증 필요
4. **입력 검증**: 모든 수신 메시지에 대한 검증 필수
5. **Rate Limiting**: DoS 공격 방지를 위한 연결 제한 및 메시지 빈도 제한

## 통신 과정

### 1. 연결 과정 (Handshake)
- Client가 Server에 HTTP요청을 보내고, WebSocket 연결을 시도한다.
```text
<<Request>>
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==
Sec-WebSocket-Protocol: v10.stomp, v11.stomp, v12.stomp
Sec-WebSocket-Version: 13
Origin: http://example.com


<<Response>>
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=
Sec-WebSocket-Protocol: v12.stomp
```

#### 핵심 헤더 설명
- **Upgrade**: WebSocket으로 프로토콜 업그레이드 요청
- **Connection**: Upgrade 헤더를 사용하기 위한 필수 헤더
- **Sec-WebSocket-Key**: 무작위 난수 (WebSocket프로토콜을 서버가 이해했는지 확인하기 위한 목적)
- **Sec-WebSocket-Protocol**: 사용하려는 서브프로토콜 명시 (쉼표로 구분)
  - Client는 자신이 원하는 sub-protocol 목록을 송신한다.
  - Server는 Client가 제시한 목록 중 하나를 골라서 응답한다.
- **Sec-WebSocket-Version**: 웹소켓 프로토콜 버전
- **Sec-WebSocket-Accept**: 클라이언트 키에서 파생된 해시, 웹소켓 표준에 맞게 생성 (클라이언트에서 요청에 대한 응답인지 확인하기 위함)

### 2. 데이터 전송
- Frame 단위로 데이터를 전송한다.
- Frame의 데이터는, Text, Binary 등 다양한 형태를 전송 할 수 있다. (정해진 Format이 없다.)
- 양방향으로 통신하고, 연결을 맺고 끊을 필요가 없기 때문에 전송지연시간이 줄어든다.

#### 프레임 타입
- **텍스트 프레임(0x01)**: UTF-8 인코딩된 텍스트 데이터
- **바이너리 프레임(0x02)**: 바이너리 데이터
- **컨트롤 프레임**: 연결 관리에 사용
  - **Close 프레임(0x08)**: 연결 종료 요청
  - **Ping 프레임(0x09)**: 연결이 살아있는지 확인
  - **Pong 프레임(0x0A)**: Ping에 대한 응답

### 3. 연결 종료 과정
- Client나 Server에서 연결을 종료하고 싶을 때 사용한다.
- **종료 Frame을 상대방에게 전송하여 연결을 종료한다.** (code와 이유를 포함 할 수 있다.)
- 양쪽이 모두 종료 Frame을 주고받으면 정상적으로 연결이 종료된다.

#### 연결 종료 코드
- **1000**: 정상적인 종료. 클라이언트와 서버 간에 연결이 성공적으로 종료되었습니다.
- **1001**: 원격 엔드포인트가 연결을 종료했습니다. 즉, 상대방이 연결을 끊었습니다.
- **1002**: 프로토콜 오류. 연결이 프로토콜 오류로 인해 종료되었습니다.
- **1003**: 데이터 형식이 지원되지 않습니다. 서버가 클라이언트로부터 받은 데이터를 처리할 수 없습니다.
- **1004**: 예약된 값. 이 코드는 현재 사용되지 않습니다.
- **1005**: 클로즈 프레임이 아닌 경우에도 연결이 종료되었습니다.
- **1006**: 예기치 않은 오류로 인해 연결이 종료되었습니다.
- **1007**: 유효하지 않은 데이터 타입으로 인한 종료
- **1008**: 정책 위반으로 인한 종료
- **1009**: 너무 큰 메시지로 인한 종료
- **1010**: 클라이언트가 필요한 확장을 서버가 제공하지 않아 종료
- **1011**: 서버 내부 오류로 인한 종료
- **1015**: TLS 핸드셰이크 실패로 인한 종료

## 하트비트(Heartbeat)와 핑/퐁 매커니즘
- 웹소켓 연결은 유휴 상태에서도 활성 상태를 유지해야 한다.
- 실제 연결이 되었는지 주기적으로 확인하는 매커니즘이다.

1. **핑/퐁(Ping/Pong)**: 서버가 클라이언트에게 주기적으로 Ping 프레임을 보내고, 클라이언트는 Pong으로 응답
2. **타임아웃 처리**: 일정 시간 응답이 없으면 연결 종료
3. **중간자(Proxy) 고려**: 많은 프록시는 유휴 연결을 일정 시간 후 종료함

## 웹소켓과 HTTP 비교

| 특성 | WebSocket | HTTP |
|------|-----------|------|
| 연결 | 지속적인 연결 | 요청-응답 후 연결 종료 (HTTP/1.1의 Keep-Alive는 제한적) |
| 통신 방향 | 양방향 (Full-Duplex) | 단방향 (클라이언트 요청 → 서버 응답) |
| 오버헤드 | 초기 핸드셰이크 후 낮음 | 매 요청마다 헤더 필요 |
| 실시간성 | 높음 | 낮음 (폴링 방식 사용 시) |
| 프로토콜 | ws://, wss:// | http://, https:// |
| 상태 | Stateful | Stateless |
| 브라우저 지원 | IE 10+, 모던 브라우저 | 모든 브라우저 |

## 주요 사용 사례
- **채팅 애플리케이션**: 실시간 메시지 교환
- **실시간 대시보드**: 지속적인 데이터 업데이트
- **협업 도구**: 공동 문서 편집, 화이트보드
- **게임**: 실시간 멀티플레이어 게임
- **금융 앱**: 실시간 주식 시세, 거래 업데이트
- **IoT**: 센서 데이터 실시간 모니터링

## STOMP 프로토콜 상세
- STOMP(Simple Text-Oriented Messaging Protocol)는 웹소켓 위에서 동작하는 메시징 프로토콜로, 구독 기반의 발행/구독 패턴을 제공
  - 구현되어있는 라이브러리가 많기때문에, 사용하기 편리하다.
  - 상태관리, 재연결 등을 제공한다.
- https://stomp.github.io/

### STOMP 프레임 구조
```
COMMAND
header1:value1
header2:value2

Body^@
```
- 모든 Frame은 COMMAND로 구분
- ^@는 프레임의 끝을 나타내는 특수 문자 (ASCII 0)
- header와 body는 \n으로 구분

### 주요 STOMP 명령어
| COMMAND      | 설명                                                                 |
|--------------|----------------------------------------------------------------------|
| `CONNECT`    | 클라이언트가 서버에 연결을 요청할 때 사용. STOMP 1.0/1.1 호환용.      |
| `STOMP`      | `CONNECT`와 동일한 목적. STOMP 1.2 이상에서는 `STOMP`를 권장.         |
| `CONNECTED`  | 서버가 클라이언트의 연결 요청을 수락했음을 알리는 응답.              |
| `SEND`       | 클라이언트가 서버로 메시지를 전송할 때 사용.                         |
| `SUBSCRIBE`  | 클라이언트가 특정 destination을 구독할 때 사용.                      |
| `UNSUBSCRIBE`| 구독을 취소할 때 사용.                                               |
| `ACK`        | 클라이언트가 메시지를 정상적으로 처리했음을 서버에 알릴 때 사용.      |
| `NACK`       | 클라이언트가 메시지 처리를 실패했음을 알릴 때 사용. (STOMP 1.1 이상) |
| `BEGIN`      | 트랜잭션 시작. 트랜잭션 ID 필요.                                      |
| `COMMIT`     | 트랜잭션 커밋. 트랜잭션 ID 필요.                                      |
| `ABORT`      | 트랜잭션 롤백. 트랜잭션 ID 필요.                                      |
| `DISCONNECT` | 클라이언트가 서버와의 연결을 종료할 때 사용.                          |
| `MESSAGE`    | 서버가 구독자에게 메시지를 전달할 때 사용.                            |
| `RECEIPT`    | 클라이언트 요청에 대해 서버가 receipt-id를 기반으로 응답할 때 사용.   |
| `ERROR`      | 서버가 에러 상황을 클라이언트에게 전달할 때 사용.                    |


## 스케일링 고려사항
대규모 애플리케이션에서 웹소켓을 사용할 때 고려해야 할 점:

1. **연결 상태 관리**: 서버가 다수의 동시 연결을 처리할 수 있어야 함
2. **클러스터링**: 서버 간 연결 및 메시지 공유를 위한 메커니즘 필요
3. **로드 밸런싱**: 웹소켓 연결은 지속적이므로 일반적인 라운드 로빈 방식이 적합하지 않음
   - Sticky Sessions 또는 IP 기반 라우팅 사용
4. **메시지 브로커**: RabbitMQ, Kafka, Redis 등을 통한 메시지 분배
5. **재연결 전략**: 네트워크 문제 발생 시 클라이언트 재연결 메커니즘

## 참고 자료
- [WebSocket API (MDN)](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)
- [RFC 6455 - WebSocket 프로토콜](https://datatracker.ietf.org/doc/html/rfc6455)
- [STOMP 프로토콜 명세](https://stomp.github.io/stomp-specification-1.2.html)
