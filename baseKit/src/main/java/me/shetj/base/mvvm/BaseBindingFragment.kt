package me.shetj.base.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import me.shetj.base.base.AbBaseFragment
import me.shetj.base.ktx.getClazz
import org.greenrobot.eventbus.EventBus

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 *
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 *
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 *
 * 可见:     [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 *
 * if stop -> 到可见，需要start
 */
@Keep
abstract class BaseBindingFragment<VM : BaseViewModel, VB : ViewBinding> : AbBaseFragment() {

    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null

    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel: VM by lazyViewModel

    protected lateinit var mViewBinding: VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = initViewBinding(inflater, container)
        return mViewBinding.root
    }

    /**
     * 系统会默认生成对应的[ViewBinding]
     */
    @Suppress("UNCHECKED_CAST")
    @NonNull
    open fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return getClazz<VB>(this, 1).getMethod("inflate", LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
                .invoke(null, inflater,container,false) as VB
    }

    /**
     * 默认创建一个
     *
     * [useActivityVM] = true 才会使用activity的[viewModel]
     */
    @NonNull
    open fun initViewModel(): VM {
        if (useActivityVM()) {
            return getActivityViewModel(getClazz(this))
        }
        return getFragmentViewModel(getClazz(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 是否使用Activity的VM，默认不使用
     */
    open fun useActivityVM() = false


    protected open fun <T : ViewModel?> getFragmentViewModel(@NonNull modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!!.get(modelClass)
    }

    protected open fun <T : ViewModel?> getActivityViewModel(@NonNull modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(requireActivity())
        }
        return mActivityProvider!!.get(modelClass)
    }

}
