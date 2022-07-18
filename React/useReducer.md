# useReducer
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