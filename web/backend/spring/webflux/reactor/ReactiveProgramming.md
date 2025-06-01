# Reactive Programming
- **데이터의 변화와 이벤트 흐름에 반응하는, 유연하고 선언적인 소프트웨어 구조**
- 클라이언트의 요청에 즉각적으로 응답하는 것을 목표로하여, 수행시간을 최소화 하고자 한다.
- **NonBlocking I/O 기반, 비동기 메세지 통신이다.**
- 가독성과 간결함을 가지기 위해서, Method Chain 혙태의 프로그래밍을 수행한다.

## 목표
1. 비동기·Non-Blocking 메시지 기반 통신
   - 시스템 구성 요소들이 느슨하게 결합(Loosely Coupled)
   - 위치, 구현에 투명(Transparency), 격리성(Isolation) 확보
2. 탄력성(Resilience)과 회복성
   - 부하 변화에도 일정한 응답 속도 보장
   - BackPressure: 과부하 시 데이터 흐름을 자동으로 조절
   - 컴포넌트/서비스 간 격리로 부분 장애가 전체 시스템에 파급되지 않도록 설계
3. 높은 응답성(Responsiveness)
   - 클라이언트 요청에 신속하게 응답
4. 유지보수 용이성 & 확장성
   - 선언적 코드와 표준 패턴(Streams, Operator, Publisher/Subscriber)으로 코드의 변경 및 확장이 쉽고 안전

## 특징
### [1] 선언적 프로그래밍 (Declarative Programming)
- 실행할 동작의 목표만을 선언할 뿐, 구체적인 동작을 정의하지 않는다.
- 전통적인 명령형 프로그래밍과 반대 된다.

### [2] DataStreams & Propagation Of Change (데이터 흐름 & 변화의 전파)
- 지속적으로 발생하는 데이터 흐름(DataStream)을 가진다.
- 변화의 전파(Propagation Of Change)를 통해서, 이벤트를 발생시키면서 지속적으로 데이터를 전달한다.

### [3] 비동기·Non-Blocking 구조
- 블로킹 없이, 대기시간 최소화
- 하나의 스레드로도 수많은 작업을 효율적으로 처리

## 구성요소

### [1] Publisher
- Subscriber가 사용할 데이터를 제공하는 역할을 한다.

### [2] Subscriber
- Publisher가 제공하는 데이터를 소비하는 역항르 한다.

### [3] DataStreams
- Publisher의 Input으로 전달 될 연속적인 데이터의 흐름

### [4] Operator
- 중간연산이다.
- Publisher에서 Subscriber에게로 전달 될 때 중간 가공 연산을 의미한다.