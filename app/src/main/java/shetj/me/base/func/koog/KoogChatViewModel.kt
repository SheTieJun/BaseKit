package shetj.me.base.func.koog

import ai.koog.agents.core.agent.AIAgent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.shetj.base.tools.app.KoogAgentKit
import me.shetj.base.BaseKit

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
    
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var currentAgent: AIAgent<String, String>? = null

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
                    currentAgent = KoogAgentKit.createAgent(provider, apiKey, baseUrl = activeAgent.baseUrl)
                    _state.update {
                        it.copy(
                            currentAgentName = activeAgent.name,
                            isConfigured = currentAgent != null
                        )
                    }
                } else {
                    currentAgent = null
                    _state.update { it.copy(isConfigured = false, currentAgentName = "") }
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
        _state.update {
            it.copy(
                messages = it.messages + userMessage + loadingMessage,
                inputText = "",
                isGenerating = true
            )
        }

        val agent = currentAgent!!
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
        _state.update { it.copy(messages = emptyList()) }
    }
}
