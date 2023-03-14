package me.shetj.base.base

interface BaseCallback<T> {
    /**
     * 成功
     */
    fun onSuccess()

    /**
     * 失败
     */
    fun onFail(ex: Exception?)

    fun onSuccess(key: T)

    fun onFail()
}
