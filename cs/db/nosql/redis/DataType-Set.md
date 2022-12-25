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