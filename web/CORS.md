# CORS (Cross-Origin-Resource-Sharing)

- **클라이언트와 서버의 Origin이 다를 때 발생하는 이슈**
- **Same-Origin-Policy 때문에 Origin이다르면 정보를 받아 올 수 없다.**
- 서버에서 접근권한을 허용해줘야 이용가능하다.

## Same-Origin-Policy
- **프로토콜, 포트, 도메인이 같아야 Same Origin**
- 문서나 스크립트가 다른 출처(origin)에서 가져온 리소스와 상호작용하는 것을 제한하는 보안방식,
공격받을 수 있는 잠재적인 경로를 줄이는게 목적이다.


## CORS 동작과정

### 1. Preflight Request
**동작과정**
- **예비요청과 본요청으로 나누어서 서버로 전송** (예비요청을 통한 CORS 위반 확인)
- **HTTP Method중 Option 메소드를 사용 (예비요청)**
- 예비요청 통과시 본요청 발송
- 매번 예비요청을 던지는걸 막기위해 캐시적용이 가능하다.

### 2. Simple Request
**동작 과정**
- Access-Control-Allow-Origin Header를 통해서 검증
- Client요청에 Server는 Access-Control-Allow-Origin을 응답으로 내려준다.
- 브라우저는 Server가 내려준 값을 통해서  CORS위반인지 아닌지를 검사한다.

**동작 조건**
- 요청은 GET,HEAD,POST 중 하나여야한다.
- Accept, Accept-Language, Content-Language, Content-Type, DPR, Downlink, Save-Data, Viewport-Width, Width 외의 다른 헤더를 사용 불가
- Content-Type은 application/x-www-form-urlencoded, multipart/form-data, text/plain만 허용


### 3. Credentialed Request
- **인증된 요청을 사용하는 방법**
- 쿠키, 인증과 관련된 정보를 담을 수 있게 해주는 요청이다.

**옵션**
- same-origin: 같은 출처간 요청만 인증정보를 담을 수 있다.
- include: 모든 요청에 인증 정보를 담을 수 있다.
- omit: 모든 요청에 인증정보를 담지 않는다.

**동작 조건**
- *이 아닌 명시적으로 URL을 정의해주어야한다.
- 서버는 응답헤더에 Access-Control-Allow-Credentials: true가 존재해야한다.
