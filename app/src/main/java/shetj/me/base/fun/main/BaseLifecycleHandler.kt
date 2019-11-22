package shetj.me.base.`fun`.main

import android.os.Handler
import android.os.Looper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

import io.reactivex.annotations.NonNull

/**
 * 绑定生命周期的Handler
 */
open class BaseLifecycleHandler : Handler, LifecycleObserver {

    constructor(@NonNull owner: LifecycleOwner) {
        bindLifecycleOwner(owner)
    }

    constructor(@NonNull owner: LifecycleOwner, callback: Callback) : super(callback) {
        bindLifecycleOwner(owner)
    }

    constructor(@NonNull owner: LifecycleOwner, looper: Looper) : super(looper) {
        bindLifecycleOwner(owner)
    }

    @JvmOverloads
    constructor(@NonNull owner: LifecycleOwner, looper: Looper, callback: Callback) : super(looper, callback) {
        bindLifecycleOwner(owner)
    }

    /**
     * bind lifecycleOwner for handler that can remove all pending messages when ON_DESTROY Event occur
     */
    private fun bindLifecycleOwner(owner: LifecycleOwner?) {
        owner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner?) {
        // 移除队列中所有未执行的消息
        removeCallbacksAndMessages(null)
        owner?.lifecycle?.removeObserver(this)
    }
}