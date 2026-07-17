---
name: "koog-dev"
description: "Koog 开发工具包：梳理 Tools/MCP/Prompt/LLM 参数/记忆/压缩/审核等规范并产出模板。用户提到 Koog、ToolRegistry、MCP、AskUser、ChatMemory 时调用。"
---

# Koog 开发 SKILL 工具包（koog-dev）

## 你什么时候该用它

- 你要在 Koog 上开发/改造 Agent（Basic / Functional / Graph / Planner）
- 你要实现或接入工具（Tool / SimpleTool / ToolRegistry）
- 你要把工具调用轨迹写进 Prompt（toolCall / toolResult）
- 你要接 MCP（stdio / SSE）并桥接到 ToolRegistry
- 你要接入 ChatMemory / LongTermMemory / HistoryCompression
- 你要做内容审核（输入/输出/工具三段式）
- 你要做模型参数与能力降级（LLMParams / LLMCapability）

## 输出物（工具包结构）

- 入口与使用说明：本文件
- Koog 规范与 API 参考（本 SKILL 内置）：[koog-reference.md](docs/koog-reference.md)
- 工程模板（可复制）：[toolkit](toolkit/)
- 测试用例（可复制）：[KoogDevSkillSmokeTest.kt](toolkit/tests/KoogDevSkillSmokeTest.kt)

## 规范总览（最重要的 10 条）

- API Key 只从环境变量/安全存储读取，禁止硬编码
- 必设 `maxIterations/maxAgentIterations` 防止循环成本失控
- Tool 入参用 object schema（`@Serializable data class Args(...)`），避免 schema 初始化问题
- Tool 描述/参数描述必须清晰，输出尽量结构化（便于二段式扩写/后处理）
- 工具必须注册进 `ToolRegistry`，Agent 才能调用
- 不确定/信息不足时必须走 `AskUser` 工具（不要靠猜）
- 长对话必须启用 `HistoryCompression`，并按需 `preserveMemory`
- ChatMemory 负责短期对话历史；LongTermMemory 负责长期可检索记忆（默认建议显式写入）
- 接 MCP 时要明确传输方式（stdio/SSE）、ServerInfo、版本与工具来源隔离
- 内容审核默认开启：输入前审、输出前审、工具边界审

## 关键 API 与调用方式（对照表）

### Tools / ToolRegistry

- 工具基类：`Tool<Args, Result>`、`SimpleTool<Args>`
- 注册：`ToolRegistry { tools(myTool) }`
- 参考：见 [koog-reference.md](docs/koog-reference.md)

### Prompt DSL（含 toolCall/toolResult）

- DSL 入口：`ai.koog.prompt.dsl.prompt`
- 工具消息：`toolCall(id, tool, args)` / `toolResult(id, tool, output)`
- 参考：见 [koog-reference.md](docs/koog-reference.md)

### LLM 参数与模型能力

- Prompt 级参数：`LLMParams(temperature, toolChoice, numberOfChoices, maxTokens, schema, ...)`
- 能力检测：`LLModel.supports(LLMCapability.Tools)` 等
- 参考：见 [koog-reference.md](docs/koog-reference.md)

### MCP

- 传输：stdio / SSE
- 关键组件：`McpToolRegistryProvider`、`McpServerInfo`
- 参考：见 [koog-reference.md](docs/koog-reference.md)

### 记忆与压缩

- ChatMemory：`ChatHistoryProvider` + `ChatMemory` feature（短期对话历史）
- LongTermMemory：`SearchStorage/WriteStorage` + `SimilaritySearchStrategy` + `SystemPromptAugmenter`
- HistoryCompression：`nodeLLMCompressHistory` / `llmCompressHistory()` / `preserveMemory`
- 参考：见 [koog-reference.md](docs/koog-reference.md)

### 内容审核

- 三段式：输入前审 / 输出前审 / 工具边界审
- 参考：见 [koog-reference.md](docs/koog-reference.md)

## 关键配置清单（照单对齐）

### Agent 运行参数（最小必配）

- `systemPrompt`
- `llmModel`
- `temperature`
- `maxIterations`
- `toolRegistry`（可选，但只要你需要 tools 就必须）

