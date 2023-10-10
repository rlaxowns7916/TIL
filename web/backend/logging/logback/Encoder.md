# Encoder
- Logback에서 Log를 ByteArray로 변환하는 역할을 하는 컴포넌트 
- 로그 메시지가 실제로 어떻게 문자열 또는 다른 형태로 인코딩될지를 결정
- 말 그대로, 로그를 특정한 형태로 인코딩(변환) 하는 것이다.

## Logback 에서 제공해주는 기본 Encoder
- PatternLayoutEncoder: 사용자가 제공한 패턴에 따라 로그 이벤트를 문자열로 포맷
- LayoutWrappingEncoder: 다른 Layout 구현체를 감싸서 사용할 수 있는 Encoder. 초기 버전과의 호환성을 위해 제공된다.

## Encoder 적용 예시
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

## Layout과의 차이점

### [1] Layout
- 목적: LoggingEvent를 String으로 변환한다.
- 출력: String
- 사용
  - 주로 초기버전 
  - String기반 
  - 로그포맷 설정에 용이하다.  

### [2] Encoder
- 목적: LoggingEvent를 ByteArray로 변환한다.
- 출력: ByteArray
- 사용: 
  - Network 전송이나, Binary 사용에 용이하다. 
  - 다양한 인코딩 요구사항에 적합하다.

## Custom한 Encoder 만들기

### [1] Custom Encoder
```java
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CustomEncoder extends EncoderBase<ILoggingEvent> {

    @Override
    public byte[] encode(ILoggingEvent event) {
        // 로그 이벤트를 바이트로 인코딩하는 로직
        String formattedMessage = "Custom: " + event.getFormattedMessage();
        return formattedMessage.getBytes();
    }

    @Override
    public void close() {
        // 필요한 경우, 여기에 리소스 해제 로직을 추가합니다.
    }

    @Override
    public void doEncode(ILoggingEvent event) throws IOException {
        // 일반적으로 이 메서드에 로직을 추가할 필요는 없습니다.
    }
}
```

### [2] XML 설정
```xml
<appender name="SLACK_APPENDER" class="com.example.SlackAppender">
    <encoder class="com.example.YourCustomEncoder" />
</appender>
```

## Appender와의 연계
1. Appender가 LogEvent를 수신한다.
2. Encoder가 수신한 Log를 Appender로부터 이어받아, 특정현태로 변환한다.
3. 변환한 Log를 바탕으로 Appender가 로그를 처리한다.

```java
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;

public class SlackAppender extends AppenderBase<ILoggingEvent> {

       private Encoder<ILoggingEvent> encoder;

       /**
        * xml 설정에 따라서, 모든 것은 Setter로 주입된다.
        */
       public void setEncoder(Encoder<ILoggingEvent> encoder) {
              this.encoder = encoder;
       }

       @Override
       protected void append(ILoggingEvent eventObject) {
              byte[] byteData = encoder.encode(eventObject);
              String formattedMessage = new String(byteData);

              // Slack에 formattedMessage를 전송하는 로직
              // ...
       }
}
```