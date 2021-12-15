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
package me.shetj.base.network.subscriber

import android.content.Context
import io.reactivex.rxjava3.observers.DisposableObserver
import java.lang.ref.WeakReference
import me.shetj.base.network.exception.ApiException
import me.shetj.base.tools.app.NetworkUtils.isAvailable
import timber.log.Timber

// 继承DisposableObserver 允许被取消
abstract class BaseSubscriber<T> : DisposableObserver<T> {
    private var contextWeakReference: WeakReference<Context?>? = null

    constructor()

    override fun onStart() {
        if (contextWeakReference != null && contextWeakReference!!.get() != null &&
            !isAvailable(contextWeakReference!!.get()!!)
        ) {
            onComplete()
        }
    }

    constructor(context: Context?) {
        if (context != null) {
            contextWeakReference = WeakReference(context)
        }
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable) {
        Timber.i("-->http is onError")
        if (e is ApiException) {
            Timber.i("--> e is ApiException err:$e")
            onError(e)
        } else {
            Timber.i("--> e is not ApiException err:$e")
            onError(ApiException.handleException(e))
        }
    }

    override fun onComplete() {
    }

    abstract fun onError(e: ApiException)
}
