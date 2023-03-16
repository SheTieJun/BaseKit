

package shetj.me.base.func.main

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


/**
 * 绑定生命周期的Handler
 */
open class BaseLifecycleHandler : Handler, LifecycleEventObserver {

    constructor(@NonNull owner: LifecycleOwner) : super(Looper.getMainLooper()) {
        bindLifecycleOwner(owner)
    }

    constructor(
        @NonNull owner: LifecycleOwner,
        callback: Callback
    ) : super(Looper.getMainLooper(), callback) {
        bindLifecycleOwner(owner)
    }

    constructor(@NonNull owner: LifecycleOwner, looper: Looper) : super(looper) {
        bindLifecycleOwner(owner)
    }

    constructor(
        @NonNull owner: LifecycleOwner,
        looper: Looper, callback: Callback
    ) : super(looper, callback) {
        bindLifecycleOwner(owner)
    }

    /**
     * bind lifecycleOwner for handler that can remove all pending messages when ON_DESTROY Event occur
     */
    private fun bindLifecycleOwner(owner: LifecycleOwner?) {
        owner?.lifecycle?.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            // 移除队列中所有未执行的消息
            removeCallbacksAndMessages(null)
            source.lifecycle.removeObserver(this)
        }
    }
}