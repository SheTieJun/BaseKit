package shetj.me.base.func.koog

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.shetj.base.tools.app.KoogAgentKit
import java.util.UUID

private val Context.agentDataStore: DataStore<Preferences> by preferencesDataStore("koog_agents_v2")

/**
 * 单个 Agent 配置
 */
data class AgentConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val provider: String = KoogAgentKit.Provider.OPENAI.name,
    val apiKey: String = "",
    val model: String = "",
    val systemPrompt: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
) {
    fun getDisplayName() = getProviderDisplayName(provider)
    fun getModelName() = model.ifEmpty { getDefaultModel(provider) }
}

/**
 * Agent 管理器 - 支持多 Agent 配置和切换
 */
class AgentManager(private val context: Context) {

    companion object {
        private val KEY_AGENTS = stringPreferencesKey("koog_agents")
        private val KEY_ACTIVE_ID = stringPreferencesKey("koog_active_agent_id")
        private val gson = Gson()

        @Volatile
        private var INSTANCE: AgentManager? = null

        fun getInstance(context: Context): AgentManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AgentManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val _stateFlow = MutableStateFlow(AgentState())
    
    init {
        GlobalScope.launch {
            context.agentDataStore.data.collect { prefs ->
                val agentsJson = prefs[KEY_AGENTS] ?: "[]"
                val agents = gson.fromJson<List<AgentConfig>>(agentsJson, object : TypeToken<List<AgentConfig>>() {}.type)
                val activeId = prefs[KEY_ACTIVE_ID]
                val normalizedAgents = normalizeAgents(agents)
                val finalActiveId = activeId ?: normalizedAgents.find { it.isDefault }?.id
                _stateFlow.value = AgentState(normalizedAgents, finalActiveId)
            }
        }
    }

    val stateFlow = _stateFlow.asStateFlow()

    data class AgentState(
        val agents: List<AgentConfig> = emptyList(),
        val activeAgentId: String? = null
    ) {
        val activeAgent: AgentConfig? get() = activeAgentId?.let { id -> agents.find { it.id == id } }
        val isEmpty: Boolean get() = agents.isEmpty()
    }

    suspend fun addAgent(name: String, provider: KoogAgentKit.Provider, apiKey: String, model: String = "", systemPrompt: String = "") {
        val current = _stateFlow.value
        val newAgent = AgentConfig(
            name = name,
            provider = provider.name,
            apiKey = apiKey,
            model = model,
            systemPrompt = systemPrompt,
            isDefault = current.agents.isEmpty()
        )
        saveAgents(current.agents + newAgent, current.activeAgentId ?: newAgent.id)
    }

    suspend fun updateAgent(agent: AgentConfig) {
        val current = _stateFlow.value
        val updated = current.agents.map { if (it.id == agent.id) agent else it }
        saveAgents(updated, current.activeAgentId)
    }

    suspend fun deleteAgent(agentId: String) {
        val current = _stateFlow.value
        val remaining = current.agents.filter { it.id != agentId }
        if (remaining.isEmpty()) {
            saveAgents(emptyList(), null)
            return
        }
        val newActiveId = if (current.activeAgentId == agentId) {
            remaining.find { it.isDefault }?.id ?: remaining[0].id
        } else current.activeAgentId
        saveAgents(remaining, newActiveId)
    }

    suspend fun setActiveAgent(agentId: String) {
        context.agentDataStore.edit { prefs ->
            prefs[KEY_ACTIVE_ID] = agentId
        }
    }

    suspend fun setDefaultAgent(agentId: String) {
        val current = _stateFlow.value
        val updated = current.agents.map { it.copy(isDefault = it.id == agentId) }
        saveAgents(updated, agentId)
    }

    private suspend fun saveAgents(agents: List<AgentConfig>, activeId: String?) {
        context.agentDataStore.edit { prefs ->
            prefs[KEY_AGENTS] = gson.toJson(agents)
            if (activeId != null) prefs[KEY_ACTIVE_ID] = activeId
        }
    }

    private fun normalizeAgents(agents: List<AgentConfig>): List<AgentConfig> {
        if (agents.isEmpty()) return agents
        val hasDefault = agents.any { it.isDefault }
        return if (!hasDefault) {
            agents.toMutableList().apply { this[0] = this[0].copy(isDefault = true) }
        } else agents
    }
}

internal fun getProviderDisplayName(provider: String): String {
    return when (provider) {
        "OPENAI" -> "OpenAI"
        "ANTHROPIC" -> "Anthropic"
        "GOOGLE" -> "Google"
        "DEEPSEEK" -> "DeepSeek"
        "OPENROUTER" -> "OpenRouter"
        "BEDROCK" -> "Bedrock"
        "MISTRAL" -> "Mistral"
        "OLLAMA" -> "Ollama"
        else -> provider
    }
}

private fun getDefaultModel(provider: String): String {
    return when (provider) {
        "OPENAI" -> "gpt-4o"
        "ANTHROPIC" -> "claude-sonnet-4-5"
        "GOOGLE" -> "gemini-2.5-pro"
        "DEEPSEEK" -> "deepseek-chat"
        "OLLAMA" -> "llama3.2"
        "OPENROUTER" ->"qwen3.6-plus"
        else -> ""
    }
}
