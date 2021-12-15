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
package me.shetj.base.tools.app

import android.annotation.SuppressLint
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import java.io.InputStream
import me.shetj.base.tools.json.EmptyUtils.Companion.isNotEmpty

/**
 * WebView管理器，提供常用设置
 * @author Administrator
 */
class WebViewManager(private val webView: WebView) {
    private val webSettings: WebSettings = webView.settings

    init {
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8)
        val appCachePath: String = webView.context.cacheDir.absolutePath
        webSettings.setAppCachePath(appCachePath)
        webSettings.allowFileAccess = true
        webSettings.setAppCacheEnabled(true)
        webSettings.loadWithOverviewMode = true // 设置加载进来的页面自适应手机屏幕
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = false // 设置页面可缩放,必须把缩放按钮禁掉,不然无法取消
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     */
    fun imgReset() {
        webView.loadUrl(
            "javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "var img = objs[i];   " +
                "    img.style.maxWidth = '100%';" +
                "    img.style.height = 'auto';  " +
                "}" +
                "})()"
        )
    }

    /**
     * 这段js函数的功能就是，
     * 遍历所有的img节点，
     * 并添加onclick函数，
     * 函数的功能是在图片点击的时候调用本地java接口并传递url过去
     */
    fun addImageClickListner() {
        webView.loadUrl(
            "javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "    objs[i].onclick=function()  " +
                "    {  " +
                "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()"
        )
    }

    fun addVideoStartListener() {
        webView.context?.assets?.let {
            val inputStream: InputStream = it.open("video.js")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            webView.loadUrl(
                "javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('$encoded');" +
                    "parent.appendChild(script)" +
                    "})()"
            )
        }
    }

    /**
     * 给设置localStorage 设置数据
     */
    fun setLocalStorage(itmes: Map<String, String>) {
        val jsonBuf = StringBuilder()
        for (key in itmes.keys) {
            if (isNotEmpty(itmes[key])) {
                jsonBuf.append("localStorage.setItem('key', '")
                    .append(itmes[key])
                    .append("');")
            }
        }
        val info = jsonBuf.toString()
        if (isNotEmpty(info)) {
            webView.evaluateJavascript(info, null)
        }
    }

    /**
     * 设置cookie
     */
    fun setCookie(map: MutableMap<String, String>) {
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        map.onEach { entry ->
            cookieManager.setCookie(entry.key, entry.value)
        }
        cookieManager.flush()
    }

    /**
     * 开启自适应功能
     */
    fun enableAdaptive(): WebViewManager {
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        return this
    }

    /**
     * 禁用自适应功能
     */
    fun disableAdaptive(): WebViewManager {
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        return this
    }

    /**
     * 开启缩放功能
     */
    fun enableZoom(): WebViewManager {
        webSettings.setSupportZoom(true)
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        return this
    }

    /**
     * 禁用缩放功能
     */
    fun disableZoom(): WebViewManager {
        webSettings.setSupportZoom(false)
        webSettings.useWideViewPort = false
        webSettings.builtInZoomControls = false
        return this
    }

    /**
     * 开启JavaScript
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun enableJavaScript(): WebViewManager {
        webSettings.javaScriptEnabled = true
        return this
    }

    /**
     * 禁用JavaScript
     */
    fun disableJavaScript(): WebViewManager {
        webSettings.javaScriptEnabled = false
        return this
    }

    /**
     * 开启JavaScript自动弹窗
     */
    fun enableJavaScriptOpenWindowsAutomatically(): WebViewManager {
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        return this
    }

    /**
     * 禁用JavaScript自动弹窗
     */
    fun disableJavaScriptOpenWindowsAutomatically(): WebViewManager {
        webSettings.javaScriptCanOpenWindowsAutomatically = false
        return this
    }

    /**
     * 返回
     * @return true：已经返回，false：到头了没法返回了
     */
    fun goBack(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true
        } else {
            false
        }
    }
}
