# Record
- 아래와 같은 동의어들이 있다.
    - Message
    - Event
    - Message
    - Data .

## 구성 요소
### Timestamp
- Stream Processing을 하기 위한 시간을 저장하는 용도로 사용된다.
- 기본 값으로 Producer의 Record 생성 시간이 들어간다.

### Offset
- Producer가 생성한 Record에는 존재하지 않는다.
- Broker에 적재될 때 Record에 지정되며, 0부터 시작하여 1씩 증가한다.
- Consumer가 Offset 기반으로 처리했던 데이터와 처리해야 할 데이터를 구분한다.
  - Consumer Group 마다 독립적인 Offset을 가진다.

### Headers
- key/value 데이터를 추가 할 수 있다.
- schema 버전, 포맷과 같이 Data Processing에 필요한 정보를 담아 사용할 수 있다.

### Key
- Record 분류를 위해서 사용한다.
- Record를 Partition에 분류하기 위한 용도이다.
- null이면 RoundRobin, 값이 존재하면 Hash를 통해서 분류된다.

### Value
- 실제 저장 할 Data
- 직렬화, 역직렬화가 가능하다.
- Consumer는 역직렬화 포맷을 알고있어야만 한다.

## Record의 영속성
- Consumer가 Consume했다고 사라지지 않는다.
  - 옵션에 따라서 달라진다.
- Log Segment 단위로 삭제된다.
