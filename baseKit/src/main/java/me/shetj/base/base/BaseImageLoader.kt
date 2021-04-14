package me.shetj.base.base

import android.content.Context
import android.widget.ImageView

/**
 * **@packageName：** me.shetj.base.base<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/9/4 0004<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe** 图片加载<br></br>
 */
interface BaseImageLoader {
    /**
     * 加载普通的图片
     * @param context
     * @param url
     * @param view
     */
    fun displayImage(context: Context, url: String, view: ImageView)

    /**
     * 加载用户头像
     * @param context
     * @param url
     * @param view
     */
    fun displayUserImage(context: Context, url: String, view: ImageView)

    /**
     * 预加载
     * @param context
     * @param url
     */
    fun preLoad(context: Context, url: String)

}
