package me.shetj.base.mvp

import android.os.Bundle
import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AppLaunchChecker.onActivityCreate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.base.BaseControllerFunctionsImpl
import me.shetj.base.ktx.getClazz
import me.shetj.base.tools.app.ArmsUtils

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
open class BaseActivity<T : BasePresenter<*>> : AbBaseActivity(), IView, LifecycleEventObserver , BaseControllerFunctionsImpl {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter
    protected val saveStateMap: MutableMap<String, Any> by lazy { mutableMapOf() }
    override val rxContext: AppCompatActivity
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
        initBaseView()
        addObservers()
        setUpClicks()
        onInitialized()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
       if (event == Lifecycle.Event.ON_DESTROY){
           onActivityDestroy()
       }
    }

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

    open fun onActivityDestroy() {
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
        return getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }

    override fun updateView(message: Message) {
    }
}
