package me.shetj.base.base

import android.os.Message
import androidx.annotation.Keep


import io.reactivex.annotations.NonNull
import me.shetj.base.tools.app.getMessage

/**
 * **@packageName：** me.shetj.base.base<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/2/28<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */

@Keep
abstract class BaseModel : IModel {

    @NonNull
    override fun getMessage(code: Int, obj: Any): Message {
        return Message.obtain().getMessage(code,obj)
    }

    override fun onDestroy() {

    }
}
