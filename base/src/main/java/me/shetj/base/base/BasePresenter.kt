package me.shetj.base.base


import android.content.Intent
import android.os.Message

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

import androidx.annotation.Keep


import org.simple.eventbus.EventBus

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import me.shetj.base.tools.app.getMessage
import org.simple.eventbus.Subscriber
import org.simple.eventbus.ThreadMode
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 *
 * 在model中获取数据，在view（Activity）中展示数据
 * @author shetj
 */
@Keep
open class BasePresenter<T : BaseModel>(protected var view: IView?) : IPresenter,CoroutineScope {

    private var mCompositeDisposable: CompositeDisposable? = null
    protected var model: T? = null

    private val job = SupervisorJob()

    private fun SupervisorJob(): Job {
        return Job()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    internal val rxContext: RxAppCompatActivity
        get() = view!!.rxContext

    init {
        Timber.i("onStart")
        onStart()
    }

    override fun onStart() {
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    /**
     * eventbus 默认主线程处理
     */
    @Subscriber(mode = ThreadMode.MAIN,tag = "onMainEvent")
    fun onEvent(message: Message){

    }

    /**
     * //解除订阅
     * Activity#onDestroy() 调用[IPresenter.onDestroy]
     */
    override fun onDestroy() {
        Timber.i("onDestroy")
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        coroutineContext.cancelChildren()
        unDispose()
        this.mCompositeDisposable = null
        if (model != null) {
            model!!.onDestroy()
            model = null
        }
    }

    /**
     * 是否使用 [EventBus],默认为使用(true)，
     *
     * @return
     */
    fun useEventBus(): Boolean {
        return true
    }


    /**
     * 将 [Disposable] 添加到 [CompositeDisposable] 中统一管理
     * 可在 [中使用 ][android.app.Activity.onDestroy]
     */
    fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable)
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    fun unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    fun startActivity(intent: Intent) {
        if (null != view) {
            view?.rxContext?.startActivity(intent)
        }
    }



    fun getMessage(code: Int, msg: Any): Message {
        return  Message.obtain().getMessage (code,msg)
    }


    fun updateMessage(code: Int, msg: Any){
        view?.updateView(getMessage(code,msg))
    }
}


