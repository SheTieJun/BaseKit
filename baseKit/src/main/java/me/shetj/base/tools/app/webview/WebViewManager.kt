package me.shetj.base.tools.app.webview

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.telecom.VideoProfile.isVideo
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebBackForwardList
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import androidx.webkit.CookieManagerCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import me.shetj.base.BaseKit
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.searchFiles
import me.shetj.base.ktx.startRequestPermission
import me.shetj.base.ktx.startRequestPermissions
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.tools.json.EmptyUtils.Companion.isNotEmpty
import java.io.InputStream

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
        webSettings.defaultTextEncodingName = "utf-8" // 设置编码格式
        webSettings.allowFileAccess = true // 将图片调整到适合WebView的大小
        webSettings.loadsImagesAutomatically = true // 支持自动加载图片
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true // 设置加载进来的页面自适应手机屏幕
        webSettings.builtInZoomControls = false // 设置页面可缩放,必须把缩放按钮禁掉,不然无法取消
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }

    companion object {

        /**
         * Start safe browsing
         * 用于启动安全浏览服务。这个服务可以帮助 WebView 防止用户访问被认为是恶意的网站
         */
        fun startSafeBrowsing() {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
                WebViewCompat.startSafeBrowsing(BaseKit.app) {
                    ("WebView.startSafeBrowsing isSuccess = $it").logI()
                }
            }
//            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
//            }
        }

        /**
         * Set web contents debugging enabled
         * @param enabled
         */
        fun setWebContentsDebuggingEnabled(enabled: Boolean) {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(enabled)
            }
        }

        fun getCookieInfo(url: String): MutableList<String> {
            return CookieManagerCompat.getCookieInfo(CookieManager.getInstance(), url)
        }
    }

//    /**
//     * Add hybird
//     *
//     * @param jsObjectName Webpage 的js对象名
//     * @param allowedRules 允许的规则
//     */
//    fun addHybird(jsObjectName:String,allowedRules: Set<String>? = null) {
//        val myListener = object : WebViewCompat.WebMessageListener {
//
//            /**
//             * On post message
//             *
//             * @param view WebView
//             * @param message js代码发送的消息,TYPE_STRING, TYPE_ARRAY_BUFFER
//             * @param sourceOrigin 发送消息的网页地址
//             * @param isMainFrame 是否是主页面，iFrame中的页面为false
//             * @param replyProxy 回复消息的代理
//             */
//            override fun onPostMessage(view: WebView, message: WebMessageCompat, sourceOrigin: Uri, isMainFrame: Boolean, replyProxy: JavaScriptReplyProxy) {
//                // do something about view, message, sourceOrigin and isMainFrame.
//
//                message.type
//            }
//        }
//        val allowedOriginRules = allowedRules ?: setOf()
//        WebViewCompat.addWebMessageListener(
//            /* webView = */ webView,
//            /* jsObjectName = */jsObjectName,
//            /* allowedOriginRules = */ allowedOriginRules,
//            /* listener = */myListener
//        );
//    }


    fun muteAudio(isMute: Boolean) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.MUTE_AUDIO)) {
            WebViewCompat.setAudioMuted(webView, isMute)
        }
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
        // 是否可访问Content Provider的资源，默认值 true
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

    private fun isVideo(resources: Array<String>): Boolean {
        return resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
    }

    private fun isOnlyAudio(resources: Array<String>): Boolean {
        return !resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) && resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
    }

    /**
     * 处理权限的获取：相机和录音
     * 录音需要权限` <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>`
     */
    fun onPermissionRequest(activity: FragmentActivity, request: PermissionRequest?) {
        request?.let {
            kotlin.runCatching {
                if (isVideo(request.resources)) {
                    activity.startRequestPermissions(
                        permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    ) {
                        if (it.filter { m -> !m.value }.isEmpty()) {
                            request.grant(request.resources)
                            request.origin
                        }
                    }
                } else if (isOnlyAudio(request.resources)) {
                    activity.startRequestPermission(permission = Manifest.permission.RECORD_AUDIO) {
                        if (it) {
                            request.grant(request.resources)
                            request.origin
                        }
                    }
                }
            }
        }
    }

    /**

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
     * * 这段js函数的功能就是，
     *      * 遍历所有的img节点，
     *      * 并添加onclick函数，
     *      * 函数的功能是在图片点击的时候调用本地java接口并传递url过去
     *      * onPageFinish 调用才有用
     *
     * @param jsName Webview.addJavascriptInterface(xxx,jsName)
     */
    fun addImageClick(jsName: String) {
        webView.loadUrl(
            "javascript:(function() { " +
                    "var imgList = document.getElementsByTagName('img');" +
                    "var imgSrcList = [];" +
                    "for (var i = 0; i < imgList.length; i++) {" +
                    "    imgSrcList.push(imgList[i].src);" +
                    "    imgList[i].onclick=function()  " +
                    "    {  " +
                    "        window.$jsName.openImage(this.src);  " +
                    "    }  " +
                    "}" +
                    "window.$jsName.onImageListReceived(JSON.stringify(imgSrcList));" +
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
                """
                    javascript:(function() {   
                        var parent = document.getElementsByTagName('head').item(0);   
                        var scriptConsole = document.createElement('script');   
                        scriptConsole.innerHTML = window.atob('$encoded');   
                        parent.appendChild(scriptConsole);   
                        var scriptAdd = document.createElement('script');   
                        scriptAdd.innerHTML = 'var vConsole = new window.VConsole();';   
                        parent.appendChild(scriptAdd);   
                     })()    
                """.trimIndent()
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
                activity.searchFiles(fileChooserParams.acceptTypes) { files ->
                    files.let {
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
            cookieManager.setCookie(entry.key, entry.value){
                "setCookie: success = $it".logI("webview")
            }
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
        return webSettings.userAgentString.orEmpty()
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
     * 自动加载图片
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
