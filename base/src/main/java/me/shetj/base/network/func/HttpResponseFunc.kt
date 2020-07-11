package me.shetj.base.network.func

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import me.shetj.base.network.exception.ApiException

class HttpResponseFunc<T> : Function<Throwable, Observable<T>> {
    @Throws(Exception::class)
    override fun apply(throwable: Throwable): Observable<T> {
        return Observable.error(ApiException.handleException(throwable))
    }
}