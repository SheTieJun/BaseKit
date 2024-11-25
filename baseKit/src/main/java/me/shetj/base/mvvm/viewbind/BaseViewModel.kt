package me.shetj.base.mvvm.viewbind

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import me.shetj.base.model.SingleLiveEvent
import me.shetj.base.tip.TipType

open class BaseViewModel : ViewModel() {

    val baseAction: MutableLiveData<ViewAction> = SingleLiveEvent()

    override fun onCleared() {
        super.onCleared()
    }
}


/**
 *
 * [ViewModel 的已保存状态模块](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate?hl=zh-cn)
 *
 * - 处理系统发起的进程终止，可使用 SavedStateHandle API 作为备用方式。（从 Fragment 1.2.0 或其传递依赖项 Activity 1.1.0 开始）
 */
open class SaveStateViewModel(private val savedStateHandle: SavedStateHandle):BaseViewModel(){

    fun <T> filteredLiveData(key: String): MutableLiveData<T> = savedStateHandle.getLiveData(key)

    fun <T> filteredData(key: String) = savedStateHandle.get<T>(key)

    fun <T> setQuery(key: String,value: T) {
        savedStateHandle["query"] = value
    }

}

sealed class ViewAction

data class TipAction(val tipType: TipType, val msg: String) : ViewAction()

data class NetErrorAction(val msg: String) : ViewAction()

data class LoadingAction(val msg: String) : ViewAction()
