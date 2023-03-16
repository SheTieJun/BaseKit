

package me.shetj.base.mvvm.databinding

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.base.BaseControllerFunctionsImpl

/**
 * Base class for activities that using databind feature to bind the view
 * also Implements [BaseControllerFunctionsImpl] interface
 * @param T A class that extends [ViewDataBinding] that will be used by the activity layout binding view.
 * @param layoutId the resource layout view going to bind with the [binding] variable
 */
abstract class BaseBindingActivity<T : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    AbBaseActivity(), BaseControllerFunctionsImpl {

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
}