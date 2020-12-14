package me.shetj.base.tip

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.shetj.base.R
import me.shetj.base.weight.AbLoadingDialog
import java.util.concurrent.TimeUnit

/**
 * 消息提示框
 */
class TipPopupWindow(private val mContext: AppCompatActivity) : PopupWindow(mContext), LifecycleObserver {
    private var tvTip: TextView? = null
    private val lazyComposite = lazy { CompositeDisposable() }
    private val mCompositeDisposable: CompositeDisposable by lazyComposite
    private var currentDuration = AbLoadingDialog.LOADING_SHORT

    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        animationStyle = R.style.tip_pop_anim_style
        isOutsideTouchable = false// 设置点击窗口外边窗口不消失
        isFocusable = false
        mContext.lifecycle.addObserver(this)
        initUI()
    }

    private fun initUI() {
        val rootView = View.inflate(mContext, R.layout.base_dialog_tip, null)
        tvTip = rootView.findViewById(R.id.tv_msg)
        contentView = rootView
        setOnDismissListener {
            if (lazyComposite.isInitialized()) {
                mCompositeDisposable.clear()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun tipDismissStop() {
        try {
            dismiss()
        } catch (ignored: Exception) {
            //暴力解决，可能的崩溃
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun tipDismiss() {
        try {
            dismiss()
        } catch (_: Exception) {
            //暴力解决，可能的崩溃
        }
    }

    /**
     * 展示
     * @param tip
     * @param tipMsg
     */
    fun showTip(tipMsg: CharSequence?, @AbLoadingDialog.LoadingTipsDuration duration: Long) {
        tvTip!!.text = tipMsg
        this.currentDuration = duration
        showAtLocation(mContext.window.decorView, Gravity.CENTER, 0, 0)
        mCompositeDisposable.add(AndroidSchedulers.mainThread().scheduleDirect({
            tipDismiss()
        }, duration, TimeUnit.MILLISECONDS))
    }

    companion object {
        @JvmOverloads
        fun showTip(context: Context, tipMsg: CharSequence?, @AbLoadingDialog.LoadingTipsDuration duration: Long = AbLoadingDialog.LOADING_SHORT) {
            TipPopupWindow(context as AppCompatActivity).showTip(tipMsg, duration)
        }
    }


}