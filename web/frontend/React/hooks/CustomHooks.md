# CustomHooks
- 중복을 줄이기 위한 방법이다.
  - 내부에서 여러가지의 Hook을 사용 할 수 있다.
  - **CustomHook내부의 State는 완전히 독립적이다.**
  - 리턴 값도 자유롭게 정의 할 수 있다.
- use로 시작하는 것이 관습이다.

## 중복코드
```js

const App = () =>{
  const [input1,setInput1] = useState("")
  const [input2,setInput2] = useState("")
  const [input3,setInput3] = useState("")
  const [input4,setInput4] = useState("")
  
  const onHandleChangeInput1 = ((e) =>{
    setInput1(e.target.value)
  })

  const onHandleChangeInput2 = ((e) =>{
    setInput2(e.target.value)
  })

  const onHandleChangeInput3 = ((e) =>{
    setInput3(e.target.value)
  })

  const onHandleChangeInput4 = ((e) =>{
    setInput4(e.target.value)
  })
  
  return(
      <>
        <input value= {input1} onchange= {onHandleChangeInput1} />
        <input value= {input1} onchange= {onHandleChangeInput2} />
        <input value= {input1} onchange= {onHandleChangeInput3} />
        <input value= {input1} onchange= {onHandleChangeInput4} />
      </>
  )
}
```

## Custom Hook
```js
const useInput = (initialState) =>{
  const [input,setInput] = useState(initialState)
  
  const onChangeHandler = (e) =>{
    setInput(e.target.value)
  }
  
  return [input,onChangeHandler]
}


const App = () =>{
  
  const [input1,onChangeHandler1] = useInput("")
  const [input2,onChangeHandler2] = useInput("")
  const [input3,onChangeHandler3] = useInput("")
  const [input4,onChangeHandler4] = useInput("")
  
  return(
      <>
        <input value={input1} onchange= {onChangeHandler1}>
        <input value={input1} onchange= {onChangeHandler2}>
        <input value={input1} onchange= {onChangeHandler3}>
        <input value={input1} onchange= {onChangeHandler4}>
                
      </>
  )
}
```