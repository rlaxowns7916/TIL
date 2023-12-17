# Hibernate QueryPlan Cache
- 대부분 JPA의 구현체로 Hibernate를 사용한다.
- Hibernate에서는 쿼리 컴파일 과정을 최적화하기위해서 QueryPlanCache라는걸 사용한다.
  - ```text
       Hibernate에서는 JPQL (Java Persistence Query Language) 쿼리나 Criteria 쿼리를 실행하기 전에 이를 Abstract Syntax Tree (AST)로 파싱하여 SQL 문을 생성합니다. 
       쿼리 컴파일 과정에 시간이 소요되기 때문에, Hibernate는 성능 향상을 위해 QueryPlanCache를 제공합니다.
       네이티브 쿼리의 경우, Hibernate는 명명된 매개변수와 쿼리 반환 유형에 대한 정보를 추출하고 이를 ParameterMetadata에 저장합니다.
       쿼리를 실행할 때마다 Hibernate는 먼저 계획 캐시를 확인하고, 캐시에 계획이 없는 경우 새로운 계획을 생성하여 이후 참조를 위해 캐시에 저장합니다.
       요약하자면, Hibernate는 쿼리의 파싱 및 컴파일 시간을 줄이기 위해 쿼리 실행 계획을 캐싱하는 기능을 제공하며, 이를 통해 반복적인 쿼리 실행의 성능을 향상시킵니다. 네이티브 쿼리에 대해서도 매개변수와 반환 유형 정보를 캐싱하여 처리합니다.
    ```
- 기본설정을 사용할 때 OOM이 발생할 수 있다.

# 자주발생하는 트러블 슈팅 (파라미터 크기가 다양한 in 절)
- QueryPlanCache의 기본용량은 2048mb
- in절을 사용하면 갯수가 고정적이지 않게되기 떄문에 많은 수의 QueryPlanCache가 생성된다.
  - in절에서는 Parameter의 갯수에 따라서 Cache가 달라진다.
- 2048에 근접하거나, 그보다 적은 JVM 용량이라면 문제 발생의 여지가 있다.

## 해결법

### [1] Padding주기 
- ```yaml
spring:
    jpa:
        properties:
            hibernate:
                query.in_clause_parameter_padding: true
```
```
- 2의 제곱 단위로 QueryPlanCache를 만들게한다.
  - ```text
        # 실행 SQL문
        select ~ from ~ where id in (1,2,3)
        select ~ from ~ where id in (1,2,3,4,5)
    
        # Query Plan Cache
        select ~ from ~ where id in (1,2,3,3)
        select ~ from ~ where id in (1,2,3,4,5,5)
    ```
- QueryPlanCache는 그대로 타면서, 갯수는 조절 할 수 있다.

### [2] QueryPlanCache 크기 조정하기
- 캐시의 크기를 제한한다.
- 가장 간단한 방법이다.
- ```yaml
      spring:
        jpa:
          properties:
            hibernate:
              query:
                plan_cache_max_size: 1024
  ```

