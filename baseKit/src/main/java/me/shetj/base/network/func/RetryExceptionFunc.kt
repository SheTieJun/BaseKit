package me.shetj.base.network.func

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import me.shetj.base.network.exception.ApiException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
        return observable.zipWith(Observable.range(1, count + 1),
            { throwable, integer -> Wrapper(throwable, integer) })
                .flatMap { wrapper ->
                    if (wrapper.index > 1) Timber.i("重试次数：%s", wrapper.index)
                    var errCode = 0
                    if (wrapper.throwable is ApiException) {
                        val exception: ApiException = wrapper.throwable
                        errCode = exception.code
                    }
                    if ((wrapper.throwable is ConnectException
                                || wrapper.throwable is SocketTimeoutException
                                || errCode == ApiException.ERROR.NETWORD_ERROR
                                || errCode == ApiException.ERROR.TIMEOUT_ERROR
                                || wrapper.throwable is SocketTimeoutException
                                || wrapper.throwable is TimeoutException)
                        && wrapper.index < count + 1    //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
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