package me.shetj.base.base

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

/**
 * ================================================
 * 基类 [Service]
 * @author shetj
 */
@Keep
abstract class BaseService : Service() {
    protected val TAG = this.javaClass.simpleName
    protected var mCompositeDisposable: CompositeDisposable? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        init()
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (needNotification()) {
            createNotification(this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        unDispose()//解除订阅
        this.mCompositeDisposable = null
    }

    protected fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        //将所有subscription放入,集中处理
        mCompositeDisposable!!.add(disposable)
    }

    private fun unDispose() {
        //保证activity结束时取消所有正在执行的订阅
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    open fun needNotification(): Boolean {
        return false
    }

    /**
     * 初始化
     */
    abstract fun init()

    /**
     * 展示通知栏
     */
    open fun createNotification(context: Context) {

    }

}