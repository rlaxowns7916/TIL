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

## Publish
- publish명령어를 통해서 Channel에 Message를 발행한다.
- **리턴값은 Message 구독에 성공한 Client의 수이다.**
```shell
PUBLISH CHANNEL MESSAGE

> publish channel1 hello
(integer) 1
```