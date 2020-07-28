package shetj.me.base.mvvmtest

import android.view.View
import androidx.lifecycle.Observer
import me.shetj.base.ktx.showToast
import me.shetj.base.mvvm.BaseActivity
import me.shetj.base.mvvm.DataBindingConfig
import org.koin.android.ext.android.get
import shetj.me.base.BR
import shetj.me.base.R
import shetj.me.base.utils.TimeUtil
import timber.log.Timber

class MVVMTestActivity : BaseActivity<MVVMViewModel>() {

    private val click = View.OnClickListener {
        it?.id?.toString()?.showToast()
        Timber.i(it?.id?.toString())
        when (it?.id) {
            R.id.btn_change -> {
                //Observable双向更新
                mViewModel.change.set("时间：${TimeUtil.getHMSTime()}")
                //LiveData
                mViewModel.timeLive.postValue(TimeUtil.getHMSTime())
            }
        }
    }

    override fun getViewModel(): MVVMViewModel {
        return get()
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        //LiveData 的通知更新
        mViewModel.timeLive.observe(this, Observer<String> {
            mBinding?.setVariable(BR.time, it)
        })
        mViewModel.timeLive.observe(this, Observer<String> {
            Timber.tag("timeLive").i(it?.toString())
        })
        mViewModel.timeLive.postValue(TimeUtil.getHMSTime())

        Timber.tag("getViewModel").i("id = ${getViewModel().toString()}")
    }

    override fun getDataBindingConfig(): DataBindingConfig? {
        return DataBindingConfig(R.layout.activity_m_v_v_m_test, BR.vm, mViewModel)
                .apply {
                    addBindingParam(BR.click, click)
                }
    }


}