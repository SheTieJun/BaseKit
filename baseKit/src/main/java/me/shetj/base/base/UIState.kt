package me.shetj.base.base



/**
 * 界面状态
 */
sealed class UIState {
    //正在加载
    object Loading  : UIState ()

    //加载完成
    object End : UIState ()

    //错误状态
    class Error(val e:Exception):UIState()

}




