# Koog LLM Parameters 总结（temperature / maxTokens / toolChoice ...）

本文档总结 Koog 的 LLM 参数（LLM parameters）在 Prompt 与 Agent 中的使用方式，以及写作助手场景下的推荐取值思路。

## 1. 这些参数解决什么问题

LLM 参数用于控制模型输出的“随机性、长度、工具调用方式、输出结构”等行为。它们可以在两类位置设置：

- **Prompt 级别**：对某一次请求生效（更灵活）。
- **Agent 级别**：对该 agent 的所有 run 生效（更统一）。

## 2. 常见参数清单（按影响力排序）

### 2.1 temperature

- 含义：控制输出随机性（越高越发散，越低越稳定）。
- 写作助手建议：
  - “灵感/脑暴”：偏高（更发散）
  - “改写/润色/按模板产出”：偏低（更稳定）

### 2.2 maxTokens

- 含义：限制模型输出 token 上限。
- 写作助手建议：
  - 给“示例正文/多段输出”留足上限，否则会中途截断。
  - 若你发现输出总是过长，可配合更明确的输出结构与 maxTokens 限制。

### 2.3 toolChoice

- 含义：控制工具调用行为（例如自动决定是否调用工具）。
- 建议：
  - 当你要让模型“自主调用工具”时使用自动策略。
  - 当你只想让模型“绝不调用工具/必须调用特定工具”时，使用对应策略（具体取值以 Koog 当前版本的 `LLMParams.ToolChoice` 为准）。

### 2.4 numberOfChoices

- 含义：请求多个候选答案。
- 写作助手建议：
  - 适合“开篇方案给 3 个版本”这类场景。
  - 成本会显著增加，且 UI/落盘需要能容纳多候选。

### 2.5 schema

- 含义：约束模型输出结构（结构化输出）。
- 写作助手建议：
  - 当你需要把输出解析成“人物卡/大纲/章节结构”等结构体时非常关键。
  - 与 “固定格式 Markdown” 不同，schema 更适合做机器可解析的结果。

### 2.6 speculation

- 含义：给模型一个输出格式提示（仅部分模型支持）。
- 写作助手建议：
  - 用于“提示输出是 JSON/列表/表格”等，作为 schema 的轻量替代或补充。

## 3. 在 Prompt 里设置参数（示意）

Koog 支持在 Prompt 构造时带上 `LLMParams`：

```kotlin
val prompt = prompt(
    id = "custom_params",
    params = LLMParams(
        temperature = 0.7,
        numberOfChoices = 1,
        toolChoice = LLMParams.ToolChoice.Auto
    )
) {
    system("你是一个写作助手。")
    user("给我一个开篇冲突切入点。")
}
```

如果你是用 `Prompt.builder("id")`（Java/Builder 风格），原则相同：先 build，再 `withParams(...)`（或 builder 直接接 params，取决于 API）。

## 4. 在 Agent 里设置参数（示意）

部分参数也支持直接在 Agent 构造时指定（例如 temperature），这适合把“风格”固化在 agent 上：

```kotlin
val agent = AIAgent(
    promptExecutor = promptExecutor,
    systemPrompt = "你是一个写作助手。",
    llmModel = model,
    temperature = 0.7
)
```

本仓库 BaseKit 目前将系统提示词下沉到了 `KoogAgentKit.createAgent(..., systemPrompt)`，参数部分建议按“Prompt 级别”优先（因为写作任务在同一个 agent 内也会频繁切换：脑暴/润色/结构化输出）。

## 5. 写作助手参数建议（可直接抄）

这里给一套“经验默认值”方向（不是硬规则，你可以按模型和任务调）：

- **灵感/点子/设定脑暴**
  - temperature：偏高
  - numberOfChoices：可选 2~3（用于多方案）
  - maxTokens：中等偏高
- **开篇三章/章节骨架（偏结构化）**
  - temperature：中等
  - maxTokens：偏高（避免截断）
  - schema：可选（若你要落盘为结构体）
- **润色/改写/按模板输出**
  - temperature：偏低
  - maxTokens：中等
  - toolChoice：倾向禁止或自动（取决于你是否允许工具参与）

## 6. 常见问题

- 输出不稳定：优先降低 temperature，并强化输出结构要求（必要时用 schema）。
- 输出被截断：提高 maxTokens，同时减少无关上下文（必要时配合 history compression）。
- 工具调用混乱：完善 tool 描述与参数描述；必要时用 toolChoice 限制调用方式。

