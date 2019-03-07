package me.shetj.base.http.callback

import androidx.annotation.Keep

import com.zhouyou.http.callback.ProgressDialogCallBack
import com.zhouyou.http.subsciber.IProgressDialog

@Keep
class EasyProgressCallBack<T> : ProgressDialogCallBack<T> {

    constructor(progressDialog: IProgressDialog) : super(progressDialog) {}

    constructor(progressDialog: IProgressDialog, isShowProgress: Boolean, isCancel: Boolean) : super(progressDialog, isShowProgress, isCancel) {}

    override fun onSuccess(t: T) {

    }
}
