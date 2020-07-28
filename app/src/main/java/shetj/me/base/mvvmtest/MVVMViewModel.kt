package shetj.me.base.mvvmtest

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import me.shetj.base.mvvm.BaseViewModel
import shetj.me.base.utils.TimeUtil


class MVVMViewModel : BaseViewModel() {

    val test: String = "这是测试文本"

    val url = "https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png"

    //    val liveData:MutableLiveData<Boolean> = MutableLiveData()
    val change = ObservableField<String>().apply {
        set("时间：${TimeUtil.getHMSTime()}")
    }

    val timeLive:MutableLiveData<String> = MutableLiveData()
}