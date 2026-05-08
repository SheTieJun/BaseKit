package shetj.me.base.func.koog

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.bedrock.BedrockModels
import ai.koog.prompt.executor.clients.deepseek.DeepSeekModels
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.mistralai.MistralAIModels
import ai.koog.prompt.executor.clients.modelsById
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.clients.openrouter.OpenRouterModels
import ai.koog.prompt.executor.ollama.client.OllamaModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.shetj.base.tools.app.KoogAgentKit

/**
 * 配置界面 ViewModel
 */
class KoogSettingsViewModel : androidx.lifecycle.ViewModel() {
    private val agentManager = AgentManager.getInstance(me.shetj.base.BaseKit.app)
    val stateFlow = agentManager.stateFlow

    suspend fun addAgent(name: String, provider: KoogAgentKit.Provider, apiKey: String, model: String = "", systemPrompt: String = "", baseUrl: String = "") {
        agentManager.addAgent(name, provider, apiKey, model, systemPrompt, baseUrl)
    }

    suspend fun updateAgent(agent: AgentConfig) = agentManager.updateAgent(agent)
    suspend fun deleteAgent(id: String) = agentManager.deleteAgent(id)
    suspend fun setActiveAgent(id: String) = agentManager.setActiveAgent(id)
    suspend fun setDefaultAgent(id: String) = agentManager.setDefaultAgent(id)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KoogSettingsScreen(
    onBack: () -> Unit,
    onChat: () -> Unit = {}
) {
    val viewModel: KoogSettingsViewModel = viewModel()
    val state by viewModel.stateFlow.collectAsState(initial = AgentManager.AgentState())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAgent by remember { mutableStateOf<AgentConfig?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Agent 管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加 Agent")
            }
        }
    ) { padding ->
        if (state.isEmpty) {
            // 空状态
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.StarBorder, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("还没有配置 Agent", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("点击下方 + 添加第一个 Agent", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("添加 Agent")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.agents, key = { it.id }) { agent ->
                    val isActive = agent.id == state.activeAgentId
                    AgentListItem(
                        agent = agent,
                        isActive = isActive,
                        onClick = {
                            scope.launch { viewModel.setActiveAgent(agent.id); onChat() }
                        },
                        onEdit = { editingAgent = agent },
                        onDelete = { scope.launch { viewModel.deleteAgent(agent.id) } },
                        onSetDefault = { scope.launch { viewModel.setDefaultAgent(agent.id) } }
                    )
                }
            }
        }
    }

    // 添加/编辑对话框
    if (showAddDialog || editingAgent != null) {
        AgentEditorDialog(
            agent = editingAgent,
            onDismiss = { showAddDialog = false; editingAgent = null },
            onSave = { name, provider, apiKey, model, systemPrompt, baseUrl ->
                scope.launch {
                    if (editingAgent != null) {
                        viewModel.updateAgent(editingAgent!!.copy(name = name, provider = provider.name, apiKey = apiKey, model = model, systemPrompt = systemPrompt, baseUrl = baseUrl))
                    } else {
                        viewModel.addAgent(name, provider, apiKey, model, systemPrompt, baseUrl)
                    }
                    showAddDialog = false
                    editingAgent = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentListItem(
    agent: AgentConfig,
    isActive: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(agent.name, style = MaterialTheme.typography.titleMedium)
                        if (agent.isDefault) {
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Outlined.Star, null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFB300))
                        }
                    }
                    Text(
                        "${agent.getDisplayName()} · ${agent.getModelName()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isActive) {
                    Text("活跃", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.align(Alignment.End)) {
                IconButton(onClick = onSetDefault, modifier = Modifier.size(32.dp)) {
                    Icon(if (agent.isDefault) Icons.Outlined.Star else Icons.Outlined.StarBorder, null, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AgentEditorDialog(
    agent: AgentConfig?,
    onDismiss: () -> Unit,
    onSave: (String, KoogAgentKit.Provider, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(agent?.name ?: "") }
    var selectedProvider by remember { mutableStateOf(KoogAgentKit.Provider.valueOf(agent?.provider ?: "OPENAI")) }
    var apiKey by remember { mutableStateOf(agent?.apiKey ?: "") }
    var model by remember { mutableStateOf(agent?.model ?: "") }
    var systemPrompt by remember { mutableStateOf(agent?.systemPrompt ?: "") }
    var baseUrl by remember { mutableStateOf(agent?.baseUrl ?: "") }
    var showProviderDialog by remember { mutableStateOf(false) }
    var showModelDialog by remember { mutableStateOf(false) }
    var showAdvancedOptions by remember { mutableStateOf(!agent?.baseUrl.isNullOrBlank()) }

    // 获取当前提供商的模型列表
    val availableModels = getModelsForProvider(selectedProvider)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (agent == null) "添加 Agent" else "编辑 Agent") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                
                // 提供商选择按钮
                OutlinedButton(
                    onClick = { showProviderDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(getProviderDisplayName(selectedProvider.name))
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                
                if (selectedProvider != KoogAgentKit.Provider.OLLAMA) {
                    OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                }
                
                // 模型选择按钮
                OutlinedButton(
                    onClick = { showModelDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(if (model.isEmpty()) "选择模型 (可选)" else model, style = MaterialTheme.typography.bodyMedium)
                        if (model.isEmpty()) {
                            Text("推荐: ${getDefaultModelForProvider(selectedProvider)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                
                OutlinedTextField(value = systemPrompt, onValueChange = { systemPrompt = it }, label = { Text("系统提示词 (可选)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                
                // 高级选项折叠区域
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showAdvancedOptions = !showAdvancedOptions },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("高级选项", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Icon(
                        imageVector = if (showAdvancedOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                AnimatedVisibility(visible = showAdvancedOptions) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val defaultUrl = getDefaultBaseUrl(selectedProvider.name)
                        OutlinedTextField(
                            value = baseUrl,
                            onValueChange = { baseUrl = it },
                            label = { Text("API Base URL (可选)") },
                            placeholder = { Text(defaultUrl) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (baseUrl.isNotBlank() && baseUrl != defaultUrl) {
                            Text("自定义", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedProvider, apiKey, model, systemPrompt, baseUrl) },
                enabled = name.isNotBlank() && (selectedProvider == KoogAgentKit.Provider.OLLAMA || apiKey.isNotBlank())
            ) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )

    // 提供商选择对话框
    if (showProviderDialog) {
        val providers = KoogAgentKit.Provider.entries
        AlertDialog(
            onDismissRequest = { showProviderDialog = false },
            title = { Text("选择提供商") },
            text = {
                LazyColumn {
                    items(providers) { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedProvider = p; showProviderDialog = false }.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(p.name, style = MaterialTheme.typography.bodyLarge)
                                Text(getProviderDescription(p.name), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (p == selectedProvider) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // 模型选择对话框
    if (showModelDialog) {
        AlertDialog(
            onDismissRequest = { showModelDialog = false },
            title = { Text("选择模型") },
            text = {
                LazyColumn {
                    if (availableModels.isNotEmpty()) {
                        item {
                            Text("推荐模型", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                        }
                        items(availableModels) { m ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { model = m; showModelDialog = false }.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(m, style = MaterialTheme.typography.bodyMedium)
                                if (m == model) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    item {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("自定义模型", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("输入模型名称") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { showModelDialog = false }, modifier = Modifier.fillMaxWidth()) {
                            Text("确认")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

/**
 * 获取指定提供商的预设模型列表
 * 使用 Koog SDK 内置的 modelsById() 方法，升级 SDK 时自动获取新模型
 */
private fun getModelsForProvider(provider: KoogAgentKit.Provider): List<String> {
    return when (provider) {
        KoogAgentKit.Provider.OPENAI -> OpenAIModels.modelsById().keys.toList()
        KoogAgentKit.Provider.ANTHROPIC -> AnthropicModels.modelsById().keys.toList()
        KoogAgentKit.Provider.GOOGLE -> GoogleModels.modelsById().keys.toList()
        KoogAgentKit.Provider.DEEPSEEK -> DeepSeekModels.modelsById().keys.toList()
        KoogAgentKit.Provider.OPENROUTER -> OpenRouterModels.modelsById().keys.toList()
        KoogAgentKit.Provider.MISTRAL -> MistralAIModels.modelsById().keys.toList()
        KoogAgentKit.Provider.OLLAMA -> OllamaModels.modelsById().keys.toList()
        KoogAgentKit.Provider.BEDROCK -> BedrockModels.modelsById().keys.toList()
        KoogAgentKit.Provider.CUSTOM -> emptyList()
    }
}

/**
 * 获取指定提供商的默认推荐模型
 */
private fun getDefaultModelForProvider(provider: KoogAgentKit.Provider): String {
    return when (provider) {
        KoogAgentKit.Provider.OPENAI -> "gpt-4o"
        KoogAgentKit.Provider.ANTHROPIC -> "claude-sonnet-4-5"
        KoogAgentKit.Provider.GOOGLE -> "gemini-2.5-flash"
        KoogAgentKit.Provider.DEEPSEEK -> "deepseek-chat"
        KoogAgentKit.Provider.OPENROUTER -> "openai/gpt-4o"
        KoogAgentKit.Provider.MISTRAL -> "mistral-medium-3.1"
        KoogAgentKit.Provider.OLLAMA -> "llama3.2"
        KoogAgentKit.Provider.BEDROCK -> "anthropic.claude-sonnet-4-5"
        KoogAgentKit.Provider.CUSTOM -> ""
    }
}

private fun getProviderDescription(provider: String): String {
    return when (provider) {
        "OPENAI" -> "GPT-4o 模型，通用对话"
        "ANTHROPIC" -> "Claude 系列，代码能力强"
        "GOOGLE" -> "Gemini 2.5 Pro，长上下文"
        "DEEPSEEK" -> "DeepSeek Chat，中文优化"
        "OPENROUTER" -> "多模型聚合路由"
        "BEDROCK" -> "AWS Bedrock 企业级"
        "MISTRAL" -> "Mistral Medium 3.1"
        "OLLAMA" -> "本地运行，无需 API Key"
        "CUSTOM" -> "兼容 OpenAI 格式的第三方 API"
        else -> ""
    }
}
