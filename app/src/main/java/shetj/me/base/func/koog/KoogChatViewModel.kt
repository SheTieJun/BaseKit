package shetj.me.base.func.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.dsl.Prompt
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
import shetj.me.base.func.koog.tools.InspirationTool
import timber.log.Timber
import kotlin.time.ExperimentalTime

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
    private val inspirationTool = InspirationTool()
    
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
                    currentAgent = KoogAgentKit.createAgent(
                        provider = provider,
                        apiKey = apiKey,
                        baseUrl = activeAgent.baseUrl,
                        modelName = activeAgent.model,
                        systemPrompt = activeAgent.systemPrompt
                    )
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
        val snapshot = _state.value
        val input = snapshot.inputText.trim()
        if (input.isEmpty() || snapshot.isGenerating) return
        val storedHistory = snapshot.messages.filter { !it.isLoading }
        val promptHistory = normalizeHistoryForPrompt(storedHistory)

        val manualInspirationTopic = parseManualInspirationTopic(input)
        val inspirationTopic = manualInspirationTopic ?: input.takeIf { shouldAutoInspiration(input) }

        val userMessage = ChatMessage(content = input, isUser = true)
        val loadingMessage = ChatMessage(content = "", isUser = false, isLoading = true)
        val messagesWithUser = storedHistory + userMessage + loadingMessage
        
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

        if (inspirationTopic != null) {
            val agentId = activeAgentId
            val agent = currentAgent
            viewModelScope.launch {
                try {
                    val topic = inspirationTopic.take(100).trim()
                    val toolResult = inspirationTool.execute(topic)
                    val expanded = if (agent != null) {
                        KoogAgentKit.runAgent(
                            agent,
                            buildInspirationExpansionPrompt(
                                toolTopic = topic,
                                toolOutput = toolResult,
                                originalUserText = input,
                                isManualCommand = manualInspirationTopic != null,
                                history = promptHistory
                            )
                        )
                    } else null

                    val response = buildString {
                        append("灵感工具：")
                        append('\n')
                        append(toolResult.trim())
                        if (!expanded.isNullOrBlank()) {
                            append('\n')
                            append('\n')
                            append("扩写方案：")
                            append('\n')
                            append(expanded.trim())
                        } else if (agent == null) {
                            append('\n')
                            append('\n')
                            append("提示：如果你希望我把灵感扩写成开篇方案，请先在设置页配置一个可用的 Agent。")
                        }
                        if (manualInspirationTopic != null) {
                            append('\n')
                            append('\n')
                            append("提示：你也可以直接输入你的题材关键词，例如：/inspiration 赛博朋克+修仙")
                        }
                    }.trim()
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
            return
        }

        if (currentAgent == null) {
            _state.update { s ->
                val updatedMessages = s.messages.toMutableList()
                val lastIdx = updatedMessages.lastIndex
                if (lastIdx >= 0 && updatedMessages[lastIdx].isLoading) {
                    updatedMessages[lastIdx] = updatedMessages[lastIdx].copy(
                        content = "当前未配置 Agent，请先到设置页配置 API Key / 选择本地 Ollama。",
                        isLoading = false
                    )
                }
                s.copy(messages = updatedMessages, isGenerating = false)
            }
            return
        }

        val agent = currentAgent!!
        val agentId = activeAgentId
        viewModelScope.launch {
            try {
                val response = KoogAgentKit.runAgent(
                    agent,
                    buildChatPrompt(userInput = input, history = promptHistory)
                ) ?: "抱歉，获取回复失败"
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

    @OptIn(ExperimentalTime::class)
    private fun buildChatPrompt(userInput: String, history: List<ChatMessage>): Prompt {
        val builder = Prompt.builder("koog_chat_${System.nanoTime()}")
        history.forEach { msg ->
            if (msg.isUser) builder.user(msg.content) else builder.assistant(msg.content)
        }
        builder.user(userInput)
        return builder.build()
    }

    private fun normalizeHistoryForPrompt(messages: List<ChatMessage>): List<ChatMessage> {
        if (messages.isEmpty()) return emptyList()
        val maxMessages = 12
        val maxCharsPerMessage = 1200
        val selected = if (messages.size <= maxMessages) messages else messages.takeLast(maxMessages)
        return selected.map { msg ->
            val trimmed = msg.content.take(maxCharsPerMessage).trim()
            if (trimmed == msg.content) msg else msg.copy(content = trimmed)
        }
    }

    private fun parseManualInspirationTopic(input: String): String? {
        val raw = input.trim()
        val prefixes = listOf("/inspiration", "/inspire", "/灵感")
        val matched = prefixes.firstOrNull { raw.startsWith(it, ignoreCase = true) } ?: return null
        return raw.removePrefix(matched).trim()
    }

    private fun shouldAutoInspiration(input: String): Boolean {
        val text = input.trim()
        if (text.isEmpty()) return false
        if (text.startsWith("/")) return false
        val keywords = listOf("灵感", "开篇", "切入点", "设定点子", "点子", "冲突切入")
        if (keywords.none { text.contains(it) }) return false
        val intents = listOf("给我", "来点", "想要", "求", "帮我", "能不能", "请你")
        return intents.any { text.contains(it) } || text.endsWith("？") || text.endsWith("?")
    }

    @OptIn(ExperimentalTime::class)
    private fun buildInspirationExpansionPrompt(
        toolTopic: String,
        toolOutput: String,
        originalUserText: String,
        isManualCommand: Boolean,
        history: List<ChatMessage>
    ): Prompt {
        val toolCallId = "inspiration_${System.nanoTime()}"
        val argsJson = JSONObject()
            .put("topic", toolTopic)
            .toString()

        val userReq = originalUserText.trim().takeIf { !isManualCommand }.orEmpty()
        val expansionReq = buildString {
            append("请基于上面的工具结果输出可直接用于网文创作的开篇方案。")
            append('\n')
            append("输出结构固定：要点清单 -> 关键冲突 -> 章节骨架（黄金三章） -> 可直接复制的示例正文（300~500字）。")
            if (userReq.isNotBlank()) {
                append('\n')
                append('\n')
                append("用户额外要求：")
                append(userReq)
            }
        }.trim()

        val builder = Prompt.builder("koog_inspiration_${System.nanoTime()}")
        history.forEach { msg ->
            if (msg.isUser) builder.user(msg.content) else builder.assistant(msg.content)
        }

        if (!isManualCommand) {
            builder.user(originalUserText.trim())
        }

        builder
            .toolCall(toolCallId, InspirationTool.NAME, argsJson)
            .toolResult(toolCallId, InspirationTool.NAME, toolOutput.trim())
            .user(expansionReq)

        return builder.build()
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
