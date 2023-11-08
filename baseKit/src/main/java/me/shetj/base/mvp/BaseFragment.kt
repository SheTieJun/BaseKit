package me.shetj.base.mvp

import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import me.shetj.base.base.AbBaseFragment
import me.shetj.base.ktx.getClazz

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 * 可见: [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
open class BaseFragment<T : BasePresenter<*>> : AbBaseFragment(), IView {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    override val rxContext: AppCompatActivity
        get() = (requireActivity() as AppCompatActivity?)!!

    /**
     * 抽象类不能反射
     *
     */
    open fun initPresenter(): T {
        return getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }

    override fun onDestroyView() {
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
        super.onDestroyView()
    }

    override fun updateView(message: Message) {
    }
}
