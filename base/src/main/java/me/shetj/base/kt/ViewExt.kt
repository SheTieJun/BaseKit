package me.shetj.base.kt

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.shetj.base.R
import me.shetj.base.tools.app.ArmsUtils

/**
 * 收集一些扩展函数
 */

@Suppress("UNCHECKED_CAST")
fun <R : View> ViewGroup.inflate(
        ctxt: Context = context,
        @LayoutRes res: Int
) = LayoutInflater.from(ctxt).inflate(res, this, false) as R


@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun SwipeRefreshLayout.setSwipeRefresh(
        @ColorRes color :Int = R.color.colorAccent,
        listener : SwipeRefreshLayout.OnRefreshListener ?= null ){
    this.setColorSchemeResources(color)
    this.setOnRefreshListener(listener)
}


@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun <T> ViewGroup.inflate(
        @LayoutRes res: Int,
        root: ViewGroup? = this
) = LayoutInflater.from(context).inflate(res, root, false) as T



@JvmOverloads
fun <T : View> T?.updatePadding(
        left: Int = this?.paddingLeft ?: 0,
        top: Int = this?.paddingTop ?: 0,
        right: Int = this?.paddingRight ?: 0,
        bottom: Int = this?.paddingBottom ?: 0
) {
    if (this != null &&
            left == this.paddingLeft &&
            top == this.paddingTop &&
            right == this.paddingRight &&
            bottom == this.paddingBottom
    ) {
        return
    }
    this?.setPadding(left, top, right, bottom)
}

inline fun <T : View> T.waitForLayout(crossinline f: T.() -> Unit) =
        viewTreeObserver.apply {
            addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    removeOnGlobalLayoutListener(this)
                    this@waitForLayout.f()
                }
            })
        }!!

fun <T : View> T.isVisible(): Boolean {
    return if (this is Button) {
        this.visibility == View.VISIBLE && this.text.trim().isNotBlank()
    } else {
        this.visibility == View.VISIBLE
    }
}

fun <T : View> T.isNotVisible(): Boolean {
    return !isVisible()
}

fun <T : View> T.isRtl(): Boolean {
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun TextView.setGravityStartCompat() {
    this.gravity = Gravity.START or Gravity.CENTER_VERTICAL
}

fun TextView.setGravityEndCompat() {
    this.gravity = Gravity.END or Gravity.CENTER_VERTICAL
}
/**
 * 点击动画
 */
fun View.setClicksAnima( ){
    setOnTouchListener { _, event->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start()
            MotionEvent.ACTION_UP -> animate().scaleX(1f).scaleY(1f).setDuration(500).start()
        }
        false
    }
}

/**
 * 设置文字显示缩进
 */
fun TextView.setTextAndMargin(content: String,marginStart:Float){
    val spannableString = SpannableString(content)
    val what = LeadingMarginSpan.Standard(ArmsUtils.dip2px(marginStart), 0)
    spannableString.setSpan(what, 0, spannableString.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    text = spannableString
}

//TODO TextView 设置图片