package me.shetj.base.tools.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.annotation.FloatRange
import androidx.core.math.MathUtils
import me.shetj.base.model.VolumeLiveData
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 声音音量控制
 */
internal class VolumeConfig(val context: Context) {

    private val isRegister = AtomicBoolean(false)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                VolumeLiveData.getInstance().postValue(getCurVolume())
            }
        }
    }

    private val intentFilter = IntentFilter().apply {
        addAction("android.media.VOLUME_CHANGED_ACTION")
    }

    fun getCurVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    fun registerReceiver() {
        if (isRegister.compareAndSet(false, true)) {
            context.registerReceiver(mReceiver, intentFilter)
        }
    }

    fun unregisterReceiver() {
        // 回到最初的声音
        if (isRegister.compareAndSet(true, false)) {
            context.unregisterReceiver(mReceiver)
        }
    }

    fun getMaxVoice() = max

    fun setAudioVoiceF(@FloatRange(from = 0.0, to = 1.0) volume: Float) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (MathUtils.clamp(volume, 0f, 1f) * max).toInt(),
            0
        )
    }

    fun setAudioVoice(volume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            MathUtils.clamp(volume, 0, max),
            0
        )
    }

    companion object {
        @Volatile
        private var sInstance: VolumeConfig? = null

        fun getInstance(context: Context): VolumeConfig {
            return sInstance ?: synchronized(VolumeConfig::class.java) {
                return VolumeConfig(context.applicationContext).also {
                    VolumeLiveData.getInstance().postValue(it.getCurVolume())
                    sInstance = it
                }
            }
        }

        fun onDestroy() {
            sInstance?.unregisterReceiver()
            sInstance = null
        }
    }
}
