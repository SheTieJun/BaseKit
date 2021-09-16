package me.shetj.base.base


import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding
import me.shetj.base.ktx.getClazz


/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class AbBindingBaseActivity<VB:ViewBinding>: AbBaseActivity() , LifecycleObserver {


    private val lazyViewBinding = lazy { initViewBinding() }
    protected val mViewBinding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
    }

    @Suppress("UNCHECKED_CAST")
    open fun initViewBinding(): VB {
        return getClazz<VB>(this, 1).getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }


}
