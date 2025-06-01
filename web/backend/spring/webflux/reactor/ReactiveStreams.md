# Reactive Streams
- Reactive한 Programming을 하기위한 라이브러리 표준이다.
- DataStream을 NonBlocking하면서, 비동기적으로 처리하기위한 Reactive Library 표준


## 구현체
### [1] RxJava
- Reactive Extension Java
- .NET환경의 Reactive 라이브러리를 Java언어로 바꾼 라이브러리
- RxJava 2.0부터 Reactive Streams 스펙을 지원하게 되었다.

### [2] Project Reactor
- SpringFramework 팀에서 주도적을 개발한 라이브러리
- Java기반 SpringWebFlux에서 주로 사용한다.

### [3] Java9 Flow APi
- Reactive Streams 표준 스펙을 Java스펙에 포함시킨 것이다.
- **구현체가 아닌 Interface를 제공한다.**
  - jdbc와 같은 SPI(Service Provider Interface)이다.


## 관련 용어
- publisher와 subscriber는 보통 다른 Thread에서 비동기적으로 통신한다.
- **Interface 형태로 정의되며, 코드단에서는 구현해서 사용한다.**

| Component    | Description                                                                                |
|:-------------|:-------------------------------------------------------------------------------------------|
| Publisher    | Data를 생성하고 통지하는 역할을 한다 (Subscriber를 등록)                                                    |
| Subscriber   | 구독할 Publisher로부터 나온 데이터를 구독하는 역항을 한다.                                                      |
| Subscription | Publisher에 요청할 데이터의 개수를 정하고, 구독을 취소하는 역항을 한다.                                              |
| Processor    | Publisher와 Subscriber 역할을 모두 수행가능하다.                                                       |
| Signal       | Publisher와 Subscriber사이의 주고받는 상호작용을 의미한다. <br/> (onComplete,onErrror,Request,Cancel...)    |
| Demand       | Subsriber가 Publisher에게 요청한 데이터를 의미한다.                                                      | 
| Emit         | Publisher가 Subscriber에게 데이터를 전달 할 때, Publisher의 입장에서 데이터를 Emit(발행,게시,통지) 한다고 한다.           |
|UpStream & DownStream| 데이터의 흐름을 나타낸다. <br/> MethodChaning에서 상대적으로 상위에 있으면 Upstream, 아래에 있으면 DownStream이라고 볼 수 있다. |

## 주요 Flow
```text
[Subscriber] --subscribe()----------------------------> [Publisher]
                |   (데이터 구독 시작)
                |<-- onSubscribe(subscription) ---------|
                |   (데이터 통지 가능 알림)
                |-- subscription.request(n) ----------->|
                |   (n개의 데이터 요청)
                |
                |<-- onNext(item1) -------------------- |
                |<-- onNext(item2) -------------------- |
                |   (요청 받은 데이터의 갯수만큼 계속 반복)
                |<-- onNext(itemN) -------------------- |
                |
                |<-- onComplete() or onError() -------- |
```
- Subscription.Request(n) 을 통해서, BackPressure를 구현한다.
- Publisher/Subscriber는 MessageQueue에서의 개념보다는 **Observer 패턴**에 가깝다.

###  과정
1. Subscriber가 Publisher를 구독한다.
2. Publisher가 Subscriber에게, 데이터를 보낼 준비가 되었다고 알린다. (onSubscribe)
3. Subscriber가 Publisher에게 전달받을 데이터의 갯수를 알린다. (Subscription.request)
4. Publisher가 데이터를 생성한다.
5. Publisher가 요청받은 수 만큼 데이터를 발행한다. (onNext)
6. 완료(onComplete), 에러(onError)까지 위 Flow를 반복한다. (onComplete, onError는 호출 이후 구독 취소가 된다.)


## 구성요소
### [1] Publisher
```java
public interface Publisher<T>{
    public void subscribe(Subscriber<? super T> s);
}
```
- subscribe 메소드를 통해서, Subscriber를 등록한다.
- **Kafka와 같은 MQ에서의 Publisher/Subscriber 개념과는 다르다.**
    - MQ의경우, 가운데에 Broker가 존재하기 때문에 느슨한 결합을 가지게 된다.
    - Reactive Streams의 경우, Subscriber를 Publisher에 등록하는 방식으로 진행된다.

