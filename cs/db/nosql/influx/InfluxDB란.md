# InfluxDB란? 
- 시계열 데이터 수집, 저장, 처리 및 시각화의 기능을 제공
  - 시계열 데이터: 시간 순서데로 인덱싱된 일련의 데이터 포인트
  - 동일한 소스에 대해서, 시간 경과에 따른 변화를 추적하는데 용이하다.
- CLI, WebUI, HTTP API를 통해서 접근 가능하다.

## 데이터 구성

### 1. Bucket
- 시계열 데이터에 대한 논리적 그룹이다.
- 여러개의 Measurement를 가지고 있을 수 있다.
- RDB의 DataBase와 유사한 개념이다.

### 2. Measurement
- 여러개의 Tag와 Field를 포함하고 있다.
- RDB의 테이블과 유사한 개념이다.

### 3. Tag
- 필수는 아니다.
- 값이 다르지만 **자주변경되지 않는** Key-Value 페어이다.
- 데이터를 식별 할 수 있는 값이 되어야 한다.
  - host, location ...
- Indexing이 되어 있어, 쿼리의 성능을 향상시킬 수 있다.

### 4. Field
- 시간에 따라 자주변하는 Key-Value 페어이다.
  - Temperature, Pressure, StockPrice ...
- Indexing이 되어있지 않은 데이터이며 쿼리 시 모든 데이터를 스캔하게 된다.

## 사용되는 용어의 정의

### 1. Point
- Measurement, Tag(K,V), Field(K,V), TimeStamp로 이루어진 단일 데이터 레코드이다.
- **Measurement, TimeStamp, Tag Set으로 고유하게 식별된다. (FieldSet은 고려대상이 아니다.)**
  - 충돌 시 새로운 Point가 유리하다(?)

### 2. Series
- 여러개의 Point로 이루어진 그룹이다.   
![스크린샷 2022-12-14 오후 11 05 01(2)](https://user-images.githubusercontent.com/57896918/207619953-62664e63-625e-4a5c-93d9-6269bcbdfe15.png)

## Line Protocol
- InfluxDB에서 읽기 / 쓰기 에 이용되는 기본적인 프로토콜이다.
- Telegraf, 각 언어의 Client들이 자동적으로 프로토콜을 빌드해준다. 
- Measuremet와 다른 요소들의 구분은 ,(1st Comma)를 통해서 구분된다.
- **나머지 요소(Tag,Field,Time) 의 구분은 이스케이프 처리되지 않은 공백 이다.**

###  LineProtocol 구성 요소
- Measurement **(Required)**
  - Measurment 구분을 위함
- Tag Set
  - Key=Value 형식이다.]()
  - 대소문자를 구분한다. 
  - Comma(,)로 구분되는 문자열이다.
  - 공백이나 특수문자는 이스케이프 처리 해주어야 한다.
- Field Set **(Required)** 
  - Key=Value 형식이다. 
  - 대소문자를 구분한다.
  - Comma(,)로 구분되는 문자열이다.
  - 공백이나 특수문자는 이스케이프 처리 해주어야 한다.
  - string, float, integer, unsigned integer, boolean이 가능하다.
- TimeStmap **(Required)**
  - Key=Value 형식이다. 
  - UnixTimeStamp
  - ns(NanoMilliSecond)까지 지원한다.
  - Data에 포함되지 않는다면, Host Machine의 System시간을 사용한다. (UTC)

![스크린샷 2022-12-14 오후 11 06 55(2)](https://user-images.githubusercontent.com/57896918/207619988-ff2d16bb-4ab2-49a2-a8f3-b88e68834d95.png)

```shell
home,room=Living\ Room temp=21.1,hum=35.9,co=0i 1641024000

# Measurement: home
# Tag: Living Room
# Field: temp=21.1, hum=35.9, co=0i
# TimeStmam: 1641024000
```
