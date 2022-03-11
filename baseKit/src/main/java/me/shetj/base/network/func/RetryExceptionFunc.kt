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
package me.shetj.base.network.func

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import me.shetj.base.network.exception.ApiException
import timber.log.Timber

class RetryExceptionFunc : Function<Observable<out Throwable>, Observable<*>> {
    /* retry次数*/
    private var count = 0

    /*延迟*/
    private var delay: Long = 500

    /*叠加延迟*/
    private var increaseDelay: Long = 1000

    constructor()
    constructor(count: Int, delay: Long) {
        this.count = count
        this.delay = delay
    }

    constructor(count: Int, delay: Long, increaseDelay: Long) {
        this.count = count
        this.delay = delay
        this.increaseDelay = increaseDelay
    }

    @Throws(Exception::class)
    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable.zipWith(
            Observable.range(1, count + 1),
            { throwable, integer -> Wrapper(throwable, integer) }
        )
            .flatMap { wrapper ->
                if (wrapper.index > 1) Timber.i("重试次数：%s", wrapper.index)
                var errCode = 0
                if (wrapper.throwable is ApiException) {
                    val exception: ApiException = wrapper.throwable
                    errCode = exception.code
                }
                if ((
                    wrapper.throwable is ConnectException ||
                        wrapper.throwable is SocketTimeoutException ||
                        errCode == ApiException.ERROR.NETWORD_ERROR ||
                        errCode == ApiException.ERROR.TIMEOUT_ERROR ||
                        wrapper.throwable is SocketTimeoutException ||
                        wrapper.throwable is TimeoutException
                    ) &&
                    wrapper.index < count + 1 // 如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                ) {

                    Observable.timer(
                        delay + (wrapper.index - 1) * increaseDelay,
                        TimeUnit.MILLISECONDS
                    )
                } else Observable.error<Any>(wrapper.throwable)
            }
    }

    private inner class Wrapper(val throwable: Throwable, val index: Int)
}
