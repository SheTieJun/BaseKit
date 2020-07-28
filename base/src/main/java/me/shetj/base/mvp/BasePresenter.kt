package me.shetj.base.mvp


import android.content.Intent
import android.os.Message
import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import me.shetj.base.ktx.toMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 *
 * 在model中获取数据，在view（Activity）中展示数据
 * @author shetj
 */
@Keep
open class BasePresenter<T : BaseModel>(protected var view: IView) : IPresenter, CoroutineScope {

    private var mCompositeDisposable: CompositeDisposable? = null
    protected var model: T? = null

    private val job = supervisorJob()

    open fun supervisorJob() = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    internal val rxContext: AppCompatActivity
        get() = view.rxContext

    init {
        Timber.i("${this.javaClass.name}:onStart")
        onStart()
    }

    @CallSuper
    override fun onStart() {
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    /**
     * //解除订阅
     * [BaseActivity.onDestroy] 调用[IPresenter.onDestroy]
     */
    @CallSuper
    override fun onDestroy() {
        Timber.i("${this.javaClass.simpleName}:onDestroy")
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        coroutineContext.cancelChildren()
        unDispose()
        this.mCompositeDisposable = null
        model?.onDestroy()
        model = null
    }

    /**
     * 是否使用 [EventBus],默认为使用(true)，
     *
     * @return
     */
    open fun useEventBus() = true

    /**
     * 将 [Disposable] 添加到 [CompositeDisposable] 中统一管理
     * 可在[android.app.Activity.onDestroy] 释放
     */
    fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    fun unDispose() {
        mCompositeDisposable?.clear()
    }

    fun startActivity(intent: Intent) {
        view.rxContext.startActivity(intent)
    }

    fun updateView(code: Int, msg: Any) {
        view.updateView(msg.toMessage(code))
    }
}


