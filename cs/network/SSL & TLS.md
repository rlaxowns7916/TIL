# SSL
- 1990년대 등장 > TLS로 대체되었다.
- TransportLayer(L4)위에서 동작한다. (정확히 OSI 7Layer의 어디라고 표현 하기 애매하다.)
  - Application과 TCP사이에서 **암호화/복호화**를 수행한다.


# TLS
- SSL 3.0 버전과 매우 유사하며, 표준화 한 버전이다.
- **TLS 1.2와 1.3을 주로 사용한다.** (1.2 이상 버전을 사용하는 것이 권장)
- **기본원리는 Server의 공개키로 암호화 하고, 개인키로 복호화**하는 방식이다.

## 주요 특징
1. 인증 
    - 통신하고 있는 서버를 신뢰할 수 있는지 알려준다.
    - CA(Certificate Authority)에 의해 서명된 인증서를 통해 서버의 신원을 확인한다.
    - 인증서를 통해서 올바른 서버와 통신하고 있는 것을 확인 가능하다.
2. 암호화
   - 대칭키 알고리즘과 비대칭키 알고리즘을 사용한다.
   - **통신을 시작할 때, 비대칭키를 통해서 대칭키를 공유하고 이후에는 대칭키를 통해서 통신한다.**
3. 무결성
   - 데이터가 변조되지 않았는지 확인한다.
   - 메세지 인증코드 (MAC)를 통해서 데이터가 변조되지 않았는지 확인한다.

### Handshake
1. ClientHello
   - Handshake의 시작
   - Client(Browser)가 Server에게 보내는 메시지
   - TLS버전, 지원가능한 암호 Suite 목록, Random Number, SNI 등을 통해서 **암호화 협상을 제안**
2. ServerHello
    - **Server가 Client가 제안한 암호 Suite중에 하나를 선택 (TLS버전, 암호 알고리즘)하고, 본인의 랜덤값을 보냄**
3. Server Certificate
   - **Server가 Client에게 인증서를 전송**
   - Client는 서버가 보내준 인증서의 유효성을 확인
   - 서버인증서는 CA(Certificate Authority)에 의해 서명된문서로, 서버 Domain이 맞는지 확인
4. ClientKey Exchange
   - 대칭키를 안전하게 교환하기 위해서, Client가 공개키 암호(DH, ECDH, RSA 등) 과정을 통해 프리마스터 시크릿(pre-master secret)을 생성 및 서버에 전송
   - 서버도 해당 정보를 바탕으로 동일한 세션 키(대칭키)를 생성.
5. ChangeCipherSpec / Finished
   - 핸드셰이크 완료 후, 대칭키 암호화를 시작한다는 신호(“Change Cipher Spec”)를 주고받음.
   - 양측에서 “Finished” 메시지를 전송하여 협상된 매개변수가 정상적으로 설정되었음을 확인.

## 대칭키 교환
### [1] RSA Key Exchange 방식 (과거 or 전통적인 방식 / TLS 1.3에서는 지원안함)
1. Server는 인증서 안에 RSA 공개키를 포함해 클라이언트에게 전달
2. Client의 Pre-master Secret 생성
   - Client가 임의의 난수(Pre-master Secret)를 생성
   - 이후 이 난수를 Server의 RSA 공개키로 암호화
3. Server의 개인키로 복호화
   - 암호화된 Pre-master Secret을 받은 Server는 RSA 개인키로 이를 복호화합니다.
4. 공유 비밀(Shared Secret) → 세션 키
   - **Pre-master Secret을 통해서 서로의 랜덤 값 등을 합쳐 대칭키를 생성**
   - 이후 대칭키로 실제 애플리케이션 데이터를 암호화, 복호화

### [2] Diffie-Hellman(ECDH) 기반 키 교환 방식 (TLS1.3부터 사용)

### 주요 암호 구성 요소
1. 대칭키 알고리즘(Symmetric Ciphers)
   - AES(Advanced Encryption Standard)
   - 빠르게 대량의 데이터를 암호화하기에 적합.
2. 비대칭키 알고리즘(Asymmetric Ciphers)
   - RSA, DH, ECDH(Elliptic Curve Diffie-Hellman) 등
   - 키 교환 및 인증서 서명에 활용.
3. 해시/메시지 인증 코드(MAC)
   - SHA-256, SHA-384 등
   - 무결성을 검증하고, 전송 데이터가 변조되지 않았는지 확인.
4. 디피-헬만(DH) 계열 키 교환 (TLS 1.3)
   - Forward Secrecy 보장: 세션 키가 유출되어도 과거 트래픽을 복호화하기 어려움.

## SNI (ServeNameIndication)
