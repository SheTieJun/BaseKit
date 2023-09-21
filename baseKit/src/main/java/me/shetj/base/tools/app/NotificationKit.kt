package me.shetj.base.tools.app

import android.app.PendingIntent
import android.os.Build

/**
 *
 */
object NotificationKit {

    /**
     *   通知栏 PendingIntent的FLAG
     *   @param mutable 是否可变，有些通知栏必须是可变类型
     */
    fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }
}