# useCallback
1. **성능 최적화 시점에 주로 사용**
   - Memoization의 원리이다.
   - 함수 객체를 Memory에 저장하고, 다시 불러온다.
2. **함수를 필요할 때만 생성 가능**
   - JavaSciprt에서 함수는 일종의 객체이다.
   - 객체이기 떄문에, DependencyArray로 정상적으로 동작하지 않을 수 있다. (참조주소가 달라졌기 때문)
   - **그 당시 state도 Memoization되기 떄문에, 참조하는 Memoiztaion에 대한 것을 Dependency Array로 잡고 있어야 한다.**
3. **값을 재사용하려면 useMemo, 함수를 재사용하려면 useCallback**

**useCallback(Memoization하고 싶은 Callback 함, [변경을 체크할 값])**
```jsx
/**
 * 최초 실행시점 Memoization
 */
useCallback(() => {
  console.log('hello world!');
},[])

```