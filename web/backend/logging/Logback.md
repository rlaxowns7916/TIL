# Logback
- Java의 로깅 프레임워크
  - log4j, log4j2 등이 있다.
  - logback이 성능이 준수하다.
- Slf4j(Simple Logging Facade For Java) 의 구현체이다.
- 5단계의 로그레벨을 지원한다.
- spring-boot-starter-web에 기본적으로 포함되어 있다.
- 서버를 재시작 하지 않고, 설정 변경이 가능하다.
- 압축, 보관 기간을 관리 할 수 있다.

***

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

## 5가지 Logging Level
**TRACE** < **DEBUG** < **INFO** < **WARN** < **ERROR** 

***

## LogBack 지원 Appender
### [1] Console Appender
- 기본적인 Console에 출력하는 Appender
### [2] File Appender
- File에 저장하는 Appender
- 단순히 File에 작성만 한다.
- 수동으로 재시작하거나, 파일크키를 제한 하지 않으면 무한대로 늘어난다.

### [3] RollingFile Appender
- File Appender의 확장 버전이다.
- 파일 크기, 날짜 등의 조건으로 파일을 **롤링** 하며, 현재 로그파일을 닫고 새로운 로그파일을 로그를 작성한다.

### [4] SMTP Appender
- 메일을 작성한다.

### [5] DB Appender
- DB에 작성한다.

***

## Spring에 적용하기
- application.yml (.properties)
  - 클래스(FQCN) 별로 로깅 설정이 가능하다.
- logback-spring.xml
  - classpath에 위치해야 한다.
  - resource 폴더(src/main/resources/)에 위치해야한다.
  - 꼭 해당 이름을 사용해야 한다. (logback-spring.xml, logback-spring.groovy)
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