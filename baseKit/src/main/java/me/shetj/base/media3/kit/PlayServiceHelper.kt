package me.shetj.base.media3.kit

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import me.shetj.base.media3.PlaybackService

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/8/28<br>
 */
internal object PlayServiceHelper {

    private var sessionActivityIntent: Intent? = null


    /**
     * 设置[me.shetj.media.player.media3.PlaybackService]的sessionActivityIntent
     */
    fun setSessionActivityIntent(intent: Intent) {
        sessionActivityIntent = intent
    }


    fun getSessionActivityIntent(context: Context): Intent {
        return sessionActivityIntent ?: context.packageManager.getLaunchIntentForPackage(context.packageName)!!
    }


    @OptIn(UnstableApi::class)
    internal fun getSingleTopActivity(context: Context): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            sessionActivityIntent ?: context.packageManager.getLaunchIntentForPackage(context.packageName)!!,
            PlaybackService.immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

}