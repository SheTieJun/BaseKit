package shetj.me.base.func.koog

import ai.koog.agents.core.agent.AIAgent
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.shetj.base.tools.app.KoogAgentKit
import me.shetj.base.BaseKit
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

/**
 * 聊天消息数据类
 */
data class ChatMessage(
    val id: String = System.nanoTime().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

/**
 * 聊天界面状态
 */
data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val currentAgentName: String = "",
    val isConfigured: Boolean = false
)

/**
 * 聊天 ViewModel
 */
class KoogChatViewModel : ViewModel() {

    private val agentManager = AgentManager.getInstance(BaseKit.app)
    private val chatHistoryManager = ChatHistoryManager.getInstance(BaseKit.app)
    
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var currentAgent: AIAgent<String, String>? = null
    private var activeAgentId: String? = null

    init {
        observeActiveAgent()
    }

    private fun observeActiveAgent() {
        viewModelScope.launch {
            agentManager.stateFlow.collect { agentState ->
                val activeAgent = agentState.activeAgent
                if (activeAgent != null) {
                    val provider = KoogAgentKit.Provider.valueOf(activeAgent.provider)
                    val apiKey = if (provider == KoogAgentKit.Provider.OLLAMA) null else activeAgent.apiKey
                    currentAgent = KoogAgentKit.createAgent(provider, apiKey, baseUrl = activeAgent.baseUrl, modelName = activeAgent.model)
                    val newAgentId = activeAgent.id
                    
                    // Agent 切换时加载对应的历史记录
                    if (newAgentId != activeAgentId) {
                        activeAgentId = newAgentId
                        val savedMessages = chatHistoryManager.loadMessages(newAgentId)
                        _state.update {
                            it.copy(
                                messages = savedMessages,
                                currentAgentName = activeAgent.name,
                                isConfigured = currentAgent != null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                currentAgentName = activeAgent.name,
                                isConfigured = currentAgent != null
                            )
                        }
                    }
                } else {
                    currentAgent = null
                    activeAgentId = null
                    _state.update { it.copy(isConfigured = false, currentAgentName = "", messages = emptyList()) }
                }
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val input = _state.value.inputText.trim()
        if (input.isEmpty() || _state.value.isGenerating || currentAgent == null) return

        val userMessage = ChatMessage(content = input, isUser = true)
        val loadingMessage = ChatMessage(content = "", isUser = false, isLoading = true)
        val messagesWithUser = _state.value.messages + userMessage + loadingMessage
        
        // 保存用户消息到历史（协程异步执行）
        activeAgentId?.let { id ->
            viewModelScope.launch {
                chatHistoryManager.saveMessages(id, messagesWithUser.filter { !it.isLoading })
            }
        }
        
        _state.update {
            it.copy(
                messages = messagesWithUser,
                inputText = "",
                isGenerating = true
            )
        }

        val agent = currentAgent!!
        val agentId = activeAgentId
        viewModelScope.launch {
            try {
                val response = KoogAgentKit.runAgent(agent, input) ?: "抱歉，获取回复失败"
                _state.update { s ->
                    val updatedMessages = s.messages.toMutableList()
                    val lastIdx = updatedMessages.lastIndex
                    if (lastIdx >= 0 && updatedMessages[lastIdx].isLoading) {
                        updatedMessages[lastIdx] = updatedMessages[lastIdx].copy(content = response, isLoading = false)
                    } else {
                        updatedMessages.add(ChatMessage(content = response, isUser = false))
                    }
                    s.copy(messages = updatedMessages, isGenerating = false)
                }
                // 保存 AI 回复到历史
                agentId?.let { chatHistoryManager.saveMessages(it, _state.value.messages) }
            } catch (e: Exception) {
                _state.update { s ->
                    val updatedMessages = s.messages.toMutableList()
                    val lastIdx = updatedMessages.lastIndex
                    if (lastIdx >= 0 && updatedMessages[lastIdx].isLoading) {
                        updatedMessages[lastIdx] = updatedMessages[lastIdx].copy(
                            content = "出错了: ${e.message}",
                            isLoading = false
                        )
                    }
                    s.copy(messages = updatedMessages, isGenerating = false)
                }
            }
        }
    }

    fun clearChat() {
        activeAgentId?.let { id ->
            viewModelScope.launch {
                chatHistoryManager.clearMessages(id)
            }
        }
        _state.update { it.copy(messages = emptyList()) }
    }
}

/**
 * 聊天记录持久化管理器
 */
class ChatHistoryManager private constructor(context: Context) {
    private val Context.chatDataStore: DataStore<Preferences> by preferencesDataStore("koog_chat_history")
    private val dataStore = context.chatDataStore

    companion object {
        @Volatile private var instance: ChatHistoryManager? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: ChatHistoryManager(context).also { instance = it }
        }

        private const val KEY_ID = "id"
        private const val KEY_CONTENT = "content"
        private const val KEY_IS_USER = "isUser"
        private const val KEY_TIMESTAMP = "timestamp"

        /**
         * ChatMessage -> JSONObject
         */
        private fun ChatMessage.toJson(): JSONObject = JSONObject().apply {
            put(KEY_ID, id)
            put(KEY_CONTENT, content)
            put(KEY_IS_USER, isUser)
            put(KEY_TIMESTAMP, timestamp)
        }

        /**
         * JSONObject -> ChatMessage
         */
        private fun JSONObject.toChatMessage(): ChatMessage = ChatMessage(
            id = getString(KEY_ID),
            content = getString(KEY_CONTENT),
            isUser = getBoolean(KEY_IS_USER),
            timestamp = getLong(KEY_TIMESTAMP)
        )
    }

    private fun getKey(agentId: String) = stringPreferencesKey("chat_$agentId")

    /**
     * 加载指定 Agent 的聊天历史
     */
    suspend fun loadMessages(agentId: String): List<ChatMessage> {
        return try {
            val json = dataStore.data.map { prefs -> prefs[getKey(agentId)] ?: "" }.first()
            if (json.isBlank()) emptyList()
            else {
                val array = JSONArray(json)
                (0 until array.length()).map { array.getJSONObject(it).toChatMessage() }
            }
        } catch (e: Exception) {
            Timber.tag("ChatHistory").e(e, "加载聊天记录失败: ${e.message}")
            emptyList()
        }
    }

    /**
     * 保存聊天消息列表
     */
    suspend fun saveMessages(agentId: String, messages: List<ChatMessage>) {
        try {
            val array = JSONArray()
            messages.forEach { msg -> array.put(msg.toJson()) }
            val json = array.toString()
            dataStore.updateData { prefs ->
                prefs.toMutablePreferences().apply {
                    this[getKey(agentId)] = json
                }
            }
        } catch (e: Exception) {
            Timber.tag("ChatHistory").e(e, "保存聊天记录失败: ${e.message}")
        }
    }

    /**
     * 清空指定 Agent 的聊天历史
     */
    suspend fun clearMessages(agentId: String) {
        try {
            dataStore.updateData { prefs ->
                prefs.toMutablePreferences().apply {
                    remove(getKey(agentId))
                }
            }
        } catch (e: Exception) {
            Timber.tag("ChatHistory").e(e, "清空聊天记录失败: ${e.message}")
        }
    }
}
