# Transfer-Encoding: chunked

`Transfer-Encoding: chunked`는 **HTTP/1.1**에서 응답 본문(body)의 전체 크기를 미리 알 수 없거나,
서버가 데이터를 **점진적으로(streaming)** 보내고 싶을 때 사용하는 전송 방식이다.

- `Content-Length`를 보내지 않고
- 본문을 **chunk(조각)** 단위로 쪼개서
- 각 chunk 앞에 **chunk 크기(16진수)** 를 붙여 전송한다.

---

## 언제 쓰나

- 서버가 생성 중인 데이터를 **바로바로** 내려보내고 싶을 때
  - 예: 로그 스트리밍, SSE(Server-Sent Events), 대용량 응답의 점진적 전송
- upstream(백엔드) 응답을 proxy가 그대로 스트리밍해서 내려보낼 때
- 응답 전체를 버퍼링하면 메모리 비용이 큰 경우

---

## 전송 형식(프레이밍)

chunked 응답의 기본 구조는 아래와 같다.

```
HTTP/1.1 200 OK
Content-Type: text/plain
Transfer-Encoding: chunked

<chunk-size-hex>\r\n
<chunk-data>\r\n
<chunk-size-hex>\r\n
<chunk-data>\r\n
...
0\r\n
<optional-trailers>\r\n
\r\n
```

- `chunk-size-hex`: chunk 데이터 바이트 길이(16진수)
- `0` chunk: 본문 종료를 의미
- trailers: (선택) 본문 뒤에 붙는 헤더들(무결성/메타데이터 등)

### ASCII 흐름 그림

```
[Server]  --(chunk size + data 반복)-->  [Client]
   |               | size | data |
   |               | size | data |
   |               |  0   | end  |
```

---

## 예시

```http
HTTP/1.1 200 OK
Transfer-Encoding: chunked
Content-Type: text/plain

7\r\n
Mozilla\r\n
9\r\n
Developer\r\n
7\r\n
Network\r\n
0\r\n
\r\n
```

---

## 중요한 제약/주의사항

### 1) `Content-Length`와의 관계
- chunked를 쓰는 경우 일반적으로 `Content-Length`를 함께 보내지 않는다.
- 일부 중간 프록시/서버 조합에서 `Content-Length`와 `Transfer-Encoding`가 동시에 존재하면
  **요청/응답 스머글링(request smuggling)** 같은 보안 이슈의 단서가 될 수 있다.

### 2) 프록시/서버 버퍼링
스트리밍을 원해도 중간 계층이 버퍼링하면 "한 번에" 내려갈 수 있다.
- 예: Nginx의 `proxy_buffering` 설정
- 예: 애플리케이션 레벨에서 응답을 모두 모았다가 flush하는 구현

즉, **chunked 자체가 곧바로 end-to-end streaming을 보장**하지는 않는다.

### 3) HTTP/2, HTTP/3에서는?
- HTTP/2/3는 chunked 인코딩을 같은 방식으로 쓰지 않는다.
- HTTP/2는 프레임(DATA frame) 기반으로 스트리밍이 기본이며, "chunked"는 HTTP/1.1 프레이밍 기법이다.
  - 따라서 "HTTP/2에서도 chunked" 같은 표현은 보통 부정확하다.

### 4) 압축과의 상호작용
- `Content-Encoding: gzip` 등 압축은 chunked와 함께 사용될 수 있다.
- 단, 중간 프록시가 압축/해제를 수행하면 streaming 특성이 달라질 수 있다.

---

## 실무 체크리스트

- 정말 스트리밍이 필요한가? (클라이언트 처리 방식 포함)
- 중간 프록시/로드밸런서의 버퍼링 설정 확인
- 타임아웃(서버/프록시/클라이언트) 확인
- `Content-Length`와 `Transfer-Encoding` 동시 설정 금지(정책화 추천)

---

## 참고
- RFC 9112: HTTP/1.1 메시지 문법 및 라우팅 (Transfer-Encoding, chunked)
- MDN Web Docs: Transfer-Encoding
