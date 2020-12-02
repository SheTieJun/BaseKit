package shetj.me.base.mvvmtest

import androidx.lifecycle.MutableLiveData
import me.shetj.base.mvvm.BaseViewModel
import shetj.me.base.utils.TimeUtil


class MVVMViewModel : BaseViewModel() {

    val test: String = "这是测试文本"

    val url :MutableLiveData<String> = MutableLiveData("https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png")

    //    val liveData:MutableLiveData<Boolean> = MutableLiveData()
    var change = "时间：${TimeUtil.getHMSTime()}"

    val timeLive:MutableLiveData<String> = MutableLiveData()
}