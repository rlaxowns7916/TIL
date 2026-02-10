# Model Context Protocol (MCP) 아키텍처 및 활용

## 1. MCP란 무엇인가? (Definition)
**Model Context Protocol (MCP)**는 대규모 언어 모델(LLM)을 외부 데이터 및 도구와 연결하기 위한 개방형 표준 프로토콜입니다.
기존의 파편화된 통합 방식(모델별 커스텀 커넥터 등)을 대체하여, **한 번의 구축으로 모든 모델에서 활용 가능(Write Once, Run Anywhere)**한 생태계를 지향합니다.

### 핵심 기능
- **Context Providing**: LLM에게 파일, 데이터베이스, 로그 등의 실시간 정보를 제공.
- **Tooling**: LLM이 실행할 수 있는 함수(Tools)를 노출하여 외부 시스템 제어 가능.
- **Prompts**: 사전에 정의된 템플릿을 통해 정형화된 상호작용 지원.

## 2. 아키텍처 (Architecture)
MCP는 **Host**, **Client**, **Server**의 3계층 구조로 동작합니다.

### 2.1 Host (호스트)
- **역할**: MCP 기능을 활용하는 최상위 애플리케이션 (예: Claude Desktop, IDE, AI Agent).
- **기능**: 사용자의 입력을 받아 Client를 통해 Server와 통신하고, LLM의 응답을 사용자에게 전달.

### 2.2 Client (클라이언트)
- **역할**: Host와 Server 간의 중계자 (Protocol Handler).
- **기능**: Server와의 연결(1:1 또는 1:N)을 관리하며, 권한 부여 및 요청/응답 라우팅 담당.

### 2.3 Server (서버)
- **역할**: 실제 데이터 소스나 도구를 캡슐화한 서비스.
- **기능**:
  - **Resources**: 텍스트나 바이너리 데이터를 읽을 수 있는 URI 노출.
  - **Tools**: 실행 가능한 함수(API 호출, CLI 실행 등) 제공.
  - **Prompts**: 미리 정의된 프롬프트 템플릿 제공.

## 3. 커스텀 서버 구현 계획 (Implementation Plan)
자체적인 MCP Server를 구축하여 사내 데이터베이스나 내부 API를 LLM에 연동하기 위한 구조적 계획입니다.

### 3.1 기술 스택 선정
- **언어**: TypeScript (Node.js) 또는 Python (공식 SDK 지원).
- **통신 방식**: Stdio (로컬 실행 시 간편함) 또는 SSE (Server-Sent Events, 원격 연결 시).

### 3.2 구현 단계 (Roadmap)
1.  **초기 설정 (Setup)**
    - MCP SDK 설치 및 기본 서버 인스턴스 초기화.
    - Capability 정의 (Resources, Tools 중 제공할 기능 선언).
2.  **리소스 정의 (Define Resources)**
    - `list_resources`: 접근 가능한 데이터 목록 반환.
    - `read_resource`: 특정 URI(`custom://...`) 요청 시 실제 데이터 반환 로직 구현.
3.  **도구 구현 (Implement Tools)**
    - `list_tools`: 실행 가능한 작업 명세(Schema) 정의.
    - `call_tool`: 실제 비즈니스 로직(DB 쿼리, API 호출) 매핑.
4.  **테스트 및 배포 (Deploy)**
    - `mcp-inspector`를 활용한 디버깅.
    - Claude Desktop 등의 Host 설정 파일(`claude_desktop_config.json`)에 서버 등록.

## 4. 결론
MCP는 LLM 어플리케이션의 확장성을 극대화하는 표준 계층입니다. 커스텀 서버 구현을 통해 보안이 유지된 상태로 내부 지식을 AI와 결합할 수 있습니다.

## MCP vs Skills
MCP와 Skill은 AI의 능력을 확장한다는 점에서 유사해 보이지만, 핵심적인 역할과 접근 방식이 다릅니다.

- **Skills (Guidance)**
  - **"지침 + 맥락(Context)"**을 제공합니다.
  - 작업의 방향과 정책이 문서(`SKILL.md`)에 사전 정의되어 있어, 에이전트가 일관된 방식으로 도구를 사용하도록 유도합니다.

- **MCP (Atomic Capability)**
  - **"원자적인 명령어들의 집합(Spec/OpenAPI)"**입니다.
  - 작업의 방향이 고정되어 있지 않으며, AI가 상황에 맞춰 필요한 도구를 **동적으로 선택**하여 실행합니다.

- **Analogy**
  - Tool 관점에서 **MCP는 CLI(Command Line Interface)**와 유사합니다. (실행 가능한 기능 그 자체)
  - 반면, Skill은 그 기능을 효과적으로 사용하기 위한 **숙련된 작업자의 매뉴얼**에 가깝습니다.

## 5. Tool Calling 실전 체크리스트(요약)

> Tool Calling은 ‘편의 기능’이 아니라 **신뢰할 수 없는 입력(LLM)**이 외부 시스템을 건드리는 인터페이스다. 따라서 서버는 기본적으로 **공격자 모델**을 전제로 설계한다.

### 5.1 데이터 흐름(개념)
```
User -> Host -> MCP Client -> MCP Server(tools/resources) -> External Systems
```

### 5.2 스키마/출력
- 입력(JSON Schema)은 **엄격하게**: type/enum/range/pattern/required 적극 사용
- 조회형 도구는 **limit/cursor** 필수(토큰 폭발/과다 노출 방지)
- 출력은 가능하면 **구조화(status/data/warnings/error)**

### 5.3 멱등성(Idempotency)
- LLM은 같은 호출을 반복할 수 있으므로(재시도/불확실성) 가능한 도구는 멱등하게
- 부작용 도구는 `idempotencyKey` 또는 `dryRun` 또는 prepare/commit(2단계) 고려

### 5.4 에러 모델
- 에러는 문장보다 **code + retryable**이 중요(LLM이 다음 행동을 결정)
- 예: VALIDATION_ERROR, RATE_LIMITED, UPSTREAM_TIMEOUT, CONFLICT

### 5.5 보안
- 최소 권한 + allowlist(특히 파일/네트워크/프로세스 실행)
- secret은 인자로 받지 말고 서버의 안전 저장소에서 읽기(로그 마스킹 포함)
- 대량 추출/PII 유출 방지 가드(기간/범위 제한 등)

### 5.6 관측/테스트
- 로그/메트릭: tool_name, requestId, latency, result_status, error_code
- 계약(Contract) 테스트: list_tools 스키마 스냅샷
- LLM 이상 호출 케이스(필드 누락/타입 오류/오타/과호출) 방어 테스트
