# Interceptor

- **Controller로 들어오는 HttpRequest와 응답하는 HttpResponse를 가로채는 역할**
- **Filter < -- > Servlet < -- > Interceptor < -- > Aop < -- > Controller**
- **Servlet 단위로 실행된다.**
- **ServletRequest와 ServletResponse를 가로채서 추가 작업을 할 수 있다.**
  - Request와 Reponse를 조작 할 수 없다.
    - 내부 상태의 변경이 아닌, 새로운 객체로의 변경이 불가능 하다는 것이다.
- Spring Container의 도움 (Bean 등)을 받을 수 있기 때문에, 더욱 세분화된 기능을 수행 할 수 있다.

## Interceptor 사용법
### 1. HandlerInterceptor 인터페이스 구현
```java
@Component
public class PracticeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

```
- preHandle: Controller 호출 이전에 작동 (false면 작업 중지, true면 다음 단계 실행)
- postHandle: Controller 호출 이후에 작동
- afterCompletion: 전체요청이 끝난 후 

**정확히는 Controller가 아닌 HandlerAdapter가 Handler를 호출 하는 시점**

### 2. WebMvcConfigurer를 구현하는 상속클래스에 인터셉터 추가
```java
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final PracticeInterceptor practiceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(practiceInterceptor)
                .addPathPatterns()
                .excludePathPatterns()
                .order(Ordered.LOWEST_PRECEDENCE);
    }

}
```
**어떤 URL Pattern을 인터셉트 할 지, 제외할지, 인터셉터끼리의 순서 등을 조절가능**


## 어디에 사용되는가
- Controller에 사용된다.
- 왜 AOP는 적합하지 않은가?
  - Controller의 Parameter는 일정하지 않다. (PointCut지정의 어려움)
  - HttpServletRequest / HttpServletResponse를 Interceptor에서는 객체로 받아 올 수 있다. (Controller에 최적화 되어 있다.)
