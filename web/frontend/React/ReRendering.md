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


## Key
- 반복되는 Component의 사용시에, Key값을 명시해주는 것이 좋다.
- Component를 구별 하게 해주는 식별자이다.
- Key를 통한 DOM 트리 비교를 통해 효울적으로 동작한다.
  - Key값이 없을 시에, 내부 속성 까지 모든 비교를 요구한다.
- 중복되지 않고, 바뀌지 않는 유일한 값을 주어야 한다.

### index의 사용?
- Key값을 대체할 수 있는 선택지중 하나이다.
- 컴포넌트가 재 배열 되는 경우에는 비효울적으로 동작하게 된다.

## React.Memo
- 부모컴포넌트의 변화는 자식 컴포넌트도 ReRendering 시킨다.
- memo를 사용하여, **props의 변화가 있을 때만** 부모의 변화에 ReRendering하게 최적화 시킬 수 있다.
  - object라면, 제대로 동작하지 않는다. (랜더링 이전과, 랜더링 후의 object는 다르기 때문이다.)
  - 해당 object를 useMemo나, useCallback으로 memoization을 하면 된다.
- 꼭 필요할 때만 사용해야 한다.

```jsx
/**
 * 부모 Component가 ChildComponent를 가지고 있다고 가정
 */
import React,{memo} from "react"
const Child = ({name,age}) =>{
  
  return (
      <>
        <p> {name} </p>
        <p> {age} </p>
      </>
  )
}

export default memo(Child)

```