# Claude Code vs Oh My OpenCode(=OpenCode 확장) 정리 (2026-02-15)

> 목표: **터미널 기반 코딩 에이전트**를 “제품형(Claude Code)” vs “오픈소스 하네스+확장(oh-my-opencode)” 관점에서 **팩트 중심**으로 비교하고, 어떤 상황에 무엇을 쓰면 좋은지 의사결정 기준을 만든다.

## 1) 한 줄 요약

- **Claude Code**: Anthropic이 제공하는 **공식 터미널 에이전트**. 코드베이스를 이해하고, 루틴 작업/설명/git 워크플로를 자연어로 수행하도록 설계됨. (공식 설치 스크립트/패키지 제공)  
  - Source: https://github.com/anthropics/claude-code
- **oh-my-opencode**: `opencode` 생태계에서 동작하는 **에이전트 하네스/번들** 성격의 오픈소스 프로젝트. 백그라운드 에이전트, 특화 에이전트(oracle/librarian 등), LSP/AST 도구, MCP 등을 “배터리 포함”으로 제공한다는 포지셔닝.  
  - Source: https://github.com/code-yeongyu/oh-my-opencode

## 2) 포지셔닝 비교(팩트 기반)

| 항목 | Claude Code | oh-my-opencode |
|---|---|---|
| 제공 주체 | Anthropic(공식) | 커뮤니티/오픈소스(독립 프로젝트) |
| 목표 | 터미널에서 코드 이해 + 작업 수행 + git 워크플로 지원 | “에이전트 하네스”로서 **여러 에이전트/도구/워크플로를 묶어** 생산성을 올리는 것 |
| 배포/설치 | 설치 스크립트/패키지 제공(README에 안내) | 릴리즈 제공 + npm 패키지(README에 링크/뱃지) |
| 플러그인/확장 | 레포에 플러그인 디렉토리 존재(README에 언급) | 백그라운드 에이전트/특화 에이전트/LSP·AST/MCP/호환 레이어를 강조 |
| 주의사항 | 데이터 수집/보관 정책 문서 링크가 README에 존재 | Claude OAuth 관련 제약/ToS 이슈에 대한 고지 섹션이 README에 존재(2026-01 기준) |

## 3) 아키텍처 관점(개념도)

### 3.1 Claude Code(제품형) - 단일 CLI 중심

```
[사용자] -> (claude CLI) -> [모델/서비스]
                 |
                 +-> 코드베이스 읽기/설명
                 +-> 루틴 작업 실행
                 +-> git 워크플로 지원
```

- “터미널에 사는 에이전트”라는 포지션을 명시한다.  
  - Source: anthropics/claude-code README

### 3.2 oh-my-opencode(하네스/오케스트레이션) - 에이전트/툴 번들링

```
[사용자]
  |
  v
[oh-my-opencode]
  |-- 백그라운드 에이전트 실행
  |-- 특화 에이전트 호출 (oracle / librarian / frontend engineer ...)
  |-- LSP/AST 기반 도구 사용
  |-- MCP(연동) 활용
```

- README가 “background agents, specialized agents(oracle/librarian…), crafted LSP/AST tools, curated MCPs, Claude Code compatibility layer”를 직접 언급한다.  
  - Source: code-yeongyu/oh-my-opencode README

## 4) 운영/리스크 체크(특히 ToS/인증)

- oh-my-opencode README에는 **Claude OAuth 제약(2026-01 기준)과 ToS 관련 주의**가 명시되어 있다.
  - 핵심: *기술적으로 가능할 수 있으나 권장하지 않는다*, 비공식 OAuth 구현을 프로젝트가 제공하지 않는다고 고지.
  - Source: code-yeongyu/oh-my-opencode README의 “Claude OAuth Access Notice” 섹션

## 5) 선택 가이드(의사결정 규칙)

- “공식 제품 + 단순한 워크플로 + 정책/컴플라이언스 명확성”이 우선이면 **Claude Code**.
- “여러 에이전트/도구를 묶어서 오케스트레이션하고, 작업을 분해해서 병렬로 돌리는 방식”에 관심이 있으면 **oh-my-opencode**.
- 조직/업무에서 인증/약관 이슈가 민감하면, oh-my-opencode의 OAuth/ToS 고지 섹션을 기준으로 **도입 범위(개인/실험/업무)**를 분리하는 것이 안전.

## 6) 후속 TODO(다음 TIL로 이어질 질문)

- (NEW) **오케스트레이션 패턴**: “백그라운드 에이전트 + 특화 에이전트”로 작업을 분해할 때, 어떤 입력/출력 계약이 안정적인가?
- (FIX) **현 환경(OpenClaw/OpenCode)과의 연결**: 현재 운영 중인 에이전트 런타임에서 재현 가능한 워크플로(작업 템플릿/체크리스트)로 내릴 것.
