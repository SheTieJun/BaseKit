package me.shetj.base.mvp


import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.Disposable
import me.shetj.base.S
import me.shetj.base.base.ABBaseActivity
import me.shetj.base.ktx.getClazz
import me.shetj.base.ktx.toJson
import timber.log.Timber


/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class BaseActivity<T : BasePresenter<*>> : ABBaseActivity(), IView  {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    override val rxContext: AppCompatActivity
        get() = this


    override fun onActivityDestroy() {
        super.onActivityDestroy()
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
    }

    override fun initView() {
    }

    override fun initData() {
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


    fun addDispose(disposable: Disposable) {
        mPresenter.addDispose(disposable)
    }

    override fun updateView(message: Message) {
        if (S.isDebug) {
            Timber.tag(TAG).i(message.toJson())
        }
    }

}
