package me.shetj.base.media3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import me.shetj.base.media3.factory.MediaItemFactory
import me.shetj.base.media3.factory.SimpleMediaItemFactory
import me.shetj.base.media3.kit.PlayServiceHelper
import me.shetj.base.media3.listener.PositionPlayListener
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 这里类只是为了方便，你需要根据你得实际情况，进行封装使用
 */
object MediaX {
    private val isInit = AtomicBoolean(false)
    private var mMediaItemFactory: MediaItemFactory? = null
    private var browserFuture: ListenableFuture<MediaBrowser>? = null

    private val browser: MediaBrowser?
        get() = if (browserFuture?.isDone == true) browserFuture?.get() else null

    /**
     * 初始化，会创建全局的MediaBrowser
     */
    fun init(context: Context) {
        if (isInit.compareAndSet(false, true)) {
            initializeBrowser(context)
        }
    }

    /**
     * 销毁,正常是用不到的，因为是全局的，当然不排除特殊用法
     */
    fun destroy() {
        isInit.set(false)
        browserFuture?.let { MediaBrowser.releaseFuture(it) }
    }

    @UnstableApi
    fun initWithProcessLifecycle(context: Context) {
        init(context)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    destroy()
                }
            }
        })
    }


    /**
     * Add listener with lifecycle
     * PositionPlayListener 是自定义的一个监听器，用来监听播放的位置
     */
    fun addListenerWithLifecycle(activity: FragmentActivity, listener: Player.Listener) {
        browserFuture?.addListener({
            activity.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        browser?.addListener(listener)
                        if (listener is PositionPlayListener){
                            if (browser?.isPlaying == true){
                                listener.handler.sendEmptyMessage(Player.STATE_READY)
                            }
                        }
                    } else if (event == Lifecycle.Event.ON_PAUSE) {
                        browser?.removeListener(listener)
                    }
                }
            })
        }, ContextCompat.getMainExecutor(activity))
    }

    /**
     * Set media item
     * 用来设置普通课程
     */
    fun setMediaItem(context: Context, mediaItem: MediaItem) {
        browserFuture?.addListener({
            browser?.clearMediaItems()
            browser?.setMediaItem(mediaItem)
            browser?.prepare()
            browser?.play()
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Set media items
     * 用来设置一个课程存在多个音频的场景
     */
    fun setMediaItems(context: Context, mediaItems: List<MediaItem>) {
        browserFuture?.addListener({
            browser?.clearMediaItems()
            browser?.setMediaItems(mediaItems)
            browser?.prepare()
            browser?.play()
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Add media items
     * 语音在过程中添加
     */
    fun addMediaItems(context: Context, mediaItems: List<MediaItem>) {
        browserFuture?.addListener({
            browser?.addMediaItems(mediaItems)
        }, ContextCompat.getMainExecutor(context))
    }


    /**
     * Set media item factory
     *
     */
    fun setMediaItemFactory(mediaItemFactory: MediaItemFactory) {
        mMediaItemFactory = mediaItemFactory
    }

    fun getMediaItemFactory(): MediaItemFactory {
        return mMediaItemFactory ?: SimpleMediaItemFactory()
    }

    /**
     * Set session activity intent
     * 设置点击通知栏的activity
     * @param intent
     */
    fun setSessionActivityIntent(intent: Intent) {
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        PlayServiceHelper.setSessionActivityIntent(intent)
    }

    fun getMediaBrowser(): MediaBrowser? {
        return browser
    }

    fun playOrPause() {
        if (browser?.mediaItemCount == 0) {
            return
        }
        if (browser?.isPlaying == true) {
            browser?.pause()
        } else {
            browser?.play()
        }
    }

    private fun initializeBrowser(context: Context) {
        browserFuture = MediaBrowser.Builder(
            context.applicationContext,
            SessionToken(context, ComponentName(context.applicationContext, PlaybackService::class.java))
        ).buildAsync()
    }
}