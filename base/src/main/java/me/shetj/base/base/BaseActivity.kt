package me.shetj.base.base


import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import me.shetj.base.R
import me.shetj.base.kt.toJson
import me.shetj.base.s
import me.shetj.base.tools.app.HideUtil
import me.shetj.base.tools.json.EmptyUtils
import org.simple.eventbus.EventBus
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class BaseActivity<T : BasePresenter<*>> : RxAppCompatActivity(), IView , CoroutineScope,LifecycleObserver {
    protected val TAG = this.javaClass.simpleName
    private  var myHandler: Handler ?=null
    protected var mPresenter: T? = null

    override val rxContext: RxAppCompatActivity
        get() = this

    private val job = SupervisorJob()


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAnimation()
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onActivityCreate(){
        HideUtil.init(this)
        if (useEventBus()) {
            //注册到事件主线
            EventBus.getDefault().register(this)
        }
        findViewById<View>(R.id.toolbar_back)?.setOnClickListener { back() }
        initView()
        initData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onActivityDestroy(){
        if (useEventBus()) {
            //如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this)
        }
        coroutineContext.cancelChildren()
        mPresenter?.onDestroy()
    }

    open fun setTitle(title:String){
        findViewById<TextView>(R.id.toolbar_title)?.apply {
            text = title
        }
    }
    /**
     * 连接view
     */
    protected abstract fun initView()
    /**
     * 连接数据
     */
    protected abstract fun initData()


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //true - 界面加载成功的时候
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return useEventBus
     */
    open fun useEventBus(): Boolean {
        return true
    }

    /**
     * 界面开始动画 (此处输入方法执行任务.)
     */
    open fun startAnimation() {}

    /**
     * 界面回退动画 (此处输入方法执行任务.)
     */
    open fun endAnimation() {}

    /**
     * 返回
     */
    open  fun back() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    fun addDispose(disposable: Disposable) {
         mPresenter?.addDispose(disposable)
    }

    override fun onBackPressed() {
        HideUtil.hideSoftKeyboard(rxContext)
        super.onBackPressed()
        back()
    }

    override fun updateView(message: Message) {
        if (s.isDebug && EmptyUtils.isNotEmpty(message)) {
            Timber.i(message.toJson())
        }
    }
}
