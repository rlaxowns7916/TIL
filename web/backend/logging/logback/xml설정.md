# xml 설정
- logback 설정의 기본이다.


## 예시
```xml
<configuration>
    <!-- 콘솔에 로그를 출력하는 Appender -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </Pattern>
        </layout>
    </appender>

    <!-- 특정 패키지의 로그 레벨 설정 -->
    <logger name="com.example" level="DEBUG" />

    <!-- 루트 로거 설정 -->
    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>

</configuration>
```

### 구성요소

#### [1] \<configuration>
- 진입점

#### [2] \<appender>
- 로그에 대한 처리를 담당한다.
- 로그 이벤트를 수신하고, 처리한다.
- **\<appender-ref>**
  - logger에 연결된 appender를 참조한다.
  - appnder끼리도 참조할 수 있다.

#### [3] \<root>
- root logger 이다.
- logger들의 최상단이다.

#### [4] \<logger>
- 로깅을 수행하는 대상이다.
- 로그를 필터링 할 수 있다.
- Java코드에서도 설정 가능하고, xml에서도 설정 가능하다.
- 계층관계를 갖게 된다.
  - 계층관계에 따라서 전파된다.

#### [5] \<layout>
- 로그의 포멧을 결정한다.

#### [6] \<encoder>
- 로그를 인코딩 하여, 특정형태로 변환한다.

#### [7] \<filter>
- appender내에서 로그를 필터링한다.
- 예를들어 Log Level을 통해서 필터링이 가능하다.

#### [8] \<include>
- 다른 설정을 import할 때 사용한다.
- **\<included>**
  - import 당하는 설정쪽에서 사용한다.
  - 자신을 import하는 설정에 병합된다.

#### [9] \<springProperty>
- spring과 함께 사용될 때 사용된다.
- application.yml에 설정된 값들을 참조할 수 있다.
- source를 통해서 참조할 값을, name을 통해서 logback에서 사용할 이름을 지정한다.
