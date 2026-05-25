# Koog Features 使用总结（内置 Features / Tracing / OpenTelemetry / 自定义 Feature）

本文档总结 Koog 的 Features 能力：它能做什么、怎么安装/配置、以及常见的观测（Tracing / OpenTelemetry）与自定义 Feature 的基本骨架。

> Features 是运行时可插拔的增强组件：可拦截/修改 agent 行为、记录执行过程、接入监控/链路追踪等。

## 1. Features 能解决什么问题

- **观测与调试**：记录一次 agent run 内发生了什么（LLM 请求、工具执行、状态变化），便于定位质量问题、成本问题与失败原因。
- **行为增强**：在不改 agent 核心逻辑的情况下，统一加上“日志/统计/指标/鉴权/限流/脱敏”等横切能力。
- **持久化与记忆**：把对话历史或长期记忆存下来，跨 run 复用（依赖 Koog 提供的 memory/persistence 类 feature）。
- **集成外部平台**：通过 OpenTelemetry exporter 把 trace 数据输出到 Datadog / Langfuse 等平台。

## 2. 安装方式（核心概念）

Koog 通过在创建 Agent 时安装 Feature（install）来启用能力。一个 Feature 通常包含：

- **Config**：可配置项（FeatureConfig）。
- **Feature 实例**：运行期对象，用于读 config、处理事件或拦截 pipeline。
- **安装逻辑**：把 Feature “装入” agent 的执行管线（pipeline）。

## 3. Koog 内置 Features 全览（6 个）

下表是 Koog 官方对内置 Features 的常见分类口径，适合用来做“你要不要接、先接哪个”的决策：

| Feature | 一句话 | 写作助手案例 |
| --- | --- | --- |
| Event handling | 监听/响应 agent 执行过程中的事件 | 在“工具执行后/LLM 返回前”统一做脱敏、打点、限流 |
| Tracing | 记录一次 agent run 的详细轨迹 | 复盘“为什么会这样写”、定位失败节点与耗时 |
| Chat memory | 跨 run 保存/恢复对话历史 | 写作助手持续跟写同一本书，不必每次手动拼接历史 |
| Long-term memory | 持久化更稳定的知识并可检索注入 | 人物卡/世界观/禁忌设定长期生效，按需召回 |
| Agent persistence | 保存/恢复 agent 的内部状态/工作进度 | 章节生成工作流可断点续跑（更适合 Planner/Graph） |
| OpenTelemetry | 导出 traces 到外部观测平台 | 把 LLM/Tool spans 输出到 Datadog/Langfuse 做监控分析 |

## 4. 内置能力：Event handling / Tracing / Memory / Persistence / OpenTelemetry

本节给每个内置 Feature 补一段“适用场景 + 案例”。代码只做示意（接口名以 Koog 当前版本为准），重点是理解用法与落点。

### 4.1 Event handling（事件处理）

作用：在 agent 执行过程中监听特定事件并响应（开始运行、LLM 调用、工具调用、异常等）。

写作助手案例：

- 工具输出脱敏：如果工具返回了可能包含隐私/Key 的信息，在写入 Prompt 之前统一清洗。
- 统一统计：记录每次 run 是否发生 toolCall、总耗时、失败率，用于迭代提示词/工具。

示意思路（伪代码）：

```kotlin
agent.install(EventHandling) {
    onToolResult { toolName, output -> sanitize(output) }
    onRunFinished { metrics -> report(metrics) }
}
```

### 4.2 Tracing（链路追踪）

用途：捕获一次 agent run 从开始到结束的关键信息（例如 LLM 调用、工具调用、状态变化），用于调试与观测。

写作助手案例：

- 复盘一段“写崩”的章节：定位是 prompt 设计问题、历史上下文不足、还是工具输出质量问题。
- 发现性能瓶颈：是模型慢，还是工具慢，还是历史过长导致 token 膨胀。

### 4.3 Chat memory（会话记忆）

用途：在多次 `agent.run(...)` 之间保存与恢复对话历史，让 agent 具备跨 run 的连续对话能力。

写作助手案例：

