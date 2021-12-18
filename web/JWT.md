# JWT(JsonWebToken) 토큰
- **Json포맷을 이용하여, 사용자에 대한 속성을 저장하는 Claim 기반의 WebToken**
- 토큰 자체의 정보를 사용한다.
- 주로 회원인증이나 정보전달에 사용된다.
- HttpHeader에 Authrization : Bearer ${JWT_TOKEN} 형식으로 사용된다.

## 왜 사용하는가?
- 토큰자체에 정보를 포함하기 때문에 별도의 저장소가 필요하지않다.
- 확장에 용이 (인증서버와 데이터베이스에 의존(x))
- REST서비스로 제공 가능
- Stateless하다.

## JWT의 구조

- Header, Payload, Signature 3가지로 구분된다.
- 각 부분은 Base64로 Encoding된다.(암호화 x, 같은 문자열은 항상 같은 Encoding)
- 각 부분을 구분하기 위한 구분자(.) 를 사용한다.
```json
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.(header)
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.(payload)
PEHC2lm60H5Zk0VHJjbIhthvlu55Nu7hmnRWgXTvjKk(signature)
```

### Header

**typ**과 **alg** 두가지 정보로 구성된다.

- typ: 토큰의 타입을 지정
- alg: 알고리즘 방식을 지정, Signature 검증에 사용

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload

**토큰에 사용될 정보의 조각들인 Claim이 담겨있다.**

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}
```

#### Claim

- **Payload에 포함될 정보**
- Key/Value 형태로 저장된다.

1. RegisteredClaim (이미 정의되어있는 Claim)
    - iss (토큰 발급자)
    - sub (토큰 제목)
    - aud (토큰 대상자)
    - exp (토큰 만료 시간) : 토큰이 만료되는 시간
    - nbf (토큰 활성 날짜) : 해당 날짜가 오기전까지 토큰은 활성화 되지 않음
    - iat (토큰 발급 시간) : 토큰이 발급된지 얼마나 됐는지 체크용
    - jti (JWT 토큰 식별자) : 중복방지용, 일회용으로 주로 사용

2. Public Claim (사용자 정의 클레임)
    - 공개용 정보를 위해 사용
    - **Private와의 차이점은 URL Prefix를 통해서 충돌방지를 한다는 점이다.**

3. Private Claim (사용자 정의 클레임)
    - 서버와 클라이언트 사이에 협의된 임의의 클레임


### Signature (서명)
- **Token을 인코딩하거나 디코딩할 때 사용하는 고유한 암호화 코드**

#### 과정

1. Header와 Payload를 각각 Base64로 인코딩
2. SecretKey를 이용하여 Header에 정의된 알고리즘으로 해싱
3. 다시 Base64로 인코딩

