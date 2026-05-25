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

本仓库 BaseKit 当前就是这种方式（为了快速闭环）：

- systemPrompt：由设置页配置，并在创建 Agent 时通过 Koog 的 `systemPrompt` 注入
- history：发送时截断后拼接
- tool：先运行工具，再把结果交给模型扩写（也属于“外部编排”）

### 6.2 在创建 Agent 时设置 systemPrompt（更标准）

Koog 支持在构建 Agent 时设置 `systemPrompt`，例如（示意）：

```java
AIAgent<String, String> agent = AIAgent.builder()
    .promptExecutor(simpleOpenAIExecutor(apiKey))
    .systemPrompt("You are a helpful assistant. Answer concisely.")
    .llmModel(OpenAIModels.Chat.GPT4o)
    .build();
```

这种方式的好处是：systemPrompt 不需要每次请求都拼接在 user 文本里，更清晰、更像“系统指令”。

## 7. 在 BaseKit 里怎么用（推荐路径）

- 现阶段（已实现）：创建 Agent 时注入 systemPrompt（Koog `systemPrompt`），发送时只拼接 history + userInput。
- 再下一阶段：把“工具调用轨迹”用 toolCall/toolResult 作为 Prompt 消息写进去，让模型更可控地利用工具结果。
