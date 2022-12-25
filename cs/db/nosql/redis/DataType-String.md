# DataType - String

## String
- Redis가 제공하는 가장 기본적인 DataType이다.
- BinarySafe하다. (모든 데이터를 저장 할 수 있다.)
- Redis가 제공하는 다른 DataType (List, Hash, Set, SortedSet)에 데이터가 적합하지 않다면, String이 좋은 대안일 것이다.
- 직렬화/역직렬화를 통해서 사용하면 된다.
- 많은 데이터를 작은 단위의 String으로 쪼개서 저장 할 수 있다.
- 512MB가 최대이다.

## 주요 사용처
1. Plain String
2. Caching
3. Static Page
4. Counter
5. 고정된 Config 값

#### SET
```shell
$ set KEY VALUE {옵션}
```
- Key에 따른 Value 저장
- 기본 동작은 덮어쓰기 (Key값이 존재하거나 안하거나 둘다)
  - 만료 옵션이 있을 경우에는 덮어쓰기 안된다.
  - 덮어쓰고 싶은 경우 (XX 옵션 같이주면 된다.)
- 옵션들을 조합해서 사용 가능하다.
  - ```shell
     $ set [key] [value] nx ex 200
     # key가 존재하지 않으면, 200초의 마감시간을 주고 Value 설
    ```
#### 옵션
1. NX: 겹처쓰기 방지 (Key값이 존재하지 않을 때만 저장이된다.)
2. XX: 수정 (이미 Key값이 존재할 때만 저장)
3. EX n: 만료시간 지정: (데이터 생성후 n**초** 이후에 데이터 지워짐)
4. PX n: 만료시간 지정: (데이터 생성후 n**밀리 초** 이후에 데이터 지워짐)

### GET
- 단건 조회이다.
- Key에 따른 Value 조회
```shell
$ get [Key]
```

### DEL
```shell
$ del [Key]
```
- 동기적인 Key 삭제
  - Blocking주의
  - 여러개의 Key가 있을 경우, 순서대로 삭제한 후 시스템에 제어권을 반환한다.
- 단건, 다건 모두 삭제 가능하다.
- 공백을 기점으로 Key들을 나열하면 지워진다.

### INCR
- Number Type 일 때, Value를 1 증가 시킨다.
- ```shell
    $ incr [key]
  ```
### INCRBY
- Number Type 일 떄, Value를 index만큼 증가 시킨다.
- ```shell
    $ incrby [key] [index]
  ```
  
### INCRBYFLOAT
- 부동소수점에 대한 연산이다.
- decrbyfloat은 제공해주지 않는다.
  - incrbyfloat -num 이런식으로 하면된다.
- ```shell
    $ incrbyfloat [key] [floatNum]
  ```
***

### DECR
- Number Type 일 때 Value를 1 감소 시킨다.
- ```shell
    $ decr [key]
  ```
  
### DECRBY
- Number Type 일 때 Value를 index 만큼 감소 시킨다.
- ```shell
    $ decrby [key] [index]
  ```
  
***

### APPEND
- 문자열을 뒤에 더 붙이는 것이다.
- 리턴 값은 연산을 한 후의 문자열의 길이이다.
- 시간데이터 형식에도 사용 가능하다.
- ```shell
  $ append [key] [string]
  ```  
  

### MSET
- 한번에 key-value를 여러개 지정하는 것이다.
- ***기존에 값이 있다면 대체한다.***
- ```shell
    $ mset [key1] [value1] [key2] [value2] ...
  ```
  
### MSETNX
- 기존값을 덮어쓰지않는 mset이다.

### mget
- 한번에 key-value 여러개를 가져오는 것이다.
- ```shell
    $mget [key1] [key2] [key3] ...
  ```


### getset
- atomic하게 get과 set을 한번에 수행하는 것이다.
- 현재의 결과 값을 가져오고 set한다.
- key값이 존재하지 않아도 SET은 실행된다. (GET은 당연히 Value가 존재하지 않으니 nil)
```shell
> SET app:daily_coupon 10
OK

> DECR app:daily_coupon
(integer) 9

> GETSET app:daily_coupon 10
(integer) 9
```

#### getrange
- Key값에 해당하는 Value문자열을 subString 하여 가져오는 것이다.
- index는 0부터 시작한다.
- startIndex부터 endIndex까지의 문자열을 반환한다.
  - endIndex까지 포함이다.
- endIndex는 startIndex보다 크거나 같아야한다.
- end가 실제 길이보다 길면 에러를 리턴하는 것이 아니라 전체문자열을 리턴한다.
- Negative Indexing (역순)도 가능하다.
  - 맨 마지막이 -1 부터 시작한다.
```shell
> SET Key "Value"
OK

> GETRANGE 0 1
"Va"

> GETRANGE 0 100
"Value"

> GETRANGE -3 -1
> "lue"
```

#### setrange
- Key값에 해당하는 Value문자열을 offset부터 새롭게 설정하는 것이다.
  - offset뒤에있는 것들을 모두 삭제하고 대체하는 것이 아니다.
  - offset부터 새롭게 설정하는 문자열의 길이만큼만 대체 하는 것이디.
- 존재하지 않는 Key값이어도 새롭게 생성이된다.
  - offset이전 값은 비어있게 된다.
```shell
> SETRANGE notExist 5 hello
"\x00\x00\x00\x00\x00hello"
```

***

## Encoding
- Redis가 자동적으로 인코딩 방식을 결정한다.
- 3가지의 인코딩 방식이 있다.
- 아래의 명령어로 String의 인코딩 방식에 대해서 알 수 있다.
  - ```shell
    $ object encoding [key]
    ```

### 1. int
- 64Bit의 부호를 가지고 있는 숫자에 사용된다.

### 2. embstr
- 문자열의 크기가 44Byte보다 같거나 작을 때 사용된다.
- 메모리 사용량이나, 퍼포먼스 측면에서 유리하다.

### 3. raw
- 문자열의 크기가 44Byte 이상일 때 사용된다.
- 

### JSON 저장
```shell
$ set users:1 "{"name" : "taejun", "age" : "25"}"
> "{\"name\" : \"taejun\", \"age\" : \"25\"}"
```
- 작은 따옴표와 함께 Json을 명시한다.
- Escape처리를 해준다.

