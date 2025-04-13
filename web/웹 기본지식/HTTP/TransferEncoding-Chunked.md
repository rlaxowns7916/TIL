# Transfer-Encoding: chunked
-  HTTP/1.1에서 응답 본문의 크기를 미리 알 수 없을 때 사용되는 중요한 전송 방식
  - Content-Length를 알 수 없고, 응답을 Chunk단위로 잘게 쪼개서 전송한다.
  - Chunk는 Size + Data 형식으로 전달된다.
- Streaming, ServerPush 등에서 사용된다.

## 사용이유
- Content-Length를 미리 알 수 없는 경우 사용
- 응답을 점진적으로 전달하여, 메모리 사용을 최소화

## 응답
```http request
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
