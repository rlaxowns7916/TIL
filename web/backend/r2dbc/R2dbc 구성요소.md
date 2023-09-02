## Entity
- Database의 하나의 Row와 매칭되는 Class이다.
- R2dbcEntityTemplate, R2dcRepository는 DB에 요청을 보내고, Entity로 매핑해서 반환한다.

## R2dbcEntityTemplate
- SpringDataR2dbc의 추상화 클래스이다.
- SQL 쿼리를 문자열 형태로 넘기거나, Method Chaining을 통해서 Query를 수행하고 결과를 Mapping할 수 있다.
- **R2dbcEntityOperations를 구현했다.**

## R2dbcDialect
- R2dbc버전의 Dialect
  - bindMarketFactory, convert 등 Database에 특화된 정보를 제공한다.

## R2dbcEntityOperations
- DatabaseClient를 제공한다.
  - ConnectionFactory를 Wrapping하여, 결과를 Map이나 Integer로 제공
- R2dbcConverter를 제공한다.
  - 주어진 Row를 Entity로 만드는 Converter이다.

## DatabaseClient
- 내부에 포함된 ConnectionFactory에 접근 가능하다.
- sql 메소드를 통해서, GenericExecuteSpec을 반환한다.
    - bind를 통해서, patameter를 sql에 추가한다.
    - ```java
        GenericExecuteSpec sql(String sql); 
      ```
- fetch를 통해서 fetchSpec을 반환한다.
  - one, first, all 을 통해서 Mono,Flux등을 받는다.
  - UpdatedRowFetchSpec을 통해서, 영향을 받은 Row의 갯수를 Mono로 받을 수도 있다.
    - Mapping은 직접 수행하여야 한다.
      - ```java
            createTableMono.then(insertMono)
                .thenMany(selectAllFlux)
                .doOnNext(result ->{
                    var id = (Integer) result.get("id");
                    var name = (String) result.get("name");
                    var age = (Integer) result.get("age")
        
                     log.info("{},{},{}",id,name,age)
                })      
        ```
        

## R2dbc Converter
- EntityReader와 EntityWriter를 상속
  - 구현체로 MappingR2dbcConverter가 있다.
- 다양한 전략을 통해 Object -> Row, Row -> Object로 변환한다.
  - Custom Converter (1순위)
  - SpringData ObjectMapping (2순위)
  - convetion 기반 Mapping
  - metadata 기반 Mapping

### CustomConverter
- Configuration을 통해서 converter를 등록한다.
- Target Class를 지원하는 Converter를 탐색한다.
  - Row -> TargetClass (read)
  - Target -> OutboundRow (Write)
- ReadConverter와 WritingConverer로 나뉜다.
  - @ReadingConverter, WritingConverter 에노테이션을 지원한다.
  - Converter Interface를 구현하고, Source와 Target을 Generic으로 구분한다.

### SpringDataObjectMapping
- 지원하는 Converter가 없다면 수행된다.
- MappingR2dbcConverter가 다음의 과정을 수행한다.
  1. Constructor, factoryMethod를 통해서, Row Column들을 Object로 생성한다.
  2. Property Population, setter, with 등의 메소드를 통해서 Row의 Column을 Object에 주입한다.
- **Property들이 다 Mutable 해야한다.**

### ObjectMapping 최적화 방법
1. 객체는 가능한 Immutable 하게 생성해야 한다.
2. 모든 Property를 인자로 갖는 All-args를 제공해야 한다.
3. property population을 사용하려면 mutable해야 하나, 지양해야 한다.