# Appender
- logback으로부터 logging을 위임받는 주체이다.
- 어떻게 로깅을 처리할지 결정하는 Component이다.
- https://logback.qos.ch/manual/appenders.html

## 특징
- 다중 설정이 가능하다.
  - 하나의 Log를 여러개의 Appender에서 처리하는 것이 가능하다.
- 레벨 별 Filtering
  - 특정 ErrorLevel별로, Appender 활성화 여부를 정의할 수 있다.
- **Layout 설정**
  - Appender의 LogMessage 형식을 정의하는 Layout에 대한 설정이 가능하다.
- 동적 설정변경
  - JMX를 통한 동적인 설정 변경이 가능하다.

## Logback에서 제공해주는 기본 Appender
- ConsoleAppender: 로그 메시지를 콘솔에 출력
- FileAppender: 로그 메시지를 파일에 기록
- RollingFileAppender: 로그 메시지를 파일에 기록하되, 파일의 크기나 날짜 등의 조건에 따라 로그 파일을 롤링(백업 및 새 파일 생성)
- SMTPAppender: 로그 메시지를 이메일로 전송
- AsyncAppender: 로그의 처리를 비동기로 진행

## Custom Appender만들기
### [1] Custom XML 설정 정의
```xml

<included>
    <springProperty scope="context" name="channelId" source="slack.log.channelId"/>
    <springProperty scope="context" name="token" source="slack.log.token"/>
    <springProperty scope="context" name="botName" source="slack.log.botName" defaultValue="ErrorNotiBot"/>
    <springProperty scope="context" name="botIcon" source="slack.log.botIcon" defaultValue=":ok_twitch:"/>

    <!--class에는 실제 접근 가능하고, 로직을 정의한 JavaClass의 FQCN이 들어가야 한다.-->
    <appender name="SLACK_LOG_APPENDER" class="yapp.be.appender.SlackAppender">
        <channelId>${channelId}</channelId>
        <botName>${botName}</botName>
        <botIcon>${botIcon}</botIcon>
        <token>${token}</token>

        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%-4relative [%thread] %-5level %class - %msg%n</pattern>
        </layout>
    </appender>

    <appender name="ASYNC_SLACK_LOG_APPENDER" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="SLACK_LOG_APPENDER"/>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
    </appender>
</included>
```

### [2] Custom Appender 구현
```kotlin
class SlackAppender : UnsynchronizedAppenderBase<ILoggingEvent>() {

    lateinit var token: String
    lateinit var botName: String
    lateinit var botIcon: String
    lateinit var channelId: String

    private val client = SlackLogDeliveryClient()

    /**
     * 필수 구 
     */
    override fun append(eventObject: ILoggingEvent) {
        client.send(
            token = token,
            botIcon = botIcon,
            botName = botName,
            channel = channelId,
            text = eventObject.formattedMessage
        ).subscribe()
    }
}
```

# Layout
- 로그의 포멧을 결정한다.

## Logback에서 제공해주는 기본 Layout

### [1] PatternLayout: 
- 가장 널리 사용되는 Layout.
- 로그 메시지의 포맷을 자유롭게 정의가능하다.

#### 주요 포멧
- %d: 날짜와 시간
- %msg or %m: 로그 메시지
- %logger or %c: 로거 이름
- %level or %p: 로그 레벨 (예: INFO, ERROR)
- %thread: 스레드 이름
- %n: 플랫폼 종속적인 줄바꿈 문자
- %file: 로그를 호출한 파일의 이름
- %class or %C: 로그를 호출한 클래스의 이름
- %method: 로그를 호출한 메서드의 이름
- %line: 로그를 호출한 코드의 라인 번호

### [2] HTMLLayout
- 로그 메시지를 HTML 테이블 형식으로 출력
- 웹 페이지에서 로그를 보기 좋게 표시하기 위해 사용

### [3] TTLLLayout 
- 간단한 형태의 로그 메시지를 출력 
- 주로 디버깅 목적으로 사용됩니다.

### [4] XMLLayout: 
- 로그 메시지를 XML 형식으로 출력합
- 로그 메시지를 다른 툴이나 서비스와 통합하기 위한 목적으로 사용
