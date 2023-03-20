package me.shetj.base.mvvm.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.shetj.base.base.BaseControllerFunctionsImpl
import me.shetj.base.ktx.getClazz
import me.shetj.base.mvvm.viewbind.BaseViewModel

/**
 * Base class for fragments that using databind feature to bind the view
 * also Implements [BaseControllerFunctionsImpl] interface
 * @param T A class that extends [ViewDataBinding] that will be used by the fragment layout binding view.
 * @param layoutId the resource layout view going to bind with the [binding] variable
 */
abstract class BaseBindingFragment<T : ViewDataBinding, VM : BaseViewModel>(@LayoutRes val layoutId: Int) :
    Fragment(),
    BaseControllerFunctionsImpl {
    lateinit var binding: T
    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel: VM by lazyViewModel
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
        setUpClicks()
        onInitialized()
    }

    /**
     * 是否使用Activity的VM，默认使用
     */
    open fun useActivityVM() = true

    /**
     * 默认创建一个
     *
     * [useActivityVM] = true 才会使用activity的[ViewModel]
     */
    @NonNull
    open fun initViewModel(): VM {
        if (useActivityVM()) {
            return getActivityViewModel(getClazz(this, 1))
        }
        return getFragmentViewModel(getClazz(this, 1))
    }

    protected open fun getFragmentViewModel(@NonNull modelClass: Class<VM>): VM {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelClass]
    }

    protected open fun getActivityViewModel(@NonNull modelClass: Class<VM>): VM {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(requireActivity())
        }
        return mActivityProvider!![modelClass]
    }
}