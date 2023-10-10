# Logback
- Java의 로깅 프레임워크
  - log4j, log4j2 등이 있다.
  - logback이 성능이 준수하다.
- Slf4j(Simple Logging Facade For Java) 의 구현체이다.
- 5단계의 로그레벨을 지원한다.
- **spring-boot-starter-web에 기본적으로 포함되어 있다.**
- 서버를 재시작 하지 않고, 설정 변경이 가능하다.
- 압축, 보관 기간을 관리 할 수 있다.

## 특징
- 빠른 implementation
- 적은 메모리 사용
- XML 설정
- Filter를 통한 logLevel별로 조정
- 비동기 로깅 지원 (AsyncAppender)

## LogBack Architecture

### [1] Logger
- Log Message 생성 관련 Class

### [2] Appender
- Log를 출력할 Class
- Logger가 Event를 전달한다.
- 실제 Logging을 담당하는 Class 이다.

### [3] Layout
- Log 포맷 관련한 Class
- 패턴을 통한 LogMessage 형식을 지정한다.

### [4] Encoder
- 실제 로그를 복잡한 인코딩을 통해서 구성할 때 사용된다.
- Layout보다 조금더 자유도가 높다.

## 5가지 Logging Level
**TRACE** -> **DEBUG** -> **INFO** -> **WARN** -> **ERROR** 

***

## Spring에 적용하기
- application.yml (.properties)
  - 클래스(FQCN) 별로 로깅 설정이 가능하다.
- logback-spring.xml
  - classpath에 위치해야 한다.
  - resource 폴더(src/main/resources/)에 위치해야한다.
  - logback.xml보다 우선순위를 갖는다.
    - 둘중에 하나만 있으면 (logback.xml, logback-spring.xml 둘 중 있는걸 사용한다.)
- spring profile 별로 설정 가능하다.
  - ```xml
        <springProfile name="staging" />
        <springProfile name="dev | staging" />
        <springProfile name="!production" />
    ```

## 설정
### [1] ConsoleAppender
```xml
<configuration>
  <!--Appender 정의-->
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <!--날짜 쓰레드 로깅레벨 로거이름 메세지 줄바꿈-->
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger {36} - %msg%n</pattern>
    </encoder>
  </appender>
  
  <root level ="debug">
    <appender-ref ref="STDOUT" /> <!--사용할 Appender를 참조-->
  </root>
  
</configuration>

```

## [2] RollingFileAppender
```xml
<configuration>
  <appender name="ROLLINGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logFile.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>30</maxHistory>
      <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>
    
    <encoder>
      <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
    </encoder>
  </appender>
  
  <root level="DEBUG">
    <appender-ref ref="ROLLINGFILE"/>
  </root>
</configuration>
```