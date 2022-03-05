# Event

- 과거에 벌어진 '어떤 것'
- Domain 사이의 강결합을 느슨하게 만들어준다.

## Domain에서의 강결합 문제

- BoundedContext가 모호하기 떄문에 발생한다.

### EX.1 환불예제 (기존)

- 주문과 결제 Domain의 영역이 섞여있다.

```java
public class CancelOrderService {
    private final RefundService refundService;

    @Transactional
    public void cancel(OrderNo orderNo) {
        Order order = findOrder(orderNo);
        order.cancel();

        try {
            refundService.refund(getPaymentId()); //외부 서비스 호출
            order.refundCompleted();
        } catch (Exception ex) {

        }
    }
}
```

- **과연 Transaction 으로 묶여야 할까?**
    - 주문 취소상태로 변경한 후, 환불은 나중에 진행해도된다.
- **동기식으로 진행되기 떄문에, 외부시스템에 영향을 받게된다.**
    - 외부 시스템이 응답을 늦게주면 그만큼 API의 성능에 영향을받는다.
- **확장에 취약하다.**
    - 알림 서비스 같은 것이 추가된다면?

## 이벤트 관련 구성 요소

### 1. Event 생성 주체

- 상태가 바뀌면 Event를 발행한다.
- Entity, VO, DomainService가 이벤트 생성 주체

### 2. Event Publisher

- Event 생성주체가 발행한 Event를 전파한다.
- 구현 방식에 따라 동기나 비동기로 실행이된다.

### 3. Event Subscriber

- Event생성주체가 생성한 Event에 반응한다.
- Event를 전달 받고, Event에 담긴 데이터를 이용해 원하는 기능을 실행한다.

## 이벤트의 구성

- Event는 최소한의 정보만을 담아야한다.
- Event 종류, Event발생시간, 추가 데이터 등이 포함된다.
- Event의 정보만으로 기능을 실행 할 수 없을 때는, 기능에서 추가적인 검색을 실행한다.

## 이벤트의 용도

1. 트리거
    - 예메완료 -> 이벤트 -> SMS 발송
2. 이기종간 시스템 동기화
    - 배송지 변경 -> 외부배송서비스에 배송지변경정보 전달 (동기화)

## 이벤트의 장점

### EX2. 환불예제 (이벤트)

```java
public class Order {
    public void cancel() {
        verifyNotShipped();
        this.state = Order.CANCELED;

        this.refundStatus = State.REFUND_STARTED;
        Events.raiuse(new OrderCanceledEvent(number.getNumber()));
    }
}
```

1. 서로 다른 Domain 로직이 섞이는 것을 방지 할 수 있다.
    - 주문 Domain 코드안에서 더 이상 결제 Domain 코드를 볼 수 없다.
2. 확장에 용이하다.
    - Doamin 로직 수정 없이, Handler만 구현해주면 된다.

## Event 구성요소 코드

### 1. Event

```java
import java.time.LocalDateTime;

public abstract class Event {
    private LocalDateTime timeStamp;

    public Event() {
        this.timeStamp = LocalDateTime.now();
    }
}

@Getter
@AllArgsConstructor
public class OrderCanceledEvent extends Event {
    private String orderNumber;
}
```

### 2. EventPublisher

```java
import java.beans.EventHandler;
import java.util.ArrayList;

public class Events {
    private static ThreadLocal<List<EventHandler>> handlers = new ThreadLocal();
    private static ThreadLocal<Boolean> publishing = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public static void raise(Event event) {
        /**
         이미 Event를 Publishing중이면 종료한다.
         */
        if (publishing.get()) return;

        try {
            publishing.set(Boolean.TRUE);

            List<EventHandler<?>> eventHandlers = handlers.get();
            if (eventHandlers == null)
                return;
            /**
             * 전달된 Event를  Handling 할 수 있는 Handler 찾고 Handle
             */
            for (EventHandler handler : eventHandlers) {
                if (handler.canHandle(event)) {
                    handler.handle(event);
                }
            }
        } finally {
            /**
             * Publishing이 끝난 상태로 변경
             */
            publishing.set(Boolean.FALSE);
        }
    }

    public static void addHandler(EventHandler<?> handler) {
        /**
         * Publisher가 현재 처리중이면 종료
         */
        if (publishing.get()) return;

        List<EventHandler<?>> eventHandlers = handlers.get();

        if (eventHandlers == null) {
            eventHandlers = new ArrayList<>();
            handlers.set(eventHandlers);
        }
        eventHandlers.add(handler);
    }

    /**
     * Handler에 보관된 List객체를 삭제
     * Server는 Thread를 재사용하므로, 작업이 끝나면 ThreadLocal을 비워주어야한다.
     * 안비워주면, 계속해서 쌓이게 되고, OutOfMemoryError가 발생 할 수도 있다.
     * AOP를 통해서 비워주는 것이 좋다.
     */
    public void rest() {
        if (!publishing.get()) {
            handlers.remove();
        }
    }
}
```

### 3. EventSubscriber
```java
public interface EventHandler<T>{
    void handle(T eveent);
    boolean canHandle(Event event);
}

public abstract class AbstractEventHandler<T> implements EventHandler<T>{
    @Override
    public boolean canHandle(Event event){
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(
                EventHandler.class,this.getClass());
        return typeArgs[0].isAssignableFrom(event.getClass());
    }
}
```

### 이벤트 구현 방식

### 1. MessageQueue를 이용한 방식
- EventPublisher가 Queue에 적재
- EventSubscriber는 Queue에서 꺼내서 로직 실행
- Domain 로직 실행과 MQ에 적재하는 것을 하나의 트랜잭션으로 묶는 것을 **글로벌 트랜잭션**이라고 한다.
  - 글로벌 트랜잭션은 성능에 영향을준다
  - RabbitMQ는 글로벌 트랜잭션을 지원한다.
  - Kafka는 글로벌 트랜잭션을 지원하지 않는다.

### 2. REST API
- 이벤트 저장소에 이벤트를 저장한다.
- lastOffset인자를 통해서 아직 처리못한 이벤트를 READ 할 수 있는 API를 제공한다.