# ETag
- 순서 
  1. 클라이언트가 이전에 받아본 리소스의 ETag 값을 저장하고 있는다. 
  2. 이후 캐시가 만료되어, 같은 리소스를 다시 요청할 때 If-None-Match 헤더에 ETag 값을 넣어 보낸다.
  3. 서버는 해당 ETag와 현재 리소스의 ETag를 비교해서 변경이 없다면 304 Not Modified를 응답한다.
- CDN
  - CDN은 ETag를 사용하여 캐시된 리소스의 유효성을 검증할 수 있다.
  - 클라이언트가 요청한 리소스의 ETag와 CDN에 저장된 ETag를 비교하여 변경이 없으면 캐시된 리소스를 제공한다.


## 최초 요청
```http request
GET /logo.png HTTP/1.1

HTTP/1.1 200 OK
ETag: "v1.abc123"
Content-Type: image/png
Content-Length: 50000
```

## 이후 요청
```http request
GET /logo.png HTTP/1.1
If-None-Match: "v1.abc123"


HTTP/1.1 304 Not Modified
```