package me.shetj.base.mvvm.viewbind

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import me.shetj.base.base.AbBindingActivity
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
abstract class BaseBindingActivity<VB : ViewBinding, VM : BaseViewModel> : AbBindingActivity<VB>(),
    Observer<ViewAction> {
    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel by lazyViewModel

    private val lazyViewBinding = lazy { initViewBinding() }
    private var mAcProvider: ViewModelProvider? = null

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewModel.baseAction.observe(this,this)
    }


    override fun onChanged(action: ViewAction?) {
        if (action is TipAction){
           when(action.tipType){
               DEFAULT -> TipKit.normal(this,action.msg)
               INFO -> TipKit.info(this,action.msg)
               ERROR -> TipKit.error(this,action.msg)
               SUCCESS -> TipKit.success(this,action.msg)
               WARNING -> TipKit.warn(this,action.msg)
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
        return mAcProvider!![modelClass]
    }
}