package me.shetj.base.mvvm


import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.ktx.getClazz

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
abstract class BaseBindingActivity<VM : ViewModel, VB : ViewBinding> : AbBaseActivity() {

    private var mActivityProvider: ViewModelProvider? = null

    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel by lazyViewModel

    private val lazyViewBinding = lazy { initViewBinding() }
    protected val mViewBinding: VB by lazyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
    }

    override fun initView() {

    }

    override fun initData() {

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
     *  默认生成对应的[ViewBinding],也可以重写
     */
    @Suppress("UNCHECKED_CAST")
    open fun initViewBinding(): VB {
        return getClazz<VB>(this, 1).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
    }


    protected open fun getActivityViewModel(@NonNull modelClass: Class<VM>): VM {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider!!.get(modelClass)
    }

}
