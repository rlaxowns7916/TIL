# ComponentScan

- Annotation 기반으로 자동으로 SpringContainer가 Bean으로 등록하는 과정을 의미한다.

## Java Bean의 설정 방법

### XML 설정

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="memberService" class="com.study.spring.member.service.MemberServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository"/>
    </bean>

    <bean id="memberRepository" class="com.study.spring.member.dao.MemoryMemberRepository"/>

    <bean id="discountPolicy" class="com.study.spring.discount.RateDiscountPolicy"/>

    <bean id="itemService" class="com.study.spring.item.service.ItemServiceImpl">
        <constructor-arg name="itemRepository" ref="itemRepository"/>
    </bean>

    <bean id="itemRepository" class="com.study.spring.item.dao.MemoryItemRepositoryImpl"/>

    <bean id="orderService" class="com.study.spring.order.service.OrderServiceImpl">
        <constructor-arg name="discountPolicy" ref="discountPolicy"/>
        <constructor-arg name="itemRepository" ref="itemRepository"/>
        <constructor-arg name="memberRepository" ref="memberRepository"/>
        <constructor-arg name="orderRepository" ref="orderRepository"/>
    </bean>

    <bean id="orderRepository" class="com.study.spring.order.dao.MemoryOrderRepositoryImpl"/>
</beans>
```

### Java 설정

```java
import java.beans.BeanProperty;

/**
 * Spring 제공 Annotation을 통한 Bean 등록
 */
@Component
public class MyFirstBean() {

}

public class MySecondBean() {

}

@Configuration
public class MyBeanConfig() {
    @Bean
    public MySecondBean mySecondBean() {
        return new MySecondBean();
    }
}
```

- XML을 통한 수동 Bean 설정은 너무 신경 쓸 것이 많았다.
- Bean 의존관계의 누락 또한 빈번하게 발생하였다.
- 현재는 Java 기반 Bean 설정이 자주 사용된다.

## @ComponentScan의 설정법

### XML

```xml

<context:component-scan basepackage="com.study.spring"/>
```

### Java

```java

@Configuration
@ComponentScan
public class MyConfig() {

}
```

- @Configuration Annotation과 @ComponentScan Annotation을 기반으로 동작한다.
- 해당 Annotation이 붙은 Package를 기반으로 하위 Package 까지 Bean을 탐색하며 등록한다.

### @ComponentScan을 직접 사용해본적이 없는데?

```java

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {@Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)})
public @interface SpringBootApplication {
}
```

- 패키지 최상단에 있는 ApplicationClass의 @SpringBootAplication Annotation 떄문이다.
- @SpringBootApplication Annotation 내부에 @ComponentScan Annotation이 존재한다.
- @SpringBootApplication 자체가 @Configuration Annotation을 내재 하고 있기 떄문에 해당 Class를 기준으로 ComponentScan이 실행된다.
- SpringBootApplication 위치에 @ComponentScan을 사용하면 오버라이딩이 가능하다.
## @ComponentScan
- basePackages: Component Scan을 수행할 기준 Directory를 변경하는 설정이다.
- includeFilters: 특정 조건을 만족하는 Class 만 ComponentScan을 수행한다.
    - useDefaultFilters (default: true)를 통해서 @Component, @Service ... 등 Spring이 제공하는 Annotation에 대한 ComponentScan이 가능하다.
- excludeFilters: 특정 조건을 만족하는 Class를 ComponentScan에서 제외한다.

### @Filter

```java

@Retention(RetentionPolicy.RUNTIME)
@Target({})
@interface Filter {

    FilterType type() default FilterType.ANNOTATION;

    @AliasFor("classes")
    Class<?>[] value() default {};

    @AliasFor("value")
    Class<?>[] classes() default {};

    String[] pattern() default {};

}
```

- type: 어떠한 FilterType인지 명시한다.
- classes,value: Class를 지정한다.
- pattern: FilterType이 AspectJ이면, AspectJ Expression을, REGEX이면 정규식을 사용한다.

### @FilterType

```java
public enum FilterType {
    
    ANNOTATION,

    ASSIGNABLE_TYPE,

    ASPECTJ,

    REGEX,

    CUSTOM

}

```
- ANNOTATION: Java Annotation 기반의 ComponentScan
- ASSIGNABLE_TYPE: Class를 기준으로 가져온다. Class의 구현, 상속등을 고려한다.
- ASPECTJ: AspectJ 표현식을 기준으로 한다.
- REGEX: 정규식을 기준으로 한다.
- CUSTOM: 개발자가 직접 만든 Filter를 기준으로 동작한다.
  - TypeFilter Interface를 구현하여 만든다.