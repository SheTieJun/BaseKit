package me.shetj.base.tools.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite

import me.shetj.base.R

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
     * 空界面，加载中
     * @param context 上下文
     * @param drawable 图片
     */
    @JvmStatic
    fun getLoadingView(context: Activity, drawable: Drawable): View {
        val emptyView = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        val spinKitView = emptyView.findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.setIndeterminateDrawable(drawable)
        return emptyView
    }

    /**
     *
     * 空界面，加载中
     * @param context 上下文
     * @param sprite 图片
     */
    @JvmStatic
    fun getLoadingView(context: Activity, sprite: Sprite): View {
        val emptyView = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        val spinKitView = emptyView.findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.setIndeterminateDrawable(sprite)
        return emptyView
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
