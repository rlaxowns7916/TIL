# Hooks
**리액트 v16.8에서 새로 도입**<br>
**함수형 컴포넌트에서도 상태관리 가능**

## useState
```jsx
    const [value,setValue] = useState('초기값')
    const [count,setCount] = useState('초기값')
// 첫번째 인자 : 값, 두번째 인자: Setter
```
Class형 컴포넌트 같이 State객체를 선언하고 setState를 통해서 한꺼번에 변경이아닌 쪼개서 관리가능

### useEffect
**componentDidMount + componentDidUpdate + componentWillUnmount**
1. 첫 번째 인자는 실행할 작업 정의, 두번 째 인자는 변화를 Checking할 인자
2. 두 번째 인자 생략시 랜더링 될때마다 실행 
### ComponentDidMount 역할 대체
```jsx
useEffect(() =>{
    console.log("state 변화 시 할 일 정의")
},[])
```
useEffect의 두번째 인자는 변경을 감지할 state가 들어 갈 곳<br>
빈 배열을 입력 할 시 최초 랜더링 시에만 사용(ComponentDidMount)

### componentDidUpdate 역할 대체
```jsx
useEffect(() =>{
    console.log(count)
},[count])
```
기존의 ComponentDidUpdate의경우 prevProps,prevState를 인자로 받아 변경을 비교하였으나,
두번째 인자에 변경을 감지할 state를 넣어주기 떄문에 체크할 필요없이 변경 시 수행할 작업만 명시하면된다.

### componentWillUnmount 대체
```jsx
 useEffect(() =>{
    console.log(useEffect)
    return () =>{
        console.log("cleanUp")
    }
})
```
return 을 통한 cleanUp함수를 정의해주면, 언마운트 될 때, 혹은 업데이트 되기직전에 작업을 수행 할 수 있다.
마찬가지로 unMount시점에만 작동하게 하고 싶으면 두번째 인자에 빈배열을 넣으면 된다.