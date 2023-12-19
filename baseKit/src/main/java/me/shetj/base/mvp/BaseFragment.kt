package me.shetj.base.mvp

import android.os.Bundle
import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import me.shetj.base.base.AbBaseFragment
import me.shetj.base.ktx.getClazz
import me.shetj.base.ktx.logUILife
import me.shetj.base.tools.app.ArmsUtils

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
    protected val saveStateMap: MutableMap<String, Any> by lazy { mutableMapOf() }

    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    override val rxContext: AppCompatActivity
        get() = (requireActivity() as AppCompatActivity)

    /**
     * 抽象类不能反射
     *
     */
    open fun initPresenter(): T {
        return getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateValuesFromBundle(savedInstanceState)
    }

    override fun onDestroyView() {
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
        super.onDestroyView()
    }

    override fun updateView(message: Message) {
    }

    fun keepSaveState(key: String, value: Any) {
        saveStateMap[key] = value
    }

    /**
     * 保存状态 只在mvp模式起作用
     * @param map MutableMap<String, Any>
     * @param isClear Boolean 是否清空前面的
     */
    protected fun <T> keepSaveState(map: MutableMap<String, Any>, isClear: Boolean = false) {
        if (isClear) {
            saveStateMap.clear()
        }
        saveStateMap.putAll(map)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveSate(outState)
        super.onSaveInstanceState(outState)
    }

    private fun saveSate(outState: Bundle) {
        ArmsUtils.saveStateToBundle(saveStateMap, outState)
    }

    protected fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        "$TAG updateValuesFromBundle".logUILife()
        savedInstanceState ?: return
        //    /Update the value from the Bundle.
        //    if (savedInstanceState.keySet().contains(REQUESTING_KEY)) {
        //        requestingUpdates = savedInstanceState.getBoolean(
        //                REQUESTING_KEY)
        //    }
        //    //Update UI to match restored state
    }
}
