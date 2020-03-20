package me.shetj.base.kt

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.Slide
import com.google.android.material.appbar.AppBarLayout
import me.shetj.base.R
import me.shetj.base.constant.Constant
import me.shetj.base.tools.app.ArmsUtils

/* *收集一些扩展函数 * */

//region TextView
/**
 * 设置textView 的 Drawable
 */
fun TextView.setCompoundDrawables(@DrawableRes resId: Int,
                                  @Constant.GravityType gravity : Int = Gravity.TOP){
    ContextCompat.getDrawable(context, resId)?.apply {
        setBounds(0, 0, minimumWidth, minimumHeight)
    }?.let {
        when(gravity){
            Gravity.START  ->  setCompoundDrawables(it, null, null, null)
            Gravity.TOP ->     setCompoundDrawables(null, it, null, null)
            Gravity.END ->     setCompoundDrawables(null, null, it, null)
            Gravity.BOTTOM ->  setCompoundDrawables(null, null , null, it)
        }
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

/**
 * 文字加粗
 */
internal fun TextView.testBold(isBold: Boolean){
    paint.isFakeBoldText = isBold
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
fun <T:View> ViewGroup.inflate(
        @LayoutRes res: Int,
        root: ViewGroup? = this
) = LayoutInflater.from(context).inflate(res, root, false) as T

//endregion ViewGroup

//region SwipeRefreshLayout

@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun SwipeRefreshLayout.setSwipeRefresh(
        @ColorRes color :Int = R.color.colorAccent,
        listener : SwipeRefreshLayout.OnRefreshListener ?= null ){
    this.setColorSchemeResources(color)
    this.setOnRefreshListener(listener)
}
//endregion SwipeRefreshLayout
//region 泛型

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

fun <T : View> T.isNotVisible() = !isVisible()

fun <T : View> T.isRtl()= resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
//endregion 泛型


//region View
/**
 * 点击动画
 */
fun View?.setClicksAnima(){
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start()
            MotionEvent.ACTION_UP -> animate().scaleX(1f).scaleY(1f).setDuration(500).start()
        }
        false
    }
}

//endregion View

//region EditText

/**
 * edit 获取焦点打开键盘
 */
fun EditText?.requestFocusEdit() {
    this?.let{
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
fun AppBarLayout.canDrag(){
    post {
        val layoutParams =  layoutParams as  (CoordinatorLayout.LayoutParams)
        val behavior =layoutParams.behavior as  (AppBarLayout.Behavior)
        behavior.setDragCallback(object :AppBarLayout.Behavior.DragCallback(){
            override fun canDrag(p0: AppBarLayout): Boolean {
                return true
            }
        })
    }
}
//endregion AppBarLayout

