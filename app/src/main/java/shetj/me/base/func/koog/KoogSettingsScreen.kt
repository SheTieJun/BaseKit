package shetj.me.base.func.koog

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

    suspend fun addAgent(name: String, provider: KoogAgentKit.Provider, apiKey: String, model: String = "", systemPrompt: String = "") {
        agentManager.addAgent(name, provider, apiKey, model, systemPrompt)
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
            onSave = { name, provider, apiKey, model, systemPrompt ->
                scope.launch {
                    if (editingAgent != null) {
                        viewModel.updateAgent(editingAgent!!.copy(name = name, provider = provider.name, apiKey = apiKey, model = model, systemPrompt = systemPrompt))
                    } else {
                        viewModel.addAgent(name, provider, apiKey, model, systemPrompt)
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
    onSave: (String, KoogAgentKit.Provider, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(agent?.name ?: "") }
    var selectedProvider by remember { mutableStateOf(KoogAgentKit.Provider.valueOf(agent?.provider ?: "OPENAI")) }
    var apiKey by remember { mutableStateOf(agent?.apiKey ?: "") }
    var model by remember { mutableStateOf(agent?.model ?: "") }
    var systemPrompt by remember { mutableStateOf(agent?.systemPrompt ?: "") }
    var showProviderDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (agent == null) "添加 Agent" else "编辑 Agent") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                
                OutlinedTextField(
                    value = selectedProvider.name,
                    onValueChange = {},
                    label = { Text("提供商") },
                    modifier = Modifier.fillMaxWidth().clickable { showProviderDialog = true },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )
                
                if (selectedProvider != KoogAgentKit.Provider.OLLAMA) {
                    OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                }
                
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("模型 (可选)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                
                OutlinedTextField(value = systemPrompt, onValueChange = { systemPrompt = it }, label = { Text("系统提示词 (可选)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedProvider, apiKey, model, systemPrompt) },
                enabled = name.isNotBlank() && (selectedProvider == KoogAgentKit.Provider.OLLAMA || apiKey.isNotBlank())
            ) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )

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
        else -> ""
    }
}
