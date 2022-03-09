# Api Gateway
- 서버의 최앞단에 위치한다.
- 모든 Client의 요청에 대한 End-Point를 통합한다.
- 기능이 추가된 ReverseProxy(?)
- 병목현상을 막기위해서는 ScaleOut이 필수적이다.

## 주요 역할
- 인증 및 권한 부여
- Routing 및 LoadBalancing
- 응답 캐싱
- 속도 제한
- 부하 분산
- 로깅, 추적
- IP 허용 및 차단 
- 회로 차단기 , QOS 재시도

## 주요 오픈소스
1. Kong
2. Zuul (2.4 >= Deprecated)
3. SCG (SpringCloudGateway)


## SpringCloudGateway
- netty 기반 (비동기)
- Webflux, Reactor 기반 프로젝트
- SpringMVC와 잘 호환이 되지 않는다.

### Zuul과의 차이점
1. Zuul은 Blocking API, SCG는 Non-Blocking API
    - Zuul(Servlet2.5), SCG(SpringBoot 5.x)
2. Zuul은 WebSocket을 지원하지 않는다.
3. SCG는 Spring 통합에 특화되었다.

### 구성
1. Route
    - 어떤 Server로 Routing할 것인가에 대한 설정 
2. Predicate
    - Request에 대한 검증 가능
    - Path, Header등에 대한 검사 가능
3. Filter
    - WebFilter 인스턴스
    - Request | Response 에 대한 변형 가능

### 구조



- GatewayHandlerMapping : 경로가 일치하는지 판단
- GatewayWebHandler : 요청과 관련된 필터 체인을 통해 요청을 전달