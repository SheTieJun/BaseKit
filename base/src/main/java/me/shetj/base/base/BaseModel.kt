package me.shetj.base.base

import androidx.annotation.Keep

import com.zhouyou.http.model.HttpParams

import io.reactivex.annotations.NonNull
import me.shetj.base.http.easyhttp.EasyHttpUtils

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
    override fun getMessage(code: Int, obj: Any): BaseMessage<*> {
        return EasyHttpUtils.getMessage(code, obj)
    }

    @NonNull
    fun getParamsFromMap(@NonNull map: Map<String, String>): HttpParams {
        return EasyHttpUtils.getParamsFromMap(map)
    }

    override fun onDestroy() {

    }
}
