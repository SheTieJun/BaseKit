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


package shetj.me.base.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import shetj.me.base.R


/**
 * **@packageName：** shetj.me.base.utils<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/10/29 0029<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe** 为空时，展示的界面<br></br>
 */
@SuppressLint("InflateParams")
object EmptyViewUtils {

    /**
     * 空界面，加载中
     * @param context 上下文
     */
    @JvmStatic
    fun getLoadingView(context: Activity): View {
        return LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
    }


    /**
     * 空界面。空数据
     */
    @JvmStatic
    fun getEmptyView(context: Activity): View {
        val emptyView = LayoutInflater.from(context).inflate(R.layout.base_empty_date_view, null)
        val emptyTextView = emptyView.findViewById<TextView>(R.id.tv_msg_foot)
        emptyTextView.text = ""
        return emptyView
    }

    @JvmStatic
    fun getEmptyView(context: Activity, msg: String, @DrawableRes image: Int): View {
        val emptyView = LayoutInflater.from(context).inflate(R.layout.base_empty_date_view, null)
        val emptyTextView = emptyView.findViewById<TextView>(R.id.tv_msg_foot)
        emptyTextView.text = msg
        val emptyImage = emptyView.findViewById<ImageView>(R.id.iv_empty_view)
        emptyImage.setImageResource(image)
        return emptyView
    }

    @JvmStatic
    fun getEmptyView(context: Activity, msg: String): View {
        val emptyView = LayoutInflater.from(context).inflate(R.layout.base_empty_date_view, null)
        val emptyTextView = emptyView.findViewById<TextView>(R.id.tv_msg_foot)
        emptyTextView.text = msg
        return emptyView
    }
}
