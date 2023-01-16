# Spring Application Event Publisher
- Spring내부에서 Event를 발행할 수 있게 해준다.
- **ApplicationContext가 상속하는 인터페이스 중 하나이다.**
- Obeserver Pattern의 구현체로, Spring내부에서 이벤트 기반 프로그래밍을 가능하게 해준다.
  - spring이 내부적으로 Event에 대한 Listener들을 갖고있다.
  - List를 순회하면서, Event의 Trigger를 알려주면 알맞은 Method가 실행된다.


## [1] 이벤트 정의
- Spring 4.2 미만에서는 Evnet를 정의해주어야 한다.
  - ```java
    @Getter
    public class CustomEvent extends ApplicationEvent {

    private int data;
 
    public CustomEvent(Object source) {
        super(source);
    }
 
    public CustomEvent(Object source, int data) {
        super(source);
        this.data = data;
    }
}
    ```
- 그 이상버전에서는 상관없다. (Parameter가 Object)

## [2] 이벤트 발행
- ApplicationEventPublisher를 DI받고 Event를 발행하면 된다.
  - 물론 ApplicationContext또한 구현체이기에, ApplicationContext를 DI받아도 된다.
- publishEvent는 2가지의 Parameter를 받을 수 있다.
  - 4.2미만 -> ? extends ApplicationEvent
  - 4.2이상 -> object
```java
@Service
@RequiredArgsConstructor
class ExampleService{
  
  private final ApplicationEventPublisher eventPublisher;
  
  public void somethingSuccess(){
    /**
     * 로직
     */ 
    //4.2 이전 발행 (ApplicationEvent 상속 필요)
    eventPublisher.publishEvent(new CustomEvent(this,100));
  
    //4.2 이후 발행 (ApplicationEvent 상속 필요 없음)
    eventPublisher.publishEvent(new CustomEvent(100));
  }
}
```

## [3] 이벤트 구독

### <1> Event

#### 4.2 이전
- Listener는 Bean으로 등록되어야 한다.
- ApplicationEventListener를 구현해주어야 한다.
```java
@Component
public class MyEventHandler implements ApplicationListener<Customvent> {
    
    @Override
    public void onApplicationEvent(CustomEvent event) {
      // Perform some action when event occurred.
    }
}

```

#### 4.2 이후 (@EventListener)
- Reflection을 통해서 Spring이 EventHandler로 등록한다.
```java
@Component
public class MyEventHandler{
 
    @EventListener
    public void handleMyCustomEvent(CustomEvent event) {
      // Perform some action when event occurred.
    }
}
```



### <2> TransactionEvent
- Transaction 수행 시점에 따른 동작을 정의 할 수 있다.
- 데이터 정합성을 유지할 수 있다.

#### 4.2 이전 (Callback 방식)
1. TransactionSynchronization의 구현체를 생성한다.
2. TransactionSynchronizationManager에 등록한다.
```java
/**
 * Transaction이 시작되면, 
 * TransactionSynchronization객체를 새롭게 만들어 Thread와 연결한다.
 * TransactionSynchronization객체는 트랜잭션의 상태를 추적하며, 적절한 Method를 호출한다.
 * 
 * TransactionSynchronization은 AOP를 통해서 관리한다.
 * TransactionSynchronizationManaaer 는 AOP를 통해서 Trnasaction을 시작하는 시점에 해당 객체를 등록한다.
 */
public class MyTransactionSynchronization implements TransactionSynchronization {

    @Override
    public void beforeCommit(boolean readOnly) {
        // code to execute before commit
    }

    @Override
    public void afterCommit() {
        // code to execute after commit
    }

    @Override
    public void beforeCompletion() {
        // code to execute before completion
    }

    @Override
    public void afterCompletion(int status) {
        // code to execute after completion
    }
}

@Service
@RequiredArgsConstructor
public class MyService {
  
  private final PlatformTransactionManager transactionManager;

  public void doSomething() {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    TransactionStatus status = transactionManager.getTransaction(def);
    try {
      // do something
      TransactionSynchronizationManager.registerSynchronization(new MyTransactionSynchronization());
      transactionManager.commit(status);
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
  }
}

```

#### 4.2 이후 (@TransactionEventListener)
- Spring이 내부적으로 TransactionSynchronization을 등록해주는 것은 같다.
```java
@Component
@RequiredArgsConstructor
public class MyTransactionEventListener {
  
    private final MyService myService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(TransactionPhaseEvent event) {
        // Perform some action before the transaction is committed
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TransactionPhaseEvent event) {
        // Perform some action after the transaction is committed
        myService.doSomething();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void afterRollback(TransactionPhaseEvent event) {
        // Perform some action after the transaction is rolled back
    }
}
```