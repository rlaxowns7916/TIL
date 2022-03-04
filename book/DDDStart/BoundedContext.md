# Bounded Context
- Model은 특정한 Context에서 완전한 의미를 갖는다.
- Domain은 다시 여러 하위 Domain으로 구성된다.
  - 하위 도메인들의 의미를 결정하는 경계
- 각 Model마다 명시적으로 구분되는 경계를 가짐으로 하여, 의미가 섞이지 않도록 한다.
    - 하위 Domain Model마다 Model을 만들어 주어야한다.
        - 같은 용어라도 의미가 다르고 같은 대상이라도 지칭하는 용어가 다를 수 있기 때문에,한 개의 Model로 모든 하위 Domain을 표현하려는 시도는 올바른 방법이 아니다.
        - 공통적인 Model 을 가지게 되면, 하위 Domain마다 다르게 발전하는 요구사항을 반영하기 어려워진다.
- 같은 모델이여도, Context에 따라 다른 역할을 한다.(문맥에 따라 모델의 의미가 달라짐)   
  (인증 도메인에서의 사용자와, 주문 도메인에서 주문자)
- 하나의 Domain에 여러개의 BoundedContext가 존재 할 수 도 있다.
- 한개의 BoundedContext가 최소 한개의 Aggregate를 갖는다.

## BoundexContext의 구현

- Domain Model만을 포함하는 것이 아닌, PresentationLayer, ApplicationLayer, Infrastructure Layer도 포함한다.
- DB Schema 또한 BoundedContext에 포함된다.
- BoundedContext 마다 다른 기술로 구현할 수 있다.

## BoundedContext간 통합
- BoundedContext가 다르기 때문에, 같은 이름이지만 내용이 다른 Model들이 있다.
- 하나의 BoundedContext가 다른 BoundedContext를 이용함으로 해서 완전해지는 경우가 있다.
  ex) 카탈로그 시스템 <----- 상품 추천 시스템 (상품 추천시스템을 이용하여 유사한 상품을 하단에 보여준다.)
- REST API 혹은 MessageQueue를 통한 방법도 있다. 
- 두 BoundexContext가 같은 Model을 공유하는 경우도 있다. **(Open Kernel)**
  - 프로젝트의 크기가 클 수록 OpenKernel이 유리하다. (관리가 힘들기 떄문)
## BoundedContext간 관계
- Upstream(Publisher) & DownStream(Subscriber)
- DownStream은 Upstream에 의존적이다.
- Upstream Service는 여러 DownStream의 요구사항을 충족시키는 프로토콜을 정의하고 서비스 한다.
  (OpenHostService)
- DownStream은 Upstream의 **OpenHostService**를 이용하여 세부기능을 구현한다.
  - 이 떄, **Anticorruption Layer**을 두어, 외부 Domain이 내부 Domain에 침범하는 것을 막는다.
  (InfraStructure Layer에서 외부 시스템 연동 구현체에서, 외부 Domain을 내부Domain 으로 변환)

## Context Map
- 개별 BondedContext에 집중하여, 큰 그림을 보지 못할 때가 많다.
- 전체 Bussiness를 조망 할 수 있는 지도
- BoundedContext와 주요 Aggregate를 이용하여 표현한다.