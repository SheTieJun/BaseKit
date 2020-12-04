package me.shetj.base.mvvm


import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import me.shetj.base.R
import me.shetj.base.ktx.getClazz
import me.shetj.base.tools.app.KeyboardUtil
import me.shetj.base.tools.app.TimberUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * 1. ViewModel Model和View通信的桥梁，承担业务逻辑功能
 * 2. Model 主要包括网络数据源和本地缓存数据源
 *  val viewModel by viewModels { SavedStateViewModelFactory(application, this) }
 *
 * use [ViewBinding]
 *
 * @author shetj
 */
@Keep
abstract class BaseBindingActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity(), LifecycleObserver {

    private var mActivityProvider: ViewModelProvider? = null

    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel by lazyViewModel

    private val lazyViewBinding = lazy {
        initViewBinding()
    }
    protected val mViewBinding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
        setContentView(mViewBinding.root)
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onActivityCreate() {
        KeyboardUtil.init(this)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        findViewById<View>(R.id.toolbar_back)?.setOnClickListener { back() }
    }

    /**
     * 默认创建一个实例，
     * 不过可以重写，然后使用单例
     * 如果不实现，建议写一个emptyVM
     */
    @NonNull
    open fun initViewModel(): VM {
        return getActivityViewModel(getClazz(this))
    }

    /**
     * 系统会默认生成对应的[ViewBinding]
     */
    open fun initViewBinding(): VB {
        return getClazz<VB>(this, 1).getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onActivityDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

//    protected open fun <T : ViewModel> getActivityViewModel(@NonNull modelClass: Class<T>): T {
//        return (mActivityProvider ?: ViewModelProvider(this,
//                SavedStateViewModelFactory(S.app, this))
//                .also {
//                    mActivityProvider = it
//                }).get(modelClass)
//    }

    protected open fun <T : ViewModel> getActivityViewModel(@NonNull modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider!!.get(modelClass)
    }

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    //设置横竖屏
    open fun setOrientation(landscape: Boolean) {
        requestedOrientation = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //true - 界面加载成功的时候
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return useEventBus
     */
    open fun useEventBus(): Boolean {
        return true
    }


    /**
     * 用来替换 [finish] 返回
     */
    open fun back() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }


    override fun onBackPressed() {
        KeyboardUtil.hideSoftKeyboard(this)
        super.onBackPressed()
    }

}
