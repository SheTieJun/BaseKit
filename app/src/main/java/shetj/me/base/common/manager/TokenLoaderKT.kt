package shetj.me.base.common.manager

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.shetj.base.S.app
import me.shetj.base.network_coroutine.KCHttp
import me.shetj.base.tools.file.SPUtils.Companion.get
import me.shetj.base.tools.json.EmptyUtils.Companion.isNotEmpty
import shetj.me.base.utils.TimeUtil
import java.util.concurrent.atomic.AtomicBoolean

/**
 * kotlin 获取token ,防止并发
 */
class TokenLoaderKT private constructor() {

    /**
     * 是否已经在去请求Token
     */
    private val mRefreshing = AtomicBoolean(false)
    private var flowToken: Flow<String>? = null

    private object Holder {
        val instance = TokenLoaderKT()
    }

    /**
     * 获取通过
     * 如果过期 或者token 为空就重新去获取
     */
    fun getToke(): Flow<String> {
        return if (!TextUtils.isEmpty(cacheToken)) {
            flow {
                emit(cacheToken!!)
            }.flowOn(Dispatchers.IO)
        } else {
            if (mRefreshing.compareAndSet(false, true)) {
                return flow {
                    KCHttp.get<String>("test/url",error = {
                        throw it  //把异常抛出去
                    })?.apply {
                        emit(this)
                        mRefreshing.set(false)
                    }
                }.flowOn(Dispatchers.IO).also {
                    flowToken = it
                }
            } else {
                flowToken!!
            }
        }
    }

    private val cacheToken: String?
        private get() {
            var token = get(app.applicationContext, "PRE_CUSTOM_TOKEN", "") as String?
            if (isNotEmpty(token)) {
                val timeDiff = TimeUtil.getTimeDiff(getExpire(app.applicationContext))
                token = if (timeDiff > 50000) {
                    return token
                } else {
                    ""
                }
                return token
            }
            return ""
        }


    private fun getExpire(c: Context): String? {
        return get(c, "PRE_CUSTOM_TOKEN_FAILURE_TIME", TimeUtil.getYMDHMSTime()) as String?
    }

}