### LLMParams（Prompt 级推荐）

- `temperature`
- `maxTokens`
- `toolChoice`
- `numberOfChoices`
- `schema`
- `speculation`

### Model Capabilities（能力检测/降级）

- Core：`Temperature`、`Tools`、`ToolChoice`、`MultipleChoices`、`Speculation`
- Schema：`Schema.JSON.Basic`、`Schema.JSON.Standard`
- Media/Text：`Vision.Image`、`Document`、`Embed`、`Completion`、`PromptCaching`、`Moderation`

### ChatMemory / LongTermMemory / HistoryCompression

- ChatMemory：`sessionId`（建议 userId+agentId+场景），`ChatHistoryProvider`（持久化实现）
- LongTermMemory：`namespace`（建议 userId_agentId）、`SimilaritySearchStrategy(topK, similarityThreshold)`、`SystemPromptAugmenter`
- HistoryCompression：
  - Strategy：`WholeHistory` / `FromLastNMessages(n)` / `Chunked(chunkSize)` / `FactRetrievalHistoryCompressionStrategy`
  - `preserveMemory`（避免压缩误删关键 memory 消息）

### MCP

- Transport：stdio / SSE
- `McpServerInfo(url, command?)`
- Client meta：`name`、`version`

## 工程模板（可直接复制）

### 1) 入口：创建 Agent + 注入 ToolRegistry

见：[KoogDevEntry.kt](toolkit/entry/KoogDevEntry.kt)

### 2) 配置模块：统一管理 Provider/Model/Key/Iterations/温度

见：[KoogDevConfig.kt](toolkit/config/KoogDevConfig.kt)

### 3) 工具模块：SimpleTool + Args schema + 错误处理

见：[ExampleAddTool.kt](toolkit/tools/ExampleAddTool.kt)

### 4) MCP 模块：stdio/SSE → ToolRegistry

见：[McpRegistryFactory.kt](toolkit/mcp/McpRegistryFactory.kt)

### 5) Prompt 模块：toolCall/toolResult 写入 Prompt（可选）

见：[PromptTemplates.kt](toolkit/prompt/PromptTemplates.kt)

## 错误处理方案（落地必配）

- 工具执行失败：工具内部捕获异常并返回可解释错误字符串；必要时将错误映射为“可重试/不可重试”
- AskUser：信息不足时必须走工具追问；选项为空时允许自由输入
- 重试/超时：在业务调用工具/外部 API 时设置超时；在 Agent 侧用 `maxIterations` 限制循环
- 截断：提高 `maxTokens`、减少无关历史、启用 history compression

## 常见问题排查（FAQ）

- 工具不被调用：检查是否注册到 `ToolRegistry`；检查 systemPrompt 是否明确“优先用工具”；检查模型是否支持 `LLMCapability.Tools`
- 工具参数解析失败：确认 Args 是 `@Serializable data class` 且为 object schema；减少可选字段数量；用 `@LLMDescription` 补语义
- 模型输出不稳定：降 `temperature`；强化输出格式；必要时上 JSON schema
- 长对话越聊越慢：启用 `HistoryCompression`；拆分 sessionId；把长期偏好写入 LongTermMemory
- MCP 工具不可用：确认 transport（stdio/SSE）连通；确认 `McpServerInfo`；确认 ToolRegistry 来源隔离

## 验证（模拟环境）

- 只验证 SDK 接口可调用（不依赖真实 LLM 网络）：运行 smoke test（需将本 SKILL 的测试文件复制到你的工程 test 源集后执行）

```bash
./gradlew test
```

- 仅做编译级验证：

```bash
./gradlew :baseKit:compileDebugKotlin
./gradlew :app:compileDebugKotlin
```

## 部署/集成步骤（复制即用）

- 把 [toolkit](toolkit/) 目录里的模板复制到你的工程（建议路径：`<module>/src/main/java/.../koog/`）
- 根据你的平台替换配置来源（Android：设置页/EncryptedStorage；Server：ENV/Secrets）
- 先跑 smoke test（上面命令），再替换为真实 promptExecutor（OpenAI/Anthropic/Google/DeepSeek/Ollama 等）
