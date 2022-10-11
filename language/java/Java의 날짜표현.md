# Java의 날짜 표현

## Date

- 레거시
    - java.util.date
    - java.util.calendar
- Thread-Safe 하지 않다.
- Mutable 하다.
  - 변경시 새로운 Date객체가 아니라, 변경이 된다.
- 국제화를 지원하지 않는다.
  - 시간대 지원이 없다.
  - TimeZone이 생겼지만 위의 문제를 똑같이 갖는다.

## LocalDateTime

- Java8에서 제공
- 현지의 날짜 시간을 의미한다.
    - 시간대를 명시적으로 지정할 필요가 없다.
- ISO 형식을 따른다.
- Immutable 하다.

## ZonedDateTime

- Java8에서 제공
- 특정 시간대에 맞게 설정 가능하다.
- ```java
      class Example{
        public static void main(String[] args){
            ZoneId zoneId = ZoneId.of("Europe/Paris");   
            ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), zoneId);
        }
      }
   ```
- Immutable 하다.