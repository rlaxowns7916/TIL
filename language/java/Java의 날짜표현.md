# Java의 날짜 표현

# 레거시
## Date
- java.util.date
- java.util.calendar
- Thread-Safe 하지 않다.
- Mutable 하다.
  - 변경시 새로운 Date객체가 아니라, 변경이 된다.
- 국제화를 지원하지 않는다.
  - 시간대 지원이 없다.
  - TimeZone이 생겼지만 위의 문제를 똑같이 갖는다.
- 버그가 발생할 여지가 많다.
  - Calendar의 경우 January(1월)이 상수 0 이다.


# JSR-310
- 레거시 클래스 (Date, Calendar)를 대체하기 위해서 만든 API
- 기존 JDK API와의 통합, 개발자 사용성에 중점을 둔 효율적인 API 제공
- 제공되는 모든 Class들은 Immutable 하다.

## Instant
- 기본적으로 UTC 이다.
- 1970년 1월 1일 (Epoch) 이후 현재시간(나노초) 이다.

```java
import java.time.*;
import java.util.*;
class HelloWorld {
  public static void main(String[] args) {
    Instant now = Instant.now();
    System.out.println(now); // 2022-12-15T13:48:38.817016Z
    System.out.println(now.getNano()); // 817016000
    System.out.println(now.getEpochSecond()); // 1671112118
    System.out.println(now.toEpochMilli()); // 1671112118817
  }
} 
```


## LocalDateTime
- 시간대를 명시적으로 지정할 필요가 없다.
    - Default TimeZone이 지정 되어있다. (Local)
    - OS의 System 설정을 따른다.
    - **JVM 설정 (-Duser.timezone=Asia/Seoul)이 있다면 JVM설정을 따른다.**
- 내부적으로 Instant를 사용한다.
  - Instant -> ZonedOffSet -> EpochSecond -> LocalDateTime


## ZonedDateTime
- 특정 시간대에 맞게 설정 가능하다.
- Instant로의 변환을 제공한다.
- ```java
      class Example{
        public static void main(String[] args){
            ZonedDateTime parisTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Paris"));
            Instant now = parisTime.toInstant();
            ZonedDateTime seoulTime = now.atZone(ZoneId.of("Asia/Seoul"));
        }
      }
   ```