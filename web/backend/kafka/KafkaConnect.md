# KafkaConnect
- 불필요하게 Producer와 Consumer를 개발해야 하는 공수를 줄여준다.
- 미리 작성되어, Kafka와 이기종을 연결하고, DataStreaming을 하기위한 Framework이다.
- https://www.confluent.io/hub/
  - 유/무료 connect들을 확인 가능하다.
- REST API를 지원한다.
  - Connector, Task를 동적으로 관리 가능하다.
# 용어 정리
## [1] Connector
- Task를 관리하고, DataStreaming을 조정하는 역할
- 특정 DataSource (DB, FileSystem, Cloud ...) 혹은 System과 Kafka를 연결하는 역할
  - SourceConnector (Producer)
  - SinkConnector (Consumer)
- plugin(jar), Java Class(instance)

## [2] Tasks
- Kafka와 이기종간 데이터를 전달하는 구현체 
- Connector의 실행 단위이다.
  - Connector가 여러 Task로 나누어서 실행한다.
- Java Class(instance)

## [3] Workers
- Connector 및 Task를 실행하는 Process

## [4] Converter
- Connect와 주고받는 시스템 간의 Data를 변환하는 Component
- 대부분 SchemaRegistry와 연결하는 구조로 구성되어 있다.
- String, Json, Avro 등 다양한 형태를 지원 할 수 있다.

## [5] Transforms
- Task와 Converter 사이에서 동작한다.
- Data의 특정 필드나 구조를 변환하거나, 필터링
- Data의 일부를 수정, 추가, 제거하는 작업을 처리

## [6] DeadLetterQueue
- Connector 오류를 처리하기 위해서 사용된다.

# 실행환경

## [1] StandAlone
- 단일 Worker로 실행
- Connector, Task가 동일한 JVM환경에서 실행된다.
- Connector 설정 및 상태정보는 Local에 저장

## [2] Distributed
- 여러 Worker에서 Cluster로 동작, 부하분산에 용이하다.
- 작업이 자동으로 분산되고, 하나의 Worker가 실패하면 다른 Worker가 승계받음
- Connector 설정 및 상탱정보는 KafkaTopic에 저장