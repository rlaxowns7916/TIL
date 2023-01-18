# PubSub
- Redis가 Channel 역할을 한다. (Broker 역할)
- Channel에 Publish하고, Subscribe할 수 있다.
  - 1:1관계가 아니다.
  - 여러 Client가 Channel을 Subscribe 할 수 있다.
- **fire & forget 방식이다.**
  - Message를 보관하지 않는다. 
  - Memory최적화를 하는 방식이다.

## Subscribe
- subscribe명령어를 통해 Channel을 구독한다.
  - 동시에 여러개의 Channel을 구독 할 수 있다.
- 정상적으로 subscribe되면, 프롬포트 창이 열린다.
  - redis-cli:  ctrl + c를 통해서 나갈 수 있다.
  - 기타 Tool: Unsubscribe 명령어를 통해서 구독취소가 가능하다.
```shell
SUBSCRIBE ...CHANNELS

> subscribe channel1 #channel1 구독

0) "subscribe" #명령어
1) "channel1" #채널명
2) (integer)1 #성공여부

0) "message" #명령어
1) "channel1" #채널명
2) "hello" #메세지
```

### Pattern Subscribe
- subscribe 명령어를 통해서, 일일이 채널을 지정하는 것은 힘들 일이다.
- psubscribe를 통해서, 패턴에 알맞은 채널을 구독할 수 있게 된다.
  - 패턴에 알맞기만 하다면, 새롭게 채널을 추가해주지 않아도 알아서 구독된다.
```shell
PSUBSCRIBE ...PATTERNS

> psubscribe news:* # news:가 Prefix인 모든 Channel을 구독하는 것이다.
1) "psubscribe" #명령어
2) "news:*" #구독하는 채널
3) (integer) 1 #성공 여부

#------- 다른 Publisher들의 발행-------

1)"pmessage" # 패턴에 맞는 Topic의 Message
2) "news*" #패턴
3) "news:politics" #채널 명
4) "today politics" #Message
```


## Publish
- publish명령어를 통해서 Channel에 Message를 발행한다.
- **리턴값은 Message 구독에 성공한 Client의 수이다.**
```shell
PUBLISH CHANNEL MESSAGE

> publish channel1 hello
(integer) 1
```


## PUBSUB

### PUBSUB CHANNELS
- 활성화된 채널리스트를 리턴한다.
- 하나이상의 구독자가 있는 채널을 리턴한다.
- 없으면 (empty array)를 리턴한다.
```shell
PUBSUB CHANNELS [PATTERN]

> pubsub channels *
1) "active:channel"
```

### PUBSUB NUMSUB CHANNEL
- 활성화된 구독자 수를 리턴한다.
```shell
PUBSUB NUMBSUB CHANNEL

> pubsub numsub "active:channel"
1) "active:channel" # 채널 명
2) (integer) 2 # 활성 구독자 수
```