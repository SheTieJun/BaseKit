package me.shetj.base.network.model

class ApiResult<T> {
    var code = 0
    var msg: String? = null
    var data: T? = null

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

fun ApiResult<*>?.isOkData(): Boolean {
    return this?.isOk ?: false
}