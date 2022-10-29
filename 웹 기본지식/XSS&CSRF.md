# XSS & CSRF

## XSS (Cross-Site-Scripting)
- 사용자를 대상으로 한 공격이다.
- JS를 통해서 의도치않은 동작을 실행시키게 한다.
- 게시판, 웹 메일등에서 발생 할 수 있다.

### 1. Reflected XSS
- 주로 URL 파라미터를 통한 GET 요청 (with QueryString)을 통해서 발생한다.
  - 서버가 필터링을 거치지않고, 받은 QueryString에 따른 스크립트를 응답에 담아서 전송할 때 발생한다.
- 브라우저 자체에서 차단하는 경우가 많다.
- 사용자에게 악성 스크립트가 포함된 URL을 클릭하게하여, 악성 스크립트를 실행시킨다.
  - Reflect라는 말 그대로 사용자의 QueryString요청 (악의적인 스크립트가 포함된)을 그대로 리턴해주고 
    그로인해 스크립트가 실행된다.

### 2. Stored XSS
- 가장 위험한 XSS 공격이다.
- 서버상에 악성 스크립트가 저장된 형태이다.
- SQL Injection 같이, 평문이 아닌 스크립트를 서버에서 검증없이 받을 때 발생한다.
- 서버 (DB)에 저장된 악성스크립트가 그대로 사용자에게 전해지고, 실행되게 된다.
- HTML5에서는 Inner HTML에서의 Scrit실행을 막아두었다.


### 방지책
- Secure Coding
- Cookie에 보안 관련 정보를 담지 않기
  - http-only cookie
  - secure cookie
- 입력값의 치환 (태그를 다른 문자로 변경)
- Front에서 InnerHTML을 사용하지않는다.
  - textContent
  - InnerText

## CSRF (Cross-Site-Request-Forgery)
- 사용자가 의도하지 않은채로, 해커의 의도한 행위를 하게되는 것이다.
  - 의도치 않게 서버를 공격한다.
- 서버가 사용자를 신뢰하기 때문에 발생한다.
  - 로그인되어 있는 상태로 서버에 비정상적인 명령을 내리기 때문이다.

### 조건
1. 로그인이 되어있어야 한다.
2. 해커가 제공한 사이트에 접속해야한다.


### 방지책
- Referrer를 통한 도메인 일치 검증 
  - Referrer를 보내지 안흔 사용자에게는 이방법을 사용할 수 없다. (요즘엔 거의 없다.)
- Security Token 사용
  - 요청, 세션 마다 Token을 서버에서 발급하고 Token의 존재유무를 확인한다.
  - 잠재적인 CSRF 위협을 방지한다.