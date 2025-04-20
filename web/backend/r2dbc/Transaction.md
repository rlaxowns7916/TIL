# R2DBC Transaction

## 트랜잭션 모델 비교

### [1] JDBC 트랜잭션: ThreadLocal 기반
- Spring의 `DataSourceTransactionManager`(JDBC)는 Thread에 국한
- `DataSourceTransactionManager#getTransaction()` 호출 시, 내부적으로 새로운 JDBC Connection을 가져온다
- `TransactionSynchronizationManager`의 ThreadLocal 저장소에 Connection Binding
- 장점: 단순한 구조와 명확한 트랜잭션 경계
- 제약: 동일 스레드 내에서만 트랜잭션 전파 가능

### [2] R2DBC 트랜잭션: Reactor Context 기반
- `R2dbcTransactionManager`는 Connection을 Reactor의 Context(Subscription 단위)에 바인딩하여 관리
- Reactor Context는 ThreadLocal에 대응하는 개념으로, Context 전파를 보장
- Subscription 단위로 Context가 유지되므로, 어떤 스레드에서 실행되든 트랜잭션 상태를 손실하지 않음
- 장점: 비동기 환경에서 트랜잭션 일관성 유지
- 제약: 적절한 Context 전파 코드 필요

## R2DBC 트랜잭션 상세 동작

### 트랜잭션 시작 과정
1. `TransactionalOperator` 또는 `@Transactional` 어노테이션에 의해 트랜잭션 시작
2. `R2dbcTransactionManager`가 새로운 Database Connection 획득
3. Connection으로 트랜잭션 시작(`BEGIN`)
4. 트랜잭션 정보를 Reactor Context에 저장
   ```java
   return Mono.deferContextual(ctx -> {
       R2dbcTransactionInfo txInfo = createTransactionInfo(connection, previousIsolationLevel);
       return TransactionContextManager.currentContext(ctx)
           .putTransactionInfo(txInfo)
           .then(Mono.just(txInfo));
   });
   ```

### 트랜잭션 수행
1. Context가 Reactor 체인을 따라 전파됨
2. R2DBC 연산은 Context에서 활성 트랜잭션 정보를 확인
3. 동일한 트랜잭션 내에서 모든 데이터베이스 연산이 수행됨
4. COMMIT 또는 ROLLBACK 명령이 실행 시, Connection 반환 및 Context에서 트랜잭션 정보 제거
