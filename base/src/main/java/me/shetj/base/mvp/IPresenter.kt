package me.shetj.base.mvp

import androidx.annotation.Keep

/**
 * @author shetj
 */
@Keep
interface IPresenter {

    /**
     * 做一些初始化操作
     */
    fun onStart()

    /**
     * Activity#onDestroy() 调用[IPresenter.onDestroy]
     */
    fun onDestroy()

}