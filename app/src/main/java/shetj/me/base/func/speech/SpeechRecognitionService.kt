package shetj.me.base.func.speech

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import shetj.me.base.R

/**
 * 语音识别服务
 * 用于在后台持续监听用户的语音命令
 */
class SpeechRecognitionService : Service(), SpeechRecognizerManager.SpeechRecognitionListener {

    companion object {
        private const val TAG = "SpeechRecognitionSvc"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "speech_recognition_channel"
        private const val CHANNEL_NAME = "语音识别服务"

        // 重启识别的延迟时间（毫秒）
        private const val RESTART_RECOGNITION_DELAY = 1000L
    }

    private val binder = SpeechBinder()
    private lateinit var speechManager: SpeechRecognizerManager
    private var isServiceRunning = false
    private var isContinuousListening = false
    private val handler = Handler(Looper.getMainLooper())
    private var commandListener: OnCommandListener? = null

    // 重启识别的Runnable
    private val restartRecognitionRunnable = Runnable {
        if (isContinuousListening && isServiceRunning) {
            startRecognition()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // 初始化语音识别管理器
        speechManager = SpeechRecognizerManager(this)
        if (speechManager.initialize()) {
            speechManager.setListener(this)
            isServiceRunning = true
        } else {
            Log.e(TAG, "Failed to initialize speech recognizer")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")

        // 创建通知渠道（Android 8.0及以上需要）
        createNotificationChannel()

        // 创建前台服务通知
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        stopContinuousListening()
        isServiceRunning = false
        speechManager.destroy()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    /**
     * 开始持续监听
     */
    fun startContinuousListening() {
        if (!isServiceRunning) return

        isContinuousListening = true
        startRecognition()
        showToast("持续语音识别已启动")
    }

    /**
     * 停止持续监听
     */
    fun stopContinuousListening() {
        isContinuousListening = false
        speechManager.stopListening()
        handler.removeCallbacks(restartRecognitionRunnable)
        showToast("持续语音识别已停止")
    }

    /**
     * 设置命令监听器
     * @param listener 命令监听器
     */
    fun setCommandListener(listener: OnCommandListener) {
        this.commandListener = listener
    }

    /**
     * 移除命令监听器
     */
    fun removeCommandListener() {
        this.commandListener = null
    }

    /**
     * 是否正在持续监听
     * @return 是否正在持续监听
     */
    fun isContinuousListening(): Boolean {
        return isContinuousListening
    }

    /**
     * 开始单次识别
     */
    private fun startRecognition() {
        if (!isServiceRunning) return

        try {
            speechManager.startListening()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recognition: ${e.message}")
            // 如果出错，尝试重新启动
            handler.postDelayed(restartRecognitionRunnable, RESTART_RECOGNITION_DELAY)
        }
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "用于语音识别服务的通知渠道"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 创建前台服务通知
     * @return 通知对象
     */
    private fun createNotification(): Notification {
        // 创建打开主Activity的PendingIntent
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("语音识别服务")
            .setContentText("正在监听语音命令...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 请确保此图标存在
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    /**
     * 显示Toast消息
     * @param message 消息内容
     */
    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    // SpeechRecognitionListener 接口实现
    override fun onReadyForSpeech() {
        Log.d(TAG, "Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "Beginning of speech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // 不需要处理
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "End of speech")
    }

    override fun onError(errorMessage: String) {
        Log.e(TAG, "Recognition error: $errorMessage")

        // 如果是持续监听模式，则在延迟后重新启动识别
        if (isContinuousListening && isServiceRunning) {
            handler.postDelayed(restartRecognitionRunnable, RESTART_RECOGNITION_DELAY)
        }
    }

    override fun onResults(results: ArrayList<String>) {
        if (results.isNotEmpty()) {
            val recognizedText = results[0]
            Log.d(TAG, "Recognition result: $recognizedText")

            // 处理识别到的命令
            processCommand(recognizedText)

            // 如果是持续监听模式，则在延迟后重新启动识别
            if (isContinuousListening && isServiceRunning) {
                handler.postDelayed(restartRecognitionRunnable, RESTART_RECOGNITION_DELAY)
            }
        }
    }

    override fun onPartialResults(partialResults: ArrayList<String>) {
        // 不需要处理部分结果
    }

    /**
     * 处理识别到的命令
     * @param command 命令文本
     */
    private fun processCommand(command: String) {
        // 通知监听器
        commandListener?.onCommandRecognized(command)

        // 这里可以添加一些预定义的命令处理逻辑
        when {
            command.contains("打开相机") -> {
                showToast("正在打开相机...")
                // 这里添加打开相机的代码
            }
            command.contains("停止监听") || command.contains("停止识别") -> {
                stopContinuousListening()
            }
            // 可以添加更多的命令处理
        }
    }

    /**
     * 服务绑定器
     */
    inner class SpeechBinder : Binder() {
        fun getService(): SpeechRecognitionService = this@SpeechRecognitionService
    }

    /**
     * 命令监听器接口
     */
    interface OnCommandListener {
        /**
         * 当识别到命令时调用
         * @param command 识别到的命令文本
         */
        fun onCommandRecognized(command: String)
    }
}