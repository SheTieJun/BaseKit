package me.shetj.base.model

import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import me.shetj.base.ktx.requestNetWork
import me.shetj.base.tools.app.NetworkUtils
import me.shetj.base.tools.app.Utils


/**
 * 网络状态变更
 */
class NetWorkLiveDate private constructor() : MutableLiveData<NetWorkLiveDate.NetWorkInfo>() {

    sealed class NetType() {
        object WIFI : NetType()
        object PHONE : NetType()
        object NONE : NetType() //初始化，或者没有网络
    }

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    @RequiresPermission(allOf = ["android.permission.CHANGE_NETWORK_STATE"])
    fun start(context: Context){
        context.requestNetWork()
    }

    fun onAvailable() {
        if (value?.hasNet == true) return
        postValue(value?.copy(hasNet = true))
    }

    fun onLost() {
        if (value?.hasNet == false) return
        postValue(value?.copy(hasNet = false,netType = NetType.NONE))
    }

    fun setNetType(netType: NetType) {
        if (value?.netType == netType) return
        postValue(value?.copy(hasNet = true,netType = netType))
    }

    companion object {

        @Volatile private var networkLiveData: NetWorkLiveDate? = null

        @JvmStatic
        fun getInstance(): NetWorkLiveDate {
            return networkLiveData ?: synchronized(NetWorkLiveDate::class.java) {
                return NetWorkLiveDate().apply {
                    networkLiveData = this
                    this.value = NetWorkInfo()
                }
            }
        }

    }

    data class NetWorkInfo(var hasNet: Boolean = NetworkUtils.isAvailable(Utils.app),
                           var netType: NetType = NetType.NONE) {
    }
}