# useState
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