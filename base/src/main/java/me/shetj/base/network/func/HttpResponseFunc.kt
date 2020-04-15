package me.shetj.base.network.func

import io.reactivex.Observable
import io.reactivex.functions.Function
import me.shetj.base.network.exception.ApiException

class HttpResponseFunc<T> : Function<Throwable, Observable<T>> {
    @Throws(Exception::class)
    override fun apply(throwable: Throwable): Observable<T> {
        return Observable.error(ApiException.handleException(throwable))
    }
}