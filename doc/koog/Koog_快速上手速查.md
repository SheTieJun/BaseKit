# Koog 快速上手速查（面向快速落地）

> 目标：把 Koog 的核心概念、关键字与常用套路压缩成一份“随用随查”的清单，让你能快速上手并稳定落地。

---

## 1. Koog 是什么

- **Koog**：JetBrains 开源的 JVM 生态 AI Agent 框架。  
  - **一句话**：用“类型安全 + 工作流/状态机”的方式构建 Agent（不只是调用一次 LLM）。
  - Kotlin/Java 开发体验优先；Kotlin 还可用于多平台（KMP）场景。

---

## 2. 快速开始（跑通最小 Agent）

### 2.1 前置要求
- JDK 17+
- Kotlin 2.2.0+
- Gradle 8.0+ 或 Maven 3.8+

### 2.2 安装依赖（Gradle Kotlin DSL）
```kotlin
dependencies {
    implementation("ai.koog:koog-agents:0.7.1")
}
```

### 2.3 API Key（不要硬编码）
- **强制建议**：API Key 只用环境变量读取，不要写进代码/配置仓库。
- 示例（Linux/macOS）：
```bash
export OPENAI_API_KEY=your-api-key
```

### 2.4 最小可运行示例（Kotlin）
```kotlin
fun main() = kotlinx.coroutines.runBlocking {
    // 为什么这么写：避免硬编码敏感信息，方便本地/CI/线上统一管理
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY 未设置")

    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(apiKey),
        llmModel = OpenAIModels.Chat.GPT4o,
        systemPrompt = "你是一个有用的助手。",
        temperature = 0.2,
        maxIterations = 6, // 为什么要设：防止意外循环导致成本失控
    )

    val result = agent.run("用 3 句话解释 Koog 的核心概念。")
    println(result)
}
```

---

## 3. 必记 5 条（直接照做）

1. **API Key 不要硬编码**：统一用环境变量读取（例如 `System.getenv("OPENAI_API_KEY")`）。
2. **简单任务用 Basic Agent**：先用最小实现跑通与上线，别一开始就上复杂架构。
3. **复杂流程用 Graph-based Agent**：多步骤、分支、需要可控/可调试/可观测时用图工作流。
4. **设置 `maxIterations/maxAgentIterations`**：防止无限循环，尤其 Planner/图流程必设。
5. **长对话开启 History Compression**：避免 token 溢出（长跑 Agent 强烈建议启用）。

---

## 4. 关键字速查（Glossary 精华）

> 格式：**名词** → 一句话 → 什么时候用 → 关键点

- **Agent**：能推理、按策略执行、调用工具的 AI 实体  
  - 用于：把一次性对话升级为“可执行流程”  
  - 关键点：Agent = LLM + Tools + Strategy + Features

- **LLM**：底座大模型（GPT/Claude/Gemini/DeepSeek/Ollama 等）  
  - 用于：生成、理解、决策  
  - 关键点：不同模型的工具调用/结构化输出/上下文能力会影响设计

- **Message**：对话的一条消息（system/user/assistant/toolCall/toolResult）  
  - 用于：需要精细控制对话历史或调试时  

- **Prompt**：发给 LLM 的“对话包”（消息列表 + 参数）  
  - 用于：多消息、示例、输出约束、工具结果注入等  

- **System Prompt**：长期行为准则（角色、边界、风格、约束）  
  - 用于：几乎必用  
  - 关键点：稳定、明确、可复用（建议模板化）

- **Strategy / Graph / Node / Edge / Condition / Subgraph**：工作流建模关键词  
  - 用于：复杂流程的显式控制  
  - 关键点：节点是基本执行单元，边/条件决定流转，子图用于复用与拆分职责

- **Tool / ToolCall / ToolResult / ToolRegistry**：工具体系关键词  
  - 用于：让 Agent 具备“可执行能力”（查库、调接口、跑计算、读写外部系统）  
  - 关键点：ToolRegistry 决定“Agent 能用哪些工具”

- **History Compression**：历史压缩（控制 token）  
  - 用于：长对话、多轮工具调用、Planner 多次迭代  
  - 关键点：优先保证“关键信息不丢”

- **Feature**：扩展能力（事件回调、持久化、追踪、记忆等）  
  - 用于：线上化、可观测、可恢复、可扩展

---

## 5. Agent 类型（选型）

- **Basic Agent**：简单任务/快速上线优先  
- **Functional Agent**：用函数写少量自定义逻辑（轻量分支/校验/拼装）  
- **Graph-based Agent**：复杂工作流（多步骤、分支、重试、可视化、可调试）  
- **Planner Agent**：让 Agent 自主规划并迭代执行（务必设迭代上限）

---

## 6. Tools（工具体系：最常用点）

### 6.1 工具类型
1. **内置工具**：对话管理/交互等框架自带
2. **注解工具**：快速把函数暴露给 LLM（适合业务函数）
3. **类工具（Class-based）**：完全控制参数/元数据/执行逻辑（适合复杂工具）

### 6.2 ToolRegistry（工具注册表）
- **一句话**：告诉 Agent“你能用哪些工具”。

```kotlin
val toolRegistry = ToolRegistry {
    tools(myTool)
}
```

### 6.3 工具注册表可合并（registry1 + registry2）
```kotlin
val newRegistry = firstToolRegistry + secondToolRegistry
```

### 6.4 把工具交给 Agent
```kotlin
val agent = AIAgent(
    promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
    llmModel = OpenAIModels.Chat.GPT4o,
    systemPrompt = "你是一个会优先使用工具的助手。",
    toolRegistry = toolRegistry,
    maxIterations = 8,
)
```

---

## 7. Advanced & Features（高级能力：何时要上）

- **History Compression**：长对话必备，避免 token 溢出  
- **Agent Persistence**：断点恢复/跨进程保留进度  
- **Structured Output**：对接下游程序时优先（减少解析脆弱性）  
- **Streaming API**：实时输出、并行工具调用  
- **Embeddings / RAG / Memory**：知识库与跨会话记忆  
- **Tracing / OpenTelemetry / EventHandler**：上线排障、监控、埋点

---

## 8. 常见坑与建议（快速避雷）

- **Key 管理**：API Key 不要硬编码，统一环境变量。
- **选型**：简单先 Basic；复杂多步骤优先 Graph-based（可控、可调试）。
- **无限迭代**：务必设置 `maxIterations/maxAgentIterations`。
- **长对话**：开启 history compression，避免上下文窗口溢出导致失败或性能下降。
- **工具失败**：工具内部做好异常处理/超时/重试策略，避免 Agent 直接崩。

---

## 9. 参考链接（原文）

- 首页/目录：https://docs.koog.ai/
- Quickstart：https://docs.koog.ai/quickstart/
- Glossary：https://docs.koog.ai/glossary/
- Agents：https://docs.koog.ai/agents/
- Prompts：https://docs.koog.ai/prompts/
- Tools Overview：https://docs.koog.ai/tools-overview/
