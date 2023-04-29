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
