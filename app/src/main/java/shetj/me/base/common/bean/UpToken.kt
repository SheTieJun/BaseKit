package com.qcshendeng.toyo.common.bean

/**
 *
 * <b>@packageName：</b> com.qcshengdeng.toyo.common.bean<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/5/30 0030<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
class UpToken{

     var code: String? = null
     var msg: String? = null
     var data: DataBean? = null
    class DataBean {
       
        var upToken: String? = null
    }
}