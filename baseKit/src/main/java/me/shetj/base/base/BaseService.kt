package me.shetj.base.base

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * ================================================
 * 基类 [Service]
 * @author shetj
 */
@Keep
abstract class BaseService : Service() ,KtScopeComponent{
    override val ktScope: DefCoroutineScope by defScope()


    override fun onCreate() {
        super.onCreate()
        ktScope.launch {
           init()
        }
    }

    //1. startService -> onCreate -> onStartCommand
    //2. stopService -> onDestroy
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (needNotification()) {
            createNotification(this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //1. bindService -> onCreate -> onBind
    //2. unBindService-> unBind -> onDestroy
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        ktScope.cancel()
        super.onDestroy()
    }


    open fun needNotification(): Boolean {
        return false
    }

    /**
     * 初始化
     */
    abstract suspend fun init()

    /**
     * 展示通知栏
     */
    open fun createNotification(context: Context) {

    }

}