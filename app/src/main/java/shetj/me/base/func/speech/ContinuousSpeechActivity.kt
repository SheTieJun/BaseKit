package shetj.me.base.func.speech

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import shetj.me.base.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 持续语音识别Activity
 * 演示如何使用SpeechRecognitionService进行持续语音识别
 */
class ContinuousSpeechActivity : AppCompatActivity(), SpeechRecognitionService.OnCommandListener {

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private lateinit var statusTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var commandsRecyclerView: RecyclerView
    
    private val commandsList = mutableListOf<CommandItem>()
    private lateinit var commandsAdapter: CommandsAdapter
    
    private var speechService: SpeechRecognitionService? = null
    private var isBound = false

    // 服务连接
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SpeechRecognitionService.SpeechBinder
            speechService = binder.getService()
            speechService?.setCommandListener(this@ContinuousSpeechActivity)
            isBound = true
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            speechService = null
            isBound = false
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continuous_speech)

        // 初始化视图
        statusTextView = findViewById(R.id.statusTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        commandsRecyclerView = findViewById(R.id.commandsRecyclerView)

        // 设置RecyclerView
        commandsAdapter = CommandsAdapter(commandsList)
        commandsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContinuousSpeechActivity)
            adapter = commandsAdapter
        }

        // 检查权限
        checkPermission()

        // 设置按钮点击事件
        startButton.setOnClickListener {
            startContinuousRecognition()
        }

        stopButton.setOnClickListener {
            stopContinuousRecognition()
        }

        // 启动并绑定服务
        startAndBindService()
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            bindService()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            speechService?.removeCommandListener()
            unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 如果不需要后台持续监听，可以在这里停止服务
        // stopService(Intent(this, SpeechRecognitionService::class.java))
    }

    /**
     * 启动并绑定服务
     */
    private fun startAndBindService() {
        // 启动服务
        val serviceIntent = Intent(this, SpeechRecognitionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // 绑定服务
        bindService()
    }

    /**
     * 绑定服务
     */
    private fun bindService() {
        val intent = Intent(this, SpeechRecognitionService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * 开始持续识别
     */
    private fun startContinuousRecognition() {
        if (isBound && speechService != null) {
            speechService?.startContinuousListening()
            updateUI()
        } else {
            Toast.makeText(this, "服务未连接", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 停止持续识别
     */
    private fun stopContinuousRecognition() {
        if (isBound && speechService != null) {
            speechService?.stopContinuousListening()
            updateUI()
        }
    }

    /**
     * 更新UI状态
     */
    private fun updateUI() {
        val isListening = speechService?.isContinuousListening() ?: false
        
        if (isListening) {
            statusTextView.text = "状态: 正在监听"
            startButton.isEnabled = false
            stopButton.isEnabled = true
        } else {
            statusTextView.text = "状态: 未监听"
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }

    /**
     * 检查权限
     */
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "麦克风权限已授予", Toast.LENGTH_SHORT).show()
                startAndBindService()
            } else {
                Toast.makeText(this, "没有麦克风权限，语音识别无法工作", Toast.LENGTH_SHORT).show()
                startButton.isEnabled = false
            }
        }
    }

    /**
     * 命令监听器回调
     */
    override fun onCommandRecognized(command: String) {
        // 添加命令到列表
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = timeFormat.format(Date())
        val commandItem = CommandItem(command, time)
        
        runOnUiThread {
            commandsList.add(0, commandItem) // 添加到列表开头
            commandsAdapter.notifyItemInserted(0)
            commandsRecyclerView.scrollToPosition(0)
        }
    }

    /**
     * 命令项数据类
     */
    data class CommandItem(val command: String, val time: String)

    /**
     * 命令列表适配器
     */
    inner class CommandsAdapter(private val commands: List<CommandItem>) : 
        RecyclerView.Adapter<CommandsAdapter.ViewHolder>() {

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val commandTextView: TextView = view.findViewById(R.id.commandTextView)
            val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_command, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = commands[position]
            holder.commandTextView.text = item.command
            holder.timeTextView.text = item.time
        }

        override fun getItemCount() = commands.size
    }
}