# useContext

## Context
- React는 Component 계층 구조이다.
  - 상위 계층의 state를 하위계층에서 쓰려면, 끊김없이 밑으로 내려가야 한다.
  - 전역적으로 사용하는 state라면, 모든 Component에 퍼져있어 관리하기가 힘들 것이다.
  - **이를 Prop Drilling이라고 한다.**
- **일종의 APP에서 사용되는 전역변수로, React에서 제공한다.**
- 전역적으로 필요한 state를 자유롭게 자식 Component에서 접근 가능하게 한다.
- **context 사용 시, Component 재사용이 어렵다.**
  - 꼭 필요할 때만 사용해야 한다.
  - React 공식문서에서는 PropDrilling을 피하기 위한 목적이라면, Component Composition을 먼저 고려해보라고 되어있다.
- 모든 Component 최상위에서 감싸주면 된다.
  - **createContext를 통해서 생성한다. 
  - **useContext를 통해서 사용한다.**
- Context도 State기 떄문에, 변할 시에 ReRendering 된다.

### 기존 PropsDrilling

```js

const App = () =>{
  
  const [isDark,setIsDark] = useState(false)
}

/**
 * Page Component는 중간 다리 역할이다.
 * 자신에게 필요없는 props를 자식 Component에 넘겨주기 위해서, 부모에게서 Props를 받는다.
 */
const Page = ({isDark, setIsDark}) =>{
  return (
      <>
        <Header isDark = {isDark}  />
        <p>본문</p>
        <Footer isDark = {isDark} setIsDark ={setIsDark}/>
      </>
  )
  
}


const Header = ({isDark}) => {
  return(
    <>
      <header style = {{ backgroundColor: isDark? 'black' : 'lightgray' }} />
    </> 
  )
}

const Footer = ({isDark,setIsDark}) => {
  const onClickHandler = () => {
    setIsDark(!isDark)
  }
  
  return (
      <>
        <footer style = {{ backgroundColor: isDark? 'black' : 'lightgray' }} />
        <button onclick= {onClickHandler} > 다크 모드 클릭 </button>
      </>
  )
}
```

### Context API 사용
```js
// ThemeContext.js
import {createContext} from "react"

// 초기값을 넘겨준다. value로 넘겨줘도 된다.
export const ThemeContext = createContext(null)
```

```js
const App = () =>{
  
  return(
      <ThemeContext.Provider value ={{isDark, setIsDark}}>
          <Page />
      </ThemeContext.Provider>
  )
}

const Page = () =>{
  
  return (
      <>
        <Header />
        <p>본문</p>
        <Footer />
      </>
  )
}

const Header = () =>{
  const {isDark} = useContext(ThemeContext)

  return(
          <>
            <header style = {{ backgroundColor: isDark? 'black' : 'lightgray' }} />
          </>
  )
}

const Footer = () =>{
  const {isDark, setIsDark} = useContext(ThemeContext)

  return (
          <>
            <footer style = {{ backgroundColor: isDark? 'black' : 'lightgray' }} />
            <button onclick= {onClickHandler} > 다크 모드 클릭 </button>
          </>
  )
}

```