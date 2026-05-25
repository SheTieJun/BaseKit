# Koog Model capabilities（LLMCapability / LLModel）总结

本文档沉淀 Koog 的 Model capabilities（模型能力）体系：`LLMCapability` 能力枚举/层级、`LLModel` 模型描述，以及如何基于能力做模型选择与功能降级。

## 1. Model capabilities 是什么

Koog 以 provider-agnostic（不绑厂商）的方式描述模型“能做什么”，核心是两类抽象：

- `LLMCapability`：能力集合（温度、工具、结构化输出、视觉、音频、Moderation 等）。
- `LLModel`：某个具体模型的描述（provider、id、capabilities、contextLength 等）。

Koog 可以用这些能力在运行时做：

- 功能启用/禁用（例如模型不支持 Tools 就不能走 tool calling）
- 输出方式选择（例如 structured output 会根据能力选择“原生支持”或“提示词注入 + 纠错”）

## 2. LLMCapability 能力分类（按 Koog 文档口径）

### 2.1 Core capabilities

- `Speculation`：探索式输出提示（更偏“发散”）。
- `Temperature`：随机性控制。
- `Tools`：支持工具调用。
- `ToolChoice`：控制工具调用模式（自动/只工具/只文本/强制指定工具等，取决于模型实现）。
- `MultipleChoices`：一次返回多个候选答案。

### 2.2 Media processing capabilities

- `Vision.Image` / `Vision.Video`：视觉能力。
- `Audio`：音频相关能力。
- `Document`：文档类输入输出处理能力。

### 2.3 Text processing capabilities

- `Embed`：向量 embedding。
- `Completion`：补全/生成能力（文本生成相关）。
- `PromptCaching`：提示词缓存能力。
- `Moderation`：内容审核能力（harm 分类）。

### 2.4 Schema capabilities

- `Schema.JSON.Basic`：基础 JSON schema 能力（轻量）。
- `Schema.JSON.Standard`：更完整的 JSON schema 能力（复杂结构更稳）。

## 3. LLModel（模型描述）如何定义

Koog 用 `LLModel` 描述一个模型：

- `provider`：LLMProvider（OpenAI/Anthropic/Google/DeepSeek/…）
- `id`：模型标识（例如 `gpt-4-turbo`、`claude-3-opus`、`llama-3-2`）
- `capabilities`：能力列表
- `contextLength`：上下文长度（token）
- `maxOutputTokens`：最大输出 token（可选）

示意（官方风格）：

```kotlin
val basicModel = LLModel(
    provider = LLMProvider.OpenAI,
    id = "gpt-4-turbo",
    capabilities = listOf(
        LLMCapability.Temperature,
        LLMCapability.Tools,
        LLMCapability.Schema.JSON.Standard
    ),
    contextLength = 128_000
)
```

## 4. 如何判断模型是否支持某能力

Koog 提供 `supports(...)`/`contains` 一类判断（以当前 API 为准）：

```kotlin
val supportsTools = basicModel.supports(LLMCapability.Tools)
val jsonCapability = basicModel.capabilities
    ?.filterIsInstance<LLMCapability.Schema.JSON>()
    ?.firstOrNull()
```

## 5. 写作助手的“按能力选型”建议

如果你要把写作助手做成“可持续迭代的工程”，建议先按能力把需求分层：

- 必选：`Temperature`（可控的稳定/发散）、足够的 `contextLength`
- 强推荐：`Schema.JSON.Standard`（人物卡/大纲/章节结构更稳定）、`Tools`（写作工具链）
- 体验增强：`MultipleChoices`（多方案）、`PromptCaching`（重复改稿降成本）、`Streaming`（实时显示，通常与 client/SDK 能力相关）
- 合规：`Moderation`（输入/输出/工具内容审核）

## 6. 与 BaseKit 当前实现的关系

BaseKit 的 `KoogAgentKit.createAgent(...)` 支持通过 modelName 选择模型，后续你若要做“能力驱动的 UI 降级”，可以在设置页/运行前：

- 读取模型能力（或按固定表维护）
- 决定是否开启：工具调用/结构化输出/多候选/流式输出/审核

另见：

- `KOOG_LLM_PARAMETERS.md`（参数层面的调优：temperature/toolChoice/maxTokens…）
- `KOOG_PROMPTS.md`（Prompt 与 toolCall/toolResult 的消息组织）

