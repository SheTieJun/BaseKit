package me.shetj.base.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import me.shetj.base.ktx.getClazz

/**
 * 基础类  view 层,只有binding
 * @author shetj
 */
@Keep
abstract class AbBindingActivity<VB : ViewBinding> : AbBaseActivity() ,BaseControllerFunctionsImpl{

    private val lazyViewBinding = lazy { initViewBinding() }
    protected val mViewBinding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        if (mViewBinding is ViewDataBinding){
            (mViewBinding as ViewDataBinding).lifecycleOwner = this
        }
        addObservers()
        setUpClicks()
        onInitialized()
    }

    @Suppress("UNCHECKED_CAST")
    open fun initViewBinding(): VB {
        return getClazz<VB>(this, 0).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
    }

    override fun initView() {
    }

    override fun initData() {
    }
}
