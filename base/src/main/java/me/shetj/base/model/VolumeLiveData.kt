package me.shetj.base.model

import androidx.lifecycle.MutableLiveData


/**
 * 手机媒体音乐变更
 */
class VolumeLiveData private constructor():MutableLiveData<Int>(){

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    companion object {

        @Volatile private var mPlugLiveData: VolumeLiveData? = null

        @JvmStatic
        fun getInstance(): VolumeLiveData {
            return mPlugLiveData ?: synchronized(VolumeLiveData::class.java) {
                return VolumeLiveData().also{
                    mPlugLiveData = it
                }
            }
        }
    }
}