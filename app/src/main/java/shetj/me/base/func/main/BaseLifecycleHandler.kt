package shetj.me.base.func.main

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


/**
 * 绑定生命周期的Handler
 */
open class BaseLifecycleHandler : Handler, LifecycleObserver {

    constructor(@NonNull owner: LifecycleOwner): super(Looper.getMainLooper()){
        bindLifecycleOwner(owner)
    }

    constructor(@NonNull owner: LifecycleOwner, callback: Callback) : super(Looper.getMainLooper(),callback) {
        bindLifecycleOwner(owner)
    }

    constructor(@NonNull owner: LifecycleOwner, looper: Looper) : super(looper) {
        bindLifecycleOwner(owner)
    }

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