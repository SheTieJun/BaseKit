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
