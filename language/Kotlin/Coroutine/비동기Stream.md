# 비동기 Stream
- Coroutine에만 국한된 개념이 아니다


## ColdStream
- Subscriber가 데이터를 요청할 때 실행되는 데이터스트림
  - Subscriber가 없다면 방출(Emit) 하지 않는다.
- LazyEvaluation
  - Subscribe연산(종결연산)이 수행되기 전까지 연산이 수행되지 않는다.
  - Subscriber가 Stream에 대한 생명주기를 조절 할 수 있다.
- Subscriber per stream
  - Subscriber가 여러개라면, 각 Subscriber는 독립적으로 Stream을 처리한다.

## HotStream
- Subscriber와 무관하게 데이터를 방출(Emit) 한다.
  - 중간에 Subscriber가 연결된다면, 이미 방출된 데이터를 받을 수 없다.
  - Subscribe시점에 따라서 데이터가 유실 될 수 있다.
- EagerEvaluation
  - 구독자와 무관하게 데이터는 계속생성되며, 어느시점에나 Subscriber가 연결 될 수 있다.

