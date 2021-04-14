package me.shetj.base.network.func

import io.reactivex.rxjava3.functions.Function
import me.shetj.base.network.exception.ServerException
import me.shetj.base.network.model.ApiResult
import me.shetj.base.network.model.isOkData

//把ApiResult<T>转换成 T
class HandleFuc<T> : Function<ApiResult<T>, T> {


    override fun apply(t: ApiResult<T>): T {
        if (t.isOkData()) {
            return t.data!!
        } else {
            throw ServerException(t.code, t.msg)
        }
    }
}