package shetj.me.base.utils

import android.content.Context
import android.widget.ImageView

/**
 * 图片加载
 *
 * @author shetj
 */
interface ImageLoader {
    /**
     * 加载图片
     *
     * @param simpleView 容器
     * @param url        url地址
     */
    fun load(simpleView: ImageView?, url: String)

    /**
     * 加载图片 是否展示进度
     *
     * @param simpleView  容器
     * @param url         url地址
     * @param hasProgress 是否展示进度
     */
    fun load(simpleView: ImageView?, url: String, hasProgress: Boolean)

    /**
     * 渐进式展示图片
     *
     * @param mSimpleView 容器
     * @param url         url地址
     */
    fun loadProgressive(mSimpleView: ImageView?, url: String)

    /**
     * 加载GIf
     *
     * @param simpleView view
     * @param url        url地址(uri)
     * @param isAuto     是否自动播放
     */
    fun loadGif(simpleView: ImageView?, url: String, isAuto: Boolean)

    /**
     * 预加载图片
     *
     * @param url                      图片地址
     * @param isDiskCacheOrBitmapCache true Disk 或者 false 内存
     */
    fun prefetchImage(context: Context?, url: String, isDiskCacheOrBitmapCache: Boolean)

    /**
     * 获取view
     *
     * @param context 上下文
     * @return [ImageView]
     */
    fun getSimpleView(context: Context?, url: String): ImageView?

    /**
     * 清理内存
     */
    fun clearMemCache()

    /**
     * 清理缓存
     */
    fun clearCacheFiles()
}