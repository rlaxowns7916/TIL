# DomainDrivenDesign
- 사용자의 관점에서 바라봄
- 개발과 변경이 필요한 부분을 쉽게 찾을 수 있음
- 소프트웨어 복잡성을 줄이기 위한 Design
    - 소프트웨어의 복잡성은 어디서오는가?
      - 결국 Domain에서 온다.
      - DomainModel을 통해서 복잡성을 제어하고자 한다.

## 도메인이란?
- 사용자가 사용하는 것
- 실세계에서 사건이 발생하는 집합
- 도메인은 다시 하위 도메인으로 구성된다.
    - 각각의 도메인에 똑같은 객체가 존재 할 수 있다.
    - 같은 객체라도 문맥(Context)에 따라서 역할이 바뀐다.
- 도메인 모델을 활용하여 표현 할 수 있다.
    - 도메인의 핵심로직은 주로 도메인 모델에서 구현되어야한다.
    - Entity(엔티티)와 벨류 모델(Value Model)로 나뉠 것이다.

**OOP에서 객체는 추상화 또는 구체화 할 수 있는 요소를 설명하지만, Domain은 사용자가 사용하는 것에 초점**

## 유비쿼터스 언어
- 도메인전문가와 개발자간의 언어를 일치시키는 것.
- 실제 구현과 도메인의 차이를 줄이기 위해서 일치시킴

## BoundedContext
- 도메인간의 경계
- BoundedContext가 명확하면 높은 응집력과 낮은 결합도를 갖는다.

## Layer
- 레이어간의 참조관계는 **단방향**을 유지한다.
  - 레이어간의 의존성을 줄이기위함 
- 계층간의 호출은 **Interface**를 통해서 한다.
- DIP(의존관계역전원칙)를 지키는 것이 핵심이다.
- Transaction을 가지는 로직과 그외의 로직을 Aggregate하는 역할이다.

### 1. Presentation Layer (Controller,Dto,Mapper)
- 사용자에게 정보를 주고 사용자의 명령을 해석
- 외부 통신 (Http,gRpc...)

### 2. Application Layer (Facade)
- 소프트웨어가 수행할 작업을 정의
- 도메인에게 작업을 위임
- 작업을 조정하기만 하기때문에, 도메인의 상태를 가지면 안된다.
- 도메인 로직이 ApplicationLayer에 나타나면 안된다.

### 3. Domain Layer (Entity,Service,Command,Factory,Executor...)
- 업무개념, 업무표현등을 담당하는 핵심 Layer
- 세부구현 기술에 관심(x), 로직의 실행순서가 중요
- Entity를 통한 도메인 로직 실행
- 개념, 정보, 규칙을 표현하는 일을 맡는다.
- 상태저장은 InfraStructure에 위임한다.
    - Infrastrucutre에 위임함으로 해서, 하나의 동작에 여러가지 방식을 고려 할 수 있게된다
      (데이터 저장에 NoSQL || RDB )

### 4. Infrastructure Layer (Repository)
- 외부 API, DB, 캐시 등 
- 상위계층을 지원하는 기술적 기능을 제공
- 메세지전송, 도메인 영속화 등의 작업을 담당.
- DomainLayer에서 선언한 인터페이스의 구현체

![layerdArhitecture](https://user-images.githubusercontent.com/57896918/154838795-5b2cc65d-7aeb-4d81-a181-01773c970c67.png)


## Aggregate (집합)
- 객체들의 집합
- AggregateRoot와 여러개의 VO(ValueObject)로 구성되는 경우가많다.
- 다른 Domain에서의 참조는 AggregateRoot를 통행서만 가능하다.
- 같은 Domain에서는 서로 참조가가능하다.
- AggregateRoot에 해당되는 Entity에만 Repository를 만드는 경우가많다.
  - 데이터무결성을 유지하기 위함이다. 
- 제약조건(Invariants)가 있어야 같은 Aggregate에 속할 수 있다.

### Aggregate Rule
#### 1. 다른 Aggregate Root를 참조할 경우, PK로 참조하라
- OOP에는 위배되지만, 약한 결합을 갖게된다.

#### 2. 한 트랜잭션 내에서는, 하나의 Aggregate만 변경한다.
- Aggregate는 일관성을 유지하는 하나의 단위이다.

#### 3. Aggregate는 작은단위로 쪼갠다.
- 일관성 유지에는 쉬우나, 확장성에서 좋지않다.
