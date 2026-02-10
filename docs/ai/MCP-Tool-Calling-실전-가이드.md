# MCP Tool Calling 실전 가이드 (설계/운영 체크리스트)

> 목적: MCP(Server)가 LLM에게 **"도구 호출"(Tool Calling)**을 안전하고 예측 가능하게 제공하기 위한 설계 원칙과 운영 체크리스트를 정리한다.
> 
> 범위: MCP 개념 소개가 아니라, **툴 스키마/에러/보안/관측/테스트** 중심의 실전 문서

---

## 1. 큰 그림: Host/Client/Server + Tool Calling 데이터 흐름

MCP에서 Tool Calling은 단순히 "함수 하나"가 아니라, **(1) 도구 목록 노출 → (2) 모델의 선택/인자 구성 → (3) 서버 실행 → (4) 결과를 컨텍스트로 환류**의 루프를 만든다.

ASCII 시퀀스 다이어그램:

```
User
  |
  | prompt
  v
Host (IDE/Agent)
  |
  | request
  v
MCP Client (protocol handler)
  |
  | list_tools / call_tool
  v
MCP Server (tools/resources)
  |
  | side-effect / query
  v
External Systems (DB, API, Queue, FS)
```

핵심 관점:
- **모델은 신뢰할 수 없는 입력 생성기**다. (인자 조작/과호출/정보 유출 가능)
- 따라서 서버는 "좋은 개발자"를 가정하면 안 되고, **공격자 모델**을 기본으로 둬야 한다.

---

## 2. Tool 스키마 설계 원칙 (LLM 친화 + 운영 친화)

### 2.1 입력 스키마는 "엄격"하게, 출력은 "구조화"해라
- 입력(JSON Schema)은
  - 타입, enum, min/max, pattern, required를 적극 사용
  - 자유 텍스트 필드는 가능한 줄이기
- 출력은
  - 사람이 읽는 텍스트 + 기계가 파싱 가능한 필드(예: `status`, `data`, `warnings`)를 동시에 제공

권장 응답 형태(예시):

```json
{
  "status": "ok",
  "data": { "userId": 123, "tier": "gold" },
  "warnings": ["stale_cache"],
  "debug": { "requestId": "..." }
}
```

### 2.2 Idempotency를 기본값으로 만들기
LLM은 같은 호출을 반복할 수 있다(불확실성/재시도/롱컨텍스트 손실).
- 가능한 도구는 **멱등(idempotent)** 하게 만들기
- 부작용 도구는
  - `idempotencyKey` 입력을 요구하거나
  - `dryRun: true` 옵션을 제공하거나
  - "2단계 커밋" 형태(prepare/commit)로 분리

### 2.3 Pagination/Limit는 필수
데이터 조회 도구에 limit이 없으면
- 토큰 폭발
- DB 과부하
- 개인정보 과다 노출
로 직결된다.

최소 가드:
- `limit` 상한(예: 100)
- `cursor` 기반 페이지네이션

---

## 3. 에러 모델: 사람이 아니라 "모델"이 소비한다

도구 실패는 LLM이 **다음 행동을 결정**하는 근거가 된다.
따라서 에러는 "문장"보다 **분류 가능한 코드**가 중요하다.

권장 에러 응답(예시):

```json
{
  "status": "error",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "field 'date' must match YYYY-MM-DD",
    "retryable": false
  }
}
```

운영에서 자주 쓰는 코드 예시:
- `VALIDATION_ERROR` (입력 스키마 위반)
- `AUTH_REQUIRED` / `FORBIDDEN`
- `RATE_LIMITED` (재시도 가능)
- `UPSTREAM_TIMEOUT` (재시도 가능)
- `CONFLICT` (동시성 충돌)

---

## 4. 보안: Tool Calling의 기본 방어선

### 4.1 최소 권한(Least Privilege) + 명시적 allowlist
- 서버가 접근 가능한 리소스/명령/호스트를 allowlist로 제한
- 파일/네트워크/프로세스 실행은 특히 위험

### 4.2 비밀정보(Secrets) 취급
- LLM 입력/출력/로그에 토큰이 섞이면 회수 불가한 유출이 된다.
- 원칙:
  - 도구 인자로 토큰을 받지 말고, 서버 측 안전한 저장소(환경변수/secret manager)에서 읽기
  - 로그는 **민감정보 마스킹**

### 4.3 Prompt Injection / Tool Injection 대응
- 사용자 프롬프트에 "이 도구로 DB 덤프 떠" 같은 지시가 포함될 수 있다.
- 대응:
  - 도구에 **데이터 범위 제한** (기간, 테이블, 컬럼)
  - PII/credential 패턴 탐지 후 차단
  - "관리자 승인"이 필요한 도구 분리

---

## 5. 관측(Observability): 실패는 반드시 다시 온다

최소 관측 포인트:
- `tool_name`, `requestId`, `latency_ms`, `result_status`, `error_code`
- 호출량(QPS), 에러율, 타임아웃율
- 상위 트레이스 연동(가능하면 trace id)

ASCII: 관측 이벤트 흐름

```
call_tool
  -> validate
  -> execute
  -> emit metrics/logs/traces
  -> return structured result
```

---

## 6. 테스트 전략

### 6.1 계약(Contract) 테스트
- `list_tools` 결과가 변경되면 **호스트/프롬프트가 깨질 수 있음**
- 스키마 스냅샷 테스트 권장

### 6.2 "모델이 이상하게 호출한다" 테스트
LLM이 실제로 만드는 흔한 이상 케이스:
- 타입 오류: 숫자를 문자열로
- 필드 누락/오타
- 한 번에 너무 많은 요청을 병렬로

따라서 tool 서버는 fuzz 성격의 테스트가 ROI가 높다.

---

## 7. 체크리스트 (요약)

- [ ] 입력 스키마에 upper bound(limit, size) 존재
- [ ] 멱등성 또는 idempotencyKey 제공
- [ ] 에러는 code/retryable 포함
- [ ] allowlist 기반 접근 제어
- [ ] secret은 인자로 받지 않음
- [ ] PII/대량추출 방지 가드
- [ ] metrics/logs에 tool_name/latency/status 기록
- [ ] list_tools 스키마 스냅샷 테스트

---

## 참고
- MCP 개요/구조: `docs/ai/mcp-architecture.md`
