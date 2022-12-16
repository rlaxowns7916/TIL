# DataType - List

## List
- LinkedList 형태이다.
  - 인덱스 접근은 느리지만, 데이터 추가 / 삭제가 빠르다.
- 40억개의 Element를 저장할 수 있다.
- Key와 Value가 1 : N 관계이다.
- Value가 입력된 순서대로 저장된다. (default)
- List 혹은 간단한 Queue혹은 Stack용도로 자주 사용된다.

### List의 생성과 삭제
- Value가 저장되면 새롭게 생성된다.
- Key에 해당하는 Value가 아무것도 없으면 자동으로 삭제된다.
- **생성과 삭제를 위한 별도의 작업은 존재하지 않는다.**

## LRANGE 
- GET 명령어 대신에 사용 가능하다.
- 범위를 지정해서 해당하는 만큼 List에서 가져오는 것이다.
- RRANGE는 존재하지 않는다.
- end가 start보다 작을 수 없다.
- 처리 시간은 O(S+N)이다. (S: Start, N: NumberOfElement)
- ```shell
  LRANGE [KEY] [START] [END]
  
  > lrange dept 0 -1 # 다 가져오기 (0: 첫번째, -1: 끝에서 첫번쨰)

  > lrange dept -3 -1 # 역순 > (끝에서 3번째부터 끝에서 1번쨰 까지 가져오기)
  ```

## LINDEX
- 왼쪽에서 Index로 Element를 가져오는 것이다.
- RINDEX(오른쪽 부터 순회)는 존재하지 않는다.
  - 끝에서부터의 접근은 -1 -> -2 -> -3 슨이다.
- **INDEX는 0부터 시작한다.**
  - 마지막 요소는 -1이다.
LINDEX [KEY] [INDEX]

```

## PUSH 
- 방향의 차이이다.
- 여러개의 Element를 한번에 넣을 수 있다.
- key값에 해당하는 List의 Element의 개수를 리턴한다.
- 명령어에 따라서 순서대로 Element들을 저장한다.

### 1. LPUSH
```shell
LPUSH [KEY] [...VALUES]

> lpush dept "Sales"
(integer) 1

> lpush dept "Dev" "HR"
(integer) 3
```

### 2. RPUSH
```shell
RPUSH [KEY] [...VALUES]

> rpush dept "Design"
(integer) 4

> lrange dept 0 -1

1) "Sales"
2) "Dev"
3) "HR"
4) "Design"
```

### 3. (L/R)PUSHX
- Key가 존재할 때만 List에 데이터를 추가한다.
  - Key가 존재한다 -> 이미 사용되는 중이었다. -> Insert 
  - 사용되지 않는 Key 값은 접근을 자주하지 않는다는 의미로 판단하여 정책상으로 캐시를 추가하지않는 등의 작업도 가능하다.

## LINSERT
- 왼쪽부터 특정 Element의 **(앞/뒤)에 Element를 추가 할 수 있다.
  - 특정 Element는 값에 해당한다.
  - 같은 Value를 갖는 것이 여러개라면 **첫번 째 값** 이 적용이된다.
  - RINSERT는 존재하지않는다. (-1 ~ 로 끝 Index부터 접근이 가능하기 때문이다.)
- 특정 값에 해당하는 Element가 존재하지 않을 때는 **-1**을 리턴한다.
- 결과는 INSERT 후 List의 총 개수이다.

```shell
> linsert [KEY] [BEFORE | AFTER] [PIVOT] [ELEMENT]
```

## POP 
- 특정 방향부터 N개의 요소를 제거한다.
- KEY는 필수요소이며, COUNT가 생략되면 1로 취급된다.

### 1. LPOP
- 왼쪽부터 N개의 요소를 삭제한다.
```shell
> LPOP [KEY] [COUNT]
```

### 2. RPOP
- 오른쪽부터 N개의 요소를 삭제한다.
```shell
> RPOP [KEY] [COUNT]
```


## LTRIM
- 지정함 범위만 남기고 List의 모든 것을 삭제한다.
- RTRIM은 없다 (-1 ~ 부터 끝 Index에 접근 할 수 있기 때문이다.)

```shell
> lpush ex 1 2 3 4 5 6 7 8 9 10
(integer) 10

> ltrim 4 -1 (4번째 Index부터 끝 Index 까지만 살림)

1) 6
2) 5
3) 4
4) 3
5) 2
6) 1
```