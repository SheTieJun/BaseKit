package me.shetj.base.base

import me.shetj.annotation.apt.SimpleImpl

/**
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/3/7<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */

@SimpleImpl
interface CommonCallBack<T> {
    /**
     * 成功
     * @param key 成功
     */
    fun onSuccess(key: T)

    /**
     * 结束
     */
    fun onFail(ex: Exception)
}
