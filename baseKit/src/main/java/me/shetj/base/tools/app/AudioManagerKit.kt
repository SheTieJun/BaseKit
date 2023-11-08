

@file:Suppress("DEPRECATION")

package me.shetj.base.tools.app

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2022/4/28<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 音频焦点工具 <br>
 * @param context 上下文
 * @param lifecycleOwner 让焦点板顶生命周期，可选
 *
 * * [requestAudioFocus]  申请音频焦点
 * * [abandonFocus] 放弃音频焦点
 * * [setOnAudioFocusChangeListener] 监听焦点变化
 */

class AudioManagerKit(context: Context, private val lifecycleOwner: LifecycleOwner? = null) :
    LifecycleEventObserver {

    private var onAudioFocusChangeListener: OnAudioFocusChange? = null

    private var mAudioManager: AudioManager? = null

    private val focusChangeListener: OnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS ->
                    onAudioFocusChangeListener?.onLoss()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                    // 短暂性丢失焦点，当其他应用申请AUDIOFOCUS_GAIN_TRANSIENT或AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE时，
                    onAudioFocusChangeListener?.onLossTransient()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    onAudioFocusChangeListener?.onLossTransientCanDuck()
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    onAudioFocusChangeListener?.onGain()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0+
            audioFocusRequest!!.acceptsDelayedFocusGain()
            mAudioManager!!.requestAudioFocus(audioFocusRequest)
        } else {
            mAudioManager!!.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    /**
     * 放弃音频焦点，防止内存泄漏
     */
    fun abandonFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val abandonAudioFocusRequest =
                mAudioManager?.abandonAudioFocusRequest(audioFocusRequest!!)
            abandonAudioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager!!.abandonAudioFocus(focusChangeListener)
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

    private fun init(context: Context) {
        mAudioManager = context.applicationContext.getSystemService()
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            ON_CREATE -> {
                requestAudioFocus()
            }
            ON_DESTROY -> {
                abandonFocus()
                onAudioFocusChangeListener = null
                lifecycleOwner?.lifecycle?.removeObserver(this)
            }
            else -> {}
        }
    }

    interface OnAudioFocusChange {

        /**
         * 获得了Audio Focus；
         */
        fun onLoss() {
        }

        /**
         * 失去了Audio Focus，并将会持续很长的时间。这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
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
