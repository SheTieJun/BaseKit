package shetj.me.base.bean

import androidx.annotation.Keep

@Keep
class ApiResult1<T> {
    var code = 0
    var msg: String? = null
    var data: T? = null
    var s: String? = null

    val isOk: Boolean
        get() = code == 0

    override fun toString(): String {
        return "ApiResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}'
    }
}