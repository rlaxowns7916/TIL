# AOP Self Invocation
- AOP가 제대로 동작하지 않는 경우가 있다.
- 실무에서는 주로 @Transactional, @Async 사용문제에서 발생한다.
- AOP의 동작원리 떄문에 발생한다.

# Deep Dive
- @Transactional이 제대로 동작안한다 != 같은 Bean은 Transaction에 묶이지 않는다.
- AOP는 public만 가능하니, private나 protected method들은 Transaction에 묶이지 않는다? (X)



## 상황 1 - 같은 Bean & 시작 Method에 @Transactional, 호출당하는 Method에는 @Transactional(X)

<img width="745" alt="걸림1" src="https://user-images.githubusercontent.com/57896918/193854859-76b3eb27-fde0-478b-831a-3fca196ab0f8.png">
<img width="1212" alt="걸림2" src="https://user-images.githubusercontent.com/57896918/193854898-1708322b-4900-4bfb-b97c-b329ef591b0a.png">

- 호출하는 쪽의 Transactional에 그대로 묶인다.




## 상황 2 - 같은 Bean & 시작 Method에 @Transactional(X) , 호출당하는 Method에는 @Transactional
<img width="751" alt="안걸림1" src="https://user-images.githubusercontent.com/57896918/193855282-eff6653b-e9aa-4d7f-a51e-4152c1f117ab.png">
<img width="1000" alt="안걸림2" src="https://user-images.githubusercontent.com/57896918/193855307-f795b575-b26b-48cc-8c64-24ed699a61e5.png">

- 호출당하는 쪽에는 @Transactional이 걸려있음에도, 트랜잭션이 시작되지 않는다.

## 상황 3 - 다른 Bean & 시작 Method에 @Transactional, 호출당하는 Method에는 @Transactional(propagation = REQUIRES_NEW)

<img width="706" alt="다른Bean원인1" src="https://user-images.githubusercontent.com/57896918/193855635-1193cc5f-dd5b-42d4-a465-121dd6771a19.png">
<img width="743" alt="다른 Bean 원인2" src="https://user-images.githubusercontent.com/57896918/193855649-5c25b634-f336-477b-9341-0f58509802ad.png">
<img width="1276" alt="다른Bean결과" src="https://user-images.githubusercontent.com/57896918/193855742-b8282bea-dacb-4d86-9ba2-6f8ccefc02aa.png">

- 정상 동작한다.
- 각자의 Transaction 범위를 가진다.


## 상황 4 - 같은 Bean & 시작 Method에 @Transactional, 호출당하는 Method에는 @Transactional(propagation = REQUIRES_NEW)

<img width="745" alt="같은Bean" src="https://user-images.githubusercontent.com/57896918/193856059-68c2d5ea-7606-47b7-8ded-af6ca80e01e4.png">
<img width="1205" alt="같은 Bean 결과" src="https://user-images.githubusercontent.com/57896918/193856088-e7178286-53df-40ee-9d3e-749e723b04bb.png">

- 제대로 동작안함
- 호출하는쪽의 @Transactional을 그대로 따라간다.


## 이유
![스크린샷 2022-10-05 오전 12 16 15(2)](https://user-images.githubusercontent.com/57896918/193858563-346f364e-428a-4768-b08f-33c48c2a880b.png)

- Spring에서 AOP는 Proxy로 동작한다.
- 각각의 ProxyBean의 메소드가 실행되는 것이다.
- this를 사용하게되면 ProxyBean의 메소드가 아닌 실제 Bean의 메소드를 사용하게 된다.
- 시작 Method에 @Transactional이 있으면, TransactionAdvisor가 AOP 방식으로 트랜잭션을 하나로 묶게 되는 것이다.
    - 같은 Bean일 때는, Proxy가 아닌 원본객체의 메소드를 실행하기 때문에, 새로운 Propagation옵션등과 같은 호출당하는 Method는 @Transactional이 제대로 동작하지 않는다.
    - 시작이 @Transactional이면 어쨋든 묶인다.



## 해결법
- Bean을 분리한다.
  - 동일한 Bean에서 메소드를 호출하지 않으며, 다른 Bean으로 분리한다.
- SelfInjection을 사용한다.
  - @Autowired 혹은 DependencyLookUp을 통해서 SelfInejction을 수행한다.





