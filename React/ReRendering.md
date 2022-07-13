# ReRendering
- 화면을 다시 랜더링 하는 것을 의미한다.
- 리액트는 변경된 부분만 랜더링 하기 때문에, 효율적이다.
- React Element는 Immutable하다.
  - 조건을 만족해야 리랜더링 된다.
  - props변경, state변경, 부모의 ReRendering
  
## 원리
- 바뀌기 전과 바뀐 후의 두 Virtual DOM을 비교한다.
  - Root Element 부터 비교를 시작한다.
- Element의 Type이 **다르면** 아예 그 부분부터 새롭게 랜더링을 시작한다.
- Element Type이  **같다면** 속성을 확인하여, 같은 것은 유지하고 바뀐것만 새롭게 렌더링한다.


## Virtual Dom
- DOM을 추상화한 가상 객체이다.
- DOM의 비효율적인 문제를 해결하고자 만들어진 기술이다.
  - DOM의 경우 변경부분이 있을 때, 처음부터 다 랜더링한다.
- VirtualDom을 통해서 이전상태와의 비교를 통해서 바뀐부분만 알아챌 수 있으며, 
  Dom에서 해당 부분만 리랜더링 하게 된다.