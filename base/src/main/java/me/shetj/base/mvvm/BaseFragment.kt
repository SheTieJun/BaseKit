package me.shetj.base.mvvm

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import me.shetj.base.ktx.getClazz
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 * 可见:     [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
abstract class BaseFragment<VM : ViewModel> : Fragment(),  LifecycleObserver {
    protected var mActivity: Context? = null
    private var mBinding: ViewDataBinding? = null
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    val rxContext: AppCompatActivity
        get() = (mActivity as AppCompatActivity?)!!

    protected lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = getActivityViewModel(getClazz(this))
        val dataBindingConfig: DataBindingConfig = getDataBindingConfig()
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, dataBindingConfig.layout, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(dataBindingConfig.vmVariableId, dataBindingConfig.stateViewModel)
        dataBindingConfig.getBindingParams().forEach { key, any ->
            binding.setVariable(key, any)
        }
        mBinding = binding
        return binding.root
    }


    protected abstract fun getDataBindingConfig(): DataBindingConfig

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        this.mActivity = null
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
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        this.mActivity = context
        super.onAttach(context)
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

    /**
     * Init event and data.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected abstract fun initEventAndData()


    protected open fun <T : ViewModel?> getFragmentViewModel(@NonNull modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!!.get(modelClass)
    }

    protected open fun <T : ViewModel?> getActivityViewModel(@NonNull modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(rxContext)
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
