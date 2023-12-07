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
import me.shetj.base.ktx.getClazz

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
open class BaseActivity<T : BasePresenter<*>> : AbBaseActivity(), IView, LifecycleEventObserver {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    override val rxContext: AppCompatActivity
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onActivityCreate()
            }

            Lifecycle.Event.ON_DESTROY -> {
                onActivityDestroy()
            }

            else -> {}
        }
    }

    open fun onActivityCreate() {
        initView()
        initData()
    }

    open fun onActivityDestroy() {
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
    }

    open fun initView() {
    }

    open fun initData() {
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
