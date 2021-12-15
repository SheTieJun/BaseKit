/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package me.shetj.base.model

import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import me.shetj.base.ktx.requestNetWork
import me.shetj.base.tools.app.NetworkUtils
import me.shetj.base.tools.app.Utils
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 网络状态变更
 */
class NetWorkLiveDate private constructor() : MutableLiveData<NetWorkLiveDate.NetWorkInfo>() {

    sealed class NetType() {
        object WIFI : NetType()
        object PHONE : NetType()
        object NONE : NetType() //初始化，或者没有网络
    }

    private val isStarted:AtomicBoolean = AtomicBoolean(false)

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    @RequiresPermission(allOf = ["android.permission.CHANGE_NETWORK_STATE"])
    fun start(context: Context){
        if (isStarted.compareAndSet(false,true)) {
            context.applicationContext.requestNetWork()
        }
    }

    internal fun onAvailable() {
        if (value?.hasNet == true) return
        postValue(value?.copy(hasNet = true))
    }

    internal fun onLost() {
        if (value?.hasNet == false) return
        postValue(value?.copy(hasNet = false,netType = NetType.NONE))
    }

    internal fun setNetType(netType: NetType) {
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
                           var netType: NetType = NetType.NONE)
}