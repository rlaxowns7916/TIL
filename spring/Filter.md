# Filter
- **스프링의 독자적인 기능이 아닌, Servlet에 정의된 기능**
- **필터 체인을 통해 연속적인 기능 제공 가능**
- **DispatcherServlet의 앞단에서 동작**
- **리소스 체크 및 변경을 수행 가능하다.**
- **일반적으로 인코딩, CORS, XSS, LOG, 인증, 권한 등 을 구현**

**스프링의 도움을 얻기 힘듬, 주로 넘어오는 리소스 그자체에 대한 판단 및 처리가 가능할 때 사용하는 듯**

## Filter 구현 및 등록

### 1. Filter 구현
```java
public class PracticeFilter implements Filter {
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
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

