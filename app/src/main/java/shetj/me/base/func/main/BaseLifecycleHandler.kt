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


package shetj.me.base.func.main

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * 绑定生命周期的Handler
 */
open class BaseLifecycleHandler : Handler, LifecycleEventObserver {

    constructor(@NonNull owner: LifecycleOwner) : super(Looper.getMainLooper()) {
        bindLifecycleOwner(owner)
    }

    constructor(
        @NonNull owner: LifecycleOwner,
        callback: Callback
    ) : super(Looper.getMainLooper(), callback) {
        bindLifecycleOwner(owner)
    }

    constructor(@NonNull owner: LifecycleOwner, looper: Looper) : super(looper) {
        bindLifecycleOwner(owner)
    }

    constructor(
        @NonNull owner: LifecycleOwner,
        looper: Looper, callback: Callback
    ) : super(looper, callback) {
        bindLifecycleOwner(owner)
    }

    /**
     * bind lifecycleOwner for handler that can remove all pending messages when ON_DESTROY Event occur
     */
    private fun bindLifecycleOwner(owner: LifecycleOwner?) {
        owner?.lifecycle?.addObserver(this)


    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            // 移除队列中所有未执行的消息
            removeCallbacksAndMessages(null)
            source?.lifecycle?.removeObserver(this)
        }
    }
}