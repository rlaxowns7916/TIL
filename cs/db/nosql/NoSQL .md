# NoSQL
- HA(고가용성)와 Horizontal Scalability(수평확장)가 핵심이다.
- 빅데이터를 다룰 때 RDBMS의 한계점을 극복하기 위해서 만들어졌다.

## NoSQL의 특성
1. 데이터간의 관계를 정의하지 않는다. 
   - JOIN이라는 개념은 존재하지 않는다. 
2. 데이터의 형태가 고정되어 있지 않다.
   - 비정형 데이터 처리에 적합하다.
   - 유동적인 스키마 형태이다.
   - 예측할 수 없고 구조화 되지 않은 데이터 처리에 유리하다.
3. 대용량의 데이터를 저장 가능하다.
   - 페타바이트 급까지 저장이 가능하다
   - RDB에 비해서 확장성에 유리한점을 가진다.
     - RDB의 ACID 특성을 완벽하게 유지하면서 수평적 확장을 취하는 것은 불가능하다.
     - Eventual Consistency(결과적 일관성)을 취하는 방법 밖에 없다.
   - NoSQL은 복제나 클러스터링이 훨씬 쉽다.
4. 분산형 구조를 가지고 있다.
    - 하나의 고성능 Machine을 통한 처리가 아닌, 여러 대의 서버를 이용해서 분산처리 하는 구조이다.
    - 분산 및 복제를 통해서 데이터 안정성을 제공한다.
    - Document하나가 그자체를 표현 할 수 있기 때문에, RDB처럼 Join이 필요하지 않다.
    - RDB의 트랜잭션을 제공하지 않기 떄문에, 수평적 확장성 측면에서 유리하다
      - NoSQL에서의 트랜잭션은 보통 Document (== RDB row) 수준에서만 제공된다.
## NoSQL 데이터 모델 분류

### 1. Key-Value Model
- Redis, Dynamo DB
- 단순한 저장 및 조회기능을 제공한다.
- Access에는 빠르지만 RangeScan에는 불리하다.

### 2. Document Model
- Mongo, Azure Cosmos DB
- XML,JSON,BSON 등의 문서를 저장
- Key-Value에서 확장된 형태이다.
- Tree형 구조로 Record를 저장하거나 검색하는데 효과적이다.

### 3. Graph Model
- Neo4j
- 데이터를 Node로, 관계를 Edge로 표현한다.

### 4. Wide Column Model
- Cassandra, HBase, Druid
- Row마다 Column을 저장할 때, 각각의 형태가 다르다.
- 대용량 데이터의 압축, 분산, 집계의 성능이 뛰어나다.