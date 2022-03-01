## PresentationLayer
- 사용자의 요청을 해석한다.
- 사용자의 요청을 Application영역이 원하는 요구사항에 맞게 파싱하여 넘겨준다.
- HTML/JSON/XML등의 형식을 지원한다.
- PresentationLayer의 존재로, Applciation영역은 통신기술의 영향을 받지 않는다.
- 세션관리
- 인증 / 인가
- 필수적인 값, 형식에 대한 검증

## ApplicationLayer
- Domain영역과 Presentation영역 사이의 **Facade**역할을 한다.
- Domain로직을 Applicatio영역에서 구현하면 안된다.
  - 코드의 응집성이 떨얼진다.
  - Domain로직을 파악하기위해, 여러영역을 분석하게된다.
  - 여러 Aplicaion영역에서 중복된 Domain로직을 작성하게 될 가능성이 높아진다.
  - 한 Applicaiton영역에 2~3개의 기능만 구현하자.
    - 한 클래스에 여러기능을 구현하면 중복된 코드를 줄일 수는 있으나, 코드들이 점점 얽히게 되고 의존성이 높아진다.
    - 중복방지가 목적이라면, 중복처리를 위한 클래스를 따로만들자.
  - Presentation영역에는 Aggregate를 넘기는 것이 아닌, 필요한 데이터만 넘길 것
  - Presentation영역에 연관된 것 사용하지 말 것. (HttpServletRequest ...)
    - 테스트 하기 힘들어진다.
    - Presentation영역에 대한 의존이 생기고, Presentation의 변경에 민감해진다.
  - 트랜잭션관리
  - 도메인 이벤트 관리
    - 도메인 이벤트 사용을 통해 도메인간의 의존성을 줄일 수 있다. 
  - 값에 대한 논리적인 검증