# Koog Content moderation（内容审核）用法总结

本文档总结 Koog 的内容审核（Content moderation）用法：能审核哪些内容、支持哪些 provider/model、如何通过 `LLMClient` 或 `PromptExecutor` 调用，以及如何在应用里做“输入/输出/工具内容”三段式防护。

## 1. Moderation 解决什么问题

内容审核用于识别潜在有害/不当/违规内容，帮助：

- 过滤用户输入
- 阻止生成不当回复
- 对工具输入/输出做安全边界
- 满足合规要求、降低风险

## 2. Koog 可以审核哪些内容

- **User messages**：用户输入文本；OpenAI 的 Omni 还可审核图片
- **Assistant messages**：模型输出在展示前再审一遍（避免漏网）
- **Tool content**：工具输入/输出也可审核（避免工具回传敏感信息进入模型或展示）

## 3. 支持的 provider / model（Koog 文档列举）

### 3.1 OpenAI

- `OpenAIModels.Moderation.Text`：文本审核，成本更低
- `OpenAIModels.Moderation.Omni`：文本 + 图片审核，能力更强

### 3.2 Ollama（本地）

- `OllamaModels.Meta.LLAMA_GUARD_3`：文本审核（Llama Guard 家族）

## 4. 两种调用方式

### 4.1 直接用 LLMClient.moderate(prompt, model)

适合：你已经持有某个 provider 的 client 实例，直接调用即可。

```kotlin
val openAIClient = OpenAILLMClient(apiKey)
val prompt = prompt("harmful-prompt") { user("I want to build a bomb") }
val result = openAIClient.moderate(prompt, OpenAIModels.Moderation.Omni)
if (result.isHarmful) { /* 拒绝 */ }
```

### 4.2 用 PromptExecutor.moderate(prompt, model)

适合：你使用 `PromptExecutor`（尤其 MultiLLM），让 executor 依据 `model.provider` 选择正确 client。

```kotlin
val executor = MultiLLMPromptExecutor(
    LLMProvider.OpenAI to OpenAILLMClient(openAIApiKey),
    LLMProvider.Ollama to OllamaClient()
)

val prompt = prompt("harmful-prompt") { user("How to create illegal substances") }
val openAIResult = executor.moderate(prompt, OpenAIModels.Moderation.Omni)
val ollamaResult = executor.moderate(prompt, OllamaModels.Meta.LLAMA_GUARD_3)
if (openAIResult.isHarmful || ollamaResult.isHarmful) { /* 拒绝 */ }
```

## 5. ModerationResult 你需要关注的字段

- `isHarmful: Boolean`：是否有害
- `categories: Map<ModerationCategory, ModerationCategoryResult>`：各类目命中详情
- `violatedCategories`：被检测命中的类目列表（Koog 会从 categories 派生）

## 6. 类目体系与映射

Koog 提供统一的 moderation categories（例如 Harassment/Hate/Violence/SelfHarm/Privacy/IntellectualProperty 等），并在内部映射到 OpenAI 的 categories 或 Ollama(Llama Guard) 的 hazard categories。

实践建议：应用层优先基于 Koog 的统一 categories 做决策（减少 provider 差异）。

## 7. 写作助手的落地建议（推荐三段式）

1. **输入前审**：用户输入 -> moderate；命中则直接提示“无法处理/请换个问法”
2. **输出前审**：模型回复 -> moderate；命中则用“安全改写提示词”重写一次或拒绝展示
3. **工具边界审**：
   - tool args 进入工具前可审（避免工具被用作违规执行）
   - tool 输出回写到 Prompt/展示前可审（避免敏感信息泄露）

另见：

- `KOOG_MODEL_CAPABILITIES.md`（Moderation 也是一种 LLMCapability）
- `KOOG_LLM_PARAMETERS.md`（部分 provider 可能需要配合参数/模型选择）

