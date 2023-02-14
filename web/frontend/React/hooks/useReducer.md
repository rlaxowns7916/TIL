# useReducer
1. **useState를 여러번 사용하지 않아도 되고, 더 다양한 컴포넌트 상황에 따라 상태 업데이트 가능**
    - 복잡한 State를 관리하는데 용이하다.
2. Reducer는 현재 상태, 업데이트에 필요한 정보를 담은 액션값을 전달받아 새로운 상태를 반환
3. Reducer를 통해 로직을 분리했기 때문에, 다른 곳에서도 재사용이 가능하다.

***const[state,dispatch] = useReducer(reducer,initialState)***
1. state는 우리가 사용할 상태
2. dispatch는 액션을 발생시킬 함수
   - dispatch에 action을 담아서 보내면, reducer가 해당 동작을 수행한다.
3. useReducer의 dispatch(액션)는 어떠한 값도 사용 가능하다.(event 라도)
4. useReducer도 state가 변하면 ReRendering 된다.
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
    const [state,dispatch] = useReducer(reducer,{value:0}) // 첫 번쨰 인자로 함수, 두 번째 인자로 initialState
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