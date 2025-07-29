package shetj.me.base.func.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * 语音识别管理器
 * 封装了Android SpeechRecognizer的功能，方便在应用中使用语音识别
 */
class SpeechRecognizerManager(private val context: Context) {

    companion object {
        private const val TAG = "SpeechRecognizerManager"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private var listener: SpeechRecognitionListener? = null
    private var isListening = false

    /**
     * 初始化语音识别器
     * @param language 识别语言，默认为中文
     * @return 是否初始化成功
     */
    fun initialize(language: String = "zh-CN"): Boolean {
        // 检查设备是否支持语音识别
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition is not available on this device")
            return false
        }

        try {
            // 创建语音识别器
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

            // 配置语音识别意图
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // 获取部分结果
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing speech recognizer: ${e.message}")
            return false
        }
    }

    /**
     * 设置语音识别监听器
     * @param listener 监听器接口实现
     */
    fun setListener(listener: SpeechRecognitionListener) {
        this.listener = listener

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                listener.onReadyForSpeech()
            }

            override fun onBeginningOfSpeech() {
                listener.onBeginningOfSpeech()
            }

            override fun onRmsChanged(rmsdB: Float) {
                listener.onRmsChanged(rmsdB)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // 不需要处理
            }

            override fun onEndOfSpeech() {
                isListening = false
                listener.onEndOfSpeech()
            }

            override fun onError(error: Int) {
                isListening = false
                listener.onError(getErrorMessage(error))
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    listener.onResults(matches)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    listener.onPartialResults(matches)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // 不需要处理
            }
        })
    }

    /**
     * 开始语音识别
     * @return 是否成功启动
     */
    fun startListening(): Boolean {
        if (speechRecognizer == null || recognizerIntent == null) {
            Log.e(TAG, "Speech recognizer not initialized")
            return false
        }

        if (listener == null) {
            Log.e(TAG, "No listener set")
            return false
        }

        try {
            isListening = true
            speechRecognizer?.startListening(recognizerIntent)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition: ${e.message}")
            isListening = false
            return false
        }
    }

    /**
     * 停止语音识别
     */
    fun stopListening() {
        if (isListening) {
            isListening = false
            speechRecognizer?.stopListening()
        }
    }

    /**
     * 取消语音识别
     */
    fun cancel() {
        isListening = false
        speechRecognizer?.cancel()
    }

    /**
     * 释放资源
     */
    fun destroy() {
        isListening = false
        speechRecognizer?.destroy()
        speechRecognizer = null
        recognizerIntent = null
        listener = null
    }

    /**
     * 是否正在监听
     * @return 是否正在监听
     */
    fun isListening(): Boolean {
        return isListening
    }

    /**
     * 获取错误信息
     * @param errorCode 错误代码
     * @return 错误描述
     */
    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "音频错误"
            SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
            SpeechRecognizer.ERROR_NETWORK -> "网络错误"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
            SpeechRecognizer.ERROR_NO_MATCH -> "没有匹配的结果"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
            SpeechRecognizer.ERROR_SERVER -> "服务器错误"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
            else -> "未知错误 $errorCode"
        }
    }

    /**
     * 语音识别监听器接口
     */
    interface SpeechRecognitionListener {
        /**
         * 准备好开始说话
         */
        fun onReadyForSpeech()

        /**
         * 开始说话
         */
        fun onBeginningOfSpeech()

        /**
         * 音量变化
         * @param rmsdB 音量大小
         */
        fun onRmsChanged(rmsdB: Float)

        /**
         * 说话结束
         */
        fun onEndOfSpeech()

        /**
         * 识别错误
         * @param errorMessage 错误信息
         */
        fun onError(errorMessage: String)

        /**
         * 最终识别结果
         * @param results 识别结果列表
         */
        fun onResults(results: ArrayList<String>)

        /**
         * 部分识别结果
         * @param partialResults 部分识别结果列表
         */
        fun onPartialResults(partialResults: ArrayList<String>)
    }
}