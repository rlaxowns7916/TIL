# AccessToken & RefreshToken

## AccessToken
- 일반적으로 JWT 토큰을 사용한다.
- HTTP Header(Authorization)에 값을 넣는다.
- 분실의 위험이 있기 떄문에 짧은 유효기간을 가진다.
- 사용자를 식별하는 역할을 한다.

### AccessToken이 탈취되었다면 어떻게하는가?
- JWT는 토큰 탈취를 검증하기 어렵다.
    - JWT자체가 정보를 갖고있기 때문이다.
    - Server가 유효한 Client인지 JWT 그자체 말고는 검증할 수단이 없다.
- 유효기간을 짧게 가져가고 RefershToken을 통한 주기적인 갱신으로 피해를 최소화 해야 한다.

## RefreshToken
- AccessToken이 만료되었을 때 새롭게 발급해주는 역할을 한다.
- RefreshToken을 안전하게 보관할 수 있는 클라이언트가 사용해야 한다.

### RefreshToken이 탈취되었다면 어떻게 하는가?
- 완벽한 해결책은 없다.
- 서버나 클라이언트 측의 보안 로직을 강화하는 수 밖에 없다.
- RefreshToken이 탈취되었다고 의심될 경우에는 만료시키는 방법이 필요하다.
    - Google이나 Naver에서 접속여부를 물어보는 이유가 이 것이 아닐까? 생각한다.
    - 계정 도용으로 신고된 아이디 라던가

### Refresh Token 탈취 위험성을 최소화 하기 위한 방법

#### 1. Refresh Token Rotation (RTR)
- RefreshToken을 통한 accessToken 갱신 요구가 발생하면 AccessToken과 RefreshToken 쌍을 발급한다.
- 갱신에 사용된 Token을 즉시 만료시킨다.
- RefreshToken이 아닌, 지속적인 AccessToken 탈취 시에는 막을 수 없다는 단점이 있다.

#### 2. Automatic Reuse Detection
- RefreshToken을 한번 사용하면 새로운 RefreshToken을 발급한다.
- Refresh Token이 2번 사용되면 탈취되었다고 판단한다.
- RefreshToken을 체이닝한다. (History 보관)
  - 탈취되었다고 판단이 되면 해당 체이닝에 얽혀있는 RefreshToken을 모두 만료시킨다.


### Bearer? Basic?

#### [1] Bearer
- OAuth2.0에서 자주 사용되는 스펙 (RFC 7617)
- STATELESS, 무결성을 갖는다.
- 로그인 시 Token을 부여받고, Header에 넣어서 보낸다.
- **Application의 인증 및 권한 부여에 적합하다.**

#### [2] Basic
- 가장 간단한 인증방식
- 웹 표준 (RFC 7617)
- 사용자의 ID와 Password를 Base64로 Encoding한 값을 토큰으로 사용한다.

### 어디에 저장 할 것인가?

#### 1. Storage (Local || Session)
- 응답을 받으면 Storage(Local || Session)에 저장한다.
  - 필요할 때, JavaScript로 꺼내서 동봉해서 요청을 보낸다.
  - XSS에 취약
- Cookie보다 더 많은 데이터를 저장할 수 있다.
- Client가 Expire을 별도로 관리하게된다면 따로 설정해주어야 한다. (Storage에는 만료기간이 없기 때문에)

### 2. Cookie
- HttpOnly, Secure Cookie를 통해서 XSS로부터 안전할 수 있다.
- CSRF에 취약
  - Browser가 자동으로 요청에 동봉하기 때문이다. (미미하지만 추가적인 트래픽의 발생)
  - CSRF토큰을 사용하기
  - SameSite 쿠키 속성 사용하기
  - 사용자 인증확인 (다시한번)