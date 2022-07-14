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
package me.shetj.base.ktx

import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import java.util.concurrent.atomic.AtomicBoolean

/****************************************** LiveData ***************************************/
/*** @Author stj
 * * @Date 2021/10/8-18:38
 * * @Email 375105540@qq.com
 * * 间隔固定时间内，取最后一个
 */
fun <T> LiveData<T>.throttleLast(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true) // 用来通知发送delay

    val runnable = Runnable {
        if (isUpdate.compareAndSet(false, true)) {
            mld.value = source.value
        }
    }

    mld.addSource(source) {
        if (isUpdate.compareAndSet(true, false)) {
            handler.postDelayed(runnable, duration)
        }
    }
}

/*** @Author stj
 * * @Date 2021/10/8-18:38
 * * @Email 375105540@qq.com
 * * 间隔固定时间内，取第一个的值
 */
fun <T> LiveData<T>.throttleFirst(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true)

    val runnable = Runnable {
        isUpdate.set(true)
    }

    mld.addSource(source) {
        if (isUpdate.compareAndSet(true, false)) {
            mld.value = source.value
            handler.postDelayed(runnable, duration)
        }
    }
}

/**
 * 一段时间内最新的值
 */
fun <T> LiveData<T>.throttleLatest(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->

    val isLatest = AtomicBoolean(true)
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true)

    val runnable = Runnable {
        if (isUpdate.compareAndSet(false, true)) {
            mld.value = source.value
        }
    }

    mld.addSource(source) {
        if (isLatest.compareAndSet(true, false)) {
            mld.value = source.value
        }
        if (isUpdate.compareAndSet(true, false)) {
            handler.postDelayed(runnable, duration)
        }
    }
}

/**  debounce
 * @Author stj
 * @Date 2021/10/8-18:38
 * @Email 375105540@qq.com
 * 2个值之间必须间隔，固定时间
 */
fun <T> LiveData<T>.debounce(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())

    val runnable = Runnable {
        mld.value = source.value
    }

    mld.addSource(source) {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, duration)
    }
}

fun LiveData<Boolean>.isTrue() = value == true
