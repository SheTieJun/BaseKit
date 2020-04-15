package me.shetj.base.network.subscriber

import android.content.Context
import io.reactivex.observers.DisposableObserver
import me.shetj.base.network.exception.ApiException
import me.shetj.base.tools.app.NetworkUtils.isAvailable
import timber.log.Timber
import java.lang.ref.WeakReference

//继承DisposableObserver 允许被取消
abstract class BaseSubscriber<T> : DisposableObserver<T> {
    private var contextWeakReference: WeakReference<Context?>? = null

    constructor()

    override fun onStart() {
        if (contextWeakReference != null && contextWeakReference!!.get() != null && !isAvailable(contextWeakReference!!.get()!!)) {
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
            Timber.i("--> e instanceof ApiException err:$e")
            onError(e)
        } else {
            Timber.i("--> e !instanceof ApiException err:$e")
            onError(ApiException.handleException(e))
        }
    }

    override fun onComplete() {
    }

    abstract fun onError(e: ApiException)
}