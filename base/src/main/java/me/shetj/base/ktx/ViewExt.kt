package me.shetj.base.ktx

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import me.shetj.base.R
import me.shetj.base.constant.Constant
import me.shetj.base.tools.app.ArmsUtils

/* *收集一些扩展函数 * */

//region TextView
/**
 * 设置textView 的 Drawable
 */
fun TextView.setDrawables(@DrawableRes resId: Int,
                                  @Constant.GravityType gravity: Int = Gravity.TOP) {
    ContextCompat.getDrawable(context, resId)?.apply {
        setBounds(0, 0, minimumWidth, minimumHeight)
    }?.let {
        when (gravity) {
            Gravity.START -> setCompoundDrawables(it, null, null, null)
            Gravity.TOP -> setCompoundDrawables(null, it, null, null)
            Gravity.END -> setCompoundDrawables(null, null, it, null)
            Gravity.BOTTOM -> setCompoundDrawables(null, null, null, it)
        }
    }

}

/**
 * 设置文字显示缩进
 */
fun TextView.setTextAndMargin(content: String, marginStart: Float) {
    val spannableString = SpannableString(content)
    val what = LeadingMarginSpan.Standard(ArmsUtils.dip2px(marginStart), 0)
    spannableString.setSpan(what, 0, spannableString.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    text = spannableString
}

/**
 * 文字加粗
 */
internal fun TextView.testBold(isBold: Boolean) {
    paint.isFakeBoldText = isBold
}

fun TextView.setBold(isBold: Boolean) {
    typeface = if (isBold) {
        Typeface.defaultFromStyle(Typeface.BOLD)
    } else {
        Typeface.defaultFromStyle(Typeface.NORMAL)
    }
}
//endregion TextView


//region ViewGroup
@Suppress("UNCHECKED_CAST")
fun <R : View> ViewGroup.inflate(
        ctxt: Context = context,
        @LayoutRes res: Int
) = LayoutInflater.from(ctxt).inflate(res, this, false) as R

@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun <T : View> ViewGroup.inflate(
        @LayoutRes res: Int,
        root: ViewGroup? = this
) = LayoutInflater.from(context).inflate(res, root, false) as T

//endregion ViewGroup

//region SwipeRefreshLayout

@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun SwipeRefreshLayout.setSwipeRefresh(
        @ColorRes color: Int = R.color.colorAccent,
        listener: SwipeRefreshLayout.OnRefreshListener? = null) {
    this.setColorSchemeResources(color)
    this.setOnRefreshListener(listener)
}
//endregion SwipeRefreshLayout
//region view

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

inline fun <T : View> T?.waitForLayout(crossinline f: T.() -> Unit) =
        this?.viewTreeObserver.apply {
            this?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    removeOnGlobalLayoutListener(this)
                    this@waitForLayout?.f()
                }
            })
        }

fun <T : View> T.isRtl() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
//endregion 泛型


//region View
fun View?.disableClipOnParents() {
    if (this == null) {
        return
    }
    if (parent == null) {
        return
    }
    if (this is ViewGroup) {
        clipChildren = false
    }
    if (parent is View) {
        (parent as View).disableClipOnParents()
    }
}
/**
 * 点击动画
 */
fun View?.setClicksAnimate() {
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start()
            MotionEvent.ACTION_UP -> animate().scaleX(1f).scaleY(1f).setDuration(500).start()
        }
        false
    }
}

/**
 * @param ratioW 宽占比
 * @param ratioH 高占比
 * @param parentH 基准高
 * @param parentW 基准宽
 */
@UiThread
fun View?.layoutRatio(parentW: Int? = null, parentH: Int? = null, ratioW: Int, ratioH: Int) {
    this?.apply {
        if (parentH == null && parentW == null) {
            throw NullPointerException("parentW and parentH no all be null")
        }
        if (parentW != null && parentH == null) {
            layoutRationByWidth(parentW, ratioW, ratioH)
        } else if (parentW == null && parentH != null) {
            layoutRationByHeight(parentH, ratioH, ratioW)
        } else if (parentH != null && parentW != null) {
            if (ratioW / ratioH > parentW / parentH) {
                layoutRationByWidth(parentW, ratioW, ratioH)
            } else {
                layoutRationByHeight(parentH, ratioH, ratioW)
            }
        }
    }
}

@UiThread
fun View?.layoutRationByHeight(parentH: Int, ratioH: Int, ratioW: Int) {
    this?.apply {
        var layoutParams: ViewGroup.LayoutParams? = layoutParams
        val width = (parentH / ratioH * ratioW + 0.5f).toInt()
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(-1, height)
            this.layoutParams = layoutParams
        } else {
            if (layoutParams.width != width) {
                layoutParams.width = width
                this.layoutParams = layoutParams
            }
        }
    }
}

@UiThread
fun View?.layoutRationByWidth(parentW: Int, ratioW: Int, ratioH: Int) {
    this?.apply {
        val height = (parentW / ratioW * ratioH + 0.5f).toInt()
        var layoutParams: ViewGroup.LayoutParams? = layoutParams
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(-1, height)
            this.layoutParams = layoutParams
        } else {
            if (layoutParams.height != height) {
                layoutParams.height = height
                this.layoutParams = layoutParams
            }
        }
    }
}

@UiThread
fun View?.use16And9() {
    this?.apply {
        layoutRationByWidth(ArmsUtils.getScreenWidth(), 16, 9)
    }
}

@UiThread
fun View?.use16And9ByView(view: View) {
    this?.apply {
        layoutRatio(view.width, view.height, 16, 9)
    }
}

//endregion View

//region EditText

/**
 * edit 获取焦点打开键盘
 */
fun EditText?.requestFocusEdit() {
    this?.let {
        isEnabled = true
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setSelection(text.length)
        inputManager.showSoftInput(this, 0)
    }
}

//endregion EditText
//region AppBarLayout
/**
 * AppbarLayout "高度比较高", 高概率遇到AppbarLayout无法滑动的问题.
 */
fun AppBarLayout.canDrag() {
    post {
        val layoutParams = layoutParams as (CoordinatorLayout.LayoutParams)
        val behavior = layoutParams.behavior as (AppBarLayout.Behavior)
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(p0: AppBarLayout): Boolean {
                return true
            }
        })
    }
}
//endregion AppBarLayout

