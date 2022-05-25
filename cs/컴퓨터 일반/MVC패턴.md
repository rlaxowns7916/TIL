# MVC 패턴
- Model, View, Controller의 약자이다.

## Model
- Data를 가공하는 책임을 지닌 컴포넌트이다.
- 비즈니스 로직을 처리 한 후, 변경사항을 Controller에 전달한다.

## View
- 사용자에게 보여지는 부분 User Interface를 의미한다.
- Model에 대한 정보나 상태를 저장하지 않고 보여주기만 한다.

## Controller
- Model과 View 사이를 잇는 브릿지 역할을 한다.
  - Model과 View는 서로의 존재를 알지 못해야 한다.
- Model이나 View의 변경을 모니터링 하여 변경사항을 이어주어야 한다.


## 왜 사용하는가?
- 큰 Application이더라도 Layer를 나눔으로써 구성이 쉬워진다.
- 비즈니스로직과 View를 분리함으로써 의존성을 낮춘다.
  - 낮아진 의존성을 통해 수정 및 확장성에서 유리함을 갖는다.