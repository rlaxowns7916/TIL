# Service Discovery
- MSA는 분산환경에서 서비스끼리 서로의 IP주소와 Port를 이용하여 호출하는 방식을 사용한다.
  - 각 마이크로서비스의 IP/FQDN을 저장하고 제공하는 Service Discovery 이다.
- **클라우드 환경이기 떄문에, AutoScaling,생성 삭제 등 동적으로 IP주소가 변경되는 경우가 잦아졌다.**
  - 수십 수백개의 마이크로서비스를 일일이 관리하기 어려웠다.
  - 자동적인 Service에 대한 설정관리가 장점이다.


## 기능
1. 동적환경 대응: 클라우드 환경에서의 Instance 추가 / 삭제 에 대한 실시간 대응이 가능하다.
2. 로드밸런싱: 각 인스턴스의 부하를 파악 할 수 있으며, 부하를 적절하게 분산 가능하다.
3. 확장성: Instance의 추가 / 삭제가 쉬워진다.
4. 단순화: Instance간의 통신에 필요한 정보를 개발자가 직접 관리할 필요가 없다.


## Service Discovery의 구현 방법

### 1. 클라이언트 사이드 디스커버리
- 클라이언트가 ServiceRegistery에 서비스의 위치를 찾아서 알아서 로드밸런싱 하고 호출하는 방식이다.
- Netflix OSS의 Eureka, Ribbon이 대표적이다.

### 과정
1. 각 Service는 시작시점에 Service Registry에게 자신의 정보를 알린다.
2. Service Registry에서 호출할 Service의 정보를 얻는다.
3. Client Side에서 로드밸런싱 알고리즘을 수행하고, Service를 호출한다.

#### 장점
1. 구현이 간단하다.
2. 클라이언트가 서비스 인스턴스에 대해 알고있기 때문에, 서비스별 LoadBalancing 방법을 선택 가능하다.

#### 단점
1. 클라이언트가 각 서비스마다 discovery기능을 구현해야하는 종속성이 생긴다.
2. 클라이언트와 ServiceDiscovery사이에 의존성이 생긴다.

![ClientSide](https://user-images.githubusercontent.com/57896918/157213562-0d05ae55-0422-4782-89d7-91735c3ec81c.png)


### 2. 서버 사이드 디스커버리
- 호출되는 서비스 앞에 LoadBalancer를 둔다.
- LoadBalancer가 Service Discovery를 조회하여 가용 인스턴스를 찾고, 선택해서 요청을 라우팅한다.
- **Service Registry, LB에 대한 고가용성 확보가 필수적이다.**
- AWS ELB와 Kubernates가 대표적이다.


#### 장점
1. discovery가 클라이언트로 부터 분리되어 있다.
2. 분리되어 있는 클라이언트가 단순히 LoadBalancer에 요청만 하기 떄문에, Client에는 로직구성이 불필요하다.

#### 단점
1. 배포환경에 LoadBalancer가 포함되어야한다.
2. ServiceDiscovery가 죽어버리면 전체시스템이 중단된다.



#### [1] Coordinator 이용
- k8s, Zookeeper와 같은 Coordinator을 이용하는 방식이다.
  - k8s
    - etcd: 분산 kv storage
    - gRPC: 내부 통신에 사용
  - ZooKeeper
    - ZAB: ZooKeeper Cluster 내부 통신에 사용
    - TCP: Client와 ZooKeeper간의 통신에 사용
    - ZNode: 데이터를 저장하고 관리하기 위한 계층적인 모델
- 특정 Coordinator에 의존적일 수 있다.


#### [2] VIP (Virtual IP)를 이용한 구성 방식
- 하나의 Virtual IP에 LB가 등록되고 그 뒤에 여러개의 Instance를 위치시킨다.
- Client는 해당 VIP로 요청을 보내고, LB가 알아서 요청을 중개한다.
- VIP를 관리하기 위한 추가적인 구성이 필요하다.
  - VIP는 일정수준까지만 확장이 가능하기 떄문에, 대규모시스템에서 부적합 할 수도 있다.
  - LB는 SPOF가 될 수 있기 떄문에 취약하다.
    - 이중화를 통해서 SPOF를 극복한다고해도 많은 복잡성과 오버헤드가 따른다.
      1. 이중화된 LB간의 동기화
      2. LB간의 트래픽 분산

#### [3] DNS를 이용한 구성 방식
- 내부 DNS를 통해서 Service Discovery를 구성한다.
  - DNS가 LB의 역할을 대신하기 떄문에 SPOF를 방지 할 수 있다.
  - Service 시작 시, Trigger 등으로 DNS서버에 등록한다.
- DNS - TTL값이 살아있는 동안 해당 Instance가 살아있는 것으로 판단한다.
  - TTL이 만료되었다면 죽은 것으로 판단하여 새로운 Instance와 매핑된다.
  - 여러개의 Instance는 기본적으로 RoundRobin으로 LoadBalancing된다.
- Client는 도메인을 통해서 접근하고, 알맞은 서비스에게 요청을 전달한다.
  - 도메인 이름을 사용하기 떄문에 구성이 단순하다.
  - Service Instance의 추가나 삭제 시에도, Client 코드의 수정은 발생하지 않는다.
- DNS Cache (DNS TTL) 의 문제로, 실시간성이 저하될 수 있다.
- DNS단에서 LB가 이루어지기 떄문에, 세밀한 설정이 어려울 수 있다.

##### TTL이 만료되기전에 특정 서비스가 죽어있다면?
- 어쩔수 없다. (클라이언트는 부정확한 응답을 받게 될 것이다.)
- 주기적인 HeartBeat를 통해서, 죽어있는 서비스는 제거하는 로직을 추가할 수는 있다.


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



