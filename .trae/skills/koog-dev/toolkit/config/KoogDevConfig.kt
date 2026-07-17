package koog.dev.toolkit.config

import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

data class KoogDevConfig(
    val provider: LLMProvider,
    val modelId: String,
    val apiKey: String? = null,
    val baseUrl: String? = null,
    val systemPrompt: String,
    val temperature: Double = 0.2,
    val maxIterations: Int = 6
) {
    fun requireValid() {
        require(modelId.isNotBlank()) { "modelId is blank" }
        require(systemPrompt.isNotBlank()) { "systemPrompt is blank" }
        require(maxIterations > 0) { "maxIterations must be > 0" }
        require(temperature in 0.0..2.0) { "temperature out of range: $temperature" }
    }

    fun toModel(capabilities: List<ai.koog.prompt.llm.LLMCapability> = emptyList()): LLModel {
        return LLModel(
            provider = provider,
            id = modelId,
            capabilities = capabilities
        )
    }
}

