package me.shetj.base.mvvm.viewbind

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import me.shetj.base.base.AbBaseBindingActivity
import me.shetj.base.ktx.getClazz
import me.shetj.base.tip.TipKit
import me.shetj.base.tip.TipType.DEFAULT
import me.shetj.base.tip.TipType.ERROR
import me.shetj.base.tip.TipType.INFO
import me.shetj.base.tip.TipType.SUCCESS
import me.shetj.base.tip.TipType.WARNING

/**
 * 1. ViewModel Model和View通信的桥梁，承担业务逻辑功能
 * 2. Model 主要包括网络数据源和本地缓存数据源
 *
 * use [ViewBinding]
 *
 * @author shetj
 */
@Keep
open class BaseBindingActivity<VB : ViewBinding, VM : BaseViewModel> :
    AbBaseBindingActivity<VB>(),
    Observer<ViewAction> {
    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel by lazyViewModel

    private val lazyViewBinding = lazy { initBinding() }
    private var mAcProvider: ViewModelProvider? = null

    override fun addObservers() {
        super.addObservers()
        mViewModel.baseAction.observe(this, this)
    }

    override fun onChanged(value: ViewAction) {
        if (value is TipAction) {
            when (value.tipType) {
                DEFAULT -> TipKit.normal(this, value.msg)
                INFO -> TipKit.info(this, value.msg)
                ERROR -> TipKit.error(this, value.msg)
                SUCCESS -> TipKit.success(this, value.msg)
                WARNING -> TipKit.warn(this, value.msg)
            }
        }
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
        return mAcProvider?.get(modelClass) ?: ViewModelProvider(this)
            .also {
                mAcProvider = it
            }[modelClass]
    }
}
