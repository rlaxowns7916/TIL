# Hooks
- **리액트 v16.8에서 새로 도입**<br>
- **함수형 컴포넌트에서도 상태관리 가능**
- prefix로 use가 붙은 것들을 Hook이라고 본다.
  - 선언의 위치에 따라서 호출의 순서가 바뀔 수 있다.
  - 똑같은 dependency를 Listen 하고 있더라도, 선언한 순서에 따라서 sideEffect의 발생 순서가 달라진다.

### 주의 할점
1. 실행순서가 일정하게 유지되게 할 것
2. if문 같은 조건문안에 hooks를 넣지 말 것(for문 사용가능하나 비추천)
3. hook안에 hook을 사용하지 말 것
4. 컴포넌트 안에서만 호출 할 것

***
## useState
```jsx
    const [value,setValue] = useState('초기값')
    const [count,setCount] = useState('초기값')
// 첫번째 인자 : 값, 두번째 인자: Setter
```
- 컴포넌트 instance별 독립적인 state를 가지게 된다.
- Class형 컴포넌트 같이 State객체를 선언하고 setState를 통해서 한꺼번에 변경이아닌 쪼개서 관리가능
  - 객체로 state를 관리한다면, prevState를 사용하자.
  - 이전 state를 스프레드 연산자로 넘겨 줄 수도 있지만, useState는 즉각적으로 변경되는 것이 아니기 때문에, 부정확 할 수 있다.
  - prevState는 항상 최신을 보장한다.
  - ```jsx
    const titleChangeHandler = (event) => {
      /**
        * ArrowFunction으로 넘어감을 명심할 것
        */
        setUserInput((prevState) =>{
        return {
          ...prevState,
          title:event.target.value
        }   
      })
    }
    ```
- 상태변경 함수 (setState)를 호출한 컴포넌트 부터 다시 리랜더링이 되는 것이다.
  - 이전의 것과 비교해서 변경된점을 감지하고 변경된 부분을 리랜더링한다.
- setState를 통해서 양방향 바인딩이 가능하다.
  - Form으로 작업할 때 아주 유리하다.
  - 입력을 통해서 state를 변화시킬 수 있다.
  - state를 input태그에 넘겨줌으로써, state로 input을 변경시킬 수 있다.

### useState Lazy Initialization 
- ArrowFunction을 통해서 setState를 호출하는 것이다.
- 복잡한 연산을 할 때 사용하는 것이 좋다.
  - 최초실행시점에만 초기화 함수가 실행되며, 리 랜더링 시에 함수의 호출은 무시된다.
  - ```jsx
      /**
        * 최초 랜더링 시점에만 localStorage에서 불러오는
        * 초기화 로직을  실행시키는 것으로 충분하다.
        * localStorage에 접근하는 것도 비용이들기 때문이다.
        */
      const [count, setCount] = useState(() =>
      Number.parseInt(window.localStorage.getItem(cacheKey)),
      )
    ```
  - setState에 따른 리랜더링도 정상적으로 동작한다.

## useEffect
**componentDidMount + componentDidUpdate + componentWillUnmount**
- SideEffect를 일으킨다.
  - 부수효과라고한다.
  - 변화에 따른 이벤트를 다른 곳에 전파한다.
- Render가 발생한 이후에 Effect가 발생한다.

1. 첫 번째 인자는 실행할 작업 정의, 두번 째 인자는 변화를 Checking할 인자
2. 두 번째 인자 생략시 랜더링 될때마다 실행 
3. 자식부터 useEffect가 발생 한 후, 부모의 useEffect가 발생한다.
### ComponentDidMount 역할 대체
```jsx
/**
 * 빈 배열을 입력 할 시 최초 랜더링 시에만 사용(ComponentDidMount)
 */
useEffect(() =>{
    console.log("state 변화 시 할 일 정의")
},[])
```

### componentDidUpdate 역할 대체
```jsx
/**
 * 기존의 ComponentDidUpdate의경우 prevProps,prevState를 인자로 받아 변경을 비교하였으나,
 * 두번째 인자에 변경을 감지할 state를 넣어주기 떄문에 체크할 필요없이 변경 시 수행할 작업만 명시하면된다.
 */
useEffect(() =>{
    console.log(count)
},[count])
```

