# Variables

## [1] bool
- true
- false
- bool()을 통해서 변환이 가능하다.
```shell
> string: "true" 또는 "false" 여야 한다.
- isFalse = bool("true")

> float: 0.0(거짓) 또는 1.0(참) 이어야 환다.
- isTrue = bool(1.0)

int & uint: 0(거짓) 또는 1.0(참) 이어야 한다.
- isFalse = bool(0)
```

### _value를 Bool로 변환
- Value가 위의 자료형이고 조건에 만족한다면 Bool로 변환된다.
```shell
_value: 1.0

data
  |> toBool() # true로 변환
```

### not 연산자
```shell
not true # false
not false # true
```

## [2] bytes
- 일련의 Byte 값을 나타낸다.
- bytes()는 입력 Source를 Byte로 치환한다.
```shell
bytes("Hello") #Hello 문자열을 Byte로 치환한다.
```

## [3] duration
- 시간을 나타내는 역할이다.
- 그 중에서 기간을 나타낸다.
```shell
ns: 나노초
us: 마이크로초
ms: 밀리초
s: 초
m: 분
h: 시간
d: 일
w: 주
mo: 월
y: 년
```

### 다른 자료형과의 변환
- duration() 함수를 통해서 변환이 가능하다.
- duration 자료형은 int()로 변환 가능하다. (숫자로 치환 가능)
- 기본적으로 숫자와의 덧셈도 가능하다.
```shell
string: 문자열이 위의 단위 연산자를 만족해야 한다.
> duration("1h30m") # 1시간 30분 Duration 자료형으로 변환

- int & uint: ns(나노초)로 변환된다.
> duration(v: 1000000) #1ms
> duration(v: 3000000000) #3s
```

### 숫자형과의 연산
```shell
duration(v: int(v: 6h4m) + int(v: 22h32s)) #1d4h4m32s

duration(v: int(v: 22h32s) - int(v: 6h4m)) #15h56m32s

duration(v: int(v: 32m10s) * 10) # 5h21m40s

duration(v: int(v: 24h) / 2) #Returns 12h
```

## [4] string
- **큰 따옴표로 묶인 일련의 문자열이다**
- Escape Sequence와 16진수 문자열을 지원한다.

### 다른 자료형과의 변환 (다 가능)
- bool
- bytes
- duration
- float
- int
- uint
- time

### 문자열 연결
- Java와 같은 + 연산이다.
```shell
name = "John"
"My name is " + name + "." # My name is John.

d = 1m
"the answer is " + string(v: d) #the answer is 1m

t0 = 2016-06-13T17:43:50Z
"the answer is " + string(v: t0) #the answer is 2016-06-13T17:43:50.000000000Z

p = {name: "John", age: 42}
"My name is " + p.name + " and I'm " + string(v: p.age) + " years old." #My name is John and I'm 42 years old.
```

## [5] time
- ns(나노초)의 정확도를 기준으로 한다.

### 다른 자료형과의 변환
- string
- int 
- uint
```shell
time(v: "2021-01-01") # 2021-01-01T00:00:00.000000000Z

time(v: 1609459200000000000) #2021-01-01T00:00:00.000000000Z

time(v: uint(v: 1609459200000000000)) #2021-01-01T00:00:00.000000000Z
```

### _value를 time으로 변환
- toTime()함수이다.
- 위의 자료형을 만족해야 한다.
```shell
data
    |> toTime()
```

### truncate(): 지정된 시간Unit외에 무시하기
- truncate() 함수를 통해서 지정된 시간대로만 자르기가 가능하다.
- date 패키지를 import 해야한다.
- 지정한 시간 Unit 뒤로는 기본시간이 된다.
```shell
t0 = 2021-01-08T14:54:10.023849Z

date.truncate(t: t0, unit: 1ms) #2021-01-08T14:54:10.023000000Z

date.truncate(t: t0, unit: 1m) #2021-01-08T14:54:00.000000000Z

date.truncate(t: t0, unit: 1w) #Returns 2021-01-07T00:00:00.000000000Z

date.truncate(t: t0, unit: 1mo) # Returns 2021-01-01T00:00:00.000000000Z
```

### 시간단위 구문분석

```shell
import "date"

t0 = 2021-01-08T14:54:10.023849Z

date.minute(t: t0) # 54

date.year(t: t0) #2021

date.quarter(t: t0) #1 
```

#### 시간 연산
```shell
import "date"

date.add(d: 1w, to: 2021-01-01T00:00:00Z) #2021-01-08T00:00:00.000000000Z
date.sub(d: 1w, from: 2021-01-01T00:00:00Z)# d2020-12-25T00:00:00.000000000Z
```

## [5] float
- 실수형을 나타내는 자료형이다.
- Inf(무한대), NaN(숫자가 아님)도 포함된다.

### 다른 자료형과의 변환
```shell
string: 실수형태이면 변환 가능하다.
> float(v: "1.23") # 1.23

bool: true혹은 false 둘다 변환 가능하다.
> float(v: true) #1.0
> float(v: false) #0.0

int & uint: 실수형으로 변환된다.
> floast(v: 123) # 123.0

```