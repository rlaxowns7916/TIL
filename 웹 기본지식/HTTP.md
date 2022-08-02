# HTTP
- **서버와 클라이언트의 통신 규격**<br>
- **헤더와 바디로 구성**
- TCP/IP 프로토콜 위에서 동작한다.

## HTTP 특징
1. Stateless
    - 확장성 측면에서 유리하다. (장점)
    - Client에서 추가적인 데이터 전송이 필요하다 (단점)
2. Client <- > Server구조 
3. Connectionless (1 Connection == 1 File)
4. 거의 모든형태의 데이터를 주고 받는 것이 가능
    - JSON,XML
    - Image,File
    - HTML,TEXT
5. Request <  -- > Response 구조
6. 캐시 가능 


## Http Method
1. GET : 리소스 조회<br>
    - 전달하고 싶은데이터는 QueryString을 통해서 전달한다.
    - Body의 경우 보통 지원하지 않는다. 
  
2. POST: 요청데이터 처리
    - Body를 통해서 서버로 데이터 처리 요청
    - 주로 신규 데이터 등록, 프로세스 처리 등에 사용
3. PUT: 리소스 대체, 해당 리소스가 없다면 수정 
    - Post와의 차이점은 리소스의 위치를 알고 있다는 것
    - 리소스를 덮어씀(전체)
    - 멱등성 보장
4. PATCH: 리소스 부분 대체
    - 리소스 부분 변경 
    - 멱등성 보장(x)
5. DELETE: 리소스 삭제

- URL은 빈칸이 허용되지않아, Escape처리가 필요함(예외처리)
  - ' ' : '+' or %20
  - '+' : %2B(ASCII '+')
  - '%' : %25
    
- encode / decode 필요

### 멱등성(Idempotent)
**몇번을 호출하더라도 같은 결과가 나와야한다.**<br>
HTTP의 메소드는 멱등성을 보장한다.(**POST와 PATCH제외**)

1. POST: 두 번 호출 시, 같은 결제가 중복해서 발생한다. (그 때마다 다른 Response를 주게 될 것)
2. PATCH: 구현에대한 표준 제약이없다.<br>그렇기 때문에 구현방식에 따라서 보장 할수도 보장하지 못할 수도 있다.

## StartLine
- Request에서의 첫번째 영역
- HttpMetod, RequestTarget, HttpVersion으로 구성되어 있다.
```text
GET /search HTTP/1.1
```
## StatusLine
- Response에서의 첫번째 영역
- HttpVersion, StatusCode, StatusText로 구성되어 있다.
```text
HTTP/1.1 404 Not Found
```

## HTTP Header 
- Http전송에 필요한 부가정보를 포함한다.
### HTTP Header 종류
1. Content-Type: 전송하는 Body의 형태
2. Content-Length: Request 혹은 Repponse 본문 데이터의 Byte 수
3. Connection: 연결상태 
4. Content-Language: 사용자와 잘어울리는 실제 언어
5. Date: 날짜
6. Host: 요청을 받는 측 (Server)에 대한 호스트 및 포트 번호
7. User-Agent: 클라이언트 소프트웨어 (브라우저,OS 등)의 정보<br>


등등이 있다.

## HTTP  Body
- 응답 및 요청에 해당하는 본문 

## 버전
### HTTP 0.9
- GET메소드만 지원
- Header가 존재하지 않음

### HTTP 1.0
- 헤더가 생김
- 여러가지 파일 타입을 전송가능 (헤더의 Content-Type)

### HTTP 1.1
- TCP 기반
- Persistent Connection (Keep-Alive)
  - 특정 시간 동안 Connection을 유지한다.
  - 이미 연결되어있는 TCP Connection을 재사용한다.
  - 오히려 성능 저하를 일으키는 경우도 있다.
  - **대량접속을 받는 서버라면 오히려 성능에 영향을 미친다.**
- Pipelining (한번에 여러개의 요청을 같이보내고 순서에맞춰 응답여러개를 받음)
  - HeadOfLine: 첫번째 순서의 요청이 오래걸리면 그 뒤의 것들이 실행되지못함
  - Header구조의 중복

### HTTP 2
**표준의 대체가 아닌 확장**
- 2015년에 만들어졌다.
- TCP 기반
- Multiplexed Stream
  - Frame단위로 분할, 및 Binary Format
  - 파싱, 전송속도의 상승
  - 오류 발생 가능성 저하
  - 프레임으로 쪼개져서 병렬으로 가기 떄문에 HOL문제의 해결
  - Binary Frame으로 전송 후 수신측에서 재조립
- Stream Prioritization
  - 리소스간 우선순위를 설정
- ServerPush
  - ex) a.html에 b.css, c.js가 있으면, a.html만보내주면 다시 또 요청할테니 미리 다 보내주는 것 
- Header Compression: 헤더의 크기를 줄여서 페이지 로드 시간 감소

### HTTP 3
**GOOGLE이 개발한 QUIC(Quick Udp Internet Connection) 이용**

- UDP로 변경
  - UDP로 속도 향상 (TCP에 비해서 Latency를 줄임)
    - 이론적으로 RTT(RoundTripTime)가 0 이다. (vs TCP 평균 4.5 RTT)
    - TCP의 Connection 수립, SSL 암호화등의 작업이 필요없기 때문이다.
  - 새로운 응용계층 프로토콜 개발 시 빠르게 배치 가능 
- 신뢰성 있는 통신을 제공한다.
  - TCP와 유사하게 ACK과 TimeOut을 통해서 확인한다.
- 보안강화 (TLS 적용)
- IP가 아닌 Connection ID로 통신을 하여 IP가 바뀌어도 기존 연결을 유지할 수 있음
- 멀티플렉싱으로 TCP의 한계인 HOL을 극복