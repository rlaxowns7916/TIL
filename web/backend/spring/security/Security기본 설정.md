# Spring Security 기본 설정 (Spring Boot 3 / Spring Security 6 기준)

이 문서는 **Spring Security의 “기본 골격”**(필터 체인, 인증/인가, 세션/CSRF/CORS, 예외 처리)을 빠르게 세팅하고, 운영에서 자주 겪는 함정을 피하기 위한 실전 체크리스트를 정리한다.

---

## 1) 배경: WebSecurityConfigurerAdapter는 왜 사라졌나?

- `WebSecurityConfigurerAdapter`는 **Spring Security 5.7+에서 Deprecated** 되었고, 현재는 **구성 요소(Bean) 조합 방식**이 표준이다.
- 핵심 전환점
  - `configure(HttpSecurity)` 오버라이드 → **`SecurityFilterChain` 빈 등록**
  - `antMatchers(...)` → **`requestMatchers(...)`** (Security 6)

> 결론: 이제는 “상속”이 아니라 “구성(Composition)”으로 보안 설정을 만든다.

---

## 2) Spring Security가 요청을 처리하는 큰 흐름

```
[Client]
   |
   v
[Servlet Container]
   |
   v
[DelegatingFilterProxy]
   |
   v
[SecurityFilterChain]
   |
   +--> (인증 관련 필터들)  예: BearerTokenAuthenticationFilter / UsernamePasswordAuthenticationFilter
   |
   +--> (인가/예외 처리)    예: AuthorizationFilter / ExceptionTranslationFilter
   |
   v
[Controller]
```

- **인증(Authentication)**: “누구인가?” (Principal 생성)
- **인가(Authorization)**: “이 요청을 허용할 것인가?” (권한/역할 확인)

---

## 3) 최소 동작 예제: Stateless API (JWT/Bearer 토큰 가정)

아래 예제는 **세션을 쓰지 않는 API 서버**(모바일/SPA 백엔드)에서 가장 흔한 기본형이다.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize 등을 사용할 때
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1) 기본 인증 방식 비활성화 (API 서버에서 흔히 사용)
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())

        // 2) CORS: "허용"이 아니라 "정책"을 명시해야 한다 (별도 섹션 참고)
        .cors(Customizer.withDefaults())

        // 3) CSRF: 브라우저 쿠키 기반 세션 인증을 쓰지 않는다면 보통 disable
        //    (단, Cookie 기반 인증을 쓴다면 disable 하면 안 됨)
        .csrf(csrf -> csrf.disable())

        // 4) 세션을 만들지 않음
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 5) 인가 규칙
        .authorizeHttpRequests(auth -> auth
            // 프리플라이트(OPTIONS)는 CORS에서 매우 자주 막히므로 먼저 열어두는 편이 안전
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 헬스체크/정적 리소스/공개 API
            .requestMatchers(
                new AntPathRequestMatcher("/actuator/health"),
                new AntPathRequestMatcher("/docs/**"),
                new AntPathRequestMatcher("/public/**")
            ).permitAll()

            // 나머지는 인증 필요
            .anyRequest().authenticated()
        );

    // 6) JWT 필터를 직접 추가하는 방식이라면, 적절한 위치에 addFilterBefore/After
    //    예) http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
```

### 자주 하는 실수

- `OPTIONS /**`를 열지 않아 **프리플라이트에서 401/403**이 나는 경우
- `cors()`를 호출했는데, 실제로는 `CorsConfigurationSource`가 없어서 정책이 비어 있는 경우
- Security 6 환경에서 `antMatchers()`를 그대로 쓰다가 컴파일/런타임 오류가 나는 경우

---

## 4) 인증과 인가를 어디까지 "프레임워크"에 맡길 것인가?

### (A) Spring Security가 제공하는 표준 방식 활용

- OAuth2 Resource Server (JWT)
  - `spring-boot-starter-oauth2-resource-server` 기반
  - 표준 필터/검증 로직을 사용하므로 운영 안정성이 높음
- 장점
  - 토큰 파싱/검증/예외 응답이 표준화
  - 키 로테이션(JWK), 만료, audience/issuer 검증 등을 체계적으로 적용 가능

### (B) 커스텀 JWT 필터를 직접 붙이는 방식

- 학습 목적/레거시 호환에선 가능하지만, 운영에서는 “표준 방식을 우선” 고려하는 편이 좋다.
- 직접 구현 시 체크
  - 만료/서명 검증 누락
  - clock skew 허용 범위
  - 예외 응답 형식(401/403) 일관성
  - 보안 로그(민감정보 마스킹)

---

## 5) PasswordEncoder (세션 로그인/폼 로그인 사용하는 경우)

폼 로그인/세션 기반 인증을 한다면, 비밀번호는 반드시 안전한 해시를 사용해야 한다.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Bean
public PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
}
```

- 저장: `passwordEncoder.encode(rawPassword)`
- 검증: `passwordEncoder.matches(rawPassword, encodedPassword)`

---

## 6) 예외 처리(권장): 401 vs 403를 명확히

- 401 Unauthorized: **인증 정보가 없거나 유효하지 않음**
- 403 Forbidden: **인증은 되었지만 권한이 부족**

API 서버라면 HTML 리다이렉트가 아니라 JSON 응답을 내려주는 것이 일반적이다.

(개념)
- `AuthenticationEntryPoint` → 401
- `AccessDeniedHandler` → 403

---

## 7) 운영 디버깅 팁

- Security 로그 레벨을 올려서 “어느 필터에서 막히는지”를 먼저 확인
  - `org.springframework.security` 패키지 DEBUG
- 증상별 빠른 분류
  - 프리플라이트에서 막힘: `OPTIONS` 허용 + CORS 정책 점검
  - 401: 토큰/세션/인증 필터 쪽 문제
  - 403: 인가 규칙(roles/authorities) 또는 method security 문제

---

## 8) 체크리스트

- [ ] Spring Security 6 API에 맞게 `authorizeHttpRequests + requestMatchers` 사용
- [ ] CORS 정책을 “코드/설정으로 명시”했고, 프리플라이트(OPTIONS)도 고려했다
- [ ] Stateless API라면 `SessionCreationPolicy.STATELESS`로 세션 비활성화
- [ ] 쿠키 기반 인증을 쓴다면 CSRF 정책을 다시 검토했다(무작정 disable 금지)
- [ ] 401/403 응답 형식을 일관되게 정의했다
