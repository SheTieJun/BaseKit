package me.shetj.base.tools.debug

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import me.shetj.base.constant.Constant
import me.shetj.base.netcoroutine.HttpKit
import me.shetj.base.tools.file.SPUtils

class DebugSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DebugSettingsScreen()
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, DebugSettingsActivity::class.java))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugSettingsScreen() {
    val context = LocalContext.current
    var isHttpLogEnabled by remember {
        mutableStateOf(DebugFunc.getInstance().isOutputHttp)
    }

    // LogConfig States
    val logConfig = LogManager.getConfig()
    var isLogEnable by remember { mutableStateOf(logConfig.isEnable) }
    var isSaveToFile by remember { mutableStateOf(logConfig.isSaveToFile) }
    var isPrintToConsole by remember { mutableStateOf(logConfig.isPrintToConsole) }
    var bufferSize by remember { mutableStateOf(logConfig.bufferSize.toString()) }
    var flushInterval by remember { mutableStateOf(logConfig.flushInterval.toString()) }
    var maxFileSizeMb by remember { mutableStateOf((logConfig.maxFileSize / 1024 / 1024).toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debug Settings") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = {  LogViewerActivity.start(context)  }) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = "Log")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "General Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // HTTP Log Switch
            DebugOptionItem(title = "Enable HTTP Log") {
                Switch(
                    checked = isHttpLogEnabled,
                    onCheckedChange = {
                        isHttpLogEnabled = it
                        DebugFunc.getInstance().setIsOutputHttp(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Log Config Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Log Enable
            DebugOptionItem(title = "Enable Log System") {
                Switch(
                    checked = isLogEnable,
                    onCheckedChange = {
                        isLogEnable = it
                        logConfig.isEnable = it
                    }
                )
            }

            // Save To File
            DebugOptionItem(title = "Save Logs to File") {
                Switch(
                    checked = isSaveToFile,
                    onCheckedChange = {
                        isSaveToFile = it
                        logConfig.isSaveToFile = it
                    }
                )
            }

            // Print To Console
            DebugOptionItem(title = "Print to Logcat") {
                Switch(
                    checked = isPrintToConsole,
                    onCheckedChange = {
                        isPrintToConsole = it
                        logConfig.isPrintToConsole = it
                    }
                )
            }

            // Buffer Size
            DebugOptionItem(title = "Buffer Size (Count)") {
                OutlinedTextField(
                    value = bufferSize,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            bufferSize = it
                            it.toIntOrNull()?.let { size -> logConfig.bufferSize = size }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Flush Interval
            DebugOptionItem(title = "Flush Interval (ms)") {
                OutlinedTextField(
                    value = flushInterval,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            flushInterval = it
                            it.toLongOrNull()?.let { interval -> logConfig.flushInterval = interval }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Max File Size
            DebugOptionItem(title = "Max File Size (MB)") {
                OutlinedTextField(
                    value = maxFileSizeMb,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            maxFileSizeMb = it
                            it.toLongOrNull()?.let { mb -> logConfig.maxFileSize = mb * 1024 * 1024 }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }


        }
    }
}

@Composable
fun DebugOptionItem(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
