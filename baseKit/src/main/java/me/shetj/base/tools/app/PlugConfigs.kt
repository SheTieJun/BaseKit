package me.shetj.base.tools.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import me.shetj.base.model.PlugLiveData
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 用来判断是否连接上了耳机
 */
@Suppress("DEPRECATION")
internal class PlugConfigs(val context: Context, var connected: Boolean = false) {

    private val isRegister = AtomicBoolean(false)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_HEADSET_PLUG) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        getInstance(context).connected = false
                        PlugLiveData.getInstance().disConnect()
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        PlugLiveData.getInstance().connect()
                    }
                }
            }
        }
    }
    private val intentFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)

    fun registerReceiver() {
        if (isRegister.compareAndSet(false, true)) {
            connected = audioManager.isWiredHeadsetOn
            PlugLiveData.getInstance().postValue(connected)
            context.registerReceiver(mReceiver, intentFilter)
        }
    }

    fun unregisterReceiver() {
        if (isRegister.compareAndSet(true, false)) {
            context.unregisterReceiver(mReceiver)
        }
    }

    fun getMaxVoice() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    fun setAudioVoice(volume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            0
        )
    }

    companion object {
        @Volatile
        private var sInstance: PlugConfigs? = null

        fun getInstance(context: Context): PlugConfigs {
            return sInstance ?: synchronized(PlugConfigs::class.java) {
                return PlugConfigs(context).also {
                    it.connected = it.audioManager.isWiredHeadsetOn
                    PlugLiveData.getInstance().postValue(it.connected)
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
