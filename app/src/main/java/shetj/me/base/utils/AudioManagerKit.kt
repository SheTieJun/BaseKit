@file:Suppress("DEPRECATION")

package shetj.me.base.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 *
 * 用ExoPlayer他会自行处理，就不需要用的这个了
 * @param context 上下文
 * @param lifecycleOwner 让焦点板顶生命周期，可选
 *
 * * [requestAudioFocus]  申请音频焦点
 * * [abandonFocus] 放弃音频焦点
 * * [setOnAudioFocusChangeListener] 监听焦点变化
 */

class AudioManagerKit(context: Context, val isPlayIng: () -> Boolean) {

    private var onAudioFocusChangeListener: OnAudioFocusChange? = null

    private var mAudioManager: AudioManager? = null

    private var playbackDelayed = false
    private var playbackNowAuthorized = false
    private var resumeOnFocusGain = false

    private val focusLock = Any()

    private val focusChangeListener: OnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    synchronized(focusLock) {
                        resumeOnFocusGain = false
                        playbackDelayed = false
                    }
                    onAudioFocusChangeListener?.onLoss()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    synchronized(focusLock) {
                        // only resume if playback is being interrupted
                        resumeOnFocusGain = isPlayIng()
                        playbackDelayed = false
                    }
                    // 短暂性丢失焦点，当其他应用申请AUDIOFOCUS_GAIN_TRANSIENT或AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE时，
                    onAudioFocusChangeListener?.onLossTransient()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    onAudioFocusChangeListener?.onLossTransientCanDuck()
                }

                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (playbackDelayed || resumeOnFocusGain) {
                        synchronized(focusLock) {
                            playbackDelayed = false
                            resumeOnFocusGain = false
                        }
                        onAudioFocusChangeListener?.onGain()
                    }
                }
            }
        }

    private val audioFocusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0+
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_GAME)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                }
            )
            .setWillPauseWhenDucked(true)
            .setOnAudioFocusChangeListener(focusChangeListener).build()
    } else {
        null
    }

    init {
        init(context)
    }

    fun getAudioManager() = mAudioManager

    /**
     * 申请音频焦点
     */
    fun requestAudioFocus() {
        if (mAudioManager == null) return
        val res = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0+
            if (audioFocusRequest == null) return
            mAudioManager!!.requestAudioFocus(audioFocusRequest)
        } else {
            mAudioManager?.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        synchronized(focusLock) {
            playbackNowAuthorized = when (res) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                    onAudioFocusChangeListener?.onLoss()
                    false
                }

                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    onAudioFocusChangeListener?.onGain()
                    true
                }

                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                    onAudioFocusChangeListener?.onGain()
                    false
                }

                else -> false
            }
        }
    }

    /**
     * 放弃音频焦点，防止内存泄漏
     */
    fun abandonFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val abandonAudioFocusRequest =
                audioFocusRequest?.let { mAudioManager?.abandonAudioFocusRequest(it) }
            abandonAudioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    mAudioManager?.abandonAudioFocus(focusChangeListener)
        }
    }

    fun setOnAudioFocusChangeListener(onAudioFocusChangeListener: OnAudioFocusChange) {
        this.onAudioFocusChangeListener = onAudioFocusChangeListener
    }

    fun adjustStreamVolume() {
        mAudioManager?.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    // 获取最佳采样率
    fun getBestSampleRate(): Int {
        val sampleRateStr: String? = mAudioManager?.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        val sampleRate: Int = sampleRateStr?.let { str ->
            Integer.parseInt(str).takeUnless { it == 0 }
        } ?: 44100 // Use a default value if property not found
        return sampleRate
    }

    // 获取最佳缓冲大小
    fun getBestBufferSize(): Int {
        val bufferSizeStr: String? = mAudioManager?.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        val bufferSize: Int = bufferSizeStr?.let { str ->
            Integer.parseInt(str).takeUnless { it == 0 }
        } ?: 256 // Use a default value if property not found
        return bufferSize
    }

    private fun init(context: Context) {
        mAudioManager = ContextCompat.getSystemService(context.applicationContext, AudioManager::class.java) as AudioManager
    }

    fun onDestroy() {
        mAudioManager = null
        onAudioFocusChangeListener = null
        abandonFocus()
    }

    interface OnAudioFocusChange {

        /**
         * 失去了Audio Focus，并将会持续很长的时间。这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
         */
        fun onLoss() {
        }

        /**
         * 获得了Audio Focus；
         */
        fun onGain() {
        }

        /**
         * 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
         */
        fun onLossTransient() {
        }

        /**
         * 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
         */
        fun onLossTransientCanDuck() {
        }
    }
}
