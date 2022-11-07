# Logger
- 다양한 Logging Framework가 존재한다.
- 주로 Slf4j의 추상화 계층을 이용하여 구현체를 다룬다.

## Slf4j (Simple Logging Facade For java)
- 다양한 로깅 프레임워크의 공통된 추상화 인터페이스
  - log4j, logback 등의 구현 프레임워크가 존재한다.
  - 당연히 구현체 라이브러리가 없다면 동작하지 않는다.
- 개발자는 Slf4j-api를 사용하고 바인딩된 LoggingFramework가 실제 로깅을 수행

## System.out.println은 왜 사용하면 안되는가?

### System.out.println
- I/O 작업이 항상 동기식으로 발생한다.
  - 해당 I/O 작업이 끝날 때 까지 대기한다.
  - I/O 작업이기 때문에, 꽤 큰 CPU 연산을 요구한다.
      - 성능의 문제점을 야기할 수 있다.
- Synchronized 블록이다.
  - Blocking이 발생하기 떄문에 오버헤드가 크다.
- LoggingLevel이 존재하지 않는다.
  - 다 출력한다.
  - 파일이라면 다 저장한다.
  - 오버헤드가 크다.
- Parameter만을 출력한다. (다양한 정보의 부재, 출력하기 위한 수작업)

### Logger
- 다양한 제어를 가능하게 한다.
- 구현체에 따라서 Thread-safe 한 것도있고, Non-Thread-Safe한 것도 있다.
  - Logback의 경우 내부적으로 ThreadLocal을 사용하며, Thread-Safe하다.
- Logger의 경우 Queue를 두고 다른 연산이 없을 경우에 수행한다.
- Level의 정의를 통해서 로그를 필터링 할 수 있다.
- 다양한 타입의 Appender를 제공해주어, 정책에 맞게 사용 가능하다.
  - 외부 시스템과의 연동이 쉽다.
  - Async하게 Logger를 남길 수도 있다.
  - Kafka Streams, Elastic Search 등 과도 연동이 가능하다.
- 표준화된 로그 출력 방식 (읽기 쉽다)