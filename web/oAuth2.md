# oAuth2
**다른 웹사이트 상의 자신들의 정보에 대해 웹사이트나 애플리케이션의 접근 권한을 부여할 수 있는 공통적인 수단으로서 사용되는, 접근 위임을 위한 개방형 표준**

- Social Login
- ResourceServer의 기능 일부분 제공

# oAuth1.0 vs oAuth 2.0
![1 0vs2 0](https://user-images.githubusercontent.com/57896918/146637775-d8f592c4-84b6-4900-9099-c62388c0654c.png)



## oAuth2의 참여자
1. **ResourceServer**<br>
자원을 보유하고 있는 서버

2. **Authorization Server**<br>
ResouceClient에게 ResourceServer에 접근하기 위한 인가 토큰을 발급해주는 서버 

3. **ResourceClient**<br>
ResourceServer에 접속해서 위임된 사용자의 정보를 가져오고자 하는 클라이언트

4. **ResourceOwner**<br>
Client가 제공하는 서버에 접속하려고하는 사용자



## oAuth2 승인 종류

### 1. Authorization Code Grant Type (권한부여 코드 승인)
Client가 Owner대신 ResourceServer에 접근하기 위해서 
사용자명, 비밀번호, AuthorizationServer에서 받은 권한 코드를 이용하여
AccessToken을 발급받는 방식

- 발급받은 accessToken은 Cleint에서 자제적으로 저장,관리
- 응답받은 토큰으로 ResourceServer와 통신 
- 대부분의 소셜미디어에서 지원하는 타입
- Access Token을 바로 클라이언트로 전달하지 않아 잠재적 유출을 방지
- RefreshToken사용 가능

### 2. Implicit Grant Type (암시적 승인)
AuthorizationCode가 필요없이 AccessToken를 바로 반환 받아 인증에 사용

- Access Token이 바로 전달되므로 만료기간을 짧게 설정하여 누출의 위험을 줄일 필요
- Refresh 사용이 불가
- 사용을 권장하지 않는 경우도 있다.

### 3. Resource Owner Password Credentials Grant Type (자원 소유자 암호 자격 승인)
Client가 Owner의 암호를 사용하여 AccessToken을 가져오는 방식

- 주로 자사시스템에서 사용시 이용된다.

### 4. Client Credentials Grant Type (클라이언트 자격 승인 타입)
클라이언트가 컨텍스트 외부에서 액세스 토큰을 얻어 특정 리소스에 접근을 요청할 때 사용하는 방식.

## 토큰 갱신과정
1. 권한서버에 accessToken 요청
2. 권한서버가 accessToken과 refreshToken 응답
3. accessToken을 통한 api 사용 및 만료시 refreshToken을 이용하여 유효성 통과시 재발급
