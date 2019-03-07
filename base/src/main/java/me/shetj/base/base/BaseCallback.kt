package me.shetj.base.base

/**
 * **@packageName：** com.aycm.dsy.common<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/3/8<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */

interface BaseCallback<T> {
    /**
     * 成功
     */
    fun onSuccess()

    /**
     * 成功带有结果
     * @param result 成功结果
     */
    fun onSuccess(result: T)

    /**
     * 失败
     */
    fun onFail()

    /**
     * 失败,并且带上失败信息
     * @param result 失败结果
     */
    fun onFail(result: T)
}
