package shetj.me.base.utils.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import me.shetj.base.ktx.logI

class VideoPlayerImpl(private val mActivity: Activity?, private val mWebView: WebView?) : InterVideo, EventInterceptor {
    private var mMovieView: View? = null
    private var mMovieParentView: ViewGroup? = null
    private var progressVideo: View? = null
    private var mCallback: CustomViewCallback? = null
    private var mListener: VideoWebListener? = null

    /**
     * 设置是否使用该自定义视频，默认使用
     */
    private var isShowCustomVideo = true

    fun setListener(mListener: VideoWebListener?) {
        this.mListener = mListener
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (isShowCustomVideo) {
            if (!isActivityAlive(mActivity)) {
                return
            }
            "--Video-----onShowCustomView----切换方向---,横屏".logI()
            isLFullVideo()

            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (mMovieView != null) {
                callback?.onCustomViewHidden()
                return
            }
            if (mWebView != null) {
                mWebView.visibility = View.GONE
                if (mListener != null) {
                    mListener!!.hindWebView()
                }
            }
            //添加view到decorView容齐中
            fullViewAddView(view)
            this.mCallback = callback
            this.mMovieView = view
            if (mListener != null) {
                mListener!!.showVideoFullView()
            }
        }
    }

    private fun isLFullVideo() {
        mWebView!!.postDelayed({
            try {
                //定义javaScript方法
                val javascript = """javascript:function getFullscreenVideoOrientation() {
                                    let element = document.fullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement || document.msFullscreenElement;
                                    console.log('========isLFullVideo========', element); 
                                    if (!element) {
                                      return;
                                    }
                                    if (element.tagName !== 'VIDEO') {
                                      window.LHAPP.isHorizontally(true);
                                      return;
                                    }
                                    let videoWidth = element.videoWidth;
                                    let videoHeight = element.videoHeight;    
                                    window.AndroidAPP.isHorizontally(videoWidth > videoHeight);
                                    }"""
                mWebView.loadUrl(javascript)
                mWebView.loadUrl("javascript:getFullscreenVideoOrientation();")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 150)
    }

    /**
     * 添加view到decorView容齐中
     *
     * @param view view
     */
    private fun fullViewAddView(view: View?) {
        if (mMovieParentView == null) {
            val mDecorView = mActivity!!.window.decorView.findViewById<FrameLayout>(android.R.id.content)
            mMovieParentView = FullscreenHolder(mActivity)
            "--Video-----onShowCustomView----添加view到decorView容齐中---".logI()
            mDecorView.addView(mMovieParentView)
        }
        mMovieParentView!!.addView(view)
        mMovieParentView!!.visibility = View.VISIBLE
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onHideCustomView() {
        if (isShowCustomVideo) {
            if (mMovieView == null || mActivity == null) {
                // 不是全屏播放状态
                return
            }
            "--Video-----onShowCustomView----切换方向---,竖屏".logI()
            if (mActivity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            if (mMovieParentView != null && mMovieView != null) {
                mMovieView!!.visibility = View.GONE
                mMovieParentView!!.removeView(mMovieView)
            }
            if (mMovieParentView != null) {
                mMovieParentView!!.visibility = View.GONE
                if (mListener != null) {
                    mListener!!.hindVideoFullView()
                }
            }
            if (this.mCallback != null) {
                mCallback!!.onCustomViewHidden()
            }
            this.mMovieView = null
            if (mWebView != null) {
                mWebView.visibility = View.VISIBLE
                if (mListener != null) {
                    mListener!!.showWebView()
                }
            }
        }
    }

    override fun getVideoLoadingProgressView(): View {
        if (progressVideo == null && mActivity != null) {
            progressVideo = ProgressBar(mActivity)
        }
        return progressVideo!!
    }

    override fun isVideoState(): Boolean {
        return mMovieView != null
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun event(): Boolean {
        if (isVideoState()) {
            this.onHideCustomView()
            if (isActivityAlive(mActivity)) {
                mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            return true
        } else {
            return false
        }
    }

    /**
     * 销毁的时候需要移除一下视频view
     */
    fun removeAllViews() {
        if (mMovieView != null) {
            mMovieParentView!!.removeAllViews()
        }
    }

    /**
     * 设置是否使用自定义视频视图
     *
     * @param showCustomVideo 是否使用自定义视频视图
     */
    fun setShowCustomVideo(showCustomVideo: Boolean) {
        this.isShowCustomVideo = showCustomVideo
    }

    companion object {
        fun isActivityAlive(activity: Activity?): Boolean {
            return activity != null && !activity.isFinishing && !activity.isDestroyed
        }
    }
}