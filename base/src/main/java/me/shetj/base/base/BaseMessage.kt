package me.shetj.base.base

import androidx.annotation.Keep

/**
 * **@packageName：** me.shetj.base.base<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/3/28<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
@Keep
class BaseMessage<T> {
    var type: Int = 0
    var position: Int = 0
    var obj: T? = null
    var msg: String? = null
}
