# Schema Registry
- Producer와 Consumer사이의 Message의 Schema를 저장하여 Decoupling하는 것이 목적이다.
- Message에 Schema구조를 동봉하는 것이 아닌, id를 동봉하며 LocalCache를 사용하며 필요할 때만 Schema Registry에서 데이터를 갱신한다.

## Schema란?
- Producer와 Consumer사이에 주고받는 Message의 형태를 말한다.
- 서로 주고받을 Message의 형태를 Producer와 Consumer가 협의 하여 각각정의한다면 의존성이 높아진다.
- Schema Registry는 이러한 문제를 해결해준다.
  - Schema를 각각 정의하지 않기 때문에 확장성에 유리하다.
  - Schema도 서비스의 발전에 따라 변화하게 되는데, 이를 Schema Evolution이라고 한다.


## Avro
- Confluent SchemaRegistry에서 사용가능
  - Apache Open Source 프로젝트
- 데이터 Serialization 에 사용
- 다양한 언어 지원
- 데이터 구조 형식화 제공 
  - Schema의 경우 Json 형태로 저장한다.
  - 데이터 타입도 저장한다.
  - Schema Evolution에 대응 가능하다.
- Binary이기 때문에 데이터를 효율적으로 저장한다.
  - 디버깅이 어렵다는 단점도 있다.


## Confluent Schema Registry
- 모든 Schema 버전 기록을 저장한다.
- Avro Schema 저장, 검색을 위한 REST API를 제공한다.
- Schema 확인 후, Data가 Schema와 일치하지 않으면 에러를 던진다.
- Kafka에 내부 토픽을 생성하여 schema를 저장한다.
  - "_schemas"

## 순서
1. Producer가 Message를 보내기전에, Local Cache에 Schema 정보가 있는지 확인한다.
2. 없으면 SchemaRegistry에 요청을 보낸다.
   - Schema Registry는 새로운 Schema가 등록되면 producer에게 id를 내려준다.
3. producer는 Message에 Schema ID를 동봉해서 Produce 한다.
4. Consumer는 schmea의 ID를 보고 Local Cache에서 Schema 정보를 확인한다.
5. 없으면 Schema Registry에서 정보를 갱신한다.