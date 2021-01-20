package me.shetj.base.mvp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import me.shetj.base.S
import me.shetj.base.ktx.getClazz
import me.shetj.base.ktx.toJson
import me.shetj.base.tools.json.EmptyUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 *
 *
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 *
 *
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 *
 *
 * 可见:     [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
abstract class BaseBindingFragment<T : BasePresenter<*>,VB : ViewBinding> : Fragment(), IView, LifecycleObserver {

    protected var enabledOnBack: Boolean = false
        set(value) {
            field = value
            onBackPressedCallback.isEnabled = value
        }


    private val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    protected val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
             onBack()
        }
    }

    protected lateinit var mViewBinding: VB
    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    override val rxContext: AppCompatActivity
        get() = (requireActivity() as AppCompatActivity?)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = initViewBinding(inflater,container)
        return mViewBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    open fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return getClazz<VB>(this, 1).getMethod("inflate", LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
                .invoke(null, inflater,container,false) as VB
    }
    /**
     * 抽象类不能反射
     *
     */
    open fun initPresenter(): T {
        return  getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return boolean
     */
    open fun useEventBus(): Boolean {
        return true
    }

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        onBackPressedCallback.isEnabled = enabledOnBack
        requireActivity().onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    open fun onBack() {
    }

    /**
     * 初始化数据和界面绑定
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun viewBindData(){

    }

    /**
     * On visible.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onVisible() {
    }

    /**
     * On invisible.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onInvisible() {
    }

    /**
     * Init event and data.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected abstract fun initEventAndData()


    override fun onDestroyView() {
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
        super.onDestroyView()
    }

    @SuppressLint("unchecked")
    override fun updateView(message: Message) {
        if (S.isDebug && EmptyUtils.isNotEmpty(message)) {
            Timber.i(message.toJson())
        }
    }

    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }
}
