package koog.dev.toolkit.entry

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLModel

object KoogDevEntry {
    fun createAgent(
        promptExecutor: MultiLLMPromptExecutor,
        llmModel: LLModel,
        systemPrompt: String,
        toolRegistry: ToolRegistry? = null,
        temperature: Double = 0.2,
        maxIterations: Int = 6
    ): AIAgent {
        return AIAgent(
            promptExecutor = promptExecutor,
            llmModel = llmModel,
            systemPrompt = systemPrompt,
            toolRegistry = toolRegistry,
            temperature = temperature,
            maxIterations = maxIterations
        )
    }
}

