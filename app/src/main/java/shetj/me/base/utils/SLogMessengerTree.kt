package shetj.me.base.utils

import android.util.Log
import com.shetj.messenger.SLogMessenger
import timber.log.Timber

class SLogMessengerTree : Timber.Tree() {


    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag = tag ?: "Messenger"
        when (priority) {
            Log.VERBOSE -> SLogMessenger.getInstance().v(tag, message)
            Log.INFO -> SLogMessenger.getInstance().i(tag, message)
            Log.DEBUG -> SLogMessenger.getInstance().d(tag, message)
            Log.WARN -> SLogMessenger.getInstance().w(tag, message)
            Log.ERROR -> SLogMessenger.getInstance().e(tag, message,true)
            else -> SLogMessenger.getInstance().v(tag, message)
        }
    }
}