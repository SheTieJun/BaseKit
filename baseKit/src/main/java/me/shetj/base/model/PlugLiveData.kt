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
import androidx.lifecycle.MutableLiveData
import me.shetj.base.tools.app.PlugConfigs

/**
 * 耳机状态变更
 */
class PlugLiveData private constructor() : MutableLiveData<Boolean>(false) {

    private var plugConfigs: PlugConfigs ? = null

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
