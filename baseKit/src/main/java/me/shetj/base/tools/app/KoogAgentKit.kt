package me.shetj.base.tools.app

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.chatMemory.feature.ChatHistoryProvider
import ai.koog.agents.chatMemory.feature.ChatMemory
import ai.koog.agents.core.agent.AIAgentBuilder
import ai.koog.agents.core.annotation.ExperimentalAgentsApi
import ai.koog.agents.longtermmemory.feature.LongTermMemory
import ai.koog.agents.longtermmemory.retrieval.SimilaritySearchStrategy
import ai.koog.agents.longtermmemory.retrieval.augmentation.SystemPromptAugmenter
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicClientSettings
import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.bedrock.BedrockModels
import ai.koog.prompt.executor.clients.deepseek.DeepSeekClientSettings
import ai.koog.prompt.executor.clients.deepseek.DeepSeekLLMClient
import ai.koog.prompt.executor.clients.deepseek.DeepSeekModels
import ai.koog.prompt.executor.clients.google.GoogleClientSettings
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.mistralai.MistralAIClientSettings
import ai.koog.prompt.executor.clients.mistralai.MistralAILLMClient
import ai.koog.prompt.executor.clients.mistralai.MistralAIModels
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.clients.openrouter.OpenRouterModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.executor.llms.all.simpleBedrockExecutorWithBearerToken
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleMistralAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenRouterExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.rag.base.TextDocument
import ai.koog.rag.base.storage.SearchStorage
import ai.koog.rag.base.storage.WriteStorage
import ai.koog.rag.base.storage.search.SimilaritySearchRequest
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * Koog AI Agent 工具类
 * 支持多种 LLM 提供商：OpenAI, Anthropic, Google, DeepSeek, OpenRouter, Bedrock, Mistral, Ollama
 */
object KoogAgentKit {

    /**
     * LLM 提供商枚举
     */
    enum class Provider {
        OPENAI,
        ANTHROPIC,
        GOOGLE,
        DEEPSEEK,
        OPENROUTER,
        BEDROCK,
        MISTRAL,
        OLLAMA,
        CUSTOM
    }

    /**
     * 根据模型名称字符串创建 LLModel
     */
    fun createModel(provider: Provider, modelName: String): LLModel? {
        if (modelName.isBlank()) return null
        val llmProvider = when (provider) {
            Provider.OPENAI -> LLMProvider.OpenAI
            Provider.ANTHROPIC -> LLMProvider.Anthropic
            Provider.GOOGLE -> LLMProvider.Google
            Provider.DEEPSEEK -> LLMProvider.DeepSeek
            Provider.OPENROUTER -> LLMProvider.OpenRouter
            Provider.BEDROCK -> LLMProvider.Bedrock
            Provider.MISTRAL -> LLMProvider.MistralAI
            Provider.OLLAMA -> LLMProvider.Ollama
            Provider.CUSTOM -> LLMProvider.OpenAI
        }
        // CUSTOM 使用 OpenAI 兼容接口，需要 OpenAIEndpoint.Completions 能力
        val caps = if (provider == Provider.CUSTOM) {
            listOf(LLMCapability.OpenAIEndpoint.Completions)
        } else {
            listOf(LLMCapability.Completion)
        }
        return LLModel(
            provider = llmProvider,
            id = modelName,
            capabilities = caps
        )
    }

    /**
     * 创建 Koog Agent
     * @param provider LLM 提供商
     * @param apiKey API Key（Ollama 不需要）
     * @param model LLM 模型对象（可选）
     * @param modelName 模型名称字符串（可选，会优先使用）
     * @param baseUrl 自定义 API Base URL（可选，不传则使用默认值）
     * @param systemPrompt 系统提示词（建议用于定义助手身份、输出风格与边界）
     * @param chatHistoryProvider ChatMemory 的历史存储实现（用于“短期对话上下文”）
     * @param chatWindowSize ChatMemory 的窗口大小（限制对话历史长度）
     * @param userId 长期记忆 namespace 的用户维度（默认 local；建议业务传入真实用户 ID）
     * @param agentId 长期记忆 namespace 的智能体维度（建议传入当前 Agent 配置 ID）
     * @param longTermSearchStorage LongTermMemory 检索存储（RAG：检索相关记忆注入 Prompt）
     * @param longTermWriteStorage LongTermMemory 写入存储（用于“显式写入”或开启自动入库）
     * @param longTermNamespace 长期记忆 namespace（优先使用；为空则使用 userId+agentId 组合）
     * @param enableLongTermIngestion 是否启用“自动入库”（默认关闭，避免对话噪声污染长期记忆）
     * @param longTermTopK 每次检索返回的记忆条数上限
     * @param longTermSimilarityThreshold 相似度阈值（本地 Room MVP 可按 0/1 命中近似）
     */
    fun createAgent(
        provider: Provider,
        apiKey: String? = null,
        model: LLModel? = null,
        modelName: String? = null,
        baseUrl: String? = null,
        systemPrompt: String? = null,
        chatHistoryProvider: ChatHistoryProvider? = null,
        chatWindowSize: Int = 50,
        userId: String = "local",
        agentId: String? = null,
        longTermSearchStorage: SearchStorage<TextDocument, SimilaritySearchRequest>? = null,
        longTermWriteStorage: WriteStorage<TextDocument>? = null,
        longTermNamespace: String? = null,
        enableLongTermIngestion: Boolean = false,
        longTermTopK: Int = 5,
        longTermSimilarityThreshold: Double = 0.0
    ): AIAgent<String, String>? {
        val resolvedModel = modelName?.takeIf { it.isNotBlank() }?.let { createModel(provider, it) } ?: model
        val sp = systemPrompt?.trim().orEmpty()
        // 长期记忆 namespace 规则：
        // 1) 业务显式传 longTermNamespace 时优先使用
        // 2) 否则使用 userId + agentId 拼接，确保不同用户、不同 Agent 的长期记忆隔离
        val ltmNamespace = longTermNamespace?.trim().takeIf { !it.isNullOrBlank() }
            ?: agentId?.trim().takeIf { !it.isNullOrBlank() }?.let { "${userId}_${it}" }
        return try {
            when (provider) {
                Provider.OPENAI -> {
                    val key = apiKey ?: System.getenv("OPENAI_API_KEY")
                        ?: error("OPENAI_API_KEY 未设置")
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleOpenAIExecutor(key)
                    } else {
                        val client = OpenAILLMClient(key, OpenAIClientSettings(baseUrl))
                        MultiLLMPromptExecutor(LLMProvider.OpenAI to client)
                    }
                    AIAgent.builder()
                        .promptExecutor(executor)
                        .llmModel(resolvedModel ?: OpenAIModels.Chat.GPT4o)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.ANTHROPIC -> {
                    val key = apiKey ?: System.getenv("ANTHROPIC_API_KEY")
                        ?: error("ANTHROPIC_API_KEY 未设置")
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleAnthropicExecutor(key)
                    } else {
                        val client = AnthropicLLMClient(key, AnthropicClientSettings(baseUrl = baseUrl))
                        MultiLLMPromptExecutor(LLMProvider.Anthropic to client)
                    }
                    AIAgent.builder()
                        .promptExecutor(executor)
                        .llmModel(resolvedModel ?: AnthropicModels.Sonnet_4_5)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.GOOGLE -> {
                    val key = apiKey ?: System.getenv("GOOGLE_API_KEY")
                        ?: error("GOOGLE_API_KEY 未设置")
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleGoogleAIExecutor(key)
                    } else {
                        val client = GoogleLLMClient(key, GoogleClientSettings(baseUrl = baseUrl))
                        MultiLLMPromptExecutor(LLMProvider.Google to client)
                    }
                    AIAgent.builder()
                        .promptExecutor(executor)
                        .llmModel(resolvedModel ?: GoogleModels.Gemini2_5Pro)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.DEEPSEEK -> {
                    val key = apiKey ?: System.getenv("DEEPSEEK_API_KEY")
                        ?: error("DEEPSEEK_API_KEY 未设置")
                    val client = if (baseUrl.isNullOrBlank()) {
                        DeepSeekLLMClient(key)
                    } else {
                        DeepSeekLLMClient(key, DeepSeekClientSettings(baseUrl = baseUrl))
                    }
                    AIAgent.builder()
                        .promptExecutor(MultiLLMPromptExecutor(mapOf(DeepSeekModels.DeepSeekChat.provider to client)))
                        .llmModel(resolvedModel ?: DeepSeekModels.DeepSeekChat)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.OPENROUTER -> {
                    val key = apiKey ?: System.getenv("OPENROUTER_API_KEY")
                        ?: error("OPENROUTER_API_KEY 未设置")
                    AIAgent.builder()
                        .promptExecutor(simpleOpenRouterExecutor(key))
                        .llmModel(resolvedModel ?: OpenRouterModels.GPT4o)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.CUSTOM -> {
                    val key = apiKey ?: baseUrl ?: error("CUSTOM 需要 API Key 或 baseUrl")
                    val url = baseUrl ?: "https://api.openai.com"
                    val client = OpenAILLMClient(key, OpenAIClientSettings(baseUrl = url))
                    AIAgent.builder()
                        .promptExecutor(MultiLLMPromptExecutor(LLMProvider.OpenAI to client))
                        .llmModel(resolvedModel ?: OpenAIModels.Chat.GPT4o)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.BEDROCK -> {
                    val key = apiKey ?: System.getenv("BEDROCK_API_KEY")
                        ?: error("BEDROCK_API_KEY 未设置")
                    AIAgent.builder()
                        .promptExecutor(simpleBedrockExecutorWithBearerToken(key))
                        .llmModel(resolvedModel ?: BedrockModels.AnthropicClaude4_5Sonnet)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.MISTRAL -> {
                    val key = apiKey ?: System.getenv("MISTRAL_API_KEY")
                        ?: error("MISTRAL_API_KEY 未设置")
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleMistralAIExecutor(key)
                    } else {
                        val client = MistralAILLMClient(key, MistralAIClientSettings(baseUrl = baseUrl))
                        MultiLLMPromptExecutor(LLMProvider.OpenAI to client)
                    }
                    AIAgent.builder()
                        .promptExecutor(executor)
                        .llmModel(resolvedModel ?: MistralAIModels.Chat.MistralMedium31)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }

                Provider.OLLAMA -> {
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleOllamaAIExecutor()
                    } else {
                        val client = OllamaClient(baseUrl)
                        MultiLLMPromptExecutor(LLMProvider.Ollama to client)
                    }
                    AIAgent.builder()
                        .promptExecutor(executor)
                        .llmModel(resolvedModel ?: OllamaModels.Meta.LLAMA_3_2)
                        .apply { if (sp.isNotEmpty()) systemPrompt(sp) }
                        .apply { installChatMemory(chatHistoryProvider, chatWindowSize) }
                        // LongTermMemory：长期记忆（RAG 检索注入 Prompt）
                        .apply { installLongTermMemory(longTermSearchStorage, longTermWriteStorage, ltmNamespace, enableLongTermIngestion, longTermTopK, longTermSimilarityThreshold) }
                        .build()
                }
            }
        } catch (e: Exception) {
            Timber.tag("KoogAgentKit").e(e, "创建 Agent 失败: ${e.message}")
            null
        }
    }

    /**
     * 运行 Agent（同步阻塞）
     * @param agent Agent 实例
     * @param prompt 提示词
     * @return Agent 响应结果
     */
    suspend fun runAgent(
        agent: AIAgent<String, String>,
        prompt: Prompt,
        sessionId: String? = null
    ): String? {
        return try {
                val userText = prompt.toString()
                if (sessionId.isNullOrBlank()) agent.run(userText) else agent.run(userText, sessionId)
        } catch (e: Exception) {
            Timber.tag("KoogAgentKit").e(e, "运行 Agent 失败: ${e.message}")
            null
        }
    }

    suspend fun runAgent(
        agent: AIAgent<String, String>,
        userText: String,
        sessionId: String? = null
    ): String? {
        return try {
            if (sessionId.isNullOrBlank()) agent.run(userText) else agent.run(userText, sessionId)
        } catch (e: Exception) {
            Timber.tag("KoogAgentKit").e(e, "运行 Agent 失败: ${e.message}")
            null
        }
    }

    /**
     * 历史记录
     */
    private fun AIAgentBuilder.installChatMemory(
        provider: ChatHistoryProvider?,
        windowSize: Int
    ) {
        install(ChatMemory) { config ->
            if (provider != null){
                config.chatHistoryProvider = provider
            }
            config.windowSize(windowSize)
            config.filterMessages { it is Message.User || it is Message.Assistant }
        }
    }

    @OptIn(ExperimentalAgentsApi::class)
    private fun AIAgentBuilder.installLongTermMemory(
        searchStorage: SearchStorage<TextDocument, SimilaritySearchRequest>?,
        writeStorage: WriteStorage<TextDocument>?,
        namespace: String?,
        enableIngestion: Boolean,
        topK: Int,
        similarityThreshold: Double
    ) {
        if (searchStorage == null || namespace.isNullOrBlank()) return
        install(LongTermMemory) { config ->
            // retrieval（检索）：每次 LLM 调用前自动检索相关记忆并注入 Prompt（RAG）
            config.retrieval {
                storage = searchStorage
                this.namespace = namespace
                searchStrategy = SimilaritySearchStrategy(topK = topK, similarityThreshold = similarityThreshold)
                promptAugmenter = SystemPromptAugmenter()
            }
            if (enableIngestion && writeStorage != null) {
                // ingestion：自动从对话中抽取并写入长期记忆（默认关闭，避免噪声入库）
                config.ingestion {
                    storage = writeStorage
                    this.namespace = namespace
                }
            }
        }
    }

    /**
     * 快速创建并运行 Agent
     * @param provider LLM 提供商
     * @param apiKey API Key（Ollama 不需要）
     * @param prompt 提示词
     * @param model LLM 模型（可选）
     * @return Agent 响应结果
     */
    suspend fun quickRun(
        provider: Provider,
        prompt: String,
        apiKey: String? = null,
        model: LLModel? = null
    ): String? {
        val agent = createAgent(provider, apiKey, model) ?: return null
        return runAgent(agent, prompt)
    }
}
