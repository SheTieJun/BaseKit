package me.shetj.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView


import java.util.concurrent.TimeUnit

import androidx.annotation.ColorInt
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import me.shetj.base.R

/**
 * 消息提示框
 */
class TipPopupWindow(private val context: Context) : PopupWindow(context) {
    private var tvTip: TextView? = null
    private var publishSubject: PublishSubject<TipPopupWindow>? = null

    enum class Tip {
        DEFAULT,
        INFO,
        ERROR,
        SUCCESS,
        WARNING
    }

    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        animationStyle = R.style.tip_pop_anim_style
        // 设置点击窗口外边窗口消失
        isOutsideTouchable = false
        isFocusable = false
        // 加载布局
        initUI()
    }

    private fun initUI() {
        val rootView = View.inflate(context, R.layout.base_popupwindow_tip, null)
        tvTip = rootView.findViewById(R.id.tv_tip)
        contentView = rootView

        publishSubject = PublishSubject.create()

        publishSubject!!
                .debounce(1000, TimeUnit.MILLISECONDS)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tipPopupWindow ->
                    tipPopupWindow?.dismiss()
                }, { tipDismiss() })
    }


    /**
     * 展示
     * @param tip
     * @param tipMsg
     */
    fun showTip(tip: Tip, view: View, tipMsg: String) {
        //设置背景
        when (tip) {
            Tip.INFO -> tvTip!!.background = ColorDrawable(INFO_COLOR)
            Tip.ERROR -> tvTip!!.background = ColorDrawable(ERROR_COLOR)
            Tip.DEFAULT -> tvTip!!.background = ColorDrawable(NORMAL_COLOR)
            Tip.SUCCESS -> tvTip!!.background = ColorDrawable(SUCCESS_COLOR)
            Tip.WARNING -> tvTip!!.background = ColorDrawable(WARNING_COLOR)
        }
        //设置文子
        tvTip!!.text = tipMsg
        showAsDropDown(view)
        publishSubject!!.onNext(this)
    }

    companion object {
        @ColorInt
        private val ERROR_COLOR = Color.parseColor("#ff0000")
        @ColorInt
        private val INFO_COLOR = Color.parseColor("#1CD67C")
        @ColorInt
        private val SUCCESS_COLOR = Color.parseColor("#FFFF5A31")
        @ColorInt
        private val WARNING_COLOR = Color.parseColor("#FFBB22")
        @ColorInt
        private val NORMAL_COLOR = Color.parseColor("#CCCCCC")

        @SuppressLint("StaticFieldLeak")
        private var tipPopupWindow: TipPopupWindow? = null


        /**
         * 展示信息
         */
        fun showTipMsg(context: Context, tip: Tip, view: View, tipMsg: String) {
            if (tipPopupWindow != null && tipPopupWindow!!.isShowing) {
                tipPopupWindow!!.showTip(tip, view, tipMsg)
            } else {
                tipPopupWindow = TipPopupWindow(context)
                tipPopupWindow!!.showTip(tip, view, tipMsg)
            }
        }

        /**
         * 在展示的[android.app.Activity.onDestroy] 中调用
         */
        fun tipDismiss() {
            if (tipPopupWindow != null) {
                tipPopupWindow!!.dismiss()
                tipPopupWindow = null
            }
        }
    }
}
