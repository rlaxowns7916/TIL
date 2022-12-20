# DataType - Hash
- Key값에 해당하는 Value로 Hash를 가지게 된다.
  - Key:Value의 리스트를 갖고있는 것이다.
- 스키마가 존재하지 않는다.
- 단순한 값이 아닌 object(객체)를 저장하는데 자주 사용한다.

## HSET
- Hash에 값을 저장하는 것이디.
- Update와 Insert 모두 가능하다.
- 새롭게 생성된 Field:Value의 갯수이다.
  - Update의 경우 0이 리턴 될 것이다.
```shell
HSET KEY [...field value]
  
>hset user firstName "Taejun" lastName "Kim"
(integer) 2   
 ```

## HGET
- Key에 해당하는 값들을 가져오는 것이다.
- 존재하지 않는 filed의 값을 가져오려고하면 **nil**이 리턴된다.
- field에 해당하는 값을 가지고 온다. (한 개씩 밖에 못가져온다.)
```shell
HGET KEY FIELD

> hget use firstName
"Taejun"
```


## HGETALL
- Hash에 저장되어있는 값을 모두 가져온다.
- field와 value를 각각 가져온다.
  - 항상 짝수가 나올 수 밖에 없다.
```shell
HGETALL KEY

> hgetall user
1) "firstName"
2) "Taejun"
3) "lastName"
4) "Kim"
```

## HMGET
- 한번에 여러개의 Value를 가져올 때 사용된다.
- 존재하지 않는 Field 값을 입력 했을 경우, **nil**이 리턴 된다.
```shell
HMSET KEY ...fields

> hmset user firstName lastName age
1) "Taejun"
2) "Kim"
3) (nil)
```

## HLEN
- Hash의 길이를 보여준다.
- field의 길이이다.
- O(1)이다.
```shell
HLEN KEY

> hlen user
(integer)2
```


## HDEL
- Hash에서 데이터를 삭제하는 방법이다.
- 삭제된 Field:Value 쌍의 개수를 리턴한다.
```shell
HDEL KEY ...FIELDS

> hdel user firstName lastName
(integer) 2
```

## HEXISTS
- Field값이 있는지 확인한다.
- 한개밖에 확인을 못하는 한계를 가지고있다.
  - HMGET을 통해서 사용하는 편이 더 좋다.
    - Field값에 해당하는 Value값이 없으면 nil이 뜨기 떄문이다.
- 존재하면 1, 존재하지 않으면 0을 리턴한다.
```shell
HEXISTS KEY FIELD

> hexists user firstName
(integer)1

> hexists user firstName1
(integer)0
```