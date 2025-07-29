package shetj.me.base.func.speech

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import shetj.me.base.R

/**
 * 语音识别功能入口Activity
 * 提供三种不同的语音识别实现方式的入口
 */
class SpeechRecognitionEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_recognition_entry)

        // 设置标题
        title = "语音识别功能"

        // 初始化按钮
        val basicButton: Button = findViewById(R.id.basicButton)
        val simpleButton: Button = findViewById(R.id.simpleButton)
        val continuousButton: Button = findViewById(R.id.continuousButton)

        // 设置按钮点击事件
        basicButton.setOnClickListener {
            // 打开基本语音识别Activity
            startActivity(Intent(this, SpeechRecognizerActivity::class.java))
        }

        simpleButton.setOnClickListener {
            // 打开简易语音识别Activity
            startActivity(Intent(this, SimpleSpeechActivity::class.java))
        }

        continuousButton.setOnClickListener {
            // 打开持续语音识别Activity
            startActivity(Intent(this, ContinuousSpeechActivity::class.java))
        }
    }
}