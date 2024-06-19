# SSE (ServerSentEvent)
- HTTP 스펙이다.
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
- 구현이 간단하지만, WebSocket에 비해서 성능이 안좋을 수 있다.

## 수행 방식
- Server측의 content-type을 text/event-stream으로 설정을 통해서 SSE가 설정된다.
- Server는 응답을 보낸 후에도 연결을 끊지않는다. (FIN 패킷을 보내지 않고 연결을 유지한다.)
- Client와 Server 둘다 Connection을 끊을 수 있다.

## vs WebSocket
|               | WebSocket                                           | SSE                                                         |
|:--------------|:----------------------------------------------------|:------------------------------------------------------------|
| 방향            | 양방향                                                 | 단방향 (Server -> Client)                                      
| 프로토콜          | HTTP 기반의 WebSocket 프로토콜 (HTTP로 Handshake 후 Upgrade) | HTTP 프로토콜                                                   |               
| 재연결 시도        | X                                                   | O                                                           |
| 커넥션           | 유지  / 재연결은 클라이언트가 직접 구현해야함                          | 유지 (HTTP이기 떄문에, 일반적인 HTTP Request에도 Connection 재사용) / 자동재연결 |
| Connection 한도 | 서버의 제약에 따름                                          | 하나의 도메인당 6개 제한 (HTTP/1.1)   (HTTP2 면 100개)                  |
| 데이터 형식        | binary / text                                       | text                                                        |

### 도메인당 6개 제한은 SSE만의 문제인가?
```text
서버측의 문제가 아닌 클라이언트의 문제이며, 특정 기술에 국한되지 않고, 모든 HTTP 연결에 적용된다.
HTTP/1.1의 Connection당 최대 6개의 Connection을 유지하는 것은, HTTP/1.1의 특성이다.
HTTP/1.1은 각 요청마다 별도의 연결을 사용하는 구조로 되어 있어, 브라우저는 이를 최적화하기 위해 동시 연결 수를 제한한다.

이미 해당 도메인에 6개의 Connection을 맺은 상태에서, 다음 요청이 들어오면 해당 요청은 대기상태에 이르게 된다.
(리소스 로드, AJAX 요청, 이미지 파일 요청 등 모든 HTTP 요청을 포함하며,  SSE로 인해 Connection을 6개 확보한 상태에서 일반적인 HTTP Request가 들어온다면 무한정 Block이 될 것이다)

Connection관리, 네트워크 혼잡완화등을 위해서 채택한 것이다.
HTTP/2는 하나의 Connection을 유지하면서, 여러개의 Stream을 유지한다.
```
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