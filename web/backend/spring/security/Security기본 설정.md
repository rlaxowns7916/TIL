# Security 기본 설정

## WebSecurityConfiguerAdapter

- 구 설정 (**Deprecated**)
    - WebSecurityFilterChain을 등록해서 사용해야한다.
- Security 기초 설정을 제공하는 클래스
    - 상속을 통해서 설정을 오버라이드한다.

## WebSecurityFilterChain

- Deprecated된 WebSecurityConfigurerAdapter를 대체
- @EnableWebSecurity가 필요하다.
    - 내부적으로 @Configuration을 갖고 있다.

```java

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /**
     * 인증
     */
    http
        .formLogin().disable()
        .httpBasic().disable()
        .cors().and()
        .csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);

    /**
     * 인가
     */
    http
        .authorizeRequests()
        .antMatchers("/home").permitAll()
        .antMatchers("/post").hasRole(USER)
        .anyRequest().authenticated();

    http

    return http.build();
  }
}
```