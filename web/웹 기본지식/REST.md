# REST API
- Web API의 한 종류이다.
- 대부분의 HTTP API가 따르는 아키텍처 스타일 
  - 강제가 아니다.
  - Client - Server - Representation으로 구성되어 있다.
- Protocol에 대한 제약도 없다. (HTTP 버전 등)

## REST Design Principle

### [1] Client - Server Decoupling
- Client와 Server가 완전히 Decoupling되어 있어야 한다.
- Server는 Client의 Request에 대해서 Response만 보내줄 뿐, 어디서 어떻게 사용되는지에 대해서는 몰라도된다.
- 인터페이스만 일관되게 유지되면 각각의 구현 변경이 서로에게 영향을 미치지 않아야 한다.

### [2] Stateless
- 상태를 가지지않는다.
- 요청을 처리하기 위한 모든정보를 Reuquest에 포함해야 한다.

### [3] Uniform Interface
- API의 전체적인 형식은 모두 같아야 한다. (다루는 Resource, Request만 다를 뿐)
- 같은 Resource에 대한 Request는 항상 같아야한다.
- Resource는 Client가 필요한 최소한의 데이터만을 가지고 있어야 한다.

### [4] Cacheability
- Cache를 제공해야 한다.
- 서버의 응답에는 Caching이 허용되는지에 대한 여부를 포함해야한다.
- 이 원칙은 서버측의 확장성을 높이기 위한 목적을 가진다.

### [5] Layered System Architecture
- Client와 Server가 직접적으로 연결되어있다고 가정해서는 안된다.
  - 중간 매개체가 있다고 가정하는 것이다. 
- 각 계층은 이전 계층과만 상호작용 한다.
- RESTAPI는 통신에 있어서, 직접 Client에게 전달되는지 중간 매게체를 거치는지 구분할 수 없도록 설계되어야 한다.

### [6] Code On Demand (Optional)
- 일반적으로 정적인 자원을 보내지만, 동적인 것도 Response에 포함될 수 있다.
- 일반적으로는 자바스크립트 등의 스크립트 코드를 전달하는 데 사용된다.