- 让“同一本书”的上下文天然延续：人物关系、上章结尾悬念无需重复粘贴。
- 降低你应用侧 history 拼装复杂度：从“ViewModel 拼接”演进为“Feature 管理”。

### 4.4 Long-term memory（长期记忆）

用途：持久化更稳定的知识/偏好/事实，并在需要时检索注入到 prompt（类似长期知识库/用户画像）。

写作助手案例：

- 人物卡：姓名、外貌、口癖、底线、能力边界。
- 世界观：货币体系、修炼境界、科技水平、禁忌规则。
- 风格偏好：节奏、叙事视角、禁用表达、常用钩子类型。

### 4.5 Agent persistence（Agent 持久化）

用途：保存并恢复 agent 的内部状态/工作进度（比“只存对话文本”更强，适合状态机/工作流）。

写作助手案例：

- “章节生成”做成多步：设定确认 -> 大纲 -> 分章 -> 正文 -> 自检 -> 改写。
- 中断后继续：例如生成到第 2 步退出，下次可恢复到第 3 步继续跑。

### 4.6 OpenTelemetry（遥测导出）

用途：生成、收集并导出 telemetry（traces），把 agent activity 作为 spans 输出，并组成一次完整 trace。

写作助手案例：

- 线上监控：按 `env/version/service` 对比模型质量与成本变化。
- 会话聚合：按 sessionId 聚合一次写作会话的所有 traces，便于复盘与评分。

#### Datadog exporter（示意）

要点：

- `resourceAttributes` 会附加到每个 span 上，便于在 Datadog 侧按 env/version/service 分组过滤。
- 常见字段：`env`、`service.name`、`version`。

#### Langfuse exporter（示意）

要点：

- `traceAttributes` 贴在根 trace 上，Langfuse 用它做 session/env/tags 等能力。
- 常见字段：`langfuse.session.id`、`langfuse.environment`、`langfuse.trace.tags`（数组）。

## 5. 自定义 Feature：最小骨架

Koog 提供多种 Feature 接口，按 agent 类型划分：

- `AIAgentGraphFeature`：图策略 agent。
- `AIAgentFunctionalFeature`：函数式 agent。
- `AIAgentPlannerFeature`：planner agent。

如果你希望一个 Feature 可同时用于 graph/functional/planner，需要同时实现这些接口。

最小骨架（示意，接口名以 Koog 当前版本为准）：

```kotlin
class MyFeature(val someProperty: String) {
    class Config : FeatureConfig() {
        var configProperty: String = "default"
    }

    companion object Feature :
        AIAgentGraphFeature<Config, MyFeature>,
        AIAgentFunctionalFeature<Config, MyFeature>,
        AIAgentPlannerFeature<Config, MyFeature> {

        override val key = createStorageKey<MyFeature>("my-feature")

        override fun createInitialConfig(agentConfig: AIAgentConfig): Config = Config()

        override fun install(config: Config, pipeline: AIAgentGraphPipeline): MyFeature {
            val feature = MyFeature(config.configProperty)
            pipeline.interceptAgentStarting(this) { }
            return feature
        }

        override fun install(config: Config, pipeline: AIAgentFunctionalPipeline): MyFeature { /* ... */ }

        override fun install(config: Config, pipeline: AIAgentPlannerPipeline): MyFeature { /* ... */ }
    }
}
```

## 6. 与 Tools 的关系

- Tools 解决“能做什么”（确定性能力）。
- Features 解决“怎么运行得更好”（观测、拦截、增强、合规、统计）。

在生产实践中通常组合使用：

- 用 Tools 暴露能力给 LLM
- 用 Features 做 tracing/metrics/logging/脱敏/限流

## 7. 建议：BaseKit 的落地方式

如果要在 BaseKit 的 Koog 写作助手里引入 Features，建议优先顺序：

1. **先做轻量 logging feature**：记录每次 run 的 prompt id、模型、耗时、是否发生 toolCall。
2. **再接 OpenTelemetry**：需要外部平台再开 exporter（开发环境可关）。
3. **最后做合规 feature**：对 prompt、工具输出进行脱敏/黑名单过滤（避免把敏感信息发给模型或写入日志）。
