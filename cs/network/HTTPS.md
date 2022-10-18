# HTTPS
- HTTP에 암호화를 더한 것이다.
  - HTTP 프로토콜 상위에 TLS 암호화를 구현한 것이다.
  - TLS는 SSL이 더욱 발전한 것이다.
- HTTP 자체를 암호화 하는 것이 아니다.
  - HTTP Body를 암호화한다.
  - HTTP Header는 암호화의 대상이 아니다.
- 443번 포트를 사용한다.
- Hyper Text Transfer Protocol Secure 이다.
- 대칭키 / 공개키 방식 모두 사용한다.

## Key
- Session Key를 공유한다.
  - 세션 Key는 대칭 Key 이다.
  - 세션 Key는 대칭 Key 데이터 암호화에 사용한다.
  - 대칭 Key이기 떄문에, 속도에서 이점을 누릴 수 있다.
- SessionKey 공유에 공개 Key를 사용한다.
  - 비대칭 Key이기 떄문에 보안상의 이점을 누릴 수 있다.

## SSL 인증서
- 공인받은 제 3자가 인증된 서버라는 것을 보장하는 것이다.
- CA 정보, 서버 정보(도메인, 서버 공개키)로 구성된다.
- Brower는 공인 CA목록들을 가지고 있다.

## 인증서의 내용물
1. 인증서 소유자의 이름
2. 인증서 **소유자의 공개키**
3. 인증서의 유효기간
4. 고유한 UID
5. 인증서 모든 값의 Signature

## 과정
1. Client (브라우저)가 Server로 연결을 시도한다.
2. Server는 SSL 인증서를 Client에게 넘겨준다.
3. Client는 인증서의 유효성을 검증하고, 넘겨받은 인증서안의 공개 Key로 세션 Key를 암호화하여 Server로 전송한다.
4. 서버는 개인 Key를 통해서 암호화된 세션 Key를 복호화 한다.
5. Client와 Server는 같은 세션 Key로 데이터를 암호화하여 통신한다.