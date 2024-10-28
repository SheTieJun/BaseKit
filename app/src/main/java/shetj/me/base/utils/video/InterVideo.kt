package shetj.me.base.utils.video

import android.view.View
import android.webkit.WebChromeClient.CustomViewCallback

interface InterVideo {
    /**
     * 通知应用当前页进入了全屏模式，此时应用必须显示一个包含网页内容的自定义View
     * 播放网络视频时全屏会被调用的方法，播放视频切换为横屏
     *
     * @param view     view
     * @param callback callback
     */
    fun onShowCustomView(view: View?, callback: CustomViewCallback?)

    /**
     * 通知应用当前页退出了全屏模式，此时应用必须隐藏之前显示的自定义View
     * 视频播放退出全屏会被调用的
     */
    fun onHideCustomView()

    /**
     * 当全屏的视频正在缓冲时，此方法返回一个占位视图(比如旋转的菊花)。
     * 视频加载时进程loading
     *
     * @return view
     */
    fun getVideoLoadingProgressView(): View?

    /**
     * 视频切换状态
     *
     * @return 布尔值
     */
    fun isVideoState(): Boolean
}
