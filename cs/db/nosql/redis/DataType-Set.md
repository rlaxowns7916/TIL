# DataType-Set
- 순서가 존재하지 않는다.
- 모든 요소들은 Unique 하다. (중복이 없다.)
- 모든 요소들은 문자열로 취급된다.
  - 공백을 넣고싶을 경우, "" (큰 따옴표)로 묶으면 된다.
- 집합 연산을 제공한다.
  - Intersection: 교집합
  - Difference: 차집합
  - Union: 합집합

## SADD
- Set에 요소를 추가하는 연산이다.
- 추가된 요소의 갯수를 반환한다.
```shell
SADD KEY ...MEMBERS

> sadd sample-set "1","2","3"
(integer)3
```

## SMEMBERS
- Set에 존재하는 모든 Member들을 보여주는 것이다.
- O(N) 명령어로 조심해야 한다.
```shell
SMEMBERS KEY

> smembers sample-set
1) "3"
2) "1"
3) "2"
```

## SCARD
- SET에 몇개의 Member가 있는지 확인 하는 것이다.
- O(1)의 명령어이다.
```shell
SCARD KEY

> SCARD sample-set
(integer) 3

```

## SREM
- Member를 제거한다.
- O(1)이다.
- 2.4버전 이상부터, 복수개의 Member들을 삭제 할 수 있다.
- Set에서 지워진 Member의 갯수가 리턴된다.
  - Set에 존재하지 않았던 것은 무시된다.
```shell
SREM KEY ...MEMBERS

> srem sample-set 3 4
(integer) 1
```

##  SPOP
- Random하게 Member를 제거한다.
- 지울 개수를 지정할 수 있다.,
- O(1)이다.
- 3.2 버전부터 몇개를 지울 지 정할 수 있다.
  - COUNT (개수) 에 따른 Member들이 삭제되고 리턴된다.
  - 없다면 (empty array)가 리턴된다.
```shell
SPOP KEY 

> spop sample-set 2

1) "1"
2) "3"

> spop sample-set 2
1) "2"

> spop sample-set 2
(empty array)
```

## SISMEMBER
- 하나의 Member씩만 확인 가능하다.
- SET에 Member가 존재하는지 확인한다.
```shell
SISMEMBER KEY MEMBER

> sismember cars FORD
(integer) 0 # 없을 때

> sismember cars BMW
(integer) 1
```

## SMISMEMBER 
- SISMEMBER의 복수 형태이다.
  - ISMEMBER앞에 M이 붙어있다.
- 각 Member의 Index에 맞게 존재 여부를 리턴 값으로 한다.
```shell
SMISMEMBER KEY ...MEMBERS

> smismember cars FORD BMW 
1) (integer) 0
2) (intger) 1
```

## SRANDMEMBER 
- Set에서 Random한 Member들을 가져오는 것이다.
- Count(갯수)를 지정 할 수 있다.
  - default는 1이다.
- 전체 갯수보다 많은 Count를 지정해도 전체 갯수만큼만 나온다.
  - + 로 더 나오고 nil이 나오는게 아님.
```shell
SRANDMEMBER KEY [COUNT] 

> srandmember lottery:num 6

1) (integer) 21
2) (integer) 37
3) (integer) 1
4) (integer) 5
5) (integer) 42
6) (integer) 19
```

## SMOVE
- O(1) 명령어이다.
- 한번에 하나밖에 옮길 수 없다.
- 하나의 Set에서 다른 Set으로 데이터를 옮기는 것이다.
- Source에 있는 Member를 Destination으로 옮긴다.
- 성공적으로 옮겨지면 **1**을 리턴한다.
- 실패하면 **0**을 리턴한다.
```shell
SMOVE SOURCE DESTINATION MEMBER

# 홀수 값 채우기
> sadd number:odd 1 3 4 5 9
(integer) 5

# 짝수 값 채우기
> sadd number:event 2 4 6 8 10

# 짝수집합에서 홀수집합으로 2 넘기기 (성공)
> smove number:even number:odd 2
(integer) 1

# 짝수집합에서 홀수집합으로 3넘기기 (실패)
>smove number:even number:odd 3
(integer) 0
```

## SUNION
- 여러개의 Set을 하나로 합치는 것이다.
  - 하나로 합쳐서 새로운 Set을 생성하는 것은 아니다.
  - 여러개의 Set을 합쳤을 때의 결과를 보여준다.
- Set이기 떄문에, 중복은 제거된다.
```shell
SUNION ...KEYS

# key1 > 1,2,3,4,5
# key2 > 2,4,6,8,10

> sunion key1 key2

1) 1
2) 2
3) 3
4) 4
5) 5
6) 6
7) 8
8) 10
```

## SUNIONSTORE
- SUNION의 결과 값을 저장하는 것이다.
- 새로운 Set을 생성한다.
  - 리턴 값은 저장된 Element의 갯수이다.
```shell
SUNION DESTINATION ...KEYS

# key1 > 1,2,3,4,5
# key2 > 2,4,6,8,10
> sunionstore key3 key1 key2
(integer) 8
```


### SINTER
- 교집합이다.
  - Set + Intersection
- 각 Set의 공통된 Element를 리턴한다.
```shell
SINTER KEY1 KEY2

# key1 > 1,2,3,4,5
# key2 > 2,4,6,8,10
> sinter key1 key2
1) 2
2) 4
```

### SINTERSTORE
- SINTER + STORE 이다.
- 교집합을 새로운 SET에 저장하는 역할을 한다.
```shell
SINTERSTORE DESTINATION ...KEYS

# key1 > 1,2,3,4,5
# key2 > 2,4,6,8,10
> sinterstore key1 key2
(integer) 2
```

### SDIFF
- SET + Difference
- 첫 번째 SET(key1)과, 나머지 SET (key2 + key3 + key4)과의 차이점을 리턴한다.
```shell
SDIFF KEY ...KEYS

# key1 > 1,2,3,4,5
# key2 > 2,4,6,8,10
# key3 > 1,3

>sdiff key1 key2 key3
1) 5
```

### SDIFFSTORE
- SDIFF + STORE
- SDIFF의 결과를 Set에 저장하는 것이다.
```shell
SDIFFSTORE DESTINATION KEY ...KEYS

# key1 > 1,2,3,4,5
# key2 > 2,4,6,8,10
# key3 > 1,3

>sdiffstore dest1 key1 key2 key3
(integer) 1
```