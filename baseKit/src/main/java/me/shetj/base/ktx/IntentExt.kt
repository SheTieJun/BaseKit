package me.shetj.base.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.io.File


fun Intent?.getQueryParameter(key: String): String? {
    val uri = this?.data
    return uri?.getQueryParameter(key)
}


fun Context.openMarket() {
    val uri: Uri = Uri.parse("market://details?id=$packageName")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}

fun Context.openSetting() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}


fun Context.openWifiSetting(){
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    startActivity(intent)
}

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
