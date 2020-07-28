package me.shetj.base.model

import androidx.lifecycle.MutableLiveData
import me.shetj.base.tools.app.NetworkUtils
import me.shetj.base.tools.app.Utils


class NetWorkLiveDate private constructor() : MutableLiveData<NetWorkLiveDate.NetWorkInfo>() {

    sealed class NetType() {
        object WIFI : NetType()
        object PHONE : NetType()
        object AUTO : NetType()
    }

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    fun onAvailable() {
        if (value?.hasNet == true) return
        postValue(value?.copy(hasNet = true))
    }

    fun onLost() {
        if (value?.hasNet == false) return
        postValue(value?.copy(hasNet = false,netType = NetType.AUTO))
    }

    fun setNetType(netType: NetType) {
        if (value?.netType == netType) return
        postValue(value?.copy(hasNet = true,netType = netType))
    }

    companion object {

        private var networkLiveData: NetWorkLiveDate? = null

        @JvmStatic
        fun getInstance(): NetWorkLiveDate {
            return networkLiveData ?: synchronized(NetWorkLiveDate::class.java) {
                return NetWorkLiveDate().apply {
                    networkLiveData = this
                    this.value = NetWorkInfo(hasNet = NetworkUtils.isAvailable(Utils.app))
                }
            }
        }

    }

    data class NetWorkInfo(var hasNet: Boolean = true,
                           var netType: NetType = NetType.AUTO) {
    }
}