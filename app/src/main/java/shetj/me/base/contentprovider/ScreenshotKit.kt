package shetj.me.base.contentprovider

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.Images.Media
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import me.shetj.base.BaseKit

/**
 * 用来监听用户是否截屏的工具类,在需要监听的页面调用[initActivity]方法即可
 * - 利用lifecycle，来注册[initMediaContentObserver]和反注册[unregisterMediaContentObserver]监听
 * - 已经兼容若在Android 11 版本后进行共享数据的查询，需要使用ContentReslover#query()方法参数为Bundle的方法，
 *   查[官方文档](https://developer.android.google.cn/reference/kotlin/android/content/ContentResolver?hl=en#query_2)，将查询条件使用Bundle组装。
 */
object ScreenshotKit {

    private val MEDIA_PROJECTIONS = arrayOf(
        ImageColumns.DATA,
        ImageColumns.DATE_TAKEN
    )
    private val KEYWORDS = arrayOf(
        "screenshot", "screen_shot", "screen-shot", "screen shot", "截屏",
        "screencapture", "screen_capture", "screen-capture", "screen capture",
        "screencap", "screen_cap", "screen-cap", "screen cap"
    )

    private var mInternalObserver: MediaContentObserver? = null
    private var mExternalObserver: MediaContentObserver? = null
    private var mUiHandler: Handler? = null
    private var mScreenshotListener: ScreenshotListener? = null
    private var canObserver = false


    /**
     * 因为有隐私协议的问题，添加参数在进行判断是否加入监听
     * @param canObserver
     */
    fun setCanObserver(canObserver: Boolean) {
        this.canObserver = canObserver
    }

    /**
     * Is can observer
     * 因为有隐私协议的问题，所以需要用户自己选择是否监听
     */
    private fun isCanObserver() = canObserver

    /**
     * Set screenshot listener
     * 设置截屏监听
     * @param listener
     */
    fun setScreenshotListener(listener: ScreenshotListener?) {
        mScreenshotListener = listener
    }

    /**
     * Get screenshot listener
     * 获取截屏监听
     * @param canObserver 因为有隐私协议的问题，所以需要用户自己选择是否监听
     * @return
     */
    fun initActivity(activity: FragmentActivity, canObserver: Boolean) {
        setCanObserver(canObserver)
        initActivity(activity)
    }

