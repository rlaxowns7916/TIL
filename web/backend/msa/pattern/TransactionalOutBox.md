# Transactional OutBox Pattern
- MSA환경에서 Event Message 발행을 보장하기 위해 사용하는 패턴이다.
- Event발행에 앞서, OutBox라는 DB에 이벤트를 저장하는 것이다.
- 적어도 한번이상 (AtLeastOnce)의 이벤트 발행을 보장한다.

## 과정

### [1] Local Transaction
- Transaction을 활용하여 비즈니스 로직을 수행한다.
- 이 Transaction에는 발행할 이벤트의 OutBox로의 저장도 포함된다.

### [2] Event Relaying
- Event Relay Process (Publisher)가 OutBox를 주기적으로 Polling 한다.
- Polling 한 이후, MessageBroker (Kafka, RabbitMQ)에게 Message를 Publish 한다.
- 성공적으로 publish 했다면 OutBox에 저장된 Event의 상태를 변경한다.

### [3] Event Consumption
- Consumer가 MessageBroker로 부터 Message를 전달받아 로직을 수행한다.
- Consume의 실패에 대한 정책은 OutBox Pattern과는 상관없다.