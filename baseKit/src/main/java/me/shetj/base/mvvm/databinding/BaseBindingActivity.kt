package me.shetj.base.mvvm.databinding

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.base.BaseControllerFunctionsImpl
import me.shetj.base.ktx.getClazz
import me.shetj.base.mvvm.viewbind.BaseViewModel

/**
 * Base class for activities that using databind feature to bind the view
 * also Implements [BaseControllerFunctionsImpl] interface
 * @param T A class that extends [ViewDataBinding] that will be used by the activity layout binding view.
 * @param layoutId the resource layout view going to bind with the [binding] variable
 */
abstract class BaseBindingActivity<T : ViewDataBinding, VM : BaseViewModel>(@LayoutRes val layoutId: Int) :
    AbBaseActivity(), BaseControllerFunctionsImpl {
    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel by lazyViewModel
    private var mAcProvider: ViewModelProvider? = null

    /**
     * activity layout view binding object
     */
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@BaseBindingActivity, layoutId) as T
        binding.lifecycleOwner = this
        addObservers()
        setUpClicks()
        onInitialized()
    }

    /**
     * 默认创建一个实例，
     * 不过可以重写，然后使用单例
     * 如果不实现，建议写一个emptyVM
     */
    @NonNull
    open fun initViewModel(): VM {
        return getActivityViewModel(getClazz(this, 1))
    }

    protected open fun getActivityViewModel(@NonNull modelClass: Class<VM>): VM {
        if (mAcProvider == null) {
            mAcProvider = ViewModelProvider(this)
        }
        return mAcProvider!![modelClass]
    }
}