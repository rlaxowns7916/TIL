# Composite Variables

## [1] Record
- Key : Value 쌍이다.
- { }로 열고 닫는다.
```text
{foo: "bar", baz: 123.4, quz: -2}

{"Company Name": "ACME", "Street Address": "123 Main St.", id: 1123445}
```

## [2] Array
- 배열이다.
- [ ]로 열고 닫는다.
```text
["1st", "2nd", "3rd"]

[1.23, 4.56, 7.89]

[10, 25, -15]
```

## [3] Dictionary
- Key : Value 쌍이다.
- Record와의 차이점은 Schema이다.
  - Key : Value 형태가 모두 같아야한다.
```text
[0: "Sun", 1: "Mon", 2: "Tue"]

["red": "#FF0000", "green": "#00FF00", "blue": "#0000FF"]

[1.0: {stable: 12, latest: 12}, 1.1: {stable: 3, latest: 15}]
```


## [4] Function
- 함수이다.
- 0개이상의 파라미터와 함께 ( )로 정의한다.
- 변수에 저장 할 수 있다.
```text
// Function that returns the value 1
() => 1

// Function that returns the sum of a and b
(a, b) => a + b

// Function with default values
(x=1, y=1) => x * y

// Function with a block body
(a, b, c) => { 
    d = a + b
    return d / c
}

// User Definition Function
functionName = (param1=defaultVal1, param2=defaultVal2) => functionBody
```