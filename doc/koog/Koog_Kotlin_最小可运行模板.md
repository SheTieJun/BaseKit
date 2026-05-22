# Koog（Kotlin）最小可运行模板合集

> 目标：给你“能直接复制粘贴跑起来”的 Kotlin 模板：Basic / Graph / Planner（各一份），再给一个 Tool + ToolRegistry 的最小示例。

---

## 0. 通用准备

### 0.1 依赖（Gradle Kotlin DSL）
```kotlin
dependencies {
    implementation("ai.koog:koog-agents:0.7.1")
}
```

### 0.2 环境变量（以 OpenAI 为例）
```bash
export OPENAI_API_KEY=your-api-key
```

> 说明：API Key 不要硬编码在代码/配置仓库中，统一用环境变量读取。  
> 如果你用 Anthropic / Google / DeepSeek / OpenRouter / 本地 Ollama，替换对应的 executor 与环境变量即可（Koog Quickstart 有完整列表）。

### 0.3 必记 5 条（建议写进项目 README）
1. **API Key 不要硬编码**：只从环境变量读取。
2. **简单任务用 Basic Agent**：先最小化上线，别过度设计。
3. **复杂流程用 Graph-based Agent**：多步骤、分支、可重试、需要可调试时上图工作流。
4. **务必设置 `maxIterations`**：防止无限循环导致成本失控（Planner/图流程必设）。
5. **长对话开启 History Compression**：避免 token 溢出（长跑 Agent 建议启用）。

---

## 1) Basic Agent：最小可运行（推荐先从这里起步）

```kotlin
import ai.koog.agents.core.agent.AIAgent
import ai.koog.llm.openai.OpenAIModels
import ai.koog.llm.openai.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY 未设置")

    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(apiKey),
        llmModel = OpenAIModels.Chat.GPT4o,
        systemPrompt = "你是一个简洁、可靠的 Kotlin 助手。",
        temperature = 0.2,
        maxIterations = 6,
    )

    val result = agent.run("用 3 句话解释 Koog 的核心概念。")
    println(result)
}
```

**适用场景**
- 先验证链路：依赖、Key、模型、网络等是否 OK
- 轻量问答/一次性生成

---

## 2) Tool + ToolRegistry：让 Agent 具备“可执行能力”的最小示例

> 思路：写一个最简单的工具（计算器/字符串处理都行），注册到 ToolRegistry，再把 registry 传给 Agent。

```kotlin
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.tools.SimpleTool
import ai.koog.types.typeToken
import ai.koog.llm.openai.OpenAIModels
import ai.koog.llm.openai.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class AddArgs(
    val a: Int,
    val b: Int
)

class AddTool : SimpleTool<AddArgs>(
    argsType = typeToken<AddArgs>(),
    name = "add",
    description = "计算两个整数的和"
) {
    override suspend fun execute(args: AddArgs): String {
        // 为什么这么写：工具返回 String 结果，便于给 LLM 继续推理或直接输出
        return (args.a + args.b).toString()
    }
}

fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY 未设置")

    val toolRegistry = ToolRegistry {
        tools(AddTool())
    }
    // 你也可以合并多个工具注册表：registry1 + registry2

    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(apiKey),
        llmModel = OpenAIModels.Chat.GPT4o,
        systemPrompt = "你是一个会优先使用工具来计算的助手。",
        toolRegistry = toolRegistry,
        temperature = 0.0,
        maxIterations = 8,
    )

    val result = agent.run("请计算 123 + 456，给出最终答案。")
    println(result)
}
```

**适用场景**
- 需要接入你的业务能力（查库、调接口、跑计算、读文件等）
- 想把“动作”从 LLM 文本里剥离出来，降低幻觉与不可控性

---

## 3) Graph-based Agent：当你需要“可控工作流/可视化/可调试”

> 说明：Graph-based 的 API 细节在不同版本可能略有变化。下面给你的是“结构模板”：你主要照着拆节点与状态流转即可。
>
> 如果你把你当前 Koog 版本的 graph 示例链接（或你项目里的现有 graph 代码）贴一下，我可以按你实际版本把模板改成可直接编译运行的版本。

```kotlin
// 结构模板（伪代码/结构示例）：
//
// 1. 定义状态 State（输入、工具结果、最终输出）
// 2. 用 strategy/graph 声明节点：
//    - 解析输入节点
//    - 工具调用节点
//    - 汇总输出节点
// 3. 用条件边决定分支（例如：输入不全 → 继续追问；输入齐了 → 调工具）
//
// 伪代码示意：
//
// val strategy = strategy<State, String>("my-strategy") {
//   val parse by node<State, State> { state -> ... }
//   val callTool by node<State, State> { state -> ... }
//   val summarize by node<State, String> { state -> ... }
//   edge(parse, callTool) { state -> state.ready }
//   edge(parse, parse) { state -> !state.ready }  // 信息不足就继续问
//   edge(callTool, summarize)
// }
//
// val agent = GraphBasedAIAgent(..., strategy = strategy)
// agent.run(initialState)
```

**适用场景**
- 多步骤任务，且每一步你希望“显式可控”
- 需要明确状态流转/分支/重试
- 上线后要做排障与观测（结合 tracing/event handler）

---

## 4) Planner Agent：当你希望它“自己出计划并迭代执行”

> 同样给结构模板（更强调边界与兜底）：Planner 易跑飞，务必设置迭代上限、工具白名单和失败处理。

```kotlin
// 结构模板（伪代码/结构示例）：
//
// val agent = PlannerAIAgent(
//   promptExecutor = ...,
//   llmModel = ...,
//   systemPrompt = "你是一个会分步骤完成任务的规划器。每步都要可执行、可验证。",
//   toolRegistry = ...,
//   maxIterations = 12
// )
//
// val result = agent.run("帮我：1) 总结这段文本 2) 生成要点清单 3) 输出 JSON")
// println(result)
```

**适用场景**
- 任务步骤不固定、依赖上下文决策（例如“做一份调研并输出报告”）
- 你不想手写所有图流程，但接受一定不确定性

**关键建议**
- 必设 `maxIterations`
- 工具要做错误处理与超时
- 必要时用结构化输出（JSON schema）锁定输出形态

---

## 5) 我建议你用的落地顺序（Kotlin）

1. **Basic Agent 跑通**（1 文件可运行）
2. **加 ToolRegistry + 1 个工具**（把“动作”落到工具上）
3. 流程开始变复杂时：  
   - 需要强可控 → **Graph-based**  
   - 需要自主规划 → **Planner**
