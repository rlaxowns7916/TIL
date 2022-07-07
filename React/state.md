# state
- Component안에서 관리되는, Component의 상태이다.
- 변경이 가능하다.
- state의 변경은 Compoentn의 리랜더링을 발생시킨다. 

## state 예시
```jsx
import {useState} from "react";
const ExpenseForm = (props) => {
  /**
   * 객체로 State 관리하기
   */
  const [expense, setExpense] = useState({
    title: "",
    amount: "",
    date: "",
  });

  /**
   * 스프레드 연산자 --> 기존 객체에 있었던 것들을 덮어쓴다.
   */
  const titleChangeHandler = (event) => {
    setExpense({
      ...expense,
      title: event.target.value,
    });
  };
  const amountChangeHandler = (event) => {
    setExpense({
      ...expense,
      amount: event.target.value,
    });
  };
  const dateChangeHandler = (event) => {
    setExpense({
      ...expense,
      date: event.target.value,
    });
  };
  /**
   * submit의 기본적인 동작은 서버에 요청을 보내고 Reloading이다.
   */
  const submitHandler = (event) => {
    //페이지 리로딩 기본동작(x)
    event.preventDefault();
    //state 끌어올리기
    props.onSaveExpenseData(expense);
    setExpense({
      title: "",
      amount: "",
      date: "",
    });
  };
  return (
    <form onSubmit={submitHandler}>
      <div className="new-expense__controls">
        <div className="new-expense__control">
          <label>Title</label>
          <input
            value={expense.title}
            type="text"
            onChange={titleChangeHandler}
          />
        </div>
        <div className="new-expense__control">
          <label>Amount</label>
          <input
            type="number"
            min="0.01"
            step="0.01"
            value={expense.amount}
            onChange={amountChangeHandler}
          />
        </div>
        <div className="new-expense__control">
          <label>Date</label>
          <input
            type="date"
            min="2022-01-01"
            max="2022-12-31"
            value={expense.date}
            onChange={dateChangeHandler}
          />
        </div>
      </div>
      <div className="new-expense__actions">
        <button type="submit">Add Expense</button>
      </div>
    </form>
  );
  /**
   * 브라우저에서는 fom 안의 button이 Click 된다면, Form의 submit이벤트가 발생한다.
   */
};

export default ExpenseForm;

```

## state 끌어올리기
- child에서 parent로 데이터를 전달하는 방식이다.
- sibling관계에서는 성립할 수 없다.
  - sibling의 공통부모까지 데이터를 끌어올린 후 부모가 sibling에게 데이터를 넘겨주어야한다.
- 부모의 state변경 함수를 props로 받고, child가 해당 함수를 사용함으로써, 데이터를 변경 할 수 있다.

### Parent
```jsx
import {useState} from "react";
const App = () => {
  const [expense,setExpense] = useState({
      id: "e1",
      title: "Toilet Paper",
      amount: 94.12,
      date: new Date(2020, 7, 14),
  })
    
   const onChangeExpenseHandler = (newExpense) =>{
     setExpense(newExpense)
   }

  return (
    <div>
        <Expenses item={expenses} />
        <ExpenseForm item={expenses} onChangeExpense ={onChangeExpenseHandler()} />
    </div>
  );
};

export default App;

```

### Child
```jsx
const ExpenseForm = (props) => {
    const [expense, setExpense] = useState(props.item)

  /**
   * 스프레드 연산자 --> 기존 객체에 있었던 것들을 덮어쓴다.
   */
  const titleChangeHandler = (event) => {
    setExpense({
      ...expense,
      title: event.target.value,
    });
  };
  const amountChangeHandler = (event) => {
    setExpense({
      ...expense,
      amount: event.target.value,
    });
  };
  const dateChangeHandler = (event) => {
    setExpense({
      ...expense,
      date: event.target.value,
    });
  };
  /**
   * submit의 기본적인 동작은 서버에 요청을 보내고 Reloading이다.
   */
  const submitHandler = (event) => {
    //페이지 리로딩 기본동작(x)
    event.preventDefault();
    props.onChangeExpense(expense);
    setExpense({
      title: "",
      amount: "",
      date: "",
    });
  };
  return (
    <form onSubmit={submitHandler}>
      <div className="new-expense__controls">
        <div className="new-expense__control">
          <label>Title</label>
          <input
            value={expense.title}
            type="text"
            onChange={titleChangeHandler}
          />
        </div>
        <div className="new-expense__control">
          <label>Amount</label>
          <input
            type="number"
            min="0.01"
            step="0.01"
            value={expense.amount}
            onChange={amountChangeHandler}
          />
        </div>
        <div className="new-expense__control">
          <label>Date</label>
          <input
            type="date"
            min="2022-01-01"
            max="2022-12-31"
            value={expense.date}
            onChange={dateChangeHandler}
          />
        </div>
      </div>
      <div className="new-expense__actions">
        <button type="submit">Add Expense</button>
      </div>
    </form>
  );
};

export default ExpenseForm;

```