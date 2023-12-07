package me.shetj.base.tools.app.webview

import android.app.Activity
import android.webkit.JavascriptInterface
import me.shetj.base.tools.json.GsonKit
import java.lang.ref.WeakReference

abstract class WebKitJs(activity: Activity) {

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
    fun openImage(url: String){
        openImage(urls?: emptyList(), urls?.indexOf(url)?:0)
    }


    abstract fun openImage(urls: List<String>, position: Int)
}

