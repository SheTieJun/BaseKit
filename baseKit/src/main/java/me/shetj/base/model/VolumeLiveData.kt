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
