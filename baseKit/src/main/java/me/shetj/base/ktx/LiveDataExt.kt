package me.shetj.base.ktx

import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import java.util.concurrent.atomic.AtomicBoolean

/****************************************** LiveData ***************************************/
/*** @Author stj
 * * @Date 2021/10/8-18:38
 * * @Email 375105540@qq.com
 * * 间隔固定时间取最后一个
 */
fun <T> LiveData<T>.throttleLast(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true)

    val runnable = Runnable {
        if (isUpdate.compareAndSet(false,true)){
            mld.value = source.value
        }
    }

    mld.addSource(source) {
        if (isUpdate.compareAndSet(true,false)) {
            handler.postDelayed(runnable, duration)
        }
    }
}


/*** @Author stj
 * * @Date 2021/10/8-18:38
 * * @Email 375105540@qq.com
 * * 间隔固定时间内，取第一个的值
 */
fun <T> LiveData<T>.throttleFirst(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true)

    val runnable = Runnable {
        isUpdate.set(true)
    }

    mld.addSource(source) {
        if (isUpdate.compareAndSet(true,false)){
            mld.value = source.value
            handler.postDelayed(runnable, duration)
        }
    }
}

fun <T> LiveData<T>.throttleLatest(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->

    val isLatest = AtomicBoolean(true)
    val source = this
    val handler =  HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true)

    val runnable = Runnable {
        if (isUpdate.compareAndSet(false,true)){
            mld.value = source.value
        }
    }

    mld.addSource(source) {
        if (isLatest.compareAndSet(true,false)){
            mld.value = source.value
        }
        if (isUpdate.compareAndSet(true,false)) {
            handler.postDelayed(runnable, duration)
        }
    }
}


/**  debounce
 * @Author stj
 * @Date 2021/10/8-18:38
 * @Email 375105540@qq.com
 * 2个值之间必须间隔，固定时间
 */
fun <T> LiveData<T>.debounce(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())

    val runnable = Runnable {
        mld.value = source.value
    }

    mld.addSource(source) {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, duration)
    }
}


fun LiveData<Boolean>.isTrue() = value == true
