# Koog Backend framework integrations 总结（Spring Boot / Ktor）

本文档沉淀 Koog 在服务端（Backend）框架里的集成方式，主要覆盖 Spring Boot Starter 与 Ktor 插件。

## 1. Spring Boot（koog-spring-boot-starter）

### 1.1 适合什么场景

- 你已经是 Spring Boot 3（Java 17+）体系
- 想要通过配置文件自动注入各 provider 的 `PromptExecutor` / `MultiLLMPromptExecutor`
- 想在 Controller/Service 里直接调用 LLM 或跑 agent

### 1.2 依赖

```
implementation("ai.koog:koog-spring-boot-starter:$koogVersion")
```

### 1.3 配置 Provider（application.properties / application.yml）

关键点：

- `ai.koog.<provider>.api-key` + `ai.koog.<provider>.enabled`
- 建议 API key 走环境变量（避免进仓库）
- Ollama 需要显式 enabled

### 1.4 使用方式（Controller 注入 PromptExecutor）

- 直接 `executor.execute(prompt, model)`
- 多 provider fallback：遍历候选模型，失败就切下一个（或使用 MultiLLMPromptExecutor 的 fallback settings）

## 2. Ktor（koog-ktor 插件）

### 2.1 适合什么场景

- 你是 Kotlin/Ktor 服务端
- 希望在路由里直接拿到 `llm()` 或 `aiAgent(...)`，无需手动 wiring client/executor
- 需要 streaming、moderation、MCP tools 等服务端能力

### 2.2 依赖

```
implementation("ai.koog:koog-ktor:$koogVersion")
```

### 2.3 配置 Provider（application.yaml / application.conf）

Ktor 插件读取 `koog.<provider>` 配置（示意）：

```yaml
koog:
  openai:
    apikey: ${OPENAI_API_KEY}
    baseUrl: https://api.openai.com
  ollama:
    enable: true
    baseUrl: http://localhost:11434
  llm:
    fallback:
      provider: openai
      model: openai.chat.gpt4_1
```

### 2.4 安装插件与路由中调用

```kotlin
fun Application.module() {
    install(Koog)
    routing {
        post("/chat") {
            val input = call.receiveText()
            val output = aiAgent(
                strategy = reActStrategy(),
                model = OpenAIModels.Chat.GPT4_1,
                input = input
            )
            call.respondText(output)
        }
    }
}
```

### 2.5 路由里直接用 LLM（execute / executeStreaming / moderate）

- `llm().execute(prompt(...), model)`
- `llm().executeStreaming(prompt(...), model)`：流式输出
- `llm().moderate(prompt(...), OpenAIModels.Moderation.Omni)`：内容审核

### 2.6 在 Ktor 里使用 MCP tools（JVM-only）

Ktor 插件的 `agentConfig { mcp { ... } }` 可直接把 MCP server 的工具接进默认 tool registry。

## 3. 与 BaseKit（Android）对比：你需要注意什么

- Spring Boot / Ktor 集成的核心是“**配置 -> 自动创建 executor/client -> 依赖注入/插件提供 API**”
- BaseKit 当前是 Android 侧手工封装（`KoogAgentKit`），理念相同但位置不同：Android 没有服务端配置文件与 DI starter，需要你自己做“配置存储 + agent 创建 + prompt 编排”。

另见：

- `KOOG_MCP.md`（MCP tools 集成）
- `KOOG_CONTENT_MODERATION.md`（moderate 接入与策略）
- `KOOG_FEATURES.md`（Tracing/OpenTelemetry 等可观测能力）

