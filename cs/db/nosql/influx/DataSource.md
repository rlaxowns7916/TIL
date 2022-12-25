# DataSource
- 다양한 DataSource를 지원한다.


## [1] InfluxDB
- InfluxDB에 쿼리를 날리기위해서는, Bucket과 Time조건이 필수이다.
  - from() 
    - bucket이름
    - bucketID
  - range()
    - 시간 조건
    - **start(required)**: 시작 시점을 정의한다.
    - stop: 마지막 시점에 대한 정의이다. (Default는 now() 이다.)

### 쿼리 구조
```text
from(bucket: "sample-bucket")
  |> range(start: -1h)
```

### 결과 구조
- _start: Range()에 정의된 시작 시간
- _stop: Range()에 정의된 종료 시간
- _time: 해당 Point의 TimeStamp
- _measurement: Measurement
- _field: 필드 Key
- _value: 필드 Value
- tags: 여러개의 tag가 데이터에 포함된다.