# Interceptor

- **Controller로 들어오는 HttpRequest와 응답하는 HttpResponse를 가로채는 역할**
- **Filter < -- > Servlet < -- > Interceptor < -- > Aop < -- > Controller**
- **Request와 Response를 가로채서 추가 작업을 할 수 있다.**


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
- preHandle: Controller 호출 이전에 작동 (false면 Controller로 가지않고, true면 Controller 실행)
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
