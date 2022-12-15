# Flux
- 1.8v (기본적으로 disable)부터 사용 할 수 있다.
  - 이전에는 InfluxQL사용 가능 (호환성 API 사용시에 2.x v 이상에서도 사용가능)
- InfluxDB 및 기타 데이터 소스에서 데이터를 쿼리하고 처리하도록 설계된 스크립트 언어

## 예약어
1. _field
2. _measurment
3. time

 ```shell
    from(bucket:"iac-ftp-daemon")
    |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
    |> filter(fn: (r) => r._measurement == "iac-monitoring" and r,_deliveryCompany == "hanjin" and r.type == "REQ" )
    |> filter(fn: (r) => r._field == "sendCount")
    |> group(columns: ["sendCount"], mode: "by")
    |> map(fn: (r) => ({"한진 택배 요청 건수": r._value, _time: r._time}))
 ```
- measurement는 r._measurement로 접근한다.
- tag는 r.[태그이름]으로 접근한다.
- field는 r._field로 접근한다.

## Flux 구성 요소

### 1. Source
- Input 함수를 통해서 다양한 데이터소스에서 검색가능하게한다.
  - 시계열 데이터베이스 (InfluxDB), Promethues, RDB (MySQL, Postgres), 등등
- DataSource: https://docs.influxdata.com/flux/v0.x/query-data/
- InputFunction: https://docs.influxdata.com/flux/v0.x/function-types/#inputs

### 2. Filter
- 조건이 일치하는지 true / false로 구분하여 true만 다음 동작으로 넘어갈 수 있게 한다.
- 대표적으로 range()와 filter()가 있다.
- https://docs.influxdata.com/flux/v0.x/function-types/#filters

### 3. Shape
- Process 하기 위해서 데이터의 구조를 변경하는 Function이다.
- Group화 하거나 Pivot하는 작업이 주를 이룬다.
  - group(): 그룹 키 수정
  - window(): _start부터 _stop까지 시간별로 데이터를 그룹화한다.
  - pivot(): 열 값을 행으로 pivot
  - drop(): 특정 열 삭제
  - keep(): 특정 열을 유지하고 나머지는 삭제

### 4. Process
- 다양한 형태의 데이터 처리를 가능하게 한다.
  - 중간연산
  - 종결처리
- 데이터 집계 : https://docs.influxdata.com/flux/v0.x/function-types/#aggregates
- 특정 Point 선택: https://docs.influxdata.com/flux/v0.x/function-types/#selectors
- 변환 (map)
- 알림보내기 (alert)



## Flux 필수 구성 요소

#### 1. from()
- InfluxDB Bucket을 지정한다.

#### 2. range()
- 시간 범위를 기준으로 데이터를 필터링한다.
- Flux에서 제한된 시간범위는 **필수** 이다.
- start와 stop으로 이루어진다.

```shell
from(bucket: "example-bucket")
    |> range(start: 2021-05-22T23:30:00Z, stop: 2021-05-23T00:00:00Z)
    
from(bucket: "example-bucket")
    |> range(start: 1621726200, stop: 1621728000)

from(bucket: "example-bucket")
    |> range(start: -12h)      
```

#### 3. filter()
- Column 값을 기준으로 데이터를 필터링한다.
- 각 Column은 r을 통해서 접근이 가능하다.
- Filter를 체이닝 할 수 있다.
```shell
import "sampledata"

sampledata.int()
    |> filter(fn: (r) => r._value > 0 and r._value < 10)

from(bucket: "example-bucket")
    |> range(start: -1h)
    |> filter(
        fn: (r) => r._measurement == "cpu" and r._field == "usage_system" and r.cpu == "cpu-total",
    )
```