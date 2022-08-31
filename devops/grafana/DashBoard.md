# DashBoard
- Grafana에서 DataSource에 맞는 기본적인 Exporter를 제공한다.
- https://grafana.com/oss/prometheus/exporters/
  - id를 확인하고 쉽게 Import 할 수 있다.
- Library Panel, Json등 다양하게 생성 가능하다.

## NodeExporter DashBaord Example
<img width="1639" alt="dashBoard" src="https://user-images.githubusercontent.com/57896918/185949198-183926fd-f4f9-40df-990c-2a03f32c1ede.png">

## 설정

### Tags
- playlist에서 DashBoard를 불러올 때 사용 가능하다.

### Folder
- DashBoard가 속할 Folder를 지정 하는 것이다.

### TimeOptions

#### TimeZone
- TimeZone 설정
- 기본적으로 Grafana가 위치한 브라우저의 시간

#### Auto Refresh
- 판넬의 Auto Refresh 시간 Interval

#### now delay now
- 최근부터 ~ 설정 값 까지의 데이터를 숨긴다
- ex) now delay now가 1분이고, Relative Time Range 가 5분일 때,
  현재로부터 1분전 ~ 5분전 까지의 데이터를 표시한다.

#### Hide Time Picker
- 시간 관련 옵션을 DashBoard에서 숨긴다.

## Variable 
- 여러개의 변수를 선언할 수 있다.
- 변수에 맞는 Query를 매칭할 수 있다.
![variable](https://user-images.githubusercontent.com/57896918/186190537-31e21682-9af2-4223-8077-7a87cc30807d.png)

## Annotation
- 그래프에 포인트를 표시 하는 것이다.
- 이벤트 설명, 및 이벤트 태그를 볼 수 있다.
![annotation](https://user-images.githubusercontent.com/57896918/186190479-1e0092f0-01d3-425b-9ffb-95f94c83b860.png)

## Query
- 해당 데이터 소스에 맞는 질의
