package me.shetj.base.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import me.shetj.base.R
import me.shetj.base.ktx.getClazz

/**
 * 基础类  view 层,
 * binding:可以是DataBinding/ViewBinding
 * @author shetj
 */
@Keep
abstract class AbBindingActivity<VB : ViewBinding> : AbBaseActivity(), BaseControllerFunctionsImpl {

    private val lazyViewBinding = lazy { initBinding() }
    protected val mBinding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = this
        }
        initBaseView()
        addObservers()
        setUpClicks()
        onInitialized()
    }

    override fun initBaseView() {
        findViewById<MaterialToolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                back()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    open fun initBinding(): VB {
        return getClazz<VB>(this, 0).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
    }
}