    fun initActivity(activity: FragmentActivity) {
        if (VERSION.SDK_INT >= 34) {
            val screenshotListener = Activity.ScreenCaptureCallback { handleMediaContentChange(Media.EXTERNAL_CONTENT_URI) }
            activity.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Event) {
                    if (event == Event.ON_START) {
                        activity.registerScreenCaptureCallback(Dispatchers.Main.asExecutor(), screenshotListener)
                    } else if (event == Event.ON_STOP) {
                        activity.unregisterScreenCaptureCallback(screenshotListener)
                    }
                }
            })

        } else {
            activity.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Event) {
                    if (event == Event.ON_RESUME) {
                        initMediaContentObserver(activity)
                    } else if (event == Event.ON_PAUSE) {
                        unregisterMediaContentObserver(activity)
                    }
                }
            })
        }
    }


    private fun initMediaContentObserver(context: Context) {
        if (isCanObserver()) {
            // 运行在 UI 线程的 Handler, 用于运行监听器回调
            if (mUiHandler == null) {
                mUiHandler = Handler(Looper.getMainLooper())
            }

            // 创建内容观察者，包括内部存储和外部存储
            if (mInternalObserver == null) mInternalObserver = MediaContentObserver(Media.INTERNAL_CONTENT_URI, mUiHandler)
            if (mExternalObserver == null) mExternalObserver = MediaContentObserver(Media.EXTERNAL_CONTENT_URI, mUiHandler)
            // 注意 第二个boolean参数 要设置为true 不然有些机型（Android 11必须要true）由于多媒体文件层级不同 导致变化监听不到 所以设置后代文件夹发生了文件改变也要进行通知
            context.contentResolver.registerContentObserver(
                Media.INTERNAL_CONTENT_URI, true, mInternalObserver!!
            )
            context.contentResolver.registerContentObserver(
                Media.EXTERNAL_CONTENT_URI, true, mExternalObserver!!
            )
        }
    }

    private fun unregisterMediaContentObserver(context: Context) {
        if (isCanObserver()) {
            if (mInternalObserver != null) context.contentResolver.unregisterContentObserver(mInternalObserver!!)
            if (mExternalObserver != null) context.contentResolver.unregisterContentObserver(mExternalObserver!!)
        }
    }

    private class MediaContentObserver(
        private val mediaContentUri: Uri, handler: Handler?
    ) : ContentObserver(handler) {
        // 处理媒体数据库反馈的数据变化
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            handleMediaContentChange(mediaContentUri)
        }
    }

    private fun handleMediaContentChange(contentUri: Uri) {
        try {
            fetchGalleryFirstImages(
                context = BaseKit.app.applicationContext,
                contentUri,
                orderBy = Media.DATE_ADDED,
                orderAscending = false,
                limit = 1,
                offset = 0
            )?.apply {
                handleMediaRowData(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun fetchGalleryFirstImages(
        context: Context,
        collection: Uri,
        orderBy: String,
        orderAscending: Boolean,
        limit: Int = 10,
        offset: Int = 0
    ): String? {
        createCursor(
            contentResolver = context.contentResolver,
            collection = collection,
            projection = MEDIA_PROJECTIONS,
            orderBy = orderBy,
            orderAscending = orderAscending,
            limit = limit,
            offset = offset
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                // cursor.getColumnIndex获取数据库列索引
                val dateTakenIndex = cursor.getColumnIndex(ImageColumns.DATE_TAKEN)
                val dateTaken = cursor.getLong(dateTakenIndex) // 图片生成时间

                //也会监听到截图删除的操作，判断最后一张图的时间和现在时，如果相差8s内，则认为是刚截图
                if (System.currentTimeMillis() - dateTaken > 8000) return null
                val dataIndex = cursor.getColumnIndex(ImageColumns.DATA)
                val data = cursor.getString(dataIndex) // 图片存储地址
                return data
            }
        }
        return null
    }

    private fun createCursor(
        contentResolver: ContentResolver,
        collection: Uri,
        projection: Array<String>,
        orderBy: String,
        orderAscending: Boolean,
        limit: Int = 20,
        offset: Int = 0
    ): Cursor? = when {
        VERSION.SDK_INT >= VERSION_CODES.O -> {
            val selection = createSelectionBundle(orderBy, orderAscending, limit, offset)
            contentResolver.query(collection, projection, selection, CancellationSignal())
        }

        else -> {
            val orderDirection = if (orderAscending) "ASC" else "DESC"
            var order = when (orderBy) {
                "ALPHABET" -> "${MediaStore.Audio.Media.TITLE}, ${MediaStore.Audio.Media.ARTIST} $orderDirection"
                else -> "${MediaStore.Audio.Media.DATE_ADDED} $orderDirection"
            }
            order += " LIMIT $limit OFFSET $offset"
            contentResolver.query(collection, projection, null, null, order)
        }
    }

    @RequiresApi(VERSION_CODES.O)
    fun createSelectionBundle(
        orderBy: String,
        orderAscending: Boolean,
        limit: Int = 20,
        offset: Int = 0
    ): Bundle = Bundle().apply {
        putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
        putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
        when (orderBy) {
            "ALPHABET" -> putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Files.FileColumns.TITLE))
            else -> putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Files.FileColumns.DATE_ADDED))
        }
        val orderDirection =
            if (orderAscending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
    }

    /**
     * 处理监听到的资源
     */
    private fun handleMediaRowData(data: String) {
        if (checkScreenShot(data)) {
            mScreenshotListener?.onScreenShot(data)
        }
    }

    /**
     * 判断是否是截屏
     */
    private fun checkScreenShot(data: String): Boolean {
        val dataL = data.lowercase(Locale.getDefault())
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (keyWork in KEYWORDS) {
            if (dataL.contains(keyWork)) {
                return true
            }
        }
        return false
    }

    interface ScreenshotListener {
        fun onScreenShot(path: String?)
    }
}


