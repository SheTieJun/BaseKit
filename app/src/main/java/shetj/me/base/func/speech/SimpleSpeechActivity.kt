package shetj.me.base.func.speech

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import shetj.me.base.R

/**
 * 简化版语音识别Activity
 * 使用SpeechRecognizerManager来实现语音识别功能
 */
class SimpleSpeechActivity : AppCompatActivity(), SpeechRecognizerManager.SpeechRecognitionListener {

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private lateinit var speechManager: SpeechRecognizerManager
    private lateinit var resultTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_speech)

        // 初始化视图
        resultTextView = findViewById(R.id.resultTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        // 检查权限
        checkPermission()

        // 初始化语音识别管理器
        speechManager = SpeechRecognizerManager(this)
        if (speechManager.initialize()) {
            speechManager.setListener(this)
        } else {
            Toast.makeText(this, "语音识别初始化失败", Toast.LENGTH_SHORT).show()
            startButton.isEnabled = false
        }

        // 设置按钮点击事件
        startButton.setOnClickListener {
            if (!speechManager.isListening()) {
                if (speechManager.startListening()) {
                    startButton.isEnabled = false
                    stopButton.isEnabled = true
                }
            }
        }

        stopButton.setOnClickListener {
            if (speechManager.isListening()) {
                speechManager.stopListening()
                startButton.isEnabled = true
                stopButton.isEnabled = false
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

    override fun onDestroy() {
        super.onDestroy()
        // 释放资源
        speechManager.destroy()
    }

    // SpeechRecognitionListener 接口实现
    override fun onReadyForSpeech() {
        resultTextView.text = "请开始说话..."
    }

    override fun onBeginningOfSpeech() {
        // 开始说话
    }

    override fun onRmsChanged(rmsdB: Float) {
        // 可以用来显示音量变化
    }

    override fun onEndOfSpeech() {
        // 说话结束
        startButton.isEnabled = true
        stopButton.isEnabled = false
    }

    override fun onError(errorMessage: String) {
        resultTextView.text = "错误: $errorMessage"
        startButton.isEnabled = true
        stopButton.isEnabled = false
    }

    override fun onResults(results: ArrayList<String>) {
        if (results.isNotEmpty()) {
            val recognizedText = results[0]
            resultTextView.text = recognizedText
            
            // 处理识别到的文本
            processRecognizedText(recognizedText)
        }
    }

    override fun onPartialResults(partialResults: ArrayList<String>) {
        if (partialResults.isNotEmpty()) {
            resultTextView.text = "${partialResults[0]}..."
        }
    }

    private fun processRecognizedText(text: String) {
        // 根据识别到的文本执行相应的操作
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
                // 重新初始化语音识别器
                if (speechManager.initialize()) {
                    speechManager.setListener(this)
                    startButton.isEnabled = true
                }
            } else {
                Toast.makeText(this, "没有麦克风权限，语音识别无法工作", Toast.LENGTH_SHORT).show()
                startButton.isEnabled = false
            }
        }
    }
}