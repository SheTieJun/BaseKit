package shetj.me.base.mvvmtest

import android.view.View
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.rx3.asFlowable
import me.shetj.base.ktx.launch
import me.shetj.base.mvvm.BaseActivity
import me.shetj.base.mvvm.DataBindingConfig
import me.shetj.base.tools.file.FileQUtils.searchFile
import org.koin.android.ext.android.get
import shetj.me.base.BR
import shetj.me.base.R
import shetj.me.base.databinding.ActivityMVVMTestBinding
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.utils.TimeUtil
import timber.log.Timber

class MVVMTestActivity : BaseActivity<MVVMViewModel>() {

    private val click = View.OnClickListener {
        when (it?.id) {
            R.id.btn_change -> {
                //Observable双向更新
                mViewModel.change.set("时间：${TimeUtil.getHMSTime()}")
                //LiveData
                mViewModel.timeLive.postValue(TimeUtil.getHMSTime())
            }
            R.id.btn_select_image -> {
                searchFile(callback = {
                    it.let {
                        mViewModel.url.postValue(it.toString())
                    }
                })
            }
        }
    }

    override fun initViewModel(): MVVMViewModel {
        return get()
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun onActivityCreate() {
        super.onActivityCreate()
        //LiveData 的通知更新
        mViewModel.timeLive.observe(this, {
            Timber.tag("timeLive").i(it?.toString())
            mBinding?.setVariable(BR.time, it)
        })
        mViewModel.timeLive.postValue(TimeUtil.getHMSTime())
        mViewModel.url.observe(this, {
            mBinding?.setVariable(BR.url, it)
        })
        //用来测试是否时单例的viewModel
        Timber.tag("getViewModel").i("id = ${initViewModel()}")
        launch {
            listOf(1, 2, 3).asFlow().collect { Timber.i("asFlow() = $it") }
        }
        val channel = Channel<Int>()
        launch {
            // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
            for (x in 1..5) channel.send(x * x)
            for (y in channel) {
                println(channel.receive())
            }
        }

        listOf(1, 2, 3).asFlow().asFlowable().subscribe {
            Timber.i("asFlow().asFlowable() = $it")
        }
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_m_v_v_m_test, BR.vm, mViewModel)
                .apply {
                    addBindingParam(BR.click, click)
                }
    }


}