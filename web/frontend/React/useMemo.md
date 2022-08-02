# useMemo
1. ***이전에 사용한 값을 재사용 가능하게 함***
2. ***함수형 컴포넌트에서 사용되는 연산의 최적화***
3. ***필요없는 랜더링 타이밍에 다시 연산을 하는 것을 막아줌***

## Memoization
- 동일한 계산을 반복해야 할 때, 이전에 계산했던 값을 메모리에 저장하고 다시 사용하는 것
- useMemo, useCallback의 주요 개념이다.
  - 동일한 props를 랜더링한다면 Memo나 Callback을 사용하자


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