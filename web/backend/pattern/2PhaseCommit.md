# 2-Phase Commit
- 분산 시스템에서 트랜잭션의 원자성과 일관성을 보장하기 위한 프로토콜이다.
- 이 프로토콜들은 여러 노드가 참여하는 트랜잭션에서 모든 노드가 커밋 또는 롤백을 동시에 수행하도록 하는데 사용된다.
- 여러개의 분산 시스템들과 Coordinator로 구성된다.
- 2단계로 구성되어 있다.
- 완벽하지 않은 방법이다.
  - Rollback의 경우도 부분적으로 완료되었다면?
  - 결국 수동으로 일관성을 맞춰주거나, 별도의 재처리 로직을 수행하여야한다.
- https://www.baeldung.com/transactions-across-microservices
### 과정
#### [1] Prepare (준비) 단계
1. Coordinator가 참여자들에게 커밋 할 준비가 되었는지 질의한다.
2. 참여자들은 준비 상태에 따라서, 가능 여부를 응답하고 가능할 경우 잠금을 설정한다.

#### [2] Commit (커밋) || Abort(중단) 단계
1. 모든 참여자들이 가능하다는 응답을 하면, 트랜잭션을 진행한다.
2. Coordinator가 Commit Message를 참여자들에게 전송한다.
3. 참여자들은 Message를 받은 후 Commit ACK를 보내거나, Abort ACK를 Coordinator에게 반환하고 잠금을 해제한다.
4. 하나라도 Abort Ack Message가 온다면 모두 Rollback을 진행한다.


### 2PC의 SPOF 문제
- Coordinator는 SPOF이다.
- Coordinator의 문제로 트랜잭션 최종 연산 (Commit, Rollback)이 불분명 해 질 수 있다.


![스크린샷 2023-04-29 오후 11 26 55(2)](https://user-images.githubusercontent.com/57896918/235310588-daecaf23-86c0-49a5-8e25-7db3260e3699.png)

## XA(eXtended Architecture)
- **두가지 이상의 Resource (DB, MQ ...) 간의 분산트랜잭션을 지원하는 Global Transaction 표준**
- 언어나 플랫폼에 독립적이며, 여러 시스템에서 사용 될 수 있다.


## JTA (Java Transaction API)
- XA를 Java 어플리케이션에 사용하기 적합한형태로 정의한 Interface
- **플랫폼 마다 상이한 Transcation Manager들과, Application들이 상호작용할 수 있는 Interface를 정의한다.**
  - Java EE 표준 스펙 (https://docs.oracle.com/cd/B14099_19/web.1012/b14012/jta.htm)
  - DB뿐만 아니라, MQ까지도 하나의 트랜잭션으로 묶을 수 있다.
  - 실제 구현은 구현체마다 다르지만, Application에서 공통적으로 사용할 수 있는 인터페이스가 제공되어있다.
- SpringBoot는 JTA사용이 가능하다.
  - Atomikos
  - Bitronix
  - Java EE Managed Transaction Manager: War, ear 패키징 후 Java EE 어플리케이션 서버에 위치
- **2PC를 사용한다.**

#### 구성요소
1. User Transaction: 개발자가 제어하는 여러 이기종을 묶고자 하는 트랜잭션이다.
2. Transaction Manager: 개발자 대신 트랜잭션을 관리하는 인터페이스이다.
3. XA-Resource: MQ, DB와 같은 분산트랜잭션을 수행할 대상이다.]


### 장점
1. 데이터 무결성: 손쉽게 분산 트랜잭션을 구성 할 수 있다.
2. 추상화: 추상화되어있는 Java표준이기 때문에, 손쉽게 구현체를 갈아끼거나 통합이 가능하다.

### 단점
1. 성능 오버헤드: 분산트랜잭션 처리시, 추가적인 통신과정(2PC)이 필요하므로 성능저하가 발생한다.
2. 격리수준의 상이: 이기종간, 트랜잭션 격리수준이 다를 수 있으며 일관성을 유지하기 힘들 수 있다.
3. 실패 복구 실패가능성: Coordinator의 실패시 복구의 어려움을 겪을 수 있다.