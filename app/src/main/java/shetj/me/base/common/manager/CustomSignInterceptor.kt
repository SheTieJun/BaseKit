package shetj.me.base.common.manager

import androidx.annotation.Keep
import com.zhouyou.http.interceptor.BaseDynamicInterceptor
import java.util.*


/**
 */
@Keep
class CustomSignInterceptor : BaseDynamicInterceptor<CustomSignInterceptor>() {

    override fun isAccessToken(): Boolean {
        return true
    }

    override fun isTimeStamp(): Boolean {
        return true
    }

    override fun dynamic(dynamicMap: TreeMap<String, String>): TreeMap<String, String> {
        //dynamicMap:是原有的全局参数+局部参数
        if (isTimeStamp) {
            //是否添加时间戳，因为你的字段key可能不是timestamp,这种动态的自己处理
            dynamicMap["timestamp"] = System.currentTimeMillis().toString()
        }
        if (isAccessToken) {
            //是否添加token
        }
        if (isSign) {
            //是否签名,因为你的字段key可能不是sign，这种动态的自己处理
        }
        //dynamicMap:是原有的全局参数+局部参数+新增的动态参数
        return dynamicMap
    }

}

