# PSA (Portable Service Abstraction)
- 추상화계층을 사용하여 다양한 기능을 제공 할 수 있다.
- 추상화계층의 구현체는 사용자가 알 수 없다 (숨겨져 있다.)
- 기능은 유사하나, 사용방법이 상이한 (구현이 상이한) 기술들을 추상화 + 구현체로 묶어둔 것을 의미한다.

## @Transacional
- DB에 접근 가능한 기술은 여러가지가 있다.
- JPA, JdbcTemplate, Mybatis ...
- 여태까지 동일한 @Transactional만 선언했음에도 잘 동작했다. (How?)

### PlatformTransactionManager
- 트랜잭션을 지원하는 추상화 계층이다.
- 역할은 아래와 같다.

1. DataSource를 통해서 Connection을 생성한다.
2. 생성한 Connection을 TransactionSynchronizationManger에 보관한다.
3. 같은 트랜잭션이라면, TransactionSynchronizationManager에서 Connection을 꺼내어 사용한다.
4. 트랜잭션이 종료되면, 보관했던 Connection을 종료한다.

#### 다양한 구현체들

![스크린샷 2022-10-09 오후 11 51 01(2)](https://user-images.githubusercontent.com/57896918/194763783-a4af3528-2366-49cb-a440-b89f9fd588e0.png)

- 공통적인 코드만을 사용하지만, 여러가지 구현체의 기술을 다룰 수 있다.
- 최상위 인터페이스인 PlatformTransactionManager를 통한 DI를 통해서 확장 가능고 변경이 쉬운 코드 작성이 가능하게 된다.
