package me.shetj.base.model

import androidx.lifecycle.MutableLiveData


/**
 * 耳机状态变更
 */
class PlugLiveData private constructor() : MutableLiveData<Boolean>(false) {

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
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