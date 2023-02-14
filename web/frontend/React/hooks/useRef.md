# useRef

- 특정 DOM을 선택 할 때 사용한다.
- {current: value} 형식으로 되어있다.
- 성능을 목적으로, 자주 변경되는 값을 ref로 사용하면 좋다. **(변화는 감지해야 하지만, 랜더링을 유발하면 안될 떄 사용)**
  - 자주 변경이 되는 값은 ReRendering을 유발하기 떄문이다.
- 변수를 저장하는 용도로도 사용이 가능하다.
  - **useRef는 값이 변경되어도, Rendering이 발생하지 않는다.**
  - 다른 State의 변화로 Rendering이 되어도, ref에 있는 값은 유지된다.
- JS의 경우 document.querySelector를 통해서 접근 가능하지만, React에서 해당 기능을 제공하는 이유는 리액트의 DOM 관련 최적화를 이용하기 위해서이다.
- 특정 DOM의 focus, Video.js, chart.js 등 외부 라이브러리 사용 시, DOM에 접근해야하기 때문에 사용된다.

```jsx
import {useState, useRef} from "react";

const sample = () => {
    const [name, setName] = useState("");
    const nameInput = useRef();

    const onChange = (e) => {
        setName(e.target.value);
    }
    /**
     * Button Click 후 input 창 초기화 및 Focus
     */
    const onClick = () => {
        setName("");
        nameInput.current.focus();
    }

    return (
        <>
            <input placeholder="name" onChange={onChange} value={name} ref={nameInput}/>
            <button onClick={onClick}> 전송 </button>
        </>
    )

}
export default sample;
```