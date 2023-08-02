
package me.shetj.base.tools.app.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebBackForwardList
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import java.io.InputStream
import me.shetj.base.ktx.searchFiles
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.tools.json.EmptyUtils.Companion.isNotEmpty

/**
 * WebView管理器，提供常用设置
 * @author Administrator
 */
@Suppress("DEPRECATION")
class WebViewManager(private val webView: WebView) {
    private val webSettings: WebSettings = webView.settings

    init {
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        webSettings.allowFileAccess = true//将图片调整到适合WebView的大小
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true // 设置加载进来的页面自适应手机屏幕
        webSettings.builtInZoomControls = false // 设置页面可缩放,必须把缩放按钮禁掉,不然无法取消
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
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

    fun enableLoadLocalFile() {
        //是否可访问Content Provider的资源，默认值 true
        webSettings.allowContentAccess = true
        // 是否可访问本地文件，默认值 true
        webSettings.allowFileAccess = true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.allowFileAccessFromFileURLs = false
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.allowUniversalAccessFromFileURLs = false
    }

    fun loadHtml(html: String) {
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }

    /**
     * 这段js函数的功能就是，
     * 遍历所有的img节点，
     * 并添加onclick函数，
     * 函数的功能是在图片点击的时候调用本地java接口并传递url过去
     * onPageFinish 调用才有用
     */
    fun addImageClick() {
        webView.loadUrl(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{" +
                    "    objs[i].onclick=function()  " +
                    "    {  " +
                    "        window.App.openImage(this.src);  " +
                    "    }  " +
                    "}" +
                    "})()"
        )
    }


    /**
     * Add console2
     * 通过本地文件加载(推荐)
     */
    fun addConsole() {
        webView.context?.assets?.let { manager ->
            val inputStream: InputStream = manager.open("vconsole.js")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            webView.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var scriptConsole = document.createElement('script');" +
                        "scriptConsole.innerHTML = window.atob('$encoded');" +
                        "parent.appendChild(scriptConsole);" +
                        "var scriptAdd = document.createElement('script');" +
                        "scriptAdd.innerHTML = 'var vConsole = new window.VConsole();';" +
                        "parent.appendChild(scriptAdd);" +
                        "})()"
            )
        }
    }

    /**
     *  通往CDN连接加载
     */
    fun addConsole2() {
        webView.loadUrl(
            "javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var scriptConsole = document.createElement('script');" +
                    "scriptConsole.src = 'https://unpkg.com/vconsole@latest/dist/vconsole.min.js';" +
                    "parent.appendChild(scriptConsole);" +
                    "var scriptAdd = document.createElement('script');" +
                    "scriptAdd.innerHTML = 'var vConsole = new window.VConsole();';" +
                    "parent.appendChild(scriptAdd);" +
                    "})()"
        )
    }


    fun onShowFileChooser(
        activity: FragmentActivity,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        fileChooserParams?.acceptTypes?.also {
            if (it.isNotEmpty()) {
                activity.searchFiles(fileChooserParams.acceptTypes) { files->
                    files?.let {
                        filePathCallback?.onReceiveValue(files.toTypedArray())
                    }
                }
                return true
            }
        }
        return false
    }

    fun setGeolocationEnabled(enabled: Boolean) {
        webSettings.setGeolocationEnabled(enabled)
        webSettings.setGeolocationDatabasePath(webView.context.filesDir.path)
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
     * @param map<url,cookie>
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
     * [HttpHeaders.userAgent]
     * @param userAgent
     */
    fun setUserAgent(userAgent: String) {
        webSettings.userAgentString = userAgent
    }

    fun getUserAgentString(): String {
        return webSettings.userAgentString ?: ""
    }

    /**
     * 设置可以自定播放视频，不要去强制触碰
     */
    fun autoPlay() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.mediaPlaybackRequiresUserGesture = false
        }
    }


    fun disAutoPlay() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.mediaPlaybackRequiresUserGesture = true
        }
    }

    /**
     * Is enable auto load image
     * 自动价值图片
     * @param enabled
     */
    fun isEnableAutoLoadImage(enabled: Boolean) {
        webSettings.loadsImagesAutomatically = enabled
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


    fun clearCache() {
        // 清空网页访问留下的缓存数据。
        // 需要注意的时，由于缓存是全局的，所以只要是WebView用到的缓存都会被清空，即便其他地方也会使用到。
        // 该方法接受一个参数，从命名即可看出作用。若设为false，则只清空内存里的资源缓存，而不清空磁盘里的
        webView.clearCache(true)

        // 清除当前WebView访问的历史记录
        // 只会WebView访问历史记录里的所有记录除了当前访问记录
        webView.clearHistory()

        // 清除自动完成填充的表单数据。
        // 需要注意的是，该方法仅仅清除当前表单域自动完成填充的表单数据，并不会清除WebView存储到本地的数据。
        webView.clearFormData()
    }

    // 向上滚动
    fun pageUp(top: Boolean) {
        // top为true时，将WebView展示的页面滑动至顶部
        // top为false时，将WebView展示的页面向上滚动一个页面高度
        webView.pageUp(top)
    }

    // 向下滚动
    fun pageDown(bottom: Boolean) {
        // bottom为true时，将WebView展示的页面滑动至底部
        // top为false时，将WebView展示的页面向下滚动一个页面高度
        webView.pageDown(bottom)
    }


    fun limitFile() {
        // 禁用 file 协议；
        webSettings.allowFileAccess = false
        webSettings.allowFileAccessFromFileURLs = false
        webSettings.allowUniversalAccessFromFileURLs = false
    }

    /**
     * Capture picture
     * 获取webview 内容截图
     * @return
     */
    fun capturePicture(): Bitmap {
        val width = webView.width
        val scale: Float = webView.scale
        val height = (webView.contentHeight * scale + 0.5).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        webView.draw(canvas)
        return bitmap
    }

    /**
     * Get copy back list
     * 获取WebView栈内存储了多少子页面
     */
    fun getCopyBackList(): WebBackForwardList {
        return webView.copyBackForwardList()
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
