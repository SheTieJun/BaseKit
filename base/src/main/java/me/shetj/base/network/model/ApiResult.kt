package me.shetj.base.network.model

class ApiResult<T> {
    var code = 0
    var msg: String? = null
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }

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