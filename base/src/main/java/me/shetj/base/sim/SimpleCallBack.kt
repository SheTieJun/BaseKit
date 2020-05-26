package me.shetj.base.sim

import me.shetj.base.base.BaseCallback

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2019/6/15<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 简易处理的callback <br>
 */
class SimpleCallBack<T> : BaseCallback<T> {
    override fun onSuccess() {
    }

    override fun onSuccess(key: T) {
    }

    override fun onFail() {
    }

    override fun onFail(ex: Exception) {
    }
}