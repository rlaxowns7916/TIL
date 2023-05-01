# SSE (ServerSentEvent)

- HTTP를 기반으로한다.
- Server가 Client에게 Message를 보내는 단방향 통신이다.
  - Client는 Server에 Request를 보낼 수 없다.
  - 또다른 HTTP Request가 발생된다면, 기존의 Connection을 재사용하는 것이 아닌 새로운 Connection이 생긴다.
- 지속적인 Connection을 맺는다.
  - Connection이 만료되면 Browser는 다시 Connection을 맺는다.
  - 새롭게 Connection을 맺는다.
- Web 표준이다.
    - 대부분의 최신브라우저에서 SSE를 지원한다. (https://caniuse.com/?search=Server-Sent%20events)
    - 서버전송 DOM 이벤트를 기본으로 하며, EventSource 인터페이스를 사용한다.
    - **mediaType은 text/event-stream으로 정해져 있다.**

## vs WebSocket
|                      | WebSocket                                                      | SSE                                                 |
|:---------------------|:---------------------------------------------------------------|:----------------------------------------------------|
| 방향                   | 양방향                                                            | 단방향 (Server -> Client)                              
| 프로토콜                 | HTTP 기반의 WebSocket 프로토콜                                        | HTTP 프로토콜                                           |               
| 재연결 시도               | X                                                              | O                                                   |
| 커넥션                  | 유지                                                             | 유지 (HTTP이기 떄문에, 일반적인 HTTP Request에도 Connection 재사용) |
| Client Broswer 연결 한도 | 서버의 제약에 따름                                                     | 일반적으로 Browser당 6개 제한    (HTTP2 면 100개)              |



## Client 측 코드
```javascript
const eventSource = new EventSource(`/sse`);

eventSource.onmessage = event => {
	const data = JSON.parse(event.data);
	console.log(data.message);
};
eventSource.onerror = error => {
	eventSource.close();
};
```

## Server 측 코드
- https://www.baeldung.com/spring-mvc-sse-streams
- https://www.baeldung.com/spring-server-sent-events
- https://tecoble.techcourse.co.kr/post/2022-10-11-server-sent-events/
```java
@RestController
@RequiredArgsConstructor
class SSEController{
  private final SseConnectionStore store;
  @GetMapping("/connect")
  public ResponseEntity<SseEmitter> streamSseMvc() {
          SseEmitter emitter = new SseEmitter(); // SpringBoot Default 만료시간 30초
          store.add(emitter);
          SseEventBuilder event = SseEmitter.event()
                  .name("connect")
                  .data("success");
          
          emitter.send(event);
          return ResponseEntity.ok(emitter);
  }
}


@Component
@Slf4j
public class SseConnectionStore {

  private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  public SseEmitter add(SseEmitter emitter) {
    /**
     * onCompletion: Client와 연결이 종료될 때 호출되는 Callback
     */
    emitter.onCompletion(() -> {
      this.emitters.remove(emitter);    
    });
    /**
     * onTimeout: TimeOut이 발생했을 떄 호출되는 Callback
     */
    emitter.onTimeout(() -> {
      //onCompletion callback이 호출된다.
      emitter.complete();
    });
    this.emitters.add(emitter);
    return emitter;
  }
}
```
- Controller에서 SseEmitter 반환으로 쉽게 구현 가능하다.
- connect시점에, SseEmitter를 생성하고 관리할 수 있다.
  - connect 후 아무 작업을 하지않고, 만료가 된 시점에서 재연결 요청시, 503이 뜬다고 한다.
- 특정 Event가 발생하면, 저장되어있는 List에 모두 BroadCast하면 된다, (send method)
- 여러 Thread에서 접근 할 것이기 떄문에, SseEmitter 저장소는 Thread-Safe해야 한다.