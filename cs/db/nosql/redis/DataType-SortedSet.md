# DataType-SortedSet
- Set과 Hash가 합쳐진 구조이다.
  - SkipList + HashTable
  - Set이기 떄문에 중복을 허용하지 않는다.
    - 중복된 요소를 Insert 할시에, Score만 업데이트 된다.
    - Return이 (integer)0 이기 때문에, Update 안된 것으로 생각 할 수도 있다.
  - Hash처럼, 여러가지의 Element를 저장 할 수 있다.
- Element 추가와, 정렬된 Element를 가져오는 것에 있어서 좋은 성능을 보인다.
- **여러가지 Membe Field와, 숫자 Field인 Score를 가진다.**
  - Score에 따라서 정렬된다.
  - Score가 같다면, Member로 정렬된다.

## ZADD
- SortedSet에 Data를 추가하는 것이다.
- Return 값은 저장한 Element의 갯수이다.
- 옵션
  - XX: Update (있을 때만)
  - NX: 새롭게 Insert (없을 때만)
  - LT: 새롭게 정의한 Score가 기존에 존재하던 Score보다 낮을 때만 Update
  - GT: 새롭게 정의한 Score가 기존에 존재하던 Score보다 높을 때만 Update
  - CH: Return값을 새롭게 Insert된 Element의 갯수에서, Update된 Element의 갯수로 변경한다.
  - INCR: Score를 증가시킨다.
- GT, LT, NX는 함께 사용할 수 없다.
```shell
ZADD KEY [NX | XX]  [GT | LT] [CH] [INCR] ... SCORE MEMBER

> zadd user:follower 10 kim 20 lee
(integer) 2

> zadd user:follwer ch 10 kim 20 lee 30 choi
(integer)2

# incr은 해당 값만큼 Score를 올린 후의 값을 리턴해준다.
>zadd user:follower ch incr 30 kim
(integer)40
```


## ZRANGE
- SortedSet에서 Element를 가져오는 방법이다.
- START, STOP은 LRAGNE와 같다. (0은 왼쪽부터, -1은 오른쪽부터)
- 범위에 대한 표현식이 존재한다.
  - default: 포함
  - [: 필수
  - (: 제외
- 옵션
  - REV: 역순 조회
  - BYSCORE: Score를 조회조건으로 사용한다.
  - BYLEX: Member를 조회조건으로 사용한다.
  - LIMIT: 가져올 갯수를 제한한다.
  - WITHSCORES: 리턴에 Score도 추가한다.

```shell
ZRANGE KEY START STOP [BYSCORE | BYLEX] [REV] [LIMIT offset count] [WITHSCORES]

# SortedSet의 모든 요소 (index 0 ~ index -1) Score와 함께 가져오기 (오름차순)
> zrange user:follower 0 -1 withscores
...

# SortedSet의 모든 요소 역순으로 (index 0 ~ index -1) Score와 함께 가져오기 (내림차)
> zrange user:follower 0 -1 withscores rev
...

# Member -> 100 <= SCORE <= 1000
> zrange user:follower 100 1000 BYSCORE
...

# Member -> 100 < SCORE < 1000
> zrange user:followe (100 (1000 BYSCORE
...

# Member -> choi= MEMBER <=kim
# Member는 사전순이다.
> zrange user:follower [kim [choi bylex
1)choi
2)lee
3)kim
```