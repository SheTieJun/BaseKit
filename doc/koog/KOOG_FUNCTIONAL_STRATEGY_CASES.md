# Functional Strategy 案例（A / B）

本文档给出两个“可落地”的多轮交互方案，用来解释 Koog 常说的 `strategy`（执行策略）在业务中的具体用法。

- A：不实现 `AskUser` 工具，由 App/UI 驱动多轮对话（最简单、最稳）。
- B：实现 `AskUser` 工具，让模型通过 tool call 主动发起“提问/选项”，由 UI 承接展示并把用户选择回传给工具（更像“Agent 自己会追问”）。

> 说明：BaseKit 当前聊天链路是 Basic Agent（`agent.run(text, sessionId)`）+ ChatMemory/LongTermMemory + Tools。本文档的示例可以在此基础上增量实现，不要求先重构为 Graph/Planner。

---

## 0. 目标场景（JCConf 议程助手）

系统提示词核心要求（简化版）：

- 当用户给“议程主题关键词”时，优先调用工具 `searchTitleByKeyword`
- 当用户给“讲师名字”时，优先调用工具 `searchTitleBySpeaker`
- 行为规则：先问用户想用哪种方式，再追问具体关键词/讲师名，再查工具，再用固定格式输出
- 约束：结果必须来自工具，不能编造

这类场景本质是一个“多轮问诊式流程”，`strategy` 解决的是“流程怎么跑”，而不是“prompt 怎么写”。

---

## A 方案：UI 驱动多轮（不实现 AskUser Tool）

### A1. 思路

不让模型自己“弹窗问你选哪个”，而是由 UI/业务代码把流程拆成多轮：

1) 第 1 轮：用户说“我要查议程” → 模型只返回一个追问（让用户选方式）
2) 第 2 轮：用户回答“按讲师” → 模型追问“讲师是谁”
3) 第 3 轮：用户给出“张三” → 业务代码调用工具（或让模型调用工具）→ 输出结果

关键点是：A 方案的“状态机”由 App 保存（例如 `PendingQueryMode = Keyword|Speaker`），模型每一轮只处理当前输入，不需要在同一轮里完成所有步骤。

### A2. 优点 / 缺点

- 优点：实现最短；Android 上交互最自然；不需要把 UI 能力塞进 Tool；也不需要阻塞等待用户输入。
- 缺点：流程控制在业务层；如果你希望“Agent 自己决定何时 AskUser”，A 方案不够“自动化”。

### A3. 最小实现骨架（伪代码）

```kotlin
enum class AgendaQueryMode { None, ByKeyword, BySpeaker }

data class AgendaFlowState(
    val mode: AgendaQueryMode = AgendaQueryMode.None
)

// 第 1 轮：如果用户没说明方式，就返回追问
fun handleInput(input: String, flowState: AgendaFlowState): Pair<String, AgendaFlowState> {
    if (flowState.mode == AgendaQueryMode.None) {
        return "你想用哪种方式查询：1) 主题关键词 2) 讲师名字？" to flowState
    }

    // 第 2 轮：根据 mode 追问必要参数
    if (flowState.mode == AgendaQueryMode.BySpeaker && input.isBlank()) {
        return "请输入讲师名字。" to flowState
    }

    // 第 3 轮：拿到参数后再查工具并输出
    // - 可选：这里由业务层直接调工具；或把“查工具 + 整理”交给模型
    return "（调用工具并返回列表）" to flowState.copy(mode = AgendaQueryMode.None)
}
```

### A4. 与 Koog 的关系

这类“UI 驱动多轮”在 Koog 里仍然是合法的：你继续使用 `agent.run(text, sessionId)`，并让 ChatMemory 保存多轮对话上下文；只是“流程策略”由业务层实现。

---

## B 方案：实现 AskUser Tool（模型通过工具发起追问）

### B1. 思路

把“向用户提问并等待回答”抽象成一个工具 `AskUser`：

- 模型需要用户输入时，不直接输出自然语言追问，而是 **tool call**：`AskUser(question, options...)`
- 工具执行时由 UI 弹出对话框/选项，让用户选择或输入
- 工具把用户答案作为 tool result 回传，模型再继续调用“查询工具”，最后输出结构化结果

这更接近你给的约束：“一律使用 AskUser 工具，不要自行结束对话”。

### B2. 关键难点（Android）

Tool 的 `execute()` 是一个 `suspend` 方法，但 Android 的 UI 交互是事件驱动的。要实现“工具等待用户输入”，需要一个“UI 网关”把对话框结果以 `suspend` 形式回传。

最小可行做法：

- 定义 `AskUserGateway`：对外提供 `suspend fun ask(...) : String`
- UI 层订阅一个 `SharedFlow<AskRequest>`，显示对话框
- 用户确认后把结果回传给 gateway，唤醒挂起的协程

### B3. 最小实现骨架（可直接照着落地）

**(1) Gateway（建议在 app 层）**

```kotlin
data class AskRequest(
    val id: String,
    val question: String,
    val options: List<String>
)

interface AskUserGateway {
    suspend fun ask(question: String, options: List<String>): String
}
```

**(2) Tool（在 `tools/`，由 `KoogAgentKit.createAgent` 统一注册）**

```kotlin
@Serializable
data class AskUserArgs(
    @property:LLMDescription("向用户展示的问题")
    val question: String,
    @property:LLMDescription("可选项列表；为空表示自由输入")
    val options: List<String> = emptyList()
)

class AskUserTool(
    private val gateway: AskUserGateway
) : SimpleTool<AskUserArgs>(
    argsType = typeToken<AskUserArgs>(),
    name = "AskUser",
    description = "当信息不足时必须调用该工具向用户追问；不得自行猜测。"
) {
    override suspend fun execute(args: AskUserArgs): String {
        return gateway.ask(args.question, args.options)
    }
}
```

**(3) systemPrompt（约束模型“必须用 AskUser”）**

```text
你是 JCConf 的议程助手。规则：
1) 信息不足时一律调用 AskUser 工具，不要直接输出追问句结束。
2) 查会议议程必须调用工具：searchTitleByKeyword / searchTitleBySpeaker，禁止编造。
3) 输出使用简体中文，列表格式逐场输出：标题/讲者/Track。
```

### B4. 优点 / 缺点

- 优点：策略“向 Agent 内收敛”；模型能在同一轮里连续发起多次 AskUser/tool 查询（取决于模型与执行策略）。
- 缺点：实现复杂度更高；需要 UI 网关与协程挂起/恢复；要做好取消与生命周期处理（例如对话框被关闭、页面销毁）。

---

## A vs B 选择建议

- 先上手/快速闭环：选 A（更符合移动端交互直觉，也更容易保证稳定）。
- 需要严格遵守“必须 AskUser、必须工具查、流程由 Agent 自己控”：选 B。

---

## 关联文档

- [KOOG_TOOLS.md](KOOG_TOOLS.md)（Tools 与 ToolRegistry）
- [KOOG_PROMPTS.md](KOOG_PROMPTS.md)（systemPrompt 写法与工具约束）
- [KOOG_FUNCTION_CALL_SKILL_MCP.md](KOOG_FUNCTION_CALL_SKILL_MCP.md)（术语对齐）
- Koog 官方：Functional agents（functionalStrategy DSL）：https://docs.koog.ai/agents/functional-agents/

