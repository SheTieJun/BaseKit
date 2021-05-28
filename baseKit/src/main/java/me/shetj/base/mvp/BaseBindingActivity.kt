package me.shetj.base.mvp


import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import me.shetj.base.ktx.getClazz


/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class BaseBindingActivity<P : BasePresenter<*>, VB : ViewBinding> : BaseActivity<P>() {


    private val lazyViewBinding = lazy { initViewBinding() }
    protected val mViewBinding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onActivityDestroy() {
        super.onActivityDestroy()
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
    }

    @Suppress("UNCHECKED_CAST")
    open fun initViewBinding(): VB {
        return getClazz<VB>(this, 1).getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }


}
