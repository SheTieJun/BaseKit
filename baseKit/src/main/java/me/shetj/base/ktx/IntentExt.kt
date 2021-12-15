/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
