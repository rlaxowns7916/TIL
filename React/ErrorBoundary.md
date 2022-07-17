# ErrorBoundary
- React에서 에러를 처리하는 방법
  - React 16부터 도입되었다.
  - Error가 발생하면, 로깅을 하거나 다른 UI 페이지를 보여준다.
- 함수형 컴포넌트가 아닌 **클래스형 컴포넌트** 를 사용한다.
- Child에서 발생한 에러를 Catch한다.
  - ErrorBoundary에서 발생한 에러는 Catch하지 못한다.
  - 비동기적 코드는 Catch하지 못한다. (setTimeOut 등 )
  - EventHandler 내부의 에러는 포착하지 못한다.
    - ```jsx
          const handleClick = () => {
          try {
              // 에러를 던질 수 있는 무언가를 해야합니다.
          } catch (error) {
              this.setState({ error });
          }
      }
      ```
 
## Sample
```jsx
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {error : null}
  }

  /**
   * Fallback UI를 그리는데 필요한 함수이다. 
   */
  static getDerivedStateFromError(error) {
    return { error }
  }
  /**
    * 에러 로깅을 할 때 사용한다.
    */
  componentDidCatch(error, errorInfo) {
    logErrorToMyService(error, errorInfo);
  }
  
  render(){
    if(this.state.error){
      return <h1> Oops... there is Something Wrong! </h1>
    }
    return this.props.children;
  }
}
