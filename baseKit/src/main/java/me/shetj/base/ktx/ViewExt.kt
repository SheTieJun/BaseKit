package me.shetj.base.ktx

import android.content.Context
import android.graphics.Outline
import android.graphics.Typeface
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.LeadingMarginSpan
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.shetj.base.R
import me.shetj.base.constant.Constant
import me.shetj.base.tools.app.ArmsUtils

/* *收集一些扩展函数 * */

/**
 * BottomNavigationView 去掉toast
 */
fun BottomNavigationView.clearToast(ids: MutableList<Int>) {
    val bottomNavigationMenuView = getChildAt(0) as ViewGroup
    for (i in 0 until ids.size) {
        bottomNavigationMenuView.getChildAt(i).findViewById<View>(ids[i])?.setOnLongClickListener {
            return@setOnLongClickListener true
        }
    }
}

//region TextView
/**
 * 设置textView 的 Drawable
 */
fun TextView.setDrawables(@DrawableRes resId: Int, @Constant.GravityType gravity: Int = Gravity.TOP) {
    ContextCompat.getDrawable(context, resId)?.apply {
        setBounds(0, 0, minimumWidth, minimumHeight)
    }?.let {
        when (gravity) {
            Gravity.START -> setCompoundDrawablesRelative(it, null, null, null)

            Gravity.TOP -> setCompoundDrawablesRelative(null, it, null, null)

            Gravity.END -> setCompoundDrawablesRelative(null, null, it, null)

            Gravity.BOTTOM -> setCompoundDrawablesRelative(null, null, null, it)
        }
    }
}

fun TextView.clearDrawables() {
    setCompoundDrawablesRelative(null, null, null, null)
}

/**
 * Perform haptic feedback
 * 触摸反馈
 */
fun View.performHapticFeedback() {
    // HapticFeedbackConstants.CLOCK_TICK 是一个触觉反馈常量，用于指定时钟滴答的触觉反馈效果。它用于模拟时钟滴答的感觉，通常用于时钟应用程序或需要模拟时钟滴答的场景。
    // HapticFeedbackConstants.VIRTUAL_KEY 虚拟按键反馈效果。通常用于模拟按下虚拟按键时的触觉反馈。
    val feedbackKey = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) HapticFeedbackConstants.CLOCK_TICK else HapticFeedbackConstants.VIRTUAL_KEY
    this.performHapticFeedback(feedbackKey)
}

/**
 * 设置文字显示缩进
 */
fun TextView.setTextAndMargin(content: String, marginStart: Float) {
    val spannableString = SpannableString(content)
    val what = LeadingMarginSpan.Standard(ArmsUtils.dp2px(marginStart), 0)
    spannableString.setSpan(
        what,
        0,
        spannableString.length,
        SpannableString.SPAN_INCLUSIVE_INCLUSIVE
    )
    text = spannableString
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

@JvmOverloads
fun SwipeRefreshLayout.setSwipeRefresh(
    @ColorRes color: Int = R.color.colorAccent,
    listener: SwipeRefreshLayout.OnRefreshListener? = null,
    offset: Int = 0
) {
    this.setProgressViewOffset(false, offset, progressViewEndOffset + offset)
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
    if (this != null && left == this.paddingLeft && top == this.paddingTop && right == this.paddingRight && bottom == this.paddingBottom) {
        return
    }
    this?.setPadding(left, top, right, bottom)
}

inline fun <T : View> T?.waitForLayout(crossinline f: T.() -> Unit) = this?.viewTreeObserver.apply {
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
/**
 * 不切割子view
 */
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
 * 显示密码文本
 */
fun EditText.showPassword() {
    transformationMethod = HideReturnsTransformationMethod.getInstance()
}

/**
 * 隐藏密码文本
 */
fun EditText.hidePassword() {
    transformationMethod = PasswordTransformationMethod.getInstance()
}

fun View?.setClicksAnimate() {
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_UP -> animate().scaleX(1.15f).scaleY(1.15f).setDuration(150).withEndAction {
                animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            }.start()
        }
        false
    }
}

fun View?.setClicksAnimate2() {
    var isDown: Boolean
    var isDownAnimEnd = false
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDown = true
                animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).withStartAction {
                    isDownAnimEnd = false
                }.withEndAction {
                    isDownAnimEnd = true
                    if (isDown) return@withEndAction
                    animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }.start()
            }

            MotionEvent.ACTION_UP -> {
                isDown = false
                if (isDownAnimEnd) {
                    animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
            }
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
    this?.layoutRationByWidth(ArmsUtils.getScreenWidth(), 16, 9)
}

@UiThread
fun View?.use16And9ByView() {
    this?.apply {
        layoutRatio(width, height, 16, 9)
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

/**
 * 给view 添加圆角
 */
fun View.clipRound(radius: Float = ArmsUtils.dp2px(10f).toFloat()) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    0, 0, view.width, view.height, radius
                )
            }
        }
        clipToOutline = true
    }
}