### componentWillUnmount 대체
```jsx
/**
  * return 을 통한 cleanUp함수를 정의해주면, 언마운트 될 때, 혹은 업데이트 되기직전에 작업을 수행 할 수 있다.
  * 마찬가지로 unMount시점에만 작동하게 하고 싶으면 두번째 인자에 빈배열을 넣으면 된다. 
  */ 
useEffect(() =>{
    console.log(useEffect)
    return () =>{
        console.log("cleanUp")
    }
})
```
- 부모부터 CleanUp 한 후 자식이 CleanUp 된다.

***

## useReducer
1. **useState보다 더 다양한 컴포넌트 상황에 따라 상태 업데이트 가능**
2. Reducer는 현재 상태, 업데이트에 필요한 정보를 담은 액션값을 전달받아 새로운 상태를 반환

***const[state,dispatch] = useReducer(reducer,initialState)***
1. state는 우리가 사용할 상태 
2. dispatch는 액션을 발생시킬 함수 
3. useReducer의 dispatch(액션)는 어떠한 값도 사용 가능하다.(event 라도)
```jsx
function reducer(state,action){
    switch(action.type){
        case 'INCREMENT':
            return {value : state.value+1}
        case 'DECREMENT' :
            return {value : state.value - 1}
        default:
            return state
    }
}   

const Counter = () =>{
    const [state,dispatch] = useReducer(reducer,{value:0})
    return(
    <div>
        <p>
            현재 카운터값은 <b>{state.value}</b>
        </p>
        <button onClick ={() => dispatch({type: 'INCREMENT'})}> +1 </button>
        <button onClick ={() => dispatch({type: 'DECREMENT'})}> -1 </button>
    </div>    
)
}
```

***
## useMemo
1. ***이전에 사용한 값을 재사용 가능하게 함***
2. ***함수형 컴포넌트에서 사용되는 연산의 최적화***
3. ***필요없는 랜더링 타이밍에 다시 연산을 하는 것을 막아줌***

***useMemo(callback,[변경을 체크할 값])***
```jsx
import React, { useState, useMemo } from 'react';

const getAverage = numbers => {
  if (numbers.length === 0) return 0;
  const sum = numbers.reduce((a, b) => a + b);
  return sum / numbers.length;
};

const Average = () => {
  const [list, setList] = useState([]);
  const [number, setNumber] = useState("");

const onChange = e => {
    setNumber(e.target.value);
  };
  const onInsert = () => {
    const nextList = list.concat(parseInt(number));
    setList(nextList);
    setNumber("");
  };
const avg = useMemo(() => getAverage(list), [list]); //리스트가 변경 될때만 연산
return (
    <div>
      <input value={number} onChange={onChange} />
      <button onClick={onInsert}>등록</button>
      <ul>
        {list.map((value, index) => (
          <li key={index}>{value}</li>
        ))}
      </ul>
      <div>
        <b>평균값:</b> {avg}
      </div>
    </div>
  );
};

export default Average;
```
***
## useCallback
1. **성능 최적화 시점에 주로 사용**
2. **함수를 필요할 때만 생성 가능**
3. **값을 재사용하려면 useMemo, 함수를 재사용하려면 useCallback**

**useCallback(생성하고 싶은 함수, [변경을 체크할 값])**
```jsx
useCallback(() => {
  console.log('hello world!');
}, [])


useMemo(() => {
  const fn = () => {
    console.log('hello world!');
  };
  return fn;
}, [])
```
***
### Custom Hooks
- **여러 컴포넌트가 비슷한 기능을 공유 할 때 사용**
- 중복을 방지하는 방법이다.
```jsx
const App = () => {
  const useCustom = (value) => {
    const [item, setItem] = useState(value);

    useEffect(() => {
      console.log("Chagne");
    }, [item]);

    return [item, setItem];
  };

  const [input, setInput] = useCustom("");

  const onChangeInput = (e) => {
    setInput(e.target.value);
  };

  return (
    <div className="App">
      <h1>Hello CodeSandbox</h1>
      <h2>Start editing to see some magic happen!</h2>
      <input value={input} onChange={onChangeInput} />
    </div>
  );
};

export default App;
```

