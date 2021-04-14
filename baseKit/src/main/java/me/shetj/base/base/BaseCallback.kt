package me.shetj.base.base

/**
 * **@packageName：** com.aycm.dsy.common<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/3/8<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */

interface BaseCallback<T> : CommonCallBack<T> {
    /**
     * 成功
     */
    fun onSuccess()

    /**
     * 失败
     */
    fun onFail()
}
