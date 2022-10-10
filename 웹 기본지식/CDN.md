# CDN (Content Delivery Network)
- 지리적으로 분산된 여러개의 서버
- 정적 컨텐츠(웹 페이지, 이미지, 비디오)를 사용자와 가까운 프록시 서버에 캐싱한다.
  - 물리적으로 가까운 서버와의 통신으로 인해 빠른 응답을 기대할 수 있다.
- 원본 서버에 대한 부하분산이 가능하다.
- CDN 서버에 요청한 컨텐츠가 없는 경우 원본서버에서 가져와 저장 한후 컨텐츠를 리턴해준다.(Read-Through)
  - 원본서버와 지속적인 연결을 갖는다.
- 적절한 만료시간을 설정해야 한다.
  - 너무 짧은 시간은 원본서버에 잦은 접속으로 인해 캐싱의 효과가 떨어진다.
  - 너무 긴 시간은 컨텐츠의 신선도에 영향을 끼치게 된다.
- 많은 비용을 지불해야 할 것이다.
  - 꼭 필요한 컨텐츠를 CDN에 둠으로 해서 비용과 성능의 적절한 Trade-Off의 지점을 찾아야 한다.
 
![afaf](https://user-images.githubusercontent.com/57896918/167162853-c484c0d5-6941-4d5d-9c66-352eb5cec0d3.png)


## 분류
1. pushing
   - 콘텐츠 제공자가 미리 캐시 서버에 콘텐츠를 옮겨두는 방식 
   - 변경이 적을 때 적합
2) caching
   - 사용자가 캐시서버에 콘텐츠를 요청 
   - 만약 캐시서버에 콘텐츠가 없다면 해당 데이터는 원본 서버에서 조회 후 고객에 전달 
   - 이후에는 캐시된 콘텐츠로 서비스 제공
   - 모든 콘텐츠를 관리자가 관리할 수 없음
3) spliting 
   - 실시간 동영상 제공용 
   - 실시간 동영상 전송 부하를 최소화 하기위한 기술

## Cache ReValidation (캐시 유효성 검증)
- CDN에서의 Content의 유효시간이 만료되었을 때 진행된다.

### 1. Cache Hit
- 304 (NotModified)를 리턴으로 받는다.
- CDN의 TTL만 갱신하고, 갖고있던 Content를 Client에게 리턴한다.

### 2. Cache Miss
- 200 (OK)와 함께 content를받는다.
- CDN은 Content를 갱신 한 후 Client에게 리턴한다.

## CDN Contents Invalidation (컨텐츠 무효화)

### CDN Purging
- Cache Eviction처럼 특정 조건이 발생하길 기다리는 것이 아닌, **바로 제거해버리는 것** 이다.
  - 삭제 후 원본 서버에서 컨텐츠를 갱신한다.
- cache tags(캐시 태그), surrogate-cache-keys(삭제 캐시 키)라는 개념을 사용한다.
- Cache기간을 길게 가져가고, 업데이트시에 해당 컨텐츠를 삭제하는 것이 가능하다.
  - 동적인 컨텐츠에서 쓰는 것이 맞다.
- 자주사용하면 원본서버로의 요청이 증가하여 부하가 커진다.
- CDN서버의 컨텐츠 갱신이다.
  - LocalCache나 BrowserCache까지 갱신되었다는 보장은 없다.

### Object Versioning
- 뒤에 Version번호를 붙여 다른 컨텐츠를 소비하도록 하는 것이다.
- image.png?v=2 같은 식으로, 업데이트마다 버전을 갱신해준다.


## Image CDN
- 이미지에 특화된 CDN이다.
  - 이미지를 실시간으로 크기, 형식 등을 바꾸는 역할을 한다.
- 이론적으로 이미지파일의 크기를 40~80% 줄일 수 있다고 한다.
- CDN + ProcessingServer + Storage의 구성이다. 
```text
USER < -> CDN < -> Processing Server < - > Storage
```