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