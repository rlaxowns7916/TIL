# Ref

```text
일반적으로 HTML에서는 DOM요소에 id 를 달아서 Css 혹은 Js에서 접근한다.
리액트 내부프로젝트에서 이러한 DOM 요소에 이름을 다는 방법이**Ref**개념이다.
리액트에서 DOM을 직접 건드려야 할 때 주로 사용하고, 컴포넌트에도 ref를 직접 달 수 있다.
```
### DOM에 직접 접근해야 하는상황
1. 특정 Input에 포커스를 줄 때
2. 스크롤 박스 조작하기
3. Canvas요소에 그림 그리기 <br>
        .<br>
        .<br>
        .<br>

### 리액트에서 DOM에 id값을 지정하지 않는 이유
```text
HTML에서 DOM의 id값은 유일해야한다.
같은 컴포넌트를 재사용하는 경우 이러한 DOM 값에 id를 부여한다면 문제가 될것이다.
그래서 ref 개념을 사용한다.
```

### Ref 설정 방법
1. 콜백함수를 통한 Ref 설정
```jsx
    <input ref={(ref) => {this.input = ref}/}
```
콜백함수의 인자로 받은 ref를 컴포넌트 멤버변수로 선언해준다.

2. React내부 createRef함수
```jsx
class RefSample extends Component {
  input = React.createRef();



handleFocus = () => {
    this.input.current.focus();
  }



render() {
    return (
      <div>
        <input ref={this.input} />
      </div>
    );
  }
}

자

export default RefSample;
```
컴포넌트들에 ref를 달면서 컴포넌트끼리 교류하도록 코드를 작성 할 수있지만,<br>
이건 리액트 사상에 어긋나는일, 꼭 필요할 때만 사용하