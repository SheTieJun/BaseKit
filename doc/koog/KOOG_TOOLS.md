# Koog Tools 使用总结（Tool / SimpleTool / ToolRegistry）

本文档总结 Koog 的 Tools 能力：工具的类型、推荐实现方式、注册方式（ToolRegistry）、以及在 Agent 中的使用方式与演进路线。

> 参考：Koog 官方 tools 文档（tools overview / built-in tools / class-based tools / annotation-based tools）。

## 1. Tools 是什么

Tools 用于让 Agent 具备“可确定执行”的能力（读取数据、调用 API、计算、查询等），并在需要时把执行结果返回给 LLM 继续推理。

Koog 的典型工具工作流：

1. 实现工具（内置 / 注解 / 类工具）。
2. 把工具加入 ToolRegistry。
3. 把 ToolRegistry 传给 Agent。
4. Agent 在运行中触发工具调用，执行工具并把 tool result 写回上下文，直到没有 tool call 为止。

## 2. Koog 的工具类型（3 类）

### 2.1 内置工具（Built-in tools）

用途：Koog 提供的一些通用工具，覆盖常见的“Agent-用户交互/会话管理”等场景。

特点：

- 直接可用
- 适合做基础能力补齐

### 2.2 注解工具（Annotation-based tools）

用途：把一个函数/方法暴露为工具，让 LLM 可调用。

特点：

- Java 侧推荐（因为 Java 不能很好地继承 Kotlin 的 suspend Tool 类）
- 通过 `@Tool`、`@LLMDescription` 描述工具与参数含义
- 反射注册到 ToolRegistry

### 2.3 类工具（Class-based tools）

用途：你需要更强控制力（参数类型、元信息、执行逻辑、返回类型、序列化方式）。

特点：

- Kotlin 侧推荐
- 你可以继承 `Tool<Args, Result>` 或 `SimpleTool<Args>`（文本输出更方便）

## 3. Kotlin 里怎么实现工具（推荐）

Koog 提供两种 Kotlin 基类：

- `Tool<Args, Result>`：需要返回非文本结果、或要更完整控制时使用。
- `SimpleTool<Args>`：返回文本结果时使用（最常用）。

本仓库示例（写作灵感工具）：

- `app/src/main/java/shetj/me/base/func/koog/tools/InspirationTool.kt`
- 继承 `SimpleTool<String>`，输入关键词，输出“冲突切入点”文本。

## 4. ToolRegistry：如何让 Agent “看得见”工具

Koog 要求：工具必须注册到 `ToolRegistry`，Agent 才能调用到它。

Kotlin 示例（示意）：

```kotlin
val toolRegistry = ToolRegistry {
    tools(myTool)
}
```

Java 示例（示意）：

```java
ToolRegistry registry = ToolRegistry.builder()
    .tools(myToolSet)
    .build();
```

## 5. 工具在 Agent 里的用法（两种路线）

### 5.1 “框架内工具调用”（标准路线）

目标：让 LLM 决定何时调用工具；你的 agent strategy 识别 tool call，执行工具并把结果返回给 LLM，然后继续循环，直到没有 tool call。

适用：

- 你希望模型自己“按需调用工具”
- 工具较多，需要模型自己选

注意：

- 需要你有一个能处理 tool-call 循环的策略（Functional/Graph/Planner 都可以实现）。

### 5.2 “应用侧规则触发工具”（工程折中）

目标：由 App 侧规则判断是否调用工具；工具结果直接展示给用户，或再交给模型二段式扩写。

适用：

- 你希望“高度可控”
- 想快速闭环，不先引入复杂 tool-call 自动化

本仓库目前就是这种方式：

- 手动指令 `/inspiration xxx` 或自动命中关键词 -> 调用 `InspirationTool.execute()`
- 若当前配置了可用 Agent -> 把工具结果再交给模型扩写（工具 -> 模型二段式）

## 6. 最佳实践（写作类工具的经验）

- 工具描述要清晰：名称、输入、输出边界明确（让 LLM/用户都能理解）。
- 参数类型尽量稳定：字符串/结构化对象，避免随意变更导致兼容问题。
- 输出尽量结构化：即使返回文本，也建议用固定格式（标题/要点/列表），便于后续二段式扩写。
- 工具调用要可追踪：建议在 prompt 里用 toolCall/toolResult（见 `KOOG_PROMPTS.md`）记录轨迹，提升可控性与可解释性。

## 7. 推荐演进路线（结合 BaseKit）

1. 现在（已实现）：规则触发 + 二段式（工具先产出，模型再扩写）。
2. 下一步：把“工具路由/触发/二段式编排”从 ViewModel 下沉到 Agent 层（避免 ViewModel 膨胀）。
3. 再下一步：使用 ToolRegistry + toolCall/toolResult，让 LLM 在策略内自主调用工具（更接近 Koog 标准范式）。

