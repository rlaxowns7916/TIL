# ThreadLocal
**Java에서 제공하는 Class, Thread가 사용하는 지역변수**
- 두 Thread가 하나의 ThreadLocal변수를 참조하더라도, 서로의 ThreadLocal의 값은 다르다.
- Multi Thread환경에서 각 Thread마다 독립적인 변수를 가지고 활용 가능
- Thread의 전역저장소이다.
- 내부적으로 Thread정보를 Key로 가지는 Map으로 구성되어있다.

## 사용처
- SpringSecurity에서 사용자마다 다른 인증정보 & Session 정보
  -  SecurityContextHolder 의 기본전략 
- Thread-Safe한 데이터 저장
- MDC(Mapped Diagnositc Context)
  - log4j2,logback이 제공하는 Thread별 Log구별 도구
  - MultiClient환경에서 다른 Client와 값을 구별하고 로그추적 용이


## 사용법
1. ThreadLocal 객체를 생성한다.
2. ThreadLocal.set() 메서드를 이용해서 현재 쓰레드의 로컬 변수에 값을 저장한다.
3. ThreadLocal.get() 메서드를 이용해서 현재 쓰레드의 로컬 변수 값을 읽어온다.
4. ThreadLocal.remove() 메서드를 이용해서 현재 쓰레드의 로컬 변수 값을 삭제한다.
    - 사용이 완료된 값을 제대로 지우지 않으면, 재사용되는 Thread (ex: ThreadPoll)가 잘못된 값을 참조 할 수 도있다.
    