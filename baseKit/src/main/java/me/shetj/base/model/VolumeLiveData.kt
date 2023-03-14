package me.shetj.base.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import me.shetj.base.tools.app.VolumeConfig

/**
 * 手机媒体音乐变更
 */
class VolumeLiveData private constructor() : MutableLiveData<Int>() {

    private var volumeConfig: VolumeConfig? = null

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    fun start(context: Context) {
        VolumeConfig.getInstance(context.applicationContext).apply {
            registerReceiver()
        }.also {
            volumeConfig = it
        }
    }

    fun stop() {
        volumeConfig?.unregisterReceiver()
        volumeConfig = null
    }

    companion object {

        @Volatile
        private var mVolumeLiveData: VolumeLiveData? = null

        @JvmStatic
        fun getInstance(): VolumeLiveData {
            return mVolumeLiveData ?: synchronized(VolumeLiveData::class.java) {
                return VolumeLiveData().also {
                    mVolumeLiveData = it
                }
            }
        }
    }
}
