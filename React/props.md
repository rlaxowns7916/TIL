# props
- ReadOnly이다.
- 부모에서 자식으로 내려주는 값으로 단방향이다.
  - 데이터도 가능하다.
  - 함수도 가능하다.
- 부모에서 전달 할 때는 HTML의 Attribute문법을 사용한다.
- 자식에서 받을 때는 파라미터로 받는다.
- key:value형식으로 전달된다.
  - props.{key} 를 통해서 접근 할 수 있다.'
- props의 변경은 Compoentn의 리랜더링을 발생시킨다.
- children도 props의 일부이다.

## props 예시

### Parent
```jsx
const Expenses = (props) => {
    return (
        <Card className="expenses">
            <ExpenseItem
                title={props.items[0].title}
                amount={props.items[0].amount}
                date={props.items[0].date}
            /> 
        </Card>
    );
}
export default Expenses;
```

### Child
```jsx
function ExpenseItem(props) {
  const [title, setTitle] = useState(props.title);
  const [amount, setAmount] = useState(props.amount);
  const [date, setDate] = useState(props.date);
    

  return (
    <Card className="expense-item">
      <ExpenseDate date={props.date} />
      <div className="expense-item__description">
        <h2>{title}</h2>
        <div className="expense-item__price">{props.amount}</div>
      </div>
    </Card>
  );
  export default ExpenseItem;
}
```


## props.children
```jsx
function Card(props) {
  const classes = "card " + props.className;

  return <div className={classes}>{props.children}</div>;
}

export default Card;
```
- Compoentn 사이에 들어가 있는 값들을 모두 접근 할 수 있다.
  - Component
  - Tag
  - Value