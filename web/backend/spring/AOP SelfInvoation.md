# AOP Self Invocation
- AOP가 제대로 동작하지 않는 경우가 있다.
- 실무에서는 주로 @Transactional 사용문제에서 발생한다.
- AOP의 동작원리 떄문에 발생한다.


## 일어나는 상황
```java
@Service
@Transactional
@RequiredArgsConstructor
class TransactionService{
    private final TransactionRepository transactionRepository;
    
    public void out(){
        System.out.println("Start");
        this.in();
        System.out.println("End");
    }
    
    public void in(){
        for(int i=0;i<10;i++){
            transactionRepository.save(new Transaction(i));
            if(i == 10)
                throw RuntimeException("10!!");
        }
    }
}
```
- 똑같은 Bean에서 AOP가 동작되는 로직을 호출하는 상황이다.
- @Transactional의 경우 AOP로 동작하기 때문에 발생한다.

## 결과
- @Transactional이 제대로 동작하지 않는다.
  - 9 까지 정상적으로 Save 된다.
  - 10만 저장되지 않고 Rollaback이 동작된다.

## 이유
- Spring에서 AOP는 Proxy로 동작한다.
- 각각의 ProxyBean의 메소드가 실행되는 것이다.
- this를 사용하게되면 ProxyBean의 메소드가 아닌 실제 Bean의 메소드를 사용하게 된다.

## 해결법
- Bean을 분리한다.
  - 동일한 Bean에서 메소드를 호출하지 않으며, 다른 Bean으로 분리한다.
- SelfInjection을 사용한다.
  - @Autowired 혹은 DependencyLookUp을 통해서 SelfInejction을 수행한다.