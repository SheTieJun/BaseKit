package shetj.me.base.func.speech

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import shetj.me.base.R

/**
 * 语音识别Activity
 * 使用Android的SpeechRecognizer API实现ASR功能
 */
class SpeechRecognizerActivity : AppCompatActivity(), RecognitionListener {

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var resultTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_recognizer)

        // 初始化视图
        resultTextView = findViewById(R.id.resultTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        // 检查权限
        checkPermission()

        // 初始化语音识别器
        initializeSpeechRecognizer()

        // 设置按钮点击事件
        startButton.setOnClickListener {
            if (!isListening) {
                startListening()
            }
        }

        stopButton.setOnClickListener {
            if (isListening) {
                stopListening()
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    private fun initializeSpeechRecognizer() {
        // 检查设备是否支持语音识别
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "语音识别不可用", Toast.LENGTH_SHORT).show()
            startButton.isEnabled = false
            return
        }
        startButton.isEnabled = true
        // 创建语音识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(this)

        // 配置语音识别意图
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN") // 设置为中文
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // 获取部分结果
    }

    private fun startListening() {
        resultTextView.text = "正在聆听..."
        startButton.isEnabled = false
        stopButton.isEnabled = true
        isListening = true
        speechRecognizer.startListening(recognizerIntent)
    }

    private fun stopListening() {
        startButton.isEnabled = true
        stopButton.isEnabled = false
        isListening = false
        speechRecognizer.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放资源
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }

    // RecognitionListener 接口实现
    override fun onReadyForSpeech(params: Bundle?) {
        resultTextView.text = "请开始说话..."
    }

    override fun onBeginningOfSpeech() {
        // 开始说话
    }

    override fun onRmsChanged(rmsdB: Float) {
        // 音量变化回调，可以用来显示音量大小
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // 接收到语音数据
    }

    override fun onEndOfSpeech() {
        // 说话结束
        startButton.isEnabled = true
        stopButton.isEnabled = false
        isListening = false
    }

    @SuppressLint("SetTextI18n")
    override fun onError(error: Int) {
        // 处理错误
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "音频错误"
            SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
            SpeechRecognizer.ERROR_NETWORK -> "网络错误"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
            SpeechRecognizer.ERROR_NO_MATCH -> "没有匹配的结果"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
            SpeechRecognizer.ERROR_SERVER -> "服务器错误"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
            else -> "未知错误 $error"
        }
        resultTextView.text = "错误: $errorMessage"
        startButton.isEnabled = true
        stopButton.isEnabled = false
        isListening = false
    }

    override fun onResults(results: Bundle?) {
        // 处理识别结果
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val bestMatch = matches[0]
            resultTextView.text = bestMatch
            
            // 这里可以处理识别到的文本，例如执行命令或者进行搜索等
            processRecognizedText(bestMatch)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onPartialResults(partialResults: Bundle?) {
        // 处理部分识别结果
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val partialText = matches[0]
            resultTextView.text = "$partialText..."
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // 其他事件
    }

    private fun processRecognizedText(text: String) {
        // 根据识别到的文本执行相应的操作
        // 例如：如果识别到"打开相机"，则启动相机应用
        when {
            text.contains("打开相机") -> {
                Toast.makeText(this, "正在打开相机...", Toast.LENGTH_SHORT).show()
                // 这里添加打开相机的代码
            }
            text.contains("返回") || text.contains("退出") -> {
                Toast.makeText(this, "正在退出...", Toast.LENGTH_SHORT).show()
                finish()
            }
            // 可以添加更多的命令处理
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
                initializeSpeechRecognizer()
            } else {
                Toast.makeText(this, "没有麦克风权限，语音识别无法工作", Toast.LENGTH_SHORT).show()
                startButton.isEnabled = false
            }
        }
    }
}