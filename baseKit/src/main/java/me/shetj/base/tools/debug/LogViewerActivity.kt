package me.shetj.base.tools.debug


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

class LogViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LogViewerScreen()
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LogViewerActivity::class.java))
        }
    }
}

data class LogDisplayOptions(
    val showDate: Boolean = false,//默认隐藏日期，因为标题显示
    val showTime: Boolean = true,
    val showLevel: Boolean = true,
    val showTag: Boolean = true
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogViewerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var logFiles by remember { mutableStateOf(emptyList<File>()) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var logContent by remember { mutableStateOf(emptyList<String>()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedLevel by remember { mutableStateOf<LogLevel?>(null) }
    
    // UI State
    var showSearch by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    // Display Options
    var displayOptions by remember { mutableStateOf(LogDisplayOptions()) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logFiles = LogManager.getLogFiles()
        if (logFiles.isNotEmpty()) {
            selectedFile = logFiles.first()
        }
    }

    LaunchedEffect(selectedFile) {
        selectedFile?.let { file ->
            isLoading = true
            scope.launch(Dispatchers.IO) {
                val lines = LogManager.readLogFile(file)
                withContext(Dispatchers.Main) {
                    logContent = lines
                    isLoading = false
                }
            }
        }
    }

    // Delete Dialog
    if (showDeleteDialog && selectedFile != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Log File") },
            text = { Text("Are you sure you want to delete ${selectedFile?.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedFile?.delete()
                        logFiles = LogManager.getLogFiles()
                        selectedFile = logFiles.firstOrNull()
                        if (selectedFile == null) {
                            logContent = emptyList()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Stats Bottom Sheet
    if (showStatsDialog) {
        val keywords = searchQuery.split("|").filter { it.isNotBlank() }
        val stats = remember(logContent, searchQuery) {
            keywords.associateWith { keyword ->
                logContent.count { it.contains(keyword, ignoreCase = true) }
            }
        }
        val totalLogs = logContent.size
        
        ModalBottomSheet(
            onDismissRequest = { showStatsDialog = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Search Statistics",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Total Logs: $totalLogs",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (keywords.isNotEmpty()) {
                    Text(
                        text = "Keyword Matches:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn {
                        items(keywords) { keyword ->
                            val count = stats[keyword] ?: 0
                            val progress = if (totalLogs > 0) count.toFloat() / totalLogs else 0f
                            
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = keyword,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = progress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text("No keywords entered.", style = MaterialTheme.typography.bodyMedium)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    val filteredLogs = remember(logContent, searchQuery, selectedLevel) {
        val keywords = searchQuery.split("|").filter { it.isNotBlank() }
        logContent.filter { line ->
            val matchesSearch = if (keywords.isEmpty()) true else keywords.any { line.contains(it, ignoreCase = true) }
            val matchesLevel = selectedLevel == null || line.contains("[${selectedLevel?.name}]")
            matchesSearch && matchesLevel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Viewer") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showSearch = !showSearch; if(!showSearch) searchQuery = "" }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    Box {
                        IconButton(onClick = { if (selectedFile != null) showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (displayOptions.showDate) "Hide Date" else "Show Date") },
                                onClick = {
                                    displayOptions = displayOptions.copy(showDate = !displayOptions.showDate)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (displayOptions.showTime) "Hide Time" else "Show Time") },
                                onClick = {
                                    displayOptions = displayOptions.copy(showTime = !displayOptions.showTime)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (displayOptions.showLevel) "Hide Level" else "Show Level") },
                                onClick = {
                                    displayOptions = displayOptions.copy(showLevel = !displayOptions.showLevel)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (displayOptions.showTag) "Hide Tag" else "Show Tag") },
                                onClick = {
                                    displayOptions = displayOptions.copy(showTag = !displayOptions.showTag)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // File Tabs
            if (logFiles.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = logFiles.indexOf(selectedFile).takeIf { it >= 0 } ?: 0
                ) {
                    logFiles.forEach { file ->
                        Tab(
                            selected = file == selectedFile,
                            onClick = { selectedFile = file },
                            text = { Text(file.name.removePrefix("Log_").removeSuffix(".txt")) }
                        )
                    }
                }
            }
            
            // Search Bar
            AnimatedVisibility(
                visible = showSearch,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search logs (use '|' for multiple)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { showStatsDialog = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Stats")
                            }
                        }
                    }
                )
            }

            // Level Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                LogLevel.values().forEach { level ->
                    FilterChip(
                        selected = selectedLevel == level,
                        onClick = { selectedLevel = if (selectedLevel == level) null else level },
                        label = { Text(level.name) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // Log List
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredLogs) { log ->
                            LogItem(log, displayOptions, searchQuery)
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogItem(log: String, options: LogDisplayOptions, searchQuery: String) {
    val color = when {
        log.contains("[ERROR]") -> Color.Red.copy(alpha = 0.1f)
        log.contains("[HTTP]") -> Color.Blue.copy(alpha = 0.1f)
        log.contains("[BEHAVIOR]") -> Color.Green.copy(alpha = 0.1f)
        else -> Color.Transparent
    }
    
    // ... (existing parsing logic to get displayText)
    var displayText = log
    try {
        val parts = log.split(" ", limit = 4)
        if (parts.size >= 4) {
            val date = parts[0]
            val time = parts[1]
            val level = parts[2]
            val rest = parts[3]
            
            val sb = StringBuilder()
            if (options.showDate) sb.append("$date ")
            if (options.showTime) sb.append("$time ")
            if (options.showLevel) sb.append("$level ")
            
            if (options.showTag) {
                sb.append(rest)
            } else {
                val tagSplit = rest.split(": ", limit = 2)
                if (tagSplit.size == 2) sb.append(tagSplit[1]) else sb.append(rest)
            }
            displayText = sb.toString()
        }
    } catch (e: Exception) { }

    // Highlight Logic
    val annotatedString = buildAnnotatedString {
        if (searchQuery.isBlank()) {
            append(displayText)
        } else {
            val keywords = searchQuery.split("|").filter { it.isNotBlank() }
            val lowerText = displayText.lowercase()
            
            // Map of index to length for highlighting
            val highlights = mutableListOf<Pair<Int, Int>>()
            
            keywords.forEach { keyword ->
                val lowerKeyword = keyword.lowercase()
                var startIndex = lowerText.indexOf(lowerKeyword)
                while (startIndex >= 0) {
                    highlights.add(startIndex to keyword.length)
                    startIndex = lowerText.indexOf(lowerKeyword, startIndex + 1)
                }
            }
            
            // Render text
            var currentIndex = 0
            // Sort highlights by index
            highlights.sortBy { it.first }
            
            // Merge overlapping highlights is complex, simplified here: 
            // Just apply background color for ranges.
            // But buildAnnotatedString is sequential.
            // Better approach: Use addStyle on the full text range.
            
            append(displayText)
            highlights.forEach { (start, length) ->
                if (start + length <= displayText.length) {
                    addStyle(
                        style = SpanStyle(background = Color.Yellow.copy(alpha = 0.5f)),
                        start = start,
                        end = start + length
                    )
                }
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(8.dp),
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
    )
}
