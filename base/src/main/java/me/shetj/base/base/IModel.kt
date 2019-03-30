package me.shetj.base.base

import android.os.Message
import androidx.annotation.Keep

/**
 * @author shetj
 */
@Keep
interface IModel {

    /**
     * 转化成message
     * @param code 类型
     * @param obj 数据
     * @return BaseMessage
     */
    fun getMessage(code: Int, obj: Any): Message

    /**
     * 结束
     */
    fun onDestroy()
}
