package shetj.me.base.common.manager

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.shetj.base.S.app
import me.shetj.base.ktx.logi
import me.shetj.base.network_coroutine.KCHttp
import me.shetj.base.network_coroutine.KCHttpV2
import me.shetj.base.network_coroutine.fold
import me.shetj.base.tools.file.SPUtils.Companion.get
import me.shetj.base.tools.json.EmptyUtils.Companion.isNotEmpty
import shetj.me.base.common.tag.SPKey.SAVE_TOKEN
import shetj.me.base.utils.TimeUtil
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * kotlin 获取token ,防止并发
 */
class TokenLoaderKT private constructor() {
    /**
     * 是否已经在去请求Token
     */
    private val mRefreshing = AtomicBoolean(false)
    private var count = AtomicInteger(0)
    private val channel = Channel<String>()

    object Holder {
        val instance = TokenLoaderKT()
    }

    private val tokenFlow = flow {
        if (!TextUtils.isEmpty(cacheToken)) {
            emit(cacheToken!!)
        } else {
            if (mRefreshing.compareAndSet(false, true)) {
                getTokenByHttp()?.apply {
                    mRefreshing.set(false)
                    emit(this)
                }
            } else {
                count.incrementAndGet()
                emit(channel.receive())
            }
        }
    }.flowOn(Dispatchers.IO)



    private suspend fun getTokenByHttp(): String? {
        return KCHttp.get<String>("test/url", error = {
        }).let {
            val s = "这是token${System.currentTimeMillis()}"
            TokenManager.getInstance().token = s
            while (count.get() != 0){
                channel.send(s)
                count.decrementAndGet()
            }
            s
        }
    }

    private val cacheToken: String?
        private get() {
            var token = get(app.applicationContext, SAVE_TOKEN, "") as String?
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

    fun getToke(): Flow<String> {
        return tokenFlow
    }

}