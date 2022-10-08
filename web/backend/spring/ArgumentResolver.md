# ArgumentResolver
- Controller로 요청이 들어왔을 때, 원하는 값을 객체에 매핑 할 수 있다.
- HandlerArgumentResolver를 구현하면된다.
## 코드
```kotlin

@Target(AnnotationTarget.VALUE_PARAMETER,AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authenticated()

data class AuthInfo(
    val id: Long,
    val userName: String
)

@Component
class AuthInfoArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(Authenticated::class.java) != null &&
                parameter.parameterType == AuthInfo::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        val authentication = SecurityContextHolder.getContext().authentication;
        return authentication.principal as AuthInfo
    }
}
```

## 과정
1. 요청이 들어온다.
2. Dispatcher Servlet이 HandlerMapping을 통해 요청을 처리할 ControlLer를 찾는다.
3. Handler Executor Chain의 Interceptor들이 동작한다.
4. Dispatcher Servlet이 Controller를 처리할 수 있는 HandlerAdpater를 호출한다.
5. Argument Resolver가 처리된다.
6. Message Converter가 처리된다.