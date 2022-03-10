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
  - HttpServletRequest, HttpServletResponse 사용 불가능
  - HttpServerRequest, HttpServerResponse 사요가능
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
    - 기본으로 제공해주는 Filter도 있고 CustomFilter도 있다.
    - Filter끼리는 계층이 있다.

### 구조
![SCG](https://user-images.githubusercontent.com/57896918/157435078-3cec24bc-722f-4632-9e46-2a722436aa63.png)

- GatewayHandlerMapping : 경로가 일치하는지 판단
- GatewayWebHandler : 요청과 관련된 필터 체인을 통해 요청을 전달


### Filter 만들기
- GatewayFilter, OrderGatewayFilter 등이 있다.
- chain.filter를 Return 하기 전에는 PreFilter
- return chain.filter부터는 PostFilter

#### 1. PreFilter
```java
import java.beans.BeanProperty;

@Component
@Component
public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {

   public PreGatewayFilterFactory() {
      super(Config.class);
   }

   @Override
   public GatewayFilter apply(Config config) {
      // grab configuration from Config object
      return (exchange, chain) -> {
         //If you want to build a "pre" filter you need to manipulate the
         //request before calling chain.filter
         ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
         //use builder to manipulate the request
         return chain.filter(exchange.mutate().request(builder.build()).build());
      };
   }

   public static class Config {
      //Put the configuration properties for your filter here
   }
```
#### 2. PostFilter
```java
@Component
public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<PostGatewayFilterFactory.Config> {

   public PostGatewayFilterFactory() {
      super(Config.class);
   }

   @Override
   public GatewayFilter apply(Config config) {
      // grab configuration from Config object
      return (exchange, chain) -> {
         return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            //Manipulate the response in some way
         }));
      };
   }

   public static class Config {
      //Put the configuration properties for your filter here
   }

}
```

### Service-Discovery 를 통한 LoadBalancing
#### yaml 설정
```yaml
 spring:
  cloud:
    routes:
      - id: user-service
        uri: lb://USER-SERVICE
        predicates:
          - Path=/user/**
        filters:
          - name: LoggingFilter
            args:
              message: LoggerFilter
              preLogger: true
              postLogger: true
```
- URL에 lb://를 Prefix로 가지고 있다면, SpringCloudLoadBalancer를 이용하여 Redirect한다.
- SpringCloudLoadBalancer에서 id에 해당하는 서비스를 찾아서 Redirect한다.
- Eureka는 내부적으로 SpringCloudLoadBalancer를 가지고 있다.


## 참고자료
- https://www.baeldung.com/spring-cloud-custom-gateway-filters
- https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#configuring-route-predicate-factories-and-gateway-filter-factories