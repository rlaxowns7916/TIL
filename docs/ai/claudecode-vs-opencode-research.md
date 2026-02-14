# Claude Code vs. OpenCode & Oh My OpenCode Deep Research

## 1. Overview & Positioning

| Feature | **Claude Code** | **OpenCode (Core)** | **Oh My OpenCode (Plugin)** |
| :--- | :--- | :--- | :--- |
| **Identity** | Anthropic's Official Agent | Open-Source Agent Harness | "The Ubuntu of OpenCode" |
| **Philosophy** | Minimalist, Model-Centric | Extensible, Hacker-Centric | Battery-Included, Agent-Orchestration |
| **Primary Model** | Claude 3.5/3.7 (Native) | Agnostic (OpenAI/Anthropic/Google) | Orchestrated (Sisyphus/Prometheus/Metis) |
| **Ecosystem** | Closed (Official Plugins) | Open (Community Plugins) | Curated (Best-in-class bundle) |

---

## 2. Agent System Comparison

### Claude Code Agents
- **Architecture**: Single-threaded, context-heavy execution.
- **Capabilities**: Strong reasoning, official tool integration.
- **Limitation**: Linear execution, expensive context usage.

### Oh My OpenCode Agents (The "Sisyphus" Team)
- **Sisyphus (Orchestrator)**: Main driver (`Claude Opus 4.5`). Manages sub-agents.
- **Hephaestus (Deep Worker)**: Autonomous execution (`GPT-5.2`). Goal-oriented.
- **Oracle (Architect)**: Read-only reasoning/debugging (`GPT-5.2`). No side-effects.
- **Librarian (Researcher)**: Docs/Codebase expert (`Claude Sonnet 4.5` / `GLM-4.7`).
- **Explore (Scout)**: Fast grep/search (`Claude Haiku 4.5`).
- **Multimodal-Looker**: Vision expert (`Gemini 3 Flash`).

> **Key Difference**: Oh My OpenCode uses **Specialized Delegation**. Instead of one heavy model doing everything, Sisyphus delegates "reading docs" to Librarian and "designing architecture" to Oracle, saving context and cost while increasing speed.

---

## 3. Command System

### Standard Slash Commands
- **Both**: Support `/help`, `/clear`, `/compact`.

### Oh My OpenCode Exclusive Commands
| Command | Function |
| :--- | :--- |
| `/init-deep` | Generate hierarchical `AGENTS.md` context files recursively. |
| `/ralph-loop` | Self-healing execution loop until `<promise>DONE</promise>`. |
| `/ulw-loop` | "Ultrawork" loop – high-intensity parallel execution. |
| `/refactor` | AST/LSP-aware refactoring with safety plan & TDD verification. |
| `/start-work` | Execute a `Prometheus` generated plan via `Atlas` workflow. |

---

## 4. Skill & MCP (Model Context Protocol)

### Claude Code
- **Skills**: Basic tool definitions (read/write/bash).
- **MCP**: Native support, managed via config.

### Oh My OpenCode Skills
- **Playwright Skill**: Browser automation (scraping, testing, screenshots).
- **Git Master Skill**: Atomic commits, rebase surgery, history archaeology.
- **Frontend UI/UX Skill**: Aesthetic direction, component craftsmanship.

### Built-in MCPs (Oh My OpenCode)
- **WebSearch**: Real-time search via Exa AI.
- **Context7**: Official documentation fetcher.
- **Grep.app**: GitHub public code search.

---

## 5. Rule & Hook System

### Rule Injection
- **Claude Code**: `.claude/rules/*.md`.
- **Oh My OpenCode**: Intelligent **Directory-based Injection**.
  - Reads `AGENTS.md` from current dir up to root.
  - Injects `README.md` automatically.
  - Supports conditional rules (globs).

### Hooks (Lifecycle Automation)
Oh My OpenCode exposes a rich event system:
- **PreToolUse**: Validate inputs (e.g., `thinking-block-validator`).
- **PostToolUse**: Check outputs, truncate huge logs (`grep-output-truncator`).
- **UserPromptSubmit**: Detect keywords (`ulw`, `think deep`), auto-update.
- **Stop**: Session recovery, notifications, auto-compaction.

> **Unique Feature**: **Comment Checker** hook prevents AI from adding excessive/obvious comments, enforcing "human-like" code style.

---

## 6. Research Conclusion

**Claude Code** is a polished, vertically integrated product ideal for users who want a "just works" experience within the Anthropic ecosystem.

**Oh My OpenCode** is a **force multiplier** for power users. It transforms OpenCode into a **multi-agent orchestration OS**:
1.  **Parallelism**: Background agents (Explore/Librarian) run while you work.
2.  **Specialization**: Different models for different cognitive loads (Opus for planning, Haiku for grepping).
3.  **Resilience**: Ralph Loop and Todo Enforcer prevent the agent from giving up.
4.  **Tooling**: Built-in LSP, AST-Grep, and Playwright integration outclass standard tools.

**Recommendation for TIL**:
- Adopt **Oh My OpenCode** patterns (Sisyphus workflow, Directory-based Context).
- Study **LSP/AST-Grep** integration for the `/refactor` command implementation.
- Explore **Ralph Loop** mechanism for self-healing autonomous tasks.
