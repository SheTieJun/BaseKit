package me.shetj.base.mvp

import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.ktx.getClazz

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class BaseActivity<T : BasePresenter<*>> : AbBaseActivity(), IView {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    override val rxContext: AppCompatActivity
        get() = this

    open fun onActivityDestroy() {
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
    }

    override fun initView() {
    }

    override fun initData() {
    }


    override fun onDestroy() {
        onActivityDestroy()
        super.onDestroy()
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