#### 제약사항
1. publisher가 subscriber에게 보내는 onNext signal의 총 개수는 요청된 데이터의 개수보다 작거나 같아야 한다.
2. publisher는 요청된 데이터의 개수보다 적개 보내고 onError, onComplete를 통해서 구독을 취소 할 수 있다.
3. 데이터 처리가 실패하면 onError signal을 보낸다.
4. 데이터 처리가 모두 완료되면 onComplete signal을 보낸다.
5. onComplete나 onError signal이 발생하는 경우, 그 이후에는 어떠한 signal도 발생되어서는 안된다.

### [2] Subscriber
```java
public interface Subscriber<T>{
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}
```
- onSubscribe: 구독 시작 시점에 해야할 일을 정의하며, Subscription을 등록한다.
- onNext: Publisher로 부터 데이터를 받는 역할을 한다.
- onError: 요청이 실패했을 떄의 Flow이며, 실행 후 Stream을 종료한다.
- onComplete: 요청이 성공했을 떄의 Flow이며, 실행 후 Stream을 종료한다.

#### Subscriber가 계속해서 데이터를 받는 방법
- onSubscribe에서만 Subscription.request(n)을 호출하는 것이 아니다.
- 아직 받을게 남아있다면 추가적으로 Demand Signal을 보내야 한다.
```java
/**
 * 한개 씩 요청
 */
@Override
public void onNext(Item item) {
    process(item);
    subscription.request(1); // 항상 1개씩 요청
}

/**
 * 배치 요청
 */
@Override
public void onNext(Item item) {
  buffer.add(item);
  if (buffer.size() == 5) {
    flush(buffer);
    buffer.clear();
    subscription.request(5); // 다음 5개 요청
  }
}

/**
 * 무한 요청 --> 사실상 BackPressure를 무시하는 방식
 */
@Override
public void onSubscribe(Subscription subscription) {
  subscription.request(Long.MAX_VALUE); // 가능한 모든 데이터 요청
}

```


#### 제약사항
1. subscriber는 onNext signal을 받기 전, Demand Signal (Subscription.request(n)) 을 먼저 호출해야 한다.
2. onComplete나 onError메소드에서는, Subscription 또는 Publisher의 Method를 사용해서는 안된다.
3. onComplete 혹은 onError signal을 수신한 이후에는, 어떠한 signal도 받아서는 안된다.
4. 구독이 필요하지 않을 때는, Subscription.cancel()을 호출하면 된다.
5. onSubscribe는 최대 한번만 호출되어야 한다. (동일한 구독자는 최대 한번만 구독할 수 있다.)

### [3] Subscription
```java
public interface Subscription{
    public void request(long n);
    public void cancel();
}
```
- onSubscribe 메소드에서 Publisher가 Subscriber에게 Subscription을 전달한다.
- request: n개의 데이터를 요청한다.
- cancel: 구독을 취소하는 역할을 한다.

#### 제약사항
1. Subscription.request(n)은 onSubscribe, onNext에서 동기적인 호출이 보장되어야 한다. ()
2. 구독이 취소된 이후의 Subscription.request(n)은 동작해서는 안된다.
3. 구독이 취소된 이후의 Subscription.cancel()은 동작해서는 안된다.
4. 구독이 취소되지 않은 상황에서의 Subscription.cancel()은 publisher에게 subscriber로 보내는 signal을 정지하게 만들어야 한다.
5. 구독이 취소되지 않은 상황에서의 Subscription.cancel()은 publisher에게 subscribeer의 참조를 삭제하게 해야한다.
5. 


### [4] Processor
```java
public interface Processor<T,R> extends Subscriber<T>,Publisher<R>{
    
}
```
- 별도로 구현해야 하는 Method는 없으며, Publisher와 Subscriber를 상속한 인터페이스이다.

### [5] Signal
- Publisher와 Subscriber 사이의 상호작용을 의미한다.
- onSubscribe, onNext, onComplete, onError, request, cancel 등이 있다.

### [6] Demand
- Subscriber가 Publisher에게 요청한 데이터의 개수를 의미한다.

### [7] Emit
- Publisher가 Subscriber에게 데이터를 전달할 때, 데이터를 발행(emit)한다고 한다.