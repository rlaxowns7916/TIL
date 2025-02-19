# 공개키 암호화 vs 대칭키 암호화

## 대칭키 암호화
- 암호화 및 복호화에 하나의 키만 사용한다.
- 빠른 속도를 자랑한다.
- 키 배송문제가 있기 떄문에 키 탈취 위험성이 있다.
- DES,AES,SEED 등이 있다.

### 대칭키 키 교환 방식
1. PSK (Pre-Shared Key: 사전공유 키)
   - Key를 미리 공유하고 사용
   - 일반적으로 오프라인으로 전달하는 방법을 사용한다.
2. KDC (Key Distribution Center)
   - 중앙 서버가 Client와 Server에게 안전한 방식으로 Key를 전달한다.
   - 보안 Protocol에서 주로 사용된다.
3. Diffie-Hellman Key Exchange (DH)
   - 수학적 공식을 통해 직접 Key를 생성하고 공유하는 방식
   - ex) HTTPS, VPN, TLS


## 공개키 암호화 (비대칭키)
- Public Key와 Private Key로 구성된다.
- **Public Key를 이용해 암호화하고, Private Key를 통해서 복호화한다.**
- Server에서 갖고있는 Private Key는 키 배송을 하지 않기 때문에 대칭키보다 상대적으로 안전하다.
- 대칭키에 비해 느리다.
- RSA 등이 있다.

### 비대칭 키 교환방식
1. RSA(공개키 알고리즘)를 이용한 Key 교환
   - Client가 Server의 PublicKey를 받아서 대칭키를 암호화 한 후 전송
   - Server는 PrivateKey로 복호화하여 대칭키를 획득
   - 이후 대칭키 사용
2. Diffie-Hellman(공개키 알고리즘)을 이용한 Key교환
   - 수학적 공식을 통해 직접 Key를 생성하고 공유하는 방식
3. ECDH (Elliptic Curve Diffie-Hellman - (공개키 알고리즘))를 이용한 교환방식
   - DH의 타원곡선(ECC) 버전, 더 짧은 Key로 높은 보안성 제공
   - ex) TLS 1.3, Signal 프로토콜
