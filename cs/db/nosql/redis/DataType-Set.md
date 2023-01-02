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