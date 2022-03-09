# Service Discovery
- MSA는 분산환경에서 서비스끼리 서로의 IP주소와 Port를 이용하여 호출하는 방식을 사용한다.
- 각 마이크로서비스의 IP/FQDN을 저장하고 제공하는 Service Discovery 이다.
- 클라우드 환경이기 떄문에, AutoScaling,생성 삭제 등 동적으로 IP주소가 변경되는 경우가 잦아졌다.
- 수십 수백개의 마이크로서비스를 일일이 관리하기 어려웠다.

## Service Discovery의 구현 방법

### 1. 클라이언트 사이드 디스커버리
- 클라이언트가 ServiceRegistery에 서비스의 위치를 찾아서 호출하는 방식이다.
- Netflix OSS의 Eureka가 대표적이다.

#### 장점
1. 구현이 간단하다.
2. 클라이언트가 서비스 인스턴스에 대해 알고있기 때문에, 서비스별 LoadBalancing 방법을 선택 가능하다.

#### 단점
1. 클라이언트가 각 서비스마다 discovery기능을 구현해야하는 종속성이 생긴다.
2. 클라이언트와 ServiceDiscovery사이에 의존성이 생긴다.

![ClientSide](https://user-images.githubusercontent.com/57896918/157213562-0d05ae55-0422-4782-89d7-91735c3ec81c.png)


### 2. 서버 사이드 디스커버리
- 호출되는 서비스 앞에 LoadBalancer를 둔다.
- LoadBalancer가 Service Discovery를 조회하여 가용 인스턴스를 찾고, 선택해서 요청을 라우팅
- AWS ELB와 Kubernates가 대표적이다.

#### 장점
1. discovery가 클라이언트로 부터 분리되어 있다.
2. 분리되어 있는 클라이언트가 단순히 LoadBalancer에 요청만 하기 떄문에,

#### 단점
1. 배포환경에 LoadBalancer가 포함되어야한다.
2. ServiceDiscovery가 죽어버리면 전체시스템이 중단된다.

![ServerSide](https://user-images.githubusercontent.com/57896918/157213578-78aa1d0d-5d6f-4bb5-aac1-74a8a89af96b.png)


## Netflix OSS Eureka 
- EurekaServer와 EurekaClient로 구성된다.
- Netflix에서 제공하는 MSA를 위한 클라우드 오픈소스 ServiceDiscovery
- Cloud System에서 LoadBalancing과 장애처리를 목표로하는 REST 기반 서비스이다.


### 작동과정
1. Eureka Client가 시작될 때, EurekaServer에 자신을 등록한다.
2. Eureka Client는 EurekaServer로 부터 연결정보가 등록된 Registry를 받고 Local에 저장한다.
3. Eureka Client는 30초마다 변경사항을 수신받는다.
4. Eureka Client는 30초마다 자신이 동작하고 있다는 것을 EurekaServer에 전달한다.

### EurekaServer

#### application.yml
```yaml
server:
  port: 8761
spring:
  application:
    name: discovery-service
eureka:
  client:
    register-with-eureka: false # default : true , Eureka Server에 Registry 하지 않음
    fetch-registry: false # default : true , Eureka Server에 Registry 하지 않음
```

#### SpringBootApplication
```java
@EnableEurekaServer
@SpringBootApplication
public class ServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryApplication.class, args);
    }

}

```
### EurekaClient
#### application.yml
```yaml
server:
  port: 8080
spring:
  application:
    name: user-service
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka # Eureka Server 위치
```
#### SpringBootAppliaction
```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

```
![EurekaServer](https://user-images.githubusercontent.com/57896918/157214132-df4827da-e2d3-40c7-9922-75f20d88a21e.png)


## RandomPort 지정하기
- 수동으로 포트를 관리하는 것은 MSA에서 너무 귀찮다.
- application.yml설정을 통해서 RandomPort를 지정해주면된다.

```yaml
server:
  port: 0 #Sprng RandomPort
```

### RandomPort 문제점
![0Value](https://user-images.githubusercontent.com/57896918/157397793-4cc5ad09-2e28-4e24-b02b-f5164d04d51c.png)
여러개의 인스턴스를 띄워도 제대로 동작하지 않는 것을 볼 수 있다.

- Eureka는 EurekaClient명을 
  `${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}}` 로 만든다.
  - application.yml에 server.port가 0으로 하드코딩 되어있기 때문에 발생한 것이다.
- `${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${random.value}}`로 바꾸자.

### RandomPort 문제해결

![randomValue](https://user-images.githubusercontent.com/57896918/157399396-934435bc-141c-468b-82f0-fc97ce251cbc.png)



