# Filter
- **CrossCuttingConcern을 해결하기 위한 방법이다.**
- **스프링의 독자적인 기능이 아닌, Servlet에 정의된 기능**
- **필터 체인을 통해 연속적인 기능 제공 가능**
  - 체인의 마지막에는 클라이언트가 실제 요청한 자원이 위치한다.
- **DispatcherServlet의 앞단에서 동작**
- **리소스 체크 및 변경을 수행 가능하다.**
  - Request/Response의 변경이 가능하다.
    - 내부 상태변경, 다른 객체로의 변경 모두 가능하다.
- **일반적으로 인코딩, CORS, XSS, LOG, 인증, 권한 등 을 구현**
  - 어플리케이션에 전반적으로 수행 될 수 있는 일반화된 기능에 사용하는 것이 좋다.
- **ServletRequest / ServletResponse에 대한 조작이 가능하다.**
- **Servlet 단위로 실행된다.**

## Filter 구현 및 등록

### 1. Filter 구현
```java
/**
 * 여기서 @Component를 지정하면
 * 모든 URL에 지정된다.
 */
public class PracticeFilter implements Filter {
	@Override
	public void destroy() {
		
	}

    /**
     * doFilter 메소드로 다음 필터에 Request,Response를 넘겨준다.
     * ServletRequest와 ServletResponse를 직접 넘겨주기 떄문에 조작이 가능하다.
     */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request,response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	
  }
  
}
```
- init(): 필터 인스턴스 초기화
- doFilter(): 전/후 처리
- destory(): 필터 인스터스 소멸

### 2. Filter 등록

#### 1. FilterRegistrationBean
***Filter 생성 및 FilterRegistrationBean을 통해서 SpringBean으로 등록***
```java
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  /**
   *  Filter는 Servlet의 스펙이기 때문에
   *  @Order를 통한 순서 지정은 동작하지 않는다.
   */
  @Bean
    public FilterRegistrationBean practiceFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new PracticeFilter());
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE); //Filter 끼리의 순서 지정
        filterRegistrationBean.addUrlPatterns("/user/*"); //urlPattern을 받는다.
        filterRegistrationBean.setUrlPatterns(Collections.singleton("/user/*")); //List를 받는다.
        return filterRegistrationBean;
    }
}
```
#### 2. \@WebFilter
***Filter 생성, \@Configuration 클래스에 \@ServletComponentScan 추가***
```java
@ServletComponentScan
@SpringBootApplication
public class ExampleApplication {)
	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}
}
```
```java
@WebFilter(urlPatterns = "/user/*")
public class PracticeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
```
- ***\@Component와 \@WebFilter+@ServletComponentScan시, 동일 Filter가 2번 적용된다. (@Component + ComponentScan)***
- ***\@Component 추가시 Filter가 추가되지만 URLPattern이 먹지 않는다.***
- ***SpringBean을 통해서 사용할 일이 있으면, \@WebFilter 사용하지 말자***

### 3. Filter에는 SpringBean을 사용 할 수 없다?
- 스펙상으로는 Spring Container의 기능이 아닌 Servlet의 기능
- DelegatingFilterProxy를 사용하여 SpringBean을 주입받을 수 있다.

#### DelegatingFilterProxy
- ServletContainerFilter 이다.
- Servlet Container와 Spring Container(Web.xml, Applicaiton Context) 사이의 Link를 담당한다.
1. Filter 구현체를 SpringBean으로 등록한다.
2. 구현체를 갖는 Proxy(Delegating FilterProxy)를 Filter에 등록한다.
3. 실제 요청이 올 때, 구현체에게 요청을 위임한다.