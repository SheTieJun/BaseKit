package me.shetj.base.mvp

import androidx.annotation.Keep

/**
 * @author shetj
 */
@Keep
interface IModel {

    /**
     * 结束
     */
    fun onDestroy()
}
