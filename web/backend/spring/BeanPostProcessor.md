# BeanPostProcessor(빈 후처리기)
- Bean으로 등록할 객체를 등록하기 전에 조작하는 용도로 사용한다.
- Bean을 조작하고 변경할 수 있는 포인트이다.
- @ComponentScan으로 조작할 수 없었던 프록시를 조작할 수 있게된다.
- BeanPostProcessor를 구현하고 @Bean으로 등록하면 된다.
- 모든 Bean이 대상이되기 떄문에, PointCut으로 해당되는 Bean이 아니면 걸러주어야한다.

```java
public interface BeanPostProcessor {
 Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
 Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
```

### 처리과정 (+ LifeCycleCallback)
1. Bean 대상이되는 객체를 생성한다.
2. BeanPostProcessor에 전달한다.
3. postProcessBeforeInitialization를 실행한다.
4. 초기화 Callback (@PostConstruct)를 실행한다.
5. postProcessAfterInitialization를 실행한다.
6. 저장소에 전달한다.
7. 컨테이너 종료 후 소멸 Callback (@PreDestroy)를 실행한다.


![빈 생명주기](https://user-images.githubusercontent.com/57896918/158820479-dde3ecaa-e37d-4212-b9a7-d218e3cd989f.png)



## AutoProxyCreator
- SpringBoot-AOP를 의존성으로 갖는다.
- AnnotationAwareAspectJAutoProxyCreator가 자동으로 Bean으로 등록된다.
  - Bean으로 등록된 **Advisor**를 찾아 자동으로 Proxy를 적용해준다.
  - @Aspect 에노테이션이 붙은 것도 찾아서 자동으로 Proxy를 적용해준다.

### AutoProxyCreator 동작 과정
1. Bean 등록 대상이 되는 객체를 생성한다.
2. BeanPostProcessor에 모두 전달한다.
3. AutoProxyCreator는 Bean으로 등록된 모든 Advisor를 찾는다.
4. Advisor에 포함된 PointCut을 활용하여 대상이되는 Bean을 찾는다.
   - Class조건 뿐만 아니라, Method 조건까지 확인한다.
5. 프록시를 생성한다.
6. 프록시를 저장소에 반환한다.
