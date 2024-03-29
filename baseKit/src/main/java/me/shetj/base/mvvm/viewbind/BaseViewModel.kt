package me.shetj.base.mvvm.viewbind

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.shetj.base.model.SingleLiveEvent
import me.shetj.base.tip.TipType

open class BaseViewModel : ViewModel() {

    val baseAction: MutableLiveData<ViewAction> = SingleLiveEvent()

    override fun onCleared() {
        super.onCleared()
    }
}

sealed class ViewAction

data class TipAction(val tipType: TipType, val msg: String) : ViewAction()

data class NetErrorAction(val msg: String) : ViewAction()

data class LoadingAction(val msg: String) : ViewAction()
