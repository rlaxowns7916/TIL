# ProxyFactory (프록시 팩토리)
## 특징
- Spring이 Proxy를 만드는 방법을 추상화 한것
    - 특정 Proxy 구현 기술에 의존하지 않게된다.
- 인터페이스면 JdkProxy, 클래스 기반이면 CGLIB으로 만든다.
    - 설정을 통해서 인터페이스여도 CGLIB으로 구현하게 할 수 있다.
- 여러개의 Advisor 적용이 가능하다.

## 한계
- ComponentScan에 적용이 불가능하다.
  - ApplicationContext가 Proxy설정전에 다 Bean으로 등록
  - BeanPostProcessor를 사용해야 한다.
- @Configuration 클래스가 많아진다.
  - ComponentScan이 불가능 하기 때문에, 수동Bean설정을 해주어야 한다.

```java
class ProxyFactoryTest() {
    public void proxyTest(){
      CustomService target = new CustomServiceImpl();
      ProxyFactory proxyFactory = new ProxyFactory(target);
      DefaultPointcutAdvisor advisor = new
              DefaultPointcutAdvisor(Pointcut.TRUE, new CustomAdvice());
      proxyFactory.addAdvisor(advisor);
      
      CustomService proxy = proxyFactory.getProxy();
    }
}
```
- ProxyFactory 객체 생성시에 원본 객체를 넘긴다.
- ProxyFactory에 여러개의 Advisor를 추가 할 수 있다.
- Advisor추가시에  Advice와 PointCut을 넘겨야 한다.

## PointCut
- Spring에서 제공하는 Interface
    - 여러 PointCuot 구현체를 제공한다.
    - JdkRegexpMethodPointcut, AnnotationMatchingPointcut ...
- 부가기능을 어디에 적용할지 적용하지 않을지에 대한 정보이다.
- Class 필터링 정보와 Method 필터링 정보를 구현해야 한다.

```java
package org.springframework.aop;
public interface Pointcut {
    ClassFilter getClassFilter();
    MethodMatcher getMethodMatcher();
}
```

- 둘 다 True 일 때 Advice가 적용된다.
- ClassFilter와 MethodMatcher 모두 Interface이다 (구현 필요)

## Advice

- PointCut에 해당하는 곳에 적용할 프록시로직이다.
- MethodInterceptor를 구현해야 한다.

```java
package org.aopalliance.intercept;

@FunctionalInterface
public interface MethodInterceptor extends Interceptor {
    @Nullable
    Object invoke(@Nonnull MethodInvocation invocation) throws Throwable;

    class CustomAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            /**
             * 전처리
             */
            Object result = invocation.proceed();
            /**
             * 후처리
             */
            return result;
        }
    }
}
```

## Advisor
- PointCut과 Advice가 합쳐진 것이다.
- 기본 Interface Advisor와, 이를 상속하는 PointCutAdvisor가 있다.
```java
package org.springframework.aop.support;

import java.io.Serializable;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
@SuppressWarnings("serial")
public class DefaultPointcutAdvisor extends AbstractGenericPointcutAdvisor implements Serializable {

	private Pointcut pointcut = Pointcut.TRUE;
    
	public DefaultPointcutAdvisor() {
	}
	public DefaultPointcutAdvisor(Advice advice) {
		this(Pointcut.TRUE, advice);
	}
	public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
		this.pointcut = pointcut;
		setAdvice(advice);
	}
	public void setPointcut(@Nullable Pointcut pointcut) {
		this.pointcut = (pointcut != null ? pointcut : Pointcut.TRUE);
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}


	@Override
	public String toString() {
		return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice [" + getAdvice() + "]";
	}

}
```
- 가장 기본적인 Advisor 구현체
- PointCut의 메소드 (ClassFilter, MethodMatcher) 두개가 True일 때 Advice적용