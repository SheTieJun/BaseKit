package me.shetj.base.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.base.BaseKit
import me.shetj.base.ktx.requestNetWork
import me.shetj.base.tools.app.NetworkUtils

/**
 * 网络状态变更
 */
class NetWorkLiveDate private constructor(netWorkInfo: NetWorkInfo) :
    MutableLiveData<NetWorkLiveDate.NetWorkInfo>(netWorkInfo) {

    sealed class NetType() {
        object WIFI : NetType()
        object PHONE : NetType()
        object NONE : NetType() // 初始化，或者没有网络
    }

    private val isStarted: AtomicBoolean = AtomicBoolean(false)

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    fun start(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CHANGE_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (isStarted.compareAndSet(false, true)) {
            context.applicationContext.requestNetWork()
        }
    }

    fun isStart(): Boolean {
        return isStarted.get()
    }

    internal fun onAvailable() {
        if (value?.hasNet == true) return
        postValue(value?.copy(hasNet = true))
    }

    internal fun onLost() {
        if (value?.hasNet == false) return
        postValue(value?.copy(hasNet = false, netType = NetType.NONE))
    }

    internal fun setNetType(netType: NetType) {
        if (value?.netType == netType) return
        postValue(value?.copy(hasNet = true, netType = netType))
    }

    companion object {

        @Volatile
        private var networkLiveData: NetWorkLiveDate? = null

        @JvmStatic
        fun getInstance(): NetWorkLiveDate {
            return networkLiveData ?: synchronized(NetWorkLiveDate::class.java) {
                return NetWorkLiveDate(NetWorkInfo()).apply {
                    networkLiveData = this
                }
            }
        }
    }

    data class NetWorkInfo(
        var hasNet: Boolean = NetworkUtils.isAvailable(BaseKit.app),
        var netType: NetType = NetType.NONE
    )
}
