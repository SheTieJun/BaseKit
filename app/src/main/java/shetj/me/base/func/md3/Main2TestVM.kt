package shetj.me.base.func.md3

import androidx.lifecycle.MutableLiveData
import me.shetj.base.ktx.isTrue
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.mvvm.viewbind.BaseViewModel

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/3/20<br>
 */
class Main2TestVM:BaseViewModel() {
    var isGrayTheme = GrayThemeLiveData.getInstance().isTrue()

    val themInfo = MutableLiveData("正常模式")

    fun changeThem(){
        isGrayTheme = !isGrayTheme
        GrayThemeLiveData.getInstance().postValue(isGrayTheme)
        themInfo.value = (if (isGrayTheme){
            "正常模式"
        }else{
            "灰色模式"
        })
    }

}