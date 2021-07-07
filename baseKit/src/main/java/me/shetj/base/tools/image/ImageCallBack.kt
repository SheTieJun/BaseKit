package me.shetj.base.tools.image

import android.net.Uri


interface ImageCallBack {

    fun onSuccess(key: Uri)

    fun onFail()

    //是否需要剪切
    fun isNeedCut():Boolean
}


open class ImageCallBackImpl:ImageCallBack{
    override fun onSuccess(key: Uri) {
    }

    override fun onFail() {
    }

    override fun isNeedCut() = true
}
