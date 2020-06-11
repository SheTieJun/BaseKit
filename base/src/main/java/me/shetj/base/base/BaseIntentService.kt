package me.shetj.base.base

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

/**
 * ================================================
 * 基类 [IntentService]
 * @author shetj
 */
@Keep
abstract class BaseIntentService(name: String) : IntentService(name) {
    protected val TAG = this.javaClass.simpleName
    protected var mCompositeDisposable: CompositeDisposable? = null

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
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

    protected fun unDispose() {
        //保证activity结束时取消所有正在执行的订阅
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }


    override fun onHandleIntent(intent: Intent?) {
        init()
    }

    /**
     * 初始化
     */
    abstract fun init()
}