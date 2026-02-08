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

## MCP vs OpenClaw Skills
MCP와 OpenClaw Skill은 모두 AI의 기능을 확장하지만, 동작 계층과 역할이 다릅니다.

- **OpenClaw Skills (Client-side)**:
  - Agent의 "두뇌"이자 "손발" 역할을 하는 **클라이언트 측 로직**입니다.
  - LLM이 어떤 도구를 언제 사용할지 결정하는 정책(Policy)과 워크플로우를 정의합니다.
  - 예: `git` skill (git 명령어 조합 로직), `browser` skill (브라우저 제어 로직).

- **MCP (Server-side)**:
  - 데이터와 도구를 표준화된 방식으로 제공하는 **서버 측 프로토콜**입니다.
  - 특정 Agent 구현체에 종속되지 않으며, 데이터 소스(DB, API)를 추상화합니다.
  - **Skill은 MCP를 사용할 수 있습니다**: 예를 들어 `mcp-integration` skill은 MCP 서버에 접속하여 그 서버가 제공하는 도구를 동적으로 로드하고 실행할 수 있습니다.
