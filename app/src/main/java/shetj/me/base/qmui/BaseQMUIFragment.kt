package shetj.me.base.qmui


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import me.shetj.base.base.BasePresenter
import me.shetj.base.base.IView
import org.simple.eventbus.EventBus

/**
 * fragment基类
 * @author shetj
 */
@Keep
abstract class BaseQMUIFragment<T : BasePresenter<*>> : QMUIFragment(), IView {

    /**
     * The M activity.
     */
    protected var mActivity: Context? = null

    /**是否可见状态 */
    protected var isShow: Boolean = false

    /**View已经初始化完成 */
    private var isPrepared: Boolean = false

    /**是否第一次加载完 */
    private var isFirstLoad = true

    protected var mPresenter: T? = null

    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    override val rxContext: RxAppCompatActivity
        get() = (mActivity as RxAppCompatActivity?)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirstLoad = true
        //绑定View
        isPrepared = true
        initEventAndData()
        lazyLoad()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity
        //如果要使用eventbus请将此方法返回true
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        this.mActivity = null
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return boolean
     */
    protected fun useEventBus(): Boolean {
        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        this.mActivity = context
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        try {
            val childFragmentManager = Fragment::class.java.getDeclaredField("mChildFragmentManager")
            childFragmentManager.isAccessible = true
            childFragmentManager.set(this, null)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            isShow = true
            onVisible()
        } else {
            isShow = false
            onInvisible()
        }
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
     * Lazy load.
     */
    protected fun lazyLoad() {
        if (!isPrepared || !isShow || !isFirstLoad) {
            return
        }
        isFirstLoad = false
        lazyLoadData()
    }

    /**
     * Init event and data.
     */
    protected abstract fun initEventAndData()

    /**
     * Lazy load data.
     */
    fun lazyLoadData() {

    }


    override fun onDestroyView() {
        if (mPresenter != null) {
            mPresenter!!.onDestroy()
        }
        super.onDestroyView()
    }

    @SuppressLint("unchecked")
    override fun updateView(message: Message) {

    }

    companion object {
        private val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }
}
