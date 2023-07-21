# Reactive Programming
- 어떤 이벤트에 반응하여, 그에 맞는 적절한 행동을 수행하는 것.
- 클라이언트의 요청에 즉각적으로 응답하는 것을 목표로하여, 수행시간을 최소화 하고자 한다.
- **NonBlocking I/O 기반, 비동기 메세지 통신이다.**
- 가독성과 간결함을 가지기 위해서, Method Chain 혙태의 프로그래밍을 수행한다.

## 목표
1. 비동기 메세지 기반 NonBlocking I/O 통신
2. 탄력성과 회복성
3. 높은 응답성
4. 유지보수와 확장성

## 특징
### [1] 선언적 프로그래밍 (Declarative Programming)
- 실행할 동작의 목표만을 선언할 뿐, 구체적인 동작을 정의하지 않는다.
- 전통적인 명령형 프로그래밍과 반대 된다.

### [2] DataStreams & Propagation Of Change (데이터 흐름 & 변화의 전파)
- 지속적으로 발생하는 데이터 흐름(DataStream)을 가진다.
- 변화의 전파(Propagation Of Change)를 통해서, 이벤트를 발생시키면서 지속적으로 데이터를 전달한다.

## 구성요소

### [1] Publisher
- Subscriber가 사용할 데이터를 제공하는 역할을 한다.

### [2] Subscriber
- Publisher가 제공하는 데이터를 소비하는 역항르 한다.

### [3] DataSource
- DataStream이라고도 불린다.
- Publisher의 Input으로 전달 될 데이터를 의미한다.

### [4] Operator
- 중간연산이다.
- Publisher에서 Subscriber에게로 전달 될 때 중간 가공 연산을 의미한다.