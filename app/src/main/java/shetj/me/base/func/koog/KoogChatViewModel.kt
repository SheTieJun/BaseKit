package shetj.me.base.func.koog

import ai.koog.agents.core.agent.AIAgent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.shetj.base.tools.app.KoogAgentKit
import me.shetj.base.BaseKit
import me.shetj.base.tools.app.memory.chat.ChatMemoryDatabase
import me.shetj.base.tools.app.memory.chat.RoomChatHistoryProvider
import me.shetj.base.tools.app.memory.storage.LongTermMemoryDatabase
import me.shetj.base.tools.app.memory.storage.RoomTextDocumentStorage
import shetj.me.base.func.koog.askuser.AskUserGatewayImpl
import shetj.me.base.func.koog.askuser.AskUserRequest
import shetj.me.base.func.koog.tools.AskUserTool
import shetj.me.base.func.koog.tools.InspirationTool
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
    private val askUserGateway = AskUserGatewayImpl()
    private val longTermMemoryStorage = RoomTextDocumentStorage(
        LongTermMemoryDatabase.getInstance(BaseKit.app).memoryRecordDao()
    )
    private val chatHistoryProvider = RoomChatHistoryProvider(
        ChatMemoryDatabase.getInstance(BaseKit.app).chatMemoryDao()
    )

    val askUserRequests: SharedFlow<AskUserRequest> = askUserGateway.requests
    
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
                        systemPrompt = activeAgent.systemPrompt,
                        chatHistoryProvider = chatHistoryProvider,
                        tools = listOf(InspirationTool, AskUserTool(askUserGateway)),
                        userId = "local",
                        agentId = activeAgent.id,
                        longTermSearchStorage = longTermMemoryStorage,
                        longTermWriteStorage = longTermMemoryStorage
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
        val userMessage = ChatMessage(content = input, isUser = true)
        val loadingMessage = ChatMessage(content = "", isUser = false, isLoading = true)
        val messagesWithUser = storedHistory + userMessage + loadingMessage

        _state.update {
            it.copy(
                messages = messagesWithUser,
                inputText = "",
                isGenerating = true
            )
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
                    input,
                    sessionId = agentId
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

    fun answerAskUser(requestId: String, value: String) {
        askUserGateway.answer(requestId, value)
    }

    fun cancelAskUser(requestId: String) {
        askUserGateway.cancel(requestId)
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

}
