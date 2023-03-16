package me.shetj.base.model

/**
 * 界面状态
 */
sealed class UIState {
    // 正在加载
    object Loading : UIState()

    // 加载完成
    object End : UIState()

    // 错误状态
    class Error(val e: ErrorType) : UIState()
}


/**
 * * [SignedOut]:退出登录：当用户尚未登录时。
 * * [InProgress]:进行中：当您的应用目前正在尝试通过执行网络调用来让用户登录时。
 * * [Error]:错误：登录时出现错误。
 * * [SignIn]:登录成功：用户登录后。
 */
sealed class LoginUiState {
    object SignedOut : LoginUiState()
    object InProgress : LoginUiState()
    object Error : LoginUiState()
    object SignIn : LoginUiState()
}

sealed class ErrorType{

    /**
     * 网络错误
     */
    object NetError:ErrorType()

    /**
     * 接口错误
     */
    class APIError(msg:String):ErrorType()

    /**
     * Other error
     * 其他错误
     */
    class OtherError(msg:String):ErrorType()

}