# Koog Prompts 使用指南（Prompt DSL / Builder）

本文档解释 Koog 的 Prompt 是什么、如何创建与扩展 Prompt、以及在 Agent 中如何使用 Prompt（包含 system message、tool message、LLM 参数等）。

> 参考：Koog 官方 prompts 文档（Prompt DSL / Builder / message types / params）。

另见：`KOOG_LLM_PARAMETERS.md`（LLM 参数：temperature/toolChoice/maxTokens 等）

## 1. Prompt 是什么

Prompt 可以理解为“发给 LLM 的消息集合”，它不是一段字符串，而是一组有类型的消息（system/user/assistant/tool...）+ 可选参数（温度、maxTokens、toolChoice 等）。

在 Koog 中：

- 简单场景：你可以直接 `agent.run("...")`，Koog 会自动把字符串转换成 Prompt 并执行（适合 Basic Agent）。
- 复杂场景：你显式构建 Prompt，把 system/user/assistant/toolCall/toolResult 按顺序组织好，再执行。

## 2. 如何创建 Prompt（Kotlin DSL）

Kotlin DSL 是最推荐的方式，阅读性最好：

```kotlin
import ai.koog.prompt.dsl.prompt

val p = prompt("hello-koog") {
    system("你是一个写作助手，回答要简洁。")
    user("给我一个赛博朋克+修仙的开篇灵感")
}
```

你可以继续追加消息类型（示意）：

```kotlin
val p = prompt("chat") {
    system("你是一个写作助手。")
    user("你好")
    assistant("你好，我能帮你做剧情/大纲/开篇。")
    user("给我一个开篇三章结构")
}
```

## 3. 如何创建 Prompt（Java Builder / Kotlin 也可用）

Java/Builder 写法（Kotlin 也能调用）：

```java
import ai.koog.prompt.Prompt;

Prompt p = Prompt.builder("unique_prompt_id")
    .system("You are a helpful assistant.")
    .user("What is Koog?")
    .build();
```

## 4. Tool 消息（toolCall / toolResult）是什么

当你希望把“工具调用过程”显式写进 prompt（让模型看到“我调用了哪个工具，得到什么结果”），可以用 tool 消息。

Kotlin DSL 示例（示意）：

```kotlin
val p = prompt("calculator_example") {
    system("你是一个带工具的助手。")
    user("5+3 等于多少？")
    toolCall(
        id = "calculator_tool_id",
        tool = "calculator",
        args = """{"operation":"add","a":5,"b":3}"""
    )
    toolResult(
        id = "calculator_tool_id",
        tool = "calculator",
        output = "8"
    )
}
```

这类能力适合在你后续把“规则触发工具”升级为“Prompt 内显式工具轨迹”时使用。

BaseKit 当前已在写作灵感链路中使用该思路：调用 `InspirationTool` 后，将 `toolCall/toolResult` 写入 Prompt，再让模型基于工具输出进行扩写。

## 5. Prompt 参数（temperature / maxTokens / toolChoice…）

Koog 支持给 Prompt 附加 LLM 参数（例如温度、最大 tokens、toolChoice 等），用于更稳定/可控的输出。

示意（具体类名以 Koog SDK 版本为准）：

```java
Prompt p = Prompt.builder("dev-assistant")
    .withParams(/* LLMParams(...) */)
    .system("You are a helpful assistant.")
    .user("Tell me about Kotlin")
    .build();
```

## 6. 在 Agent 中使用 Prompt（两种思路）

### 6.1 直接 run(String)（最简单）

适用：单轮请求、或你愿意把 systemPrompt/history/toolResult 都拼进一段文本。

BaseKit 当前的推荐方式是：

- systemPrompt：由设置页配置，并在创建 Agent 时通过 Koog 的 `systemPrompt` 注入
- history：由 ChatMemory 自动加载/保存（不再需要手动拼接历史）
- tool：优先交给模型通过 Tools 调用（ToolRegistry 统一注册）

### 6.2 在创建 Agent 时设置 systemPrompt（更标准）

Koog 支持在构建 Agent 时设置 `systemPrompt`，例如（示意）：

```java
AIAgent<String, String> agent = AIAgent.builder()
    .promptExecutor(simpleOpenAIExecutor(apiKey))
    .systemPrompt("You are a helpful assistant. Answer concisely.")
    .llmModel(OpenAIModels.Chat.GPT4o)
    .build();
```
## 7. 基于 systemPrompt 的“意图判断 + 工具优先”写法

很多“会议议程助手 / 客服助手 / 学习助手”的核心不是 Prompt DSL 的花活，而是：

- 用 systemPrompt 把“意图判断规则”说清楚
- 让模型在不同意图下**优先调用不同工具**
- 用 `AskUser` 工具把多轮追问（澄清问题）做成可控流程

下面是一个可直接复用的 systemPrompt 模板（繁体输出、强制走工具、强制 AskUser 追问）。你只需要实现并注册对应工具：`searchTitleByKeyword`、`searchTitleBySpeaker`、`AskUser`。

```text
你是 JCConf 的议程助手，可以协助用户查询议程，你有以下两种方式协助用户：
* 当用户给你「议程主题关键词」时，请优先调用议程工具 (searchTitleByKeyword)，并以条列式的方式回复。
* 当用户给你「讲师名字」时，请优先调用议程工具 (searchTitleBySpeaker)，并以条列式的方式回复。

【行为规则】
1) 请先询问用户想用哪种方式来查询
2) 当用户回复后，再问他关键词或讲师名字
3) 查询后把对应的议程以条列式的方式整理给用户

【输出格式】
- 使用简体中文，简洁明了。
- 列表格式（逐场）：
  - 标题：<title>
  - 讲者：<speaker>
  - Track：<track>

规则：
- 一律使用 AskUser 工具，不要自行结束对话
- 查询一律通过工具，不要自行杜撰资料
- 结果清楚、简短，使用简体中文
```

落地要点：

- `AskUser` 必须作为 Tool 注册到 Agent，否则模型“想问”也没法走工具通道。
- 工具入参建议保持 object schema（`data class Args(...)`），便于模型稳定生成 JSON。
- 如果用户没给出明确意图，systemPrompt 里明确“先 AskUser 让用户选方式”，能显著降低误判与无效工具调用。
这种方式的好处是：systemPrompt 不需要每次请求都拼接在 user 文本里，更清晰、更像“系统指令”。
## 8. 在 BaseKit 里怎么用（推荐路径）

- 现阶段（已实现）：创建 Agent 时注入 systemPrompt（Koog `systemPrompt`），发送时直接 `run(userText, sessionId)`，历史由 ChatMemory 自动加载/保存（不再手动拼接）。
- 工具调用（已实现）：在 `KoogAgentKit.createAgent` 统一注册 Tools（ToolRegistry），由模型按 systemPrompt 的意图规则优先调用工具（例如 `AskUser`、`searchTitleByKeyword`）。
