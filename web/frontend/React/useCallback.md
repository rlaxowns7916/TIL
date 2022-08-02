# useCallback
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