package shetj.me.base.utils.video

import android.app.Activity
import android.content.Context
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import me.shetj.base.ktx.logI

class VideoChromeClient(context: Context, webView: WebView) : WebChromeClient() {
    /**
     * 网页点击全屏按钮会触发WebChromeClient的onShowCustomView方法，全屏后缩回来会触发onHideCustomView方法
     * 给你WebView所在Activity的清单文件中添加以下属性
     * 需要加：android:configChanges="orientation|screenSize"
     * 非必须：android:hardwareAccelerated="true"
     */
    private val mIVideo: InterVideo?
    private val video: VideoPlayerImpl = VideoPlayerImpl(context as Activity, webView)

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    init {
        this.mIVideo = video
    }

    /**
     * 设置视频播放监听，主要是比如全频，取消全频，隐藏和现实webView
     *
     * @param videoWebListener listener
     */
    fun setVideoListener(videoWebListener: VideoWebListener?) {
        video.setListener(videoWebListener)
    }

    /**
     * 设置是否使用
     *
     * @param showCustomVideo 是否使用自定义视频视图
     */
    fun setCustomVideo(showCustomVideo: Boolean) {
        video.setShowCustomVideo(showCustomVideo)
    }

    /**
     * 通知应用当前页进入了全屏模式，此时应用必须显示一个包含网页内容的自定义View
     * 播放网络视频时全屏会被调用的方法，播放视频切换为横屏
     *
     * @param view               view
     * @param customViewCallback callback
     */
    override fun onShowCustomView(view: View, customViewCallback: CustomViewCallback) {
        if (mIVideo != null) {
            mIVideo.onShowCustomView(view, customViewCallback)
        } else {
            super.onShowCustomView(view, customViewCallback)
        }
    }

    /**
     * 通知应用当前页退出了全屏模式，此时应用必须隐藏之前显示的自定义View
     * 视频播放退出全屏会被调用的
     */
    override fun onHideCustomView() {
        if (mIVideo != null) {
            mIVideo.onHideCustomView()
        } else {
            super.onHideCustomView()
        }
    }

    /**
     * 当全屏的视频正在缓冲时，此方法返回一个占位视图(比如旋转的菊花)。
     * 视频加载时进程loading
     */
    override fun getVideoLoadingProgressView(): View? {
        return if (mIVideo != null) {
            mIVideo.getVideoLoadingProgressView()
        } else {
            super.getVideoLoadingProgressView()
        }
    }

    /**
     * 销毁的时候需要移除一下视频view
     */
    fun removeVideoView() {
        video.removeAllViews()
    }

    /**
     * 获取video状态，判断是否是全屏
     *
     * @return
     */
    fun inCustomView(): Boolean {
        return video.isVideoState()
    }

    /**
     * 隐藏视频
     * 逻辑是：先判断是否全频播放，如果是，则退出全频播放
     * 全屏时按返加键执行退出全屏方法
     */
    fun hideCustomView() {
        val event = video!!.event()
        if (event) {
            "-----hideVideo-----隐藏视频----".logI()
        }
    }
}
