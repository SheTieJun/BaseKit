package shetj.me.base.utils

import android.util.Log
import com.shetj.messenger.SLogMessenger
import timber.log.Timber

class SLogMessengerTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag1 = tag ?: "Messenger"
        when (priority) {
            Log.VERBOSE -> SLogMessenger.getInstance().v(tag1, message)
            Log.INFO -> SLogMessenger.getInstance().i(tag1, message)
            Log.DEBUG -> SLogMessenger.getInstance().d(tag1, message)
            Log.WARN -> SLogMessenger.getInstance().w(tag1, message)
            Log.ERROR -> SLogMessenger.getInstance().e(tag1, message, true)
            else -> SLogMessenger.getInstance().v(tag1, message)
        }
    }
}
