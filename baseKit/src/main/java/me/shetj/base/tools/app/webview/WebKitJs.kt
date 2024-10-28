package me.shetj.base.tools.app.webview

import android.app.Activity
import android.content.pm.ActivityInfo
import android.webkit.JavascriptInterface
import me.shetj.base.tools.json.GsonKit
import java.lang.ref.WeakReference

class WebKitJs(activity: Activity) {

    private var weekActivity: WeakReference<Activity>? = null
    private var urls: List<String>? = null

    init {
        weekActivity = WeakReference(activity)
    }

    @JavascriptInterface
    fun onImageListReceived(urlsJson: String) {
        urls = GsonKit.jsonToList(urlsJson, String::class.java)
    }

    @JavascriptInterface
    fun openImage(url: String) {
        openImage(urls.orEmpty(), urls?.indexOf(url) ?: 0)
    }

    @JavascriptInterface
    fun setOrientation(landscape: Boolean) {
        weekActivity?.get()?.requestedOrientation = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    @JavascriptInterface
    fun openImage(urls: List<String>, position: Int) {
    }
}
