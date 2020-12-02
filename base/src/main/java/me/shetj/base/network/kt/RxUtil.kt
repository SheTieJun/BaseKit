package me.shetj.base.network.kt

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import me.shetj.base.network.func.HandleFuc
import me.shetj.base.network.func.HttpResponseFunc
import me.shetj.base.network.model.ApiResult
import timber.log.Timber

object RxUtil {


    fun <T> io_main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> _io_main(): ObservableTransformer<ApiResult<T>, T> {
        return ObservableTransformer { upstream ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(HandleFuc<T>())
                    .onErrorResumeNext(HttpResponseFunc<T>())
        }
    }

    fun <T> _computation(): ObservableTransformer<ApiResult<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.map(HandleFuc<T>())
                    .onErrorResumeNext(HttpResponseFunc<T>())
        }
    }
}