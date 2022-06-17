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
package me.shetj.base.ktx

import android.content.Context
import android.graphics.Outline
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.LeadingMarginSpan
import android.view.Gravity
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
fun TextView.setDrawables(
    @DrawableRes resId: Int,
    @Constant.GravityType gravity: Int = Gravity.TOP
) {
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
    setSelection(text.length)
}

/**
 * 隐藏密码文本
 */
fun EditText.hidePassword() {
    transformationMethod = PasswordTransformationMethod.getInstance()
    setSelection(text.length)
}


fun View?.setClicksAnimate() {
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_UP -> animate().scaleX(1.15f).scaleY(1.15f).setDuration(150)
                .withEndAction {
                    animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
                .start()
        }
        false
    }
}



fun View?.setClicksAnimate2() {
    var isDown: Boolean
    isDown = false
    var isDownAnimEnd = false
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN ->{
                isDown = true
                animate().scaleX(0.8f).scaleY(0.8f).setDuration(150)
                    .withStartAction {
                        isDownAnimEnd = false
                    }
                    .withEndAction {
                        isDownAnimEnd = true
                        if (isDown) return@withEndAction
                        animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                    }
                    .start()
            }
            MotionEvent.ACTION_UP ->{
                isDown = false
                if (isDownAnimEnd){
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
    this?.apply {
        layoutRationByWidth(ArmsUtils.getScreenWidth(), 16, 9)
    }
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
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                    0,
                    0,
                    view.width,
                    view.height,
                    radius
                )
            }
        }
        clipToOutline = true
    }
}
