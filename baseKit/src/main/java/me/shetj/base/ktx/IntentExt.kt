package me.shetj.base.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import me.shetj.base.fix.FixPermission
import java.io.File

fun Context.openUri(uri: String) {
    val intent = Intent()
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    intent.action = Intent.ACTION_VIEW
    intent.data = Uri.parse(uri)
    this.startActivity(intent)
}

fun Intent?.getQueryParameter(key: String): String? {
    val uri = this?.data
    return uri?.getQueryParameter(key)
}

fun Context.openMarket() {
    /**
     * 小米：'mimarket://details?id=xxxx’
     *
     * 华为：'appmarket://details?id=xx’
     *
     * oppo:  'oppomarket://details?id=xxx’
     *
     * vivi ：  'vivomarket://details?id=xx’
     *
     * 荣耀**：'honormarket://details?id=待下载应用包名。
     */
    val uri: Uri = Uri.parse("market://details?id=$packageName")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}

/**
 * 去设置界面
 */
fun Context.openSetting() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

/**
 * 去通知管理界面
 */
fun Context.openNotificationSetting(needCheck: Boolean = false) {
    // 检测是否具有通知权限
    if (needCheck && NotificationManagerCompat.from(this).areNotificationsEnabled()) {
        return
    }
    val intent = Intent()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
    } else {
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val fromParts = Uri.fromParts("package", packageName, null)
        intent.data = fromParts
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

fun Context.openWifiSetting() {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    startActivity(intent)
}

/**
 * Send email text
 *
 *
 * @param addresses
 * @param title
 * @param content
 */
fun Context.sendEmailText(addresses: String = "375105540@qq.com", title: String, content: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$addresses")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(addresses))
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(intent, title))
}

fun Context.sendEmailFile(addresses: String = "375105540@qq.com", title: String, file: File) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        data = Uri.parse("mailto:$addresses")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(addresses))
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(intent, title))
}
