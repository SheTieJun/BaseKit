package me.shetj.base.tools.app

import ai.koog.agents.core.agent.AIAgent
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
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
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
        OLLAMA
    }

    /**
     * 创建 Koog Agent
     * @param provider LLM 提供商
     * @param apiKey API Key（Ollama 不需要）
     * @param model LLM 模型
     * @param baseUrl 自定义 API Base URL（可选，不传则使用默认值）
     */
    fun createAgent(
        provider: Provider,
        apiKey: String? = null,
        model: LLModel? = null,
        baseUrl: String? = null
    ): AIAgent<String, String>? {
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
                    AIAgent(
                        promptExecutor = executor,
                        llmModel = model ?: OpenAIModels.Chat.GPT4o
                    )
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
                    AIAgent(
                        promptExecutor = executor,
                        llmModel = model ?: AnthropicModels.Sonnet_4_5
                    )
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
                    AIAgent(
                        promptExecutor = executor,
                        llmModel = model ?: GoogleModels.Gemini2_5Pro
                    )
                }

                Provider.DEEPSEEK -> {
                    val key = apiKey ?: System.getenv("DEEPSEEK_API_KEY")
                        ?: error("DEEPSEEK_API_KEY 未设置")
                    val client = if (baseUrl.isNullOrBlank()) {
                        DeepSeekLLMClient(key)
                    } else {
                        DeepSeekLLMClient(key, DeepSeekClientSettings(baseUrl = baseUrl))
                    }
                    AIAgent(
                        promptExecutor = MultiLLMPromptExecutor(mapOf(DeepSeekModels.DeepSeekChat.provider to client)),
                        llmModel = model ?: DeepSeekModels.DeepSeekChat
                    )
                }

                Provider.OPENROUTER -> {
                    val key = apiKey ?: System.getenv("OPENROUTER_API_KEY")
                        ?: error("OPENROUTER_API_KEY 未设置")
                    if (!baseUrl.isNullOrBlank()) {
                        val client = OpenAILLMClient(key, OpenAIClientSettings(baseUrl = baseUrl))
                        AIAgent(
                            promptExecutor = MultiLLMPromptExecutor(LLMProvider.OpenAI to client),
                            llmModel = model ?: OpenRouterModels.GPT4o
                        )
                    } else {
                        AIAgent(
                            promptExecutor = simpleOpenRouterExecutor(key),
                            llmModel = model ?: OpenRouterModels.GPT4o
                        )
                    }
                }

                Provider.BEDROCK -> {
                    val key = apiKey ?: System.getenv("BEDROCK_API_KEY")
                        ?: error("BEDROCK_API_KEY 未设置")
                    AIAgent(
                        promptExecutor = simpleBedrockExecutorWithBearerToken(key),
                        llmModel = model ?: BedrockModels.AnthropicClaude4_5Sonnet
                    )
                }

                Provider.MISTRAL -> {
                    val key = apiKey ?: System.getenv("MISTRAL_API_KEY")
                        ?: error("MISTRAL_API_KEY 未设置")
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleMistralAIExecutor(key)
                    } else {
                        val client = OpenAILLMClient(key, OpenAIClientSettings(baseUrl = baseUrl))
                        MultiLLMPromptExecutor(LLMProvider.OpenAI to client)
                    }
                    AIAgent(
                        promptExecutor = executor,
                        llmModel = model ?: MistralAIModels.Chat.MistralMedium31
                    )
                }

                Provider.OLLAMA -> {
                    val executor = if (baseUrl.isNullOrBlank()) {
                        simpleOllamaAIExecutor()
                    } else {
                        val client = OllamaClient(baseUrl)
                        MultiLLMPromptExecutor(LLMProvider.Ollama to client)
                    }
                    AIAgent(
                        promptExecutor = executor,
                        llmModel = model ?: OllamaModels.Meta.LLAMA_3_2
                    )
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
    fun runAgent(
        agent: AIAgent<String, String>,
        prompt: String
    ): String? {
        return try {
            runBlocking {
                agent.run(prompt)
            }
        } catch (e: Exception) {
            Timber.tag("KoogAgentKit").e(e, "运行 Agent 失败: ${e.message}")
            null
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
    fun quickRun(
        provider: Provider,
        prompt: String,
        apiKey: String? = null,
        model: LLModel? = null
    ): String? {
        val agent = createAgent(provider, apiKey, model) ?: return null
        return runAgent(agent, prompt)
    }
}
