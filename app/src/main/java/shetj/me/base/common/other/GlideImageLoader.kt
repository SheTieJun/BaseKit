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


package shetj.me.base.common.other

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import io.reactivex.rxjava3.schedulers.Schedulers
import me.shetj.base.tools.app.Utils.Companion.app
import shetj.me.base.utils.ImageLoader

/**
 * **@packageName：** me.shetj.base.base<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/10/29 0029<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**
 * 具体用法 {@https://muyangmin.github.io/glide-docs-cn/doc/transitions.html}<br></br>
 * 加载图片三步走，1,with; 2,load; 3 into;
 * 取消图片也是三步走，1,with; 2,load; 3 clear;
 */
class GlideImageLoader private constructor() : ImageLoader {

    companion object {

        @Volatile
        private var instance: GlideImageLoader? = null

        fun getInstance(): GlideImageLoader {
            return instance ?: synchronized(GlideImageLoader::class.java) {
                GlideImageLoader().also {
                    instance = it
                }
            }
        }
    }


    override fun load(simpleView: ImageView, url: String) {
        Glide.with(simpleView.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(simpleView)
    }

    override fun load(simpleView: ImageView, url: String, hasProgress: Boolean) {
            load(simpleView, url)
    }

    /**
     * 未写
     * @param mSimpleView 容器
     * @param url url地址
     */
    override fun loadProgressive(mSimpleView: ImageView, url: String) {
        Glide.with(mSimpleView.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mSimpleView)
    }

    override fun loadGif(simpleView: ImageView, url: String, isAuto: Boolean) {
        Glide.with(simpleView.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .downsample(DownsampleStrategy.DEFAULT)
                .into(simpleView)
    }

    override fun prefetchImage(context: Context, url: String, isDiskCacheOrBitmapCache: Boolean) {
        Glide.with(context)
                .load(url)
                .submit()
    }

    override fun getSimpleView(context: Context, url: String): ImageView {
        val imageView = ImageView(context)
        load(imageView, url)
        return imageView
    }

    override fun clearMemCache() {
        Glide.get(app.applicationContext).clearMemory()
    }

    override fun clearCacheFiles() {
        Schedulers.io().scheduleDirect {
            Glide.get(app.applicationContext).clearDiskCache()
        }
    }

    override fun displayImage(context: Context, url: String, view: ImageView) {}
    override fun displayUserImage(context: Context, url: String, view: ImageView) {}
    override fun preLoad(context: Context, url: String) {}
}