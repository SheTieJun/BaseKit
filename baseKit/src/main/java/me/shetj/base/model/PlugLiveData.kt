package me.shetj.base.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import me.shetj.base.tools.app.PlugConfigs

/**
 * 耳机状态变更
 */
class PlugLiveData private constructor() : MutableLiveData<Boolean>(false) {

    private var plugConfigs: PlugConfigs? = null

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    fun start(context: Context) {
        PlugConfigs.getInstance(context.applicationContext).apply {
            registerReceiver()
        }.also {
            plugConfigs = it
        }
    }

    fun stop() {
        plugConfigs?.unregisterReceiver()
        plugConfigs = null
    }

    internal fun connect() {
        postValue(true)
    }

    internal fun disConnect() {
        postValue(false)
    }

    companion object {

        @Volatile
        private var mPlugLiveData: PlugLiveData? = null

        @JvmStatic
        fun getInstance(): PlugLiveData {
            return mPlugLiveData ?: synchronized(PlugLiveData::class.java) {
                return PlugLiveData().also {
                    mPlugLiveData = it
                }
            }
        }
    }
}
