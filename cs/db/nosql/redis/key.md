# Key
- Redis는 Key-Value Storage이다.
- 모든 데이터는 Key를 통해서 접근이 가능하다.
  - Value를 통한 검색은 불가능하다.
- 40 밀리 세컨드 안에, 100만개의 Key를 탐색 가능하다.


## 단건

### 1. Set
```shell
$ set [Key] [Value] {옵션}
```
- Key에 따른 Value 저장
- 기본 동작은 덮어쓰기 (Key값이 존재하거나 안하거나 둘다)
  - 만료 옵션이 있을 경우에는 덮어쓰기 안된다.
  - 덮어쓰고 싶은 경우 (XX 옵션 같이주면 된다.)
#### 옵션
1. NX: 겹처쓰기 방지 (Key값이 존재하지 않을 때만 저장이된다.)
2. XX: 수정 (이미 Key값이 존재할 때만 저장)
3. EX n: 만료시간 지정: (데이터 생성후 n**초** 이후에 데이터 지워짐)
4. PX n: 만료시간 지정: (데이터 생성후 n**밀리 초** 이후에 데이터 지워짐)

### 2. Get
- Key에 따른 Value 검색
```shell
$ get [Key]
```

### 3. del
```shell
$ del [Key]
```
- 동기적인 Key 삭제
  - 여러개의 Key가 있을 경우, 순서대로 삭제한 후 시스템에 제어권을 반환한다.
- 단건, 다건 모두 삭제 가능하다.
- 공백을 기점으로 Key들을 나열하면 지워진다.

#### unlink
- 비동기적인 Key삭제
  - 별도의 쓰레드에서 백그라운드로 삭제한다.
  - 논리적 처리시간은 O(1), 실제 처리시간은 O(N)
- del은 동기적이기 떄문에, 많은 양의 Key를 삭제하게 된다면 시스템에 장애를 유발 할 수 있다.
- ```shell
   $ unlink [key]
  ```


### 4. exists
- Key가 존재하는지 확인하는 방법
- 단건 조회, 다건 조회 모두 가능하다.
- 리턴값은 존재하는 Key의 개수이다.

```shell
$ exists [Key]
```


### 5. TTL
- 만료 옵션이 존재하는 Key의 남은 시간 체크
- 리턴값을 통해 남은 시간을 알 수 있다.
  - -1: 만료 옵션이 지정되어있지 않음 
  - -2: 더이상 값이 존재하지 않는다는 것을 의미한다.
```shell
$ ttl [Key] 
```
#### pttl
- 밀리 세컨드 옵션이다.
```shell
$ pttl [Key] 
```

### 6. expire
- 만료옵션이 존재하는 Key값의 남은시간을 변경하는 것이다.
```shell
$ expire [key] [Second]
```

#### pexpire
- 밀리세컨드 옵션이다.
```shell
$ pexpire [Key] [MilliSecond]
```

### 7. persist
- 만료 옵션이 지정되있는 Key를 영구보관으로 바꾼다.
  - 만료옵션을 제거한다.
- 0: 키가 존재하지 않거나, TimeOut옵션이 지정되어있지 않음
- 1: TimeOut옵션 삭제
```shell
$ persist [Key]
```

### 8. keys [expression]
- 표현식을 통해서 존재하는 Key들을 찾는 것이다.
- DB에 부하를 주기 때문에, 실행을 심각하게 고려해봐야한다.  (꼭 실행해야 한다면, Expression을 잘 정의해야 한다.)
  - ```shell
        keys [expression]
    ```
    - ?: 하나의 문자 혹은 숫자와 일치하는 모든 Key를 보여준다.
      - ```shell
          keys h?llo;
          - hello
          - hallo
          - hqllo
          - ...
        ```
    - *: 나머지 조건에 일치하는 모든 Key를 보여준다.
      - ```shell
          keys h*llo
          - heeeeeeeeeeeeeeeello
          - hello
          - hasdcadfqwaerqasdfasdfllo
          - ...
        ```
    - []: 사이에 있는 문자와 일치하는 것들의 Key를 보여준다.
      - ```shell
            keys h[ae]llo
            - hallo
            - hello
        ```
      - 괄호 내부에 있는 것들의 조합을 의미하는 것이 아니다.
    - [^]: 괄호에 포함된 것들을 제외하고 검색한다.
      - ```shell
          keys h[^e]llo
          - hallo
          - hillo
          - ...
        ```
    - [-]: 범위에 포함된 것들을 검색한다.
      - ```shell
          keys h[a-c]llo
          - hallo
          - hbllo
          - hcllo
        ```
        
### 9. rename
- Key의 이름을 변경하는 것이다.
- ```shell
    rename [oldKey] [newKey]
  ```
  - oldKey가 존재하지 않으면 에러가 발생한다.
  - newKey에 해당하는 것이 존재한다면 Override한다.
    - del 후 insert하기 때문에, newKey에 해당하는 기존데이터가 거대했다면, 시간이 많이 소요된다.
#### renamenx
- nx 옵션을 통해서, 만약 newKey에 해당하는 것이 존재한다면, 명령을 무시할 수 있다.


### 10. getset
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

### 11. getrange
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

### 12. setrange
- Key값에 해당하는 Value문자열을 offset부터 새롭게 설정하는 것이다.
  - offset뒤에있는 것들을 모두 삭제하고 대체하는 것이 아니다.
  - offset부터 새롭게 설정하는 문자열의 길이만큼만 대체 하는 것이디.
- 존재하지 않는 Key값이어도 새롭게 생성이된다.
  - offset이전 값은 비어있게 된다.
```shell
> SETRANGE notExist 5 hello
"\x00\x00\x00\x00\x00hello"
```

## Key-Space
- MySQL의 Database 같은 것 이다.
- 하나의 KeySpace에서는 1개의 Key값과 1개의 Value값이 고정되지만, KeySpace끼리는 독립적이다.
- 즉 같은 Key가 여러개의 KeySpace에 존재 할 수 있다는 것이다.
- KeySpace는 0부터 시작하며, default가 0 이다.
- RDB와 다르게, KeySpace간의 Link는 불가능하다. 

### 1. Key Space 이동하기
- ```shell
   select [index]
  ```
  - index 번 째 KeySpace로 이동한다.

### 2. Key Space 비우기
- ```shell
  flushdb
  ```
  - 롤백이 불가능한 명령이기 때문에, 신중해야 한다.

## Key Naming Convention
1. 간단하면서 정확한 의미를 가질 것
2. 너무 짤은 길이의 Key는 좋은 방법이 아니다. 
  - 짧은 Key는 Memory상의 이점을 주기는 한다.
  - Key의 Size는 최대 512MB이다.
3. Schema Design에 맞는 형식을 가져야 한다.
4. 빈 문자열 또한 유효한 Key로 인식된다.
5. ObjectId:id 형식이 자주 사용된다.
   - ```text
       - users:100
       - users:100:group
       - users:100:friends
     ```