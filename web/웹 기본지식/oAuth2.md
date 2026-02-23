# OAuth 2.0 / OAuth 2.1 핵심 정리

OAuth는 “로그인” 자체의 표준이라기보다, **리소스 소유자(Resource Owner)가 클라이언트(Client)에게 리소스 접근 권한을 위임**하는 표준입니다.

- OAuth 2.0: RFC 6749 (Authorization Framework), RFC 6750 (Bearer Token)
- OAuth 2.1: 2.0의 모범사례를 통합한 개정(진행/정리 단계에서 널리 채택)
  - 핵심 변화: **Implicit Grant 제거**, **PKCE 기본화**, 취약한 패턴 정리

---

## 1. 참여자(roles)와 기본 용어

- Resource Owner
  - 최종 사용자(권한을 “부여”하는 주체)
- Client
  - 사용자를 대신해 API를 호출하려는 앱(웹/모바일/서버)
- Authorization Server (AS)
  - 사용자 인증/동의(consent)를 받고 토큰을 발급
- Resource Server (RS)
  - Access Token을 검증하고 리소스를 제공(API 서버)

토큰 종류:
- Access Token
  - RS 호출에 사용(보통 만료 짧음)
- Refresh Token
  - Access Token 재발급에 사용(보안 요구사항이 더 큼)

---

## 2. “OAuth 로그인”과 “인증(Authentication)”의 구분

많은 서비스에서 “소셜 로그인”이라고 부르는 흐름은 실제로는
- **OAuth(권한 위임)** +
- **OIDC(OpenID Connect: ID Token을 통한 인증)**

이 결합 형태인 경우가 많습니다.

- OAuth만으로는 “사용자가 누구인지”를 표준 방식으로 증명하지 못할 수 있습니다.
- “로그인”이 목적이면 OIDC(Authorization Code + PKCE + ID Token)를 고려합니다.

---

## 3. 권장 플로우: Authorization Code + PKCE

### 3.1 왜 PKCE가 필요한가?

Authorization Code는 브라우저 리다이렉트로 전달되기 때문에,
중간에서 code를 가로채는 공격(code interception)에 대비가 필요합니다.

PKCE(Proof Key for Code Exchange)는
- 클라이언트가 만든 `code_verifier`(비밀값)와
- 그 해시인 `code_challenge`

를 이용해 “이 code를 요청한 클라이언트가 토큰 교환도 수행한다”를 증명합니다.

### 3.2 ASCII 시퀀스 다이어그램

```
[User/Browser]        [Client]                [Auth Server]              [Resource Server]
     |                   |                          |                           |
     |  (1) /authorize    |------------------------->|                           |
     |      + code_challenge (PKCE)                  |                           |
     |<-------------------|  (2) login/consent UI    |                           |
     |  (3) redirect with code --------------------->|                           |
     |                   |                          |                           |
     |                   |  (4) /token + code + code_verifier ------------------>
     |                   |<-------------------------|   access_token(+refresh)  |
     |                   |                          |                           |
     |                   |  (5) API call: Authorization: Bearer <access_token>  |
     |                   |----------------------------------------------------->|
```

운영 팁:
- SPA/모바일도 현재는 **Authorization Code + PKCE**가 표준 선택지입니다.
- Redirect URI는 정확히 등록하고, 와일드카드/느슨한 매칭을 피합니다.

---

## 4. 기타 Grant Types (실무에서 자주 쓰는 것 위주)

### 4.1 Client Credentials

- 사용자 컨텍스트가 아닌 **서버-서버 통신**에 사용
- Refresh Token은 보통 사용하지 않음
- 권한은 “사용자 권한”이 아니라 “클라이언트 자체의 권한(서비스 계정)”으로 이해

```
Client -> /token (grant_type=client_credentials) -> access_token
Client -> API (Bearer access_token)
```

### 4.2 Device Authorization Grant (Device Code)

입력 장치가 제한된 환경(스마트 TV 등)에서 사용.

- 기기에서 user_code를 보여주고
- 사용자는 별도 브라우저에서 인증/동의

---

## 5. 권장하지 않는(또는 폐기된) 방식

### 5.1 Implicit Grant (암시적 승인)

- 브라우저 프래그먼트 등에 토큰이 노출될 수 있어 위험
- OAuth 2.1에서 제거되는 방향
- 과거 SPA에서 사용했지만, 현재는 **Authorization Code + PKCE**로 대체

### 5.2 Resource Owner Password Credentials (ROPC)

- 사용자의 비밀번호를 클라이언트가 직접 받는 모델
- 피싱/유출/권한 위임 철학 위배
- 레거시/1st-party 환경에서도 점점 지양

---

## 6. 토큰 보안/운영 체크리스트

### 6.1 Access Token 형식: JWT vs Opaque

- JWT
  - 장점: RS에서 로컬 검증 가능(서명 검증)
  - 단점: 폐기/강제 로그아웃이 어렵고, 클레임 설계가 복잡
- Opaque
  - 장점: 중앙 인트로스펙션/세션 관리가 쉬움
  - 단점: RS가 검증을 위해 AS/인트로스펙션 호출이 필요할 수 있음

### 6.2 Refresh Token 운용

- Refresh Token Rotation(회전) + 재사용 탐지(reuse detection) 고려
- 모바일/브라우저 저장소 보안(예: Secure Enclave/Keychain, httpOnly cookie 전략 등) 설계
- 유출 사고를 상정한 폐기/세션 관리(토큰 블랙리스트, 세션 테이블 등)

### 6.3 Redirect URI / state / nonce

- `state`는 CSRF 방지에 필수
- OIDC를 쓴다면 `nonce`로 재사용 공격 방지
- Redirect URI는 정확히 고정(스킴/호스트/경로)하고, 임의 파라미터 허용을 최소화

### 6.4 Scope 설계

- scope는 “권한의 최소 단위”
- 과도한 scope를 발급하지 말고, 서비스 내 권한 모델과 매핑을 명확히

---

## 7. 토큰 갱신(Refresh) 기본 흐름

```
(1) Client -> /token (grant_type=authorization_code) -> access_token + refresh_token
(2) Client -> API (Bearer access_token)
(3) access_token 만료
(4) Client -> /token (grant_type=refresh_token) -> 새 access_token(+새 refresh_token)
```

---

## 참고
- RFC 6749: The OAuth 2.0 Authorization Framework
- RFC 6750: The OAuth 2.0 Authorization Framework: Bearer Token Usage
- OAuth 2.1 Draft / Best Current Practice(BCP) 문서들
- OpenID Connect Core 1.0 (로그인 목적일 때)
