# LoadBalancer

## LoadBalancing이란?

**분산처리된 서버에서, 서버의 부하량, 속도저하 등을 고려해서 적절하게 분산 처리해주는 서비스**

- DownTime감소
- 확장성 증가
- 유연성 증가
- 처리속도 증가

### LoadBalancing 기법

#### 1. RR (Round Robin)
- 요청이 들어온대로 순서대로 라우팅 하는 방식이다.

#### 2. WeightedRR
- 서버마다 가중치를 주고, 가중치가 높은 서버부터 우선적으로 라우팅한다.

#### 3. IP Hash

- IP주소를 해시해서, 해당하는 Server에 라우팅 한다.

#### 4. Least Connection
- 가장 Connection이 적은 서버에게 라우팅한다.

#### 5. Least Response Time
- 서버의 연결 상태 + 응답시간 까지 고려하여 트래픽을 배분한다.
- 가장 짧은 응답시간을 가지는 서버에게 우선적으로 라우팅 한다.

## LoadBalancer 종류

### 1. L4 LoadBalancer

- IP & TCP/UDP 포트 정보를 기반으로 한다.
- Packet 레벨에서만 로드밸런싱을 하기 떄문에 속도가 빠르다.
- 데이터를 복호화할 필요가 없기 때문에, 안전하다.
- 저렴하다

### 2. L7 LoadBalancer

- TCP/UDP 는 물론, HTTP URL, FTP 파일명, 쿠키정보 등을 바탕으로 로드밸런싱이 가능하다.
    - 섬세한 라우팅이 가능
- 캐싱 기능을 제공한다.
- 특정한 비정상 패턴을 지닌 트래픽을 걸러낼 수 있다.
    - DoS/DDoS 같은 것들
- 비싸다.