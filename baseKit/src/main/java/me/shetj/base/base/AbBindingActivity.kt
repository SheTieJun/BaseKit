package me.shetj.base.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import me.shetj.base.ktx.getClazz

/**
 * 基础类  view 层,
 * binding:可以是DataBinding/ViewBinding
 * @author shetj
 */
@Keep
abstract class AbBindingActivity<VB : ViewBinding> : AbBaseActivity(), BaseControllerFunctionsImpl {

    private val lazyViewBinding = lazy { getBinding() }
    protected val binding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (binding is ViewDataBinding) {
            (binding as ViewDataBinding).lifecycleOwner = this
        }
        addObservers()
        setUpClicks()
        onInitialized()
    }

    @Suppress("UNCHECKED_CAST")
    open fun getBinding(): VB {
        return getClazz<VB>(this, 0).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
    }

    override fun initView() {
    }

    override fun initData() {
    }
}
