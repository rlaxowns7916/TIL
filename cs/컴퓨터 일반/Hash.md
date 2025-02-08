# Hash (해시)
- 데이터를 다루는 방법 중 하나이다.
- 임의의 데이터를 Input으로 받아, 고정된 길이의 Output으로 변형하는 함수이다.
- 해시의 Ouput을 Index로 가져, O(1)로 빠르게 접근이 가능하다.
- 눈사태 효과가 발생한다.
  - 조금의 Input만 달라져도 매우 다른 Output을 발생시킨다.
- Hash Collision이 적게 일어나야 좋은 Hash 함수 이다.
- 단방향이다. (복호화가 불가능 하다.)
- Rainbow Table을 통한 공격에 취약하다.
  - Salt나, Key Stretching(Hash N번 수행)을 통해 보완할 수 있다.

## Hash Collision (해시 충돌)
- 다른 Input이지만 동일한 Output이 나올 때를 의미한다.

### Chaining (체이닝)
- 충돌이 일어났을 때, LinkedList를 통해 Chain형태로 연결하는 것이다.
- 최악의 경우 (모든 Input이 같은 Hash Output을 가질 때), O(n)이 될 수 도 있다.

### Open Addressing (개방주소법)
- 사용하려는 Bucket이 이미 사용중일 경우 다른 Bucket을 사용한다.
1. **선형 탐색**: 해시 충돌 시, 다음 혹은 N개 후의 버킷에 데이터를 넣는다.
2. **제곱 탐색**: 해시 충돌 시, 제곱만큼 멀리 떨어진 버킷에 데이터를 넣는다.
3. **이중 해시**: 해시 충돌 시, 다른 해시함수를 적용한다.

## MDC vs MAC
### MDC (Message Digest Code)
- 무결성 Check만 수행한다.
- Message의 변경 여부는 알 수 있으나, 변경자는 알 수 없다.
- 동장방식
  1. 사용자가 Message를 입력
  2. Hash함수를 사용해 MDC(Hash값)를 생성한다.
  3. Message를 검증할 때, Hash값을 다시 계산하여 원래값과 비교한다.

### MAC (Message Authentication Code
- 무결성 Check + 인증 (비밀 키 기반)을 수행한다.
  - 전송자도 검증 가능하다.
- 동작방식
  1. 송신자가 Message를 입력하고 SecretKey를 사용하여 MAC값을 생성한다.
  2. 수신자는 SecretKey를 사용하여 MAC값을 검증한다.
  3. MAC값이 일치하면 무결성이보장되며, MAC값이 다르다면 Message가 변경되었거나, SecretKey를 공격자가 조작했음을 알 수 있다.

## Hash의 종류 

### 1. MD5
- Message Digest Algorithm 이다.
  - 데이터의 무결성 및 파일을 식별하는데 사용한다.
  - 데이터 그 자체를 비교하는 것보다, 해시를 비교하는 것이 훨씬 간편하고 빠르기 때문이다.
- 128 bit의 고정된 output을 생성한다.
- 보안 취약점을 많이 노출하였다.

### 2. SHA
- SecureHashStandard
- 256 bit의 고정된 output을 생성한다.
- 3가지 버전이 있다. (sha-1, sha-2, sha-3)
- 괜찮은 보안을 갖고 있다.
  - HashColision이 일어날 확률이 적다.
  - GPU와 같은 빠른 연산을 통한 BruteForce 공격에 취약점을 갖는다.
- 암호에 적합하지않다.
  - 암호는 짧다. 
  - 짧은 암호 + 빠르기 때문에 BruteForce Attack에 취약하다.
- 다양한 곳에서 사용된다.
  - 암호화폐
  - TLS
  - 암호 해싱
  - ...
  
### 3. BCrypt
- 보안적으로 뛰어나다.
  - sha에 비해서 보안적으로 뛰어나다.
- Salting
  - 실제 Data이외에 무작위 데이터를 넣고 Hash를 한 것이다.
  - Ouput을 바꾸는데 목적을 갖고있다.
- Stretching
  - 해시를 반복하는 것이다.
  - 보안 취약점인 빠른속도를 보완하기위해서 일부러 연산속도를 늦추는 것이다.

