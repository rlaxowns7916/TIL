# HTTP - Cache-Control
- HTTP 캐시는 여러고셍 존재 할 수 있다.
- CDN을 사용한다면 CDN도 캐시이며, 브라우저 캐시도 존재한다.

## Cache의 종류

1. Shared Cache
    - CDN, Proxy가 포함된다.
    - 여러 사용자와 캐시를 공유한다.
2. Private Cache
    - 브라우저 캐시도 포함된다.
    - Client에 존재하며, 개인, 로컬 캐시라고도 불린다.
    - 개인화된 컨텐츠를 저장하고 재사용 할 수 있다.


## Cache-Control이란?
- HTTP Header 필드이다.
- 공유캐시 (CDN, Proxy)에 포함되는 캐싱 제를 Request/Response 과정에서 포함한다.

## Cache-Control 구성 요소

### max-age = <seconds>
- 캐시의 TTL을 의미한다.
- 응답이 생성 한 후로부터 N초간 최신상태를 유지한다는 것을 의미한다.
- N초동안 캐시로 사용이 가능하다는 것을 의미한다.
- max-age가 지난후 Revalidation을 수행하고, 304를 받았을 때만 캐시 컨텐츠 변경없이 Client에게 재사용 가능하다.

#### max-age = 0
- 항상 원본 서버로 유효성 검증 요청을 보낸다.

### s-max-age
- max-age와 유사하지만 공유캐시에서만 해당된다.
- s-max-age가 존재하는 경우, max-age를 무시한다.

### no-cache
- 캐시를 사용하는 것은 가능하다.
- 캐시를 Client에게 제공할 때마다 원본서버에 검증을 받아야한다.

### no-store
- 캐시를 사용하지 않는다.

### must-revalidate
- Cache가 만료 후에는 항상 Revalidation을 수행
- must-revalidate옵션을 사용할 경우, 원본서버와의 통신이 안된다면 504(Gateway Timeout)이 발생한다.

### private (default)
- 응답을 개인 캐시에서만 사용한다는 것을 의미한다.
- 공유캐시에서 사용되지 않는다.

### public
- 응답을 공유 캐시에서도 사용가능하다는 것을 의미힌다.
- Authorization 헤더 필드가 있는 경우에서는 public을 사용해서는 안된다.

### expires
- 실제 만료될 시간을 의미한다.