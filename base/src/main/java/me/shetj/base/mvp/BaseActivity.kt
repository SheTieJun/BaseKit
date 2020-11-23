package me.shetj.base.mvp


import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.disposables.Disposable
import me.shetj.base.R
import me.shetj.base.S
import me.shetj.base.ktx.getClazz
import me.shetj.base.ktx.toJson
import me.shetj.base.tools.app.KeyboardUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class BaseActivity<T : BasePresenter<*>> : AppCompatActivity(), IView , LifecycleObserver {
    protected val TAG = this.javaClass.simpleName
    private val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    override val rxContext: AppCompatActivity
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAnimation()
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onActivityCreate() {
        KeyboardUtil.init(this)
        if (useEventBus()) {
            //注册到事件主线
            EventBus.getDefault().register(this)
        }
        findViewById<View>(R.id.toolbar_back)?.setOnClickListener { back() }
        initView()
        initData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onActivityDestroy() {
        if (useEventBus()) {
            //如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this)
        }
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
    }

    /**
     * 默认通过反射创建 T：BasePresenter
     * 可以重新 返回对应的实例 或者单例
     * 实现思想：
     *    首先Activity<Presenter> -> Presenter.class -> Presenter的参数构造函数 -> newInstance
     */
    open fun initPresenter(): T {
        return  getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }
    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    open fun setTitle(title: String) {
        findViewById<TextView>(R.id.toolbar_title)?.apply {
            text = title
        }
    }

    //设置横竖屏
    open fun setOrientation(landscape: Boolean) {
        requestedOrientation = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
     * 用来替换 [finish] 返回
     */
    open fun back() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    fun addDispose(disposable: Disposable) {
        mPresenter.addDispose(disposable)
    }

    override fun onBackPressed() {
        KeyboardUtil.hideSoftKeyboard(rxContext)
        super.onBackPressed()
    }

    override fun updateView(message: Message) {
        if (S.isDebug) {
            Timber.tag(TAG).i(message.toJson())
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
    }
}
