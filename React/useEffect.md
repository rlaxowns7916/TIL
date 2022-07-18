# useEffect
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
