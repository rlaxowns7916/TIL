# Micrometer
- actuator가 내부적으로 사용한다.
- 다양한 기술에 대한 모니터링 및 매트릭 지표를 제공한다.
- **측정방식을 표준화한 추상화 라이브러리 이다.**
  - 각각의 모니터링 툴에 통용되는 포멧으로 전달한다.
  - 각각의 모니터링 툴들이 구현체를 제공한다.

## 지원하는 모니터링 Tool
https://micrometer.io/docs
- AppOptics
- Atlas
- CloudWatch
- Datadog
- Elastic
- Influx
- Prometheus
- ...

## 지원하는 Metric
- JVM 메트릭
- System 메트릭
- Application 시작 메트릭
  - application.started.time (어플리케이션이 시작되는데 걸리는 시간 - ApplicationStartedEvent로 측정)
  - application.ready.time (어플리케이션 요청이 처리하는데 걸리는 시간 - ApplicationReadyEvent로 측정)
- Spring MVC 메트릭
  - Tag를 사용해서 정보를 분류한다.
    - uri: 요청 uri
    - method: GET, POST를 분류
    - status: 200 | 400 | 500 등 HTTP STATUS 코드
    - exception: 예외
    - outcome: HTTP Status Code를 그룹화 (1xx: INFORMATIONAL | 2xx: SUCCESS | 3xx: REDIRECTION)
  - ```text
      http://localhost:8080/actuator/metric?tag=KEY1:VALUE1&tag=KEY2:VALUE2
    ```
- Tomcat 메트릭
- DataSource 메트릭
  - Connection Pool에 관한 메트릭을 확인 가능하다.
  - **jdbc.connections.로 시작한다.**
  - **최대 커넥션 | 최소 커넥션 | 활성 커넥션 | 대기 커넥션 등을 확인 가능하다.**
  - hikari도 사용 가능하다.
- Log 메트릭
- ...

## EndPoint
https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
- 다양한 EndPoint를 통해서 실시간 정보에 접근 가능하다.