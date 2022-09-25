# Key
- Redis는 Key-Value Storage이다.
- 모든 데이터는 Key를 통해서 접근이 가능하다.
  - Value를 통한 검색은 불가능하다.


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
- Key 삭제
- 단건, 다건 모두 삭제 가능하다.
- 공백을 기점으로 Key들을 나열하면 지워진다.
```shell
$ del [Key]
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