package me.shetj.base.mvvm.viewbind

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.shetj.base.tip.TipType

open class BaseViewModel : ViewModel() {

    val baseAction:MutableLiveData<ViewAction> = MutableLiveData()


    override fun onCleared() {
        super.onCleared()
    }
}


sealed class ViewAction

class TipAction(val tipType: TipType, val msg:String): ViewAction()

class NetErrorAction(val msg: String): ViewAction()

