package me.shetj.base.mvvm

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import me.shetj.base.ktx.getClazz
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
abstract class BaseBindingFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment(), LifecycleObserver {

    protected var enabledOnBack: Boolean = false
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null

    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel: VM by lazyViewModel

    protected val onBackPressedCallback = object : OnBackPressedCallback(enabledOnBack) {
        override fun handleOnBackPressed() {
          onBack()
        }
    }

    protected lateinit var mViewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
    }


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


    open fun onBack() {
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return boolean
     */
    open fun useEventBus(): Boolean {
        return true
    }

    /**
     * 是否使用Activity的VM，默认不使用
     */
    open fun useActivityVM() = false

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        onBackPressedCallback.isEnabled = enabledOnBack
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    /**
     * 初始化数据和界面绑定
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun viewBindData() {

    }

    /**
     * On visible.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onVisible() {
    }

    /**
     * On invisible.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onInvisible() {
    }

//    protected open fun <T : ViewModel?> getFragmentViewModel(@NonNull modelClass: Class<T>): T {
//        return (mFragmentProvider ?: ViewModelProvider(this,
//                SavedStateViewModelFactory(S.app, this))
//                .also {
//                    mFragmentProvider = it
//                }).get(modelClass)
//    }
//
//    protected open fun <T : ViewModel?> getActivityViewModel(@NonNull modelClass: Class<T>): T {
//        return (mActivityProvider ?: ViewModelProvider(this,
//                SavedStateViewModelFactory(S.app, this))
//                .also {
//                    mActivityProvider = it
//                }).get(modelClass)
//    }

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

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }
}
