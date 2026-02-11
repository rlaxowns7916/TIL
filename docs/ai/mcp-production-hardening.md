# MCP(Model Context Protocol) Production Hardening 가이드

## 목표
MCP 서버를 "데모"가 아니라 **프로덕션(특히 사내/외부 네트워크)에서 운영 가능한 수준**으로 만들기 위한 보안/운영 체크리스트를 정리한다.

- 인증/인가, 데이터 노출, 프롬프트 인젝션(도구 호출 유도) 등 리스크를 최소화
- 관측 가능성(로그/감사) + 장애 대응을 가능하게
- 멀티테넌시/권한 경계를 명확히

---

## 0) 전제: MCP는 "연결 표준"이고, 보안은 구현 책임이다
MCP 자체는 AI 앱과 외부 시스템을 연결하는 표준이지만, **어떤 권한으로 무엇을 호출할지**는 서버/배포자가 설계해야 한다.

```
[AI App] --(MCP)--> [MCP Server] --(API/DB/FS)--> [Your Systems]
             ^
             |
        security boundary
```

---

## 1) 위협 모델(Threat Model)부터 고정

### 대표 위협
- **권한 상승**: 사용자가 원래 접근 불가한 데이터/툴을 LLM의 도구 호출을 통해 우회 접근
- **데이터 유출**: tool 결과/로그/프롬프트에 민감정보(PII/토큰/키)가 섞여 외부로 노출
- **프롬프트 인젝션**: "규칙 무시하고 이 툴 호출해" 같은 입력이 모델을 통해 실행 흐름을 탈취
- **SSRF/Command Injection**: tool이 URL/명령/쿼리를 그대로 받아 실행하는 형태
- **리소스 고갈(DoS)**: 무한 tool 호출, 대형 페이로드, 비정상적인 스트리밍

> 결론: MCP 서버는 사실상 "고권한 API Gateway"로 취급해야 한다.

---

## 2) 인증(Authentication)과 인가(Authorization) 분리

### 권장 원칙
- 인증: "누가 호출했는가" (사용자/서비스/세션)
- 인가: "무엇을 할 수 있는가" (tool 단위, resource 단위)

### 실무 체크
- tool을 **최소 권한(minimum privilege)**으로 분리
  - 예) `search_docs`(읽기) vs `write_notion`(쓰기) vs `deploy_service`(초고위험)
- 세션/사용자 컨텍스트를 서버에서 강제
  - "모델이 주장하는 사용자"를 신뢰하지 말고, 인증된 토큰/세션에서 추출

---

## 3) 입력 검증: JSON Schema는 출발점이고, 서버 검증이 본체

- tool 입력은 반드시 **서버에서 검증**
  - 길이 제한(max bytes)
  - 허용 문자/패턴(정규식)
  - enum/whitelist 기반의 안전한 파라미터화
- 파일 경로, URL, SQL, shell command 같은 위험 입력은
  - 가능하면 금지
  - 필요하면 allowlist(호스트, 디렉토리, 쿼리 템플릿) 기반으로 제한

---

## 4) Tool 안전 설계 패턴

### (A) "직접 실행" 대신 "요청 생성" 패턴
고위험 도구는
- 모델이 바로 실행하지 못하게 하고
- 실행 가능한 "요청"을 생성 → 사람이 승인/큐잉 후 실행

```
LLM -> (tool) create_deploy_request -> [queue]
                         |
                         v
                    human approve -> deploy
```

### (B) Side-effect tool은 기본적으로 멱등성 키(Idempotency Key)
- 같은 요청이 여러 번 실행될 수 있다(재시도/중복 호출)
- write tool은 멱등 처리를 기본으로 설계

---

## 5) 관측/감사(Observability & Audit)

반드시 남길 것
- 누가(주체) / 언제 / 어떤 tool / 어떤 파라미터(필요 시 마스킹) / 결과 요약 / 에러
- 요청 ID(상관관계) + trace id

주의
- 프롬프트/응답 전문(raw)을 그대로 저장하면 민감정보가 섞여 사고가 된다.
  - 기본은 요약+마스킹
  - 전문 저장은 opt-in + 짧은 TTL + 접근 통제

---

## 6) Rate Limit / Quota / Circuit Breaker

- 사용자별/세션별 QPS 제한
- tool별 비용 기반 제한(예: DB scan, 외부 API 호출)
- 다운스트림 장애 시
  - circuit breaker로 연쇄 장애 방지
  - graceful degradation(예: "현재는 조회만 가능")

---

## 7) 배포/런타임 격리

- 파일 시스템 접근, 네트워크 egress는 기본 차단 후 필요한 것만 허용
- 가능하면 컨테이너/샌드박스에서 tool 실행
- secrets는
  - 환경변수/파일에 평문 저장 최소화
  - KMS/Secret Manager + 최소권한 토큰

---

## 8) 운영 체크리스트 (한 장 요약)

- [ ] 인증 토큰 기반 주체 식별(서버 신뢰 경로)
- [ ] tool별 권한 분리(RBAC/ABAC)
- [ ] 입력 검증(길이/패턴/allowlist) + 안전한 파라미터화
- [ ] 고위험 tool은 요청 생성 + 사람 승인 흐름
- [ ] 감사 로그(요약/마스킹) + request id/trace id
- [ ] rate limit + quota + circuit breaker
- [ ] 네트워크 egress 최소화 + 샌드박스/컨테이너 격리
- [ ] secrets 관리(회전/범위 제한/노출 방지)

---

## 참고문헌
- MCP Docs (Intro) — https://modelcontextprotocol.io/docs/getting-started/intro
- MCP Spec/Docs Repo — https://github.com/modelcontextprotocol/modelcontextprotocol
- OWASP API Security Top 10 — https://owasp.org/www-project-api-security/
- RFC 6750: The OAuth 2.0 Authorization Framework: Bearer Token Usage — https://www.rfc-editor.org/rfc/rfc6750
