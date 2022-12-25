# Key
- Redis는 Key-Value Storage 이다.
- 모든 데이터는 Key를 통해서 접근이 가능하다.
  - Value를 통한 검색은 불가능하다.
- 40 밀리 세컨드 안에, 100만개의 Key를 탐색 가능하다.
- Bucket 기반의 Chained-LinkedList 이다.

## 명령어
### UNLINK
- 비동기적인 Key삭제
  - 별도의 쓰레드에서 백그라운드로 삭제한다.
  - 논리적 처리시간은 O(1), 실제 처리시간은 O(N)
- del (String delete) 은 동기적이기 떄문에, 많은 양의 Key를 삭제하게 된다면 시스템에 장애를 유발 할 수 있다.
- ```shell
   $ unlink [key]
  ```
  
### EXISTS
- Key가 존재하는지 확인하는 방법
- 단건 조회, 다건 조회 모두 가능하다.
- 리턴값은 존재하는 Key의 개수이다.

```shell
$ exists [Key]
```


### TTL
- 만료 옵션이 존재하는 Key의 남은 시간 체크
- 리턴값을 통해 남은 시간을 알 수 있다.
  - -1: 만료 옵션이 지정되어있지 않음 
  - -2: 더이상 값이 존재하지 않는다는 것을 의미한다.
```shell
$ ttl [Key] 
```
### PTTL
- 밀리 세컨드 옵션이다.
```shell
$ pttl [Key] 
```

### EXPIRE
- 만료옵션이 존재하는 Key값의 남은시간을 변경하는 것이다.
```shell
$ expire [key] [Second]
```

### PEXPIRE
- 밀리세컨드 옵션이다.
```shell
$ pexpire [Key] [MilliSecond]
```

### PERSIST
- 만료 옵션이 지정되있는 Key를 영구보관으로 바꾼다.
  - 만료옵션을 제거한다.
- 0: 키가 존재하지 않거나, TimeOut옵션이 지정되어있지 않음
- 1: TimeOut옵션 삭제
```shell
$ persist [Key]
```

### 8. KEYS [EXPERSSION]
- 표현식을 통해서 존재하는 Key들을 찾는 것이다.
- O(N)이다.
  - 이 명령이 실행되면 **다를 모든 명령들은 Blocking 된다.**
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
        
### RENAME
- Key의 이름을 변경하는 것이다.
- ```shell
    rename [oldKey] [newKey]
  ```
  - oldKey가 존재하지 않으면 에러가 발생한다.
  - newKey에 해당하는 것이 존재한다면 Override한다.
    - del 후 insert하기 때문에, newKey에 해당하는 기존데이터가 거대했다면, 시간이 많이 소요된다.
### RENAMENX
- nx 옵션을 통해서, 만약 newKey에 해당하는 것이 존재한다면, 명령을 무시할 수 있다.


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
     
## Scan
- 전체 Key를 페이징 크기에 따라서 순회하는 명령이다.
  - sscan: Set에서 검색
  - zscan: sortedSet에서 검색
  - hscan: Hash에서 검색
- ```shell
  $ SCAN cursor [MATCH pattern] [COUNT count]
  $ SSCAN key cursor [MATCH pattern] [COUNT count]
  $ ZSCAN key cursor [MATCH pattern] [COUNT count]
  $ HSCAN key cursor [MATCH pattern] [COUNT count]
  ```
- 일정 갯수만큼 Key를 짤라서 가져오는 것이다. (페이징)
  - Count를 지정하지 않으면 10개 씩 가져온다.
  - 적은 수의 결과를 잘라서 가져오기 때문에 Blocking이 발생하지 않는다.
    - 많은 개수를 가져오게되면 Blocking이 발생할 수 있다.
  - 많은 개수를 가져오는 만큼 날리는 쿼리의 수는 줄어들겠지만, 시간은 오래걸린다. (Trade-Off 생각하기)
- Keys는 일치하는 모든 것을 가져오는 반면에, 페이징을 통해서 가져온다.
- 이전부터 계속 있었던 데이터가 아니라면, Key가 반환 될 수도, 안될 수도 있다.
  - 이미 페이지 커서가 지났으면 Key가 반환되지 않을 것이다.
- 중복 Key가 반환될 수 있다.
  - 중복 Key에 대한 중복제거는 Application의 몫이다.
```shell
> scan 0
1) "10" # Next Cursor
2)  1) "key:9"
    2) "key:7"
    3) "key:4"
    4) "key:12"
    5) "key:32"
    6) "key:29"
    7) "key:15"
    8) "key:21"
    9) "key:3"
   10) "key:7"
   11) "key:2"
```
- 1번의 결과로 다음 Cursor의 값이 전달된다.
  - 0이 올 때는 더이상 Scan할 것이 없다는 의미이다.
  - **시작 & 끝 모두 0 이다.**
- Count 개수 만큼 페이지의 개수를 지정 할 수 있다.
  - Count의 개수가 항상 지켜지는 것은 아니다.