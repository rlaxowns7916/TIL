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

### Hooks의 사용 이유
1. this의 바인딩 이슈 등, Class는 혼란을 주었다.
2. 로직의 재활용 및 관리가 쉽다.
   - CustomHook을 통해서 조합이 가능하다.
   - Class형 Compoenent의 LifeCycle Method에서 다른 로직들이 하나의 메소드에 섞여 있었기 때문에, 
     관리가 쉽지않았지만, Hooks는 관리에 용이하다.


## Custom Hooks
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

