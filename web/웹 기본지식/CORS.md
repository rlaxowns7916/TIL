# CORS (Cross-Origin Resource Sharing)

CORS는 브라우저의 **Same-Origin Policy(SOP)** 제약 하에서, 서버가 “이 출처(Origin)의 요청을 허용한다”는 신호를 **HTTP 응답 헤더**로 명시하여 교차 출처 요청을 가능하게 하는 메커니즘이다.

> 핵심: CORS는 “서버 보안 기능”이라기보다, **브라우저가 강제하는 정책**을 서버가 헤더로 만족시키는 방식이다.

---

## 1) Origin이란?

Origin은 다음 3요소가 모두 같을 때 동일(origin)으로 취급된다.

- scheme (프로토콜): `http` / `https`
- host (도메인)
- port

예)
- `https://api.example.com:443` 와 `https://api.example.com` → (대부분 동일로 간주되지만, 표기상 포트 개념이 포함됨)
- `https://example.com` 와 `https://www.example.com` → **다른 Origin**
- `https://example.com` 와 `http://example.com` → **다른 Origin**

---

## 2) 브라우저에서 CORS가 문제가 되는 지점

- 서버는 요청을 받아 처리하고 응답을 내려줄 수 있다.
- 하지만 브라우저는 응답을 JS 코드에 노출하기 전에 CORS 검사를 수행한다.

```
[Browser JS] --(요청)--> [Server]
     ^                     |
     |                     v
     +----(응답 수신)--- [Browser CORS 검사]
                 |
                 +-- 허용 헤더가 없으면: JS에서 응답 접근 차단
```

즉, 네트워크 탭에 “응답이 온 것처럼 보여도”, JS에서 `fetch`가 실패하는 형태로 체감될 수 있다.

---

## 3) CORS의 3가지 대표 시나리오

### 3.1 Simple Request (프리플라이트 없이 바로 전송)

브라우저가 “안전하다고 판단하는” 제한된 조건에서만 프리플라이트 없이 요청을 보낸다.

주요 조건(요약)
- Method: `GET`, `HEAD`, `POST`
- 허용되는 헤더만 사용(대표적으로 `Accept`, `Accept-Language`, `Content-Language`, `Content-Type` 등)
- `Content-Type`이 다음 중 하나
  - `application/x-www-form-urlencoded`
  - `multipart/form-data`
  - `text/plain`

> `Authorization` 헤더를 붙이거나 `application/json`을 POST로 보내는 순간, 대개 Preflight로 전환된다.

### 3.2 Preflight Request (OPTIONS 예비 요청)

브라우저가 본 요청 전에 서버에 “이 요청을 보내도 되나?”를 묻는 단계.

- 예비 요청: `OPTIONS /resource`
- 핵심 요청 헤더
  - `Origin: https://app.example.com`
  - `Access-Control-Request-Method: POST`
  - `Access-Control-Request-Headers: authorization, content-type`

서버는 예비 요청에 대해 다음을 응답해야 한다.
- `Access-Control-Allow-Origin`
- `Access-Control-Allow-Methods`
- `Access-Control-Allow-Headers`
- (선택) `Access-Control-Max-Age`

```
(1) OPTIONS /api/orders
    Origin: https://app.example.com
    Access-Control-Request-Method: POST
    Access-Control-Request-Headers: authorization, content-type

(2) 204 No Content
    Access-Control-Allow-Origin: https://app.example.com
    Access-Control-Allow-Methods: GET,POST,PUT,DELETE
    Access-Control-Allow-Headers: authorization, content-type
    Access-Control-Max-Age: 3600

(3) POST /api/orders  (본 요청)
    Origin: https://app.example.com
    Authorization: Bearer ...
```

### 3.3 Credentialed Request (쿠키/인증정보 포함 요청)

브라우저에서 쿠키를 포함하거나 인증정보를 포함하는 요청.

- `fetch(url, { credentials: "include" })`
- axios: `withCredentials: true`

이 경우 중요한 제약
- `Access-Control-Allow-Origin: *` 는 **사용 불가**
  - 반드시 **구체적인 Origin**을 명시해야 한다.
- 서버는 반드시
  - `Access-Control-Allow-Credentials: true`

추가로 쿠키를 사용할 때는 `SameSite`, `Secure`, `Domain` 설정이 실제 전송 여부를 좌우한다.

---

## 4) 서버가 주로 설정해야 하는 CORS 응답 헤더

- `Access-Control-Allow-Origin`
  - 허용할 Origin
- `Access-Control-Allow-Methods`
  - 허용할 HTTP 메서드
- `Access-Control-Allow-Headers`
  - 클라이언트가 사용할 수 있는 요청 헤더(특히 preflight에서 중요)
- `Access-Control-Allow-Credentials`
  - 쿠키/인증정보 포함 허용 여부
- `Access-Control-Expose-Headers`
  - 브라우저가 JS에 노출해도 되는 응답 헤더(기본적으로 일부 헤더만 노출)
- `Access-Control-Max-Age`
  - preflight 응답 캐시 시간(초)
- `Vary: Origin`
  - 프록시/캐시가 Origin별로 응답을 분리 캐시하도록 유도(캐시 오염 방지)

---

## 5) 흔한 장애 패턴

- 프리플라이트(OPTIONS)가 401/403/404로 막힘
  - 보안 필터/라우팅/리버스 프록시에서 OPTIONS를 처리 못하는 경우
- `Access-Control-Allow-Origin`을 `*`로 두고 `credentials: include`를 사용
  - 브라우저가 정책 위반으로 차단
- `Access-Control-Allow-Headers`에 `authorization`을 누락
  - Bearer 토큰을 쓰면 거의 필수
- 캐시/프록시 환경에서 Origin별 응답이 섞임
  - `Vary: Origin` 미설정

---

## 6) Spring Boot에서의 설정 예시

### 6.1 Spring MVC (Servlet)에서 전역 CORS 정책

```java
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // credentials를 허용한다면 allowedOrigins에 "*" 사용 불가
    config.setAllowedOrigins(List.of("https://app.example.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
```

> 주의: Spring Security를 사용한다면, Security 설정에서 `.cors(...)`를 활성화해야 이 정책이 반영된다.

---

## 7) 빠른 점검 체크리스트

- [ ] 클라이언트의 Origin(스킴/호스트/포트)을 정확히 파악했다
- [ ] 프리플라이트(OPTIONS)가 서버/프록시/보안필터에서 막히지 않는다
- [ ] `Authorization` 등 커스텀 헤더를 쓴다면 `Access-Control-Allow-Headers`에 포함했다
- [ ] 쿠키/세션을 쓴다면 `Allow-Credentials: true` + `Allow-Origin`은 구체값
- [ ] 캐시/프록시가 있다면 `Vary: Origin`을 고려했다
