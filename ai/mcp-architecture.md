# Model Context Protocol (MCP) Architecture & Usage

## 1. 개요 (Overview)

**Model Context Protocol (MCP)**는 LLM(Large Language Model) 애플리케이션이 외부 데이터 및 도구와 상호작용하는 방식을 표준화하기 위한 오픈 프로토콜입니다.

기존에는 각 LLM 애플리케이션(ChatGPT, Claude Desktop 등)이 데이터 소스(Google Drive, Slack, Local Files 등)에 연결하기 위해 개별적인 연동 코드를 작성해야 했습니다 (M x N 문제). MCP는 이를 **클라이언트-호스트-서버** 아키텍처로 추상화하여, 한 번의 서버 구현으로 다양한 LLM 클라이언트에서 데이터를 사용할 수 있게 합니다.

## 2. 핵심 아키텍처 (Core Architecture)

MCP는 **JSON-RPC 2.0** 기반의 통신 프로토콜을 사용하며, 크게 세 가지 구성 요소로 나뉩니다.

```mermaid
graph LR
    User[User / AI Model] <--> Host[MCP Host (Client)]
    Host <--> Server[MCP Server]
    Server <--> Data[Data Source / Tool]
```

### 2.1. MCP Host (Client)
- **역할:** LLM을 구동하고 사용자와 상호작용하는 애플리케이션 (예: Claude Desktop, IDE, AI Agent).
- **기능:**
  - MCP 서버를 검색 및 연결.
  - 사용자의 프롬프트에 따라 적절한 도구(Tool)나 리소스(Resource) 호출 권한을 관리.
  - LLM의 생성 결과와 도구 실행 결과를 중재.

### 2.2. MCP Server
- **역할:** 실제 데이터 소스나 기능을 노출하는 경량 서버.
- **기능:** 
  - **Resources:** 파일, DB 레코드 등 읽기 전용 데이터 노출 (`uri` 기반 접근).
  - **Prompts:** 미리 정의된 프롬프트 템플릿 제공.
  - **Tools:** 실행 가능한 함수나 API 호출 (예: `execute_sql`, `fetch_weather`).

### 2.3. Transport Layer
- 현재 주로 **Stdio** (표준 입출력)를 사용하여 로컬 프로세스 간 통신을 수행.
- 원격 통신을 위한 **SSE (Server-Sent Events)** 지원.

---

## 3. 주요 기능 (Capabilities)

### 3.1. Resources (데이터 읽기)
LLM이 컨텍스트로 사용할 수 있는 데이터를 제공합니다.
- **Resource List:** 서버가 제공하는 리소스 목록 조회.
- **Resource Read:** 특정 URI(`file:///logs/error.log`)의 콘텐츠 읽기.
- **Subscriptions:** 리소스 변경 시 실시간 알림 구독.

### 3.2. Tools (기능 실행)
LLM이 외부 세계에 영향을 미치거나 복잡한 계산을 수행하도록 합니다.
- 함수 호출(Function Calling)과 유사하지만 표준화된 인터페이스 제공.
- 예: "내 로컬 DB에서 사용자 목록 조회해줘" -> `query_users` 툴 실행.

### 3.3. Prompts (템플릿)
서버가 클라이언트에게 재사용 가능한 프롬프트 템플릿을 제공합니다.
- 예: "코드 리뷰 해줘" -> `review_code` 프롬프트 템플릿 로드.

---

## 4. MCP 서버 구현 계획 (Implementation Plan)

Custom MCP 서버(예: 로컬 파일 시스템 또는 SQLite 연동)를 구현하기 위한 기본 단계입니다.

### 4.1. 기술 스택
- **Language:** TypeScript (Node.js) 또는 Python
- **SDK:** `@modelcontextprotocol/sdk` (TS) 또는 `mcp` (Python)

### 4.2. 구현 시나리오: 'Local Log Viewer'
로컬의 특정 로그 파일들을 LLM이 읽고 분석할 수 있게 해주는 서버.

#### Step 1: 프로젝트 설정
```bash
npm init -y
npm install @modelcontextprotocol/sdk zod
```

#### Step 2: 서버 인스턴스 생성
```typescript
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";

// 서버 초기화
const server = new McpServer({
  name: "local-log-viewer",
  version: "1.0.0",
});
```

#### Step 3: 리소스(Resource) 정의
```typescript
import { z } from "zod";

server.resource(
  "app-logs",
  "file:///logs/app.log",
  async (uri) => {
    const logContent = await readLogFile("./logs/app.log");
    return {
      contents: [{ uri: uri.href, text: logContent }],
    };
  }
);
```

#### Step 4: 도구(Tool) 정의
```typescript
server.tool(
  "search_logs",
  { query: z.string() },
  async ({ query }) => {
    const results = await grepLogs(query);
    return {
      content: [{ type: "text", text: results }],
    };
  }
);
```

#### Step 5: 연결 및 실행
```typescript
const transport = new StdioServerTransport();
await server.connect(transport);
```

---

## 5. 결론 및 활용 가치

MCP는 **"AI와 데이터의 연결 표준"**입니다. 이를 도입하면:
1. **확장성:** 한 번 만든 서버를 여러 AI 클라이언트에서 재사용 가능.
2. **보안:** 데이터 접근 권한을 호스트 레벨에서 제어 가능.
3. **생태계:** 이미 Google Drive, Slack, GitHub, Postgres 등 다양한 공식/커뮤니티 서버가 존재.

앞으로의 AI 개발은 모델 튜닝뿐만 아니라, **"내 데이터를 얼마나 효과적으로 MCP로 노출하느냐"**가 중요한 경쟁력이 될 것입니다.
