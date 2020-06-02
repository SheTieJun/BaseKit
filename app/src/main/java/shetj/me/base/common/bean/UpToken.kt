package shetj.me.base.common.bean

class UpToken {

    var code: String? = null
    var msg: String? = null
    var data: DataBean? = null

    class DataBean {

        var upToken: String? = null
    }
}