package me.shetj.base.network.kt

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import me.shetj.base.network.func.HandleFuc
import me.shetj.base.network.func.HttpResponseFunc
import me.shetj.base.network.model.ApiResult
import timber.log.Timber

internal object RxUtil {


    fun <T> io_main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> _io_main(): ObservableTransformer<ApiResult<T>, T> {
        return ObservableTransformer<ApiResult<T>, T> { upstream ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(HandleFuc<T>())
                    .doFinally { Timber.i("+++doFinally+++") }
                    .onErrorResumeNext(HttpResponseFunc<T>())
        }
    }

    fun <T> _main(): ObservableTransformer<ApiResult<T>, T> {
        return ObservableTransformer<ApiResult<T>, T> { upstream ->
            upstream.map(HandleFuc<T>())
                    .onErrorResumeNext(HttpResponseFunc<T>())
        }
    }
}