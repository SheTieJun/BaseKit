package me.shetj.base.weight

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import me.shetj.base.base.DefCoroutineScope
import me.shetj.base.base.KtScopeComponent
import me.shetj.base.base.defScope
import me.shetj.base.ktx.getClazz

/**
 * popup 弹窗
 * 设置点击窗口外边窗口不消失，activity 可以被点击
 * {
 *   isOutsideTouchable = false
 *   isFocusable = false
 * }
 */
abstract class AbPopupWindow<VB : ViewBinding>(private val mContext: AppCompatActivity) :
    PopupWindow(mContext), LifecycleEventObserver, KtScopeComponent {

    private val lazyViewBinding = lazy { initViewBinding() }
    protected val mViewBinding: VB by lazyViewBinding

    override val ktScope: DefCoroutineScope by defScope()

    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = false
        isFocusable = false
        contentView = mViewBinding.root
        mViewBinding.initUI()
        initData()
    }

    private fun initData() {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mContext.lifecycle.addObserver(this)
        ktScope.register(mContext.lifecycle)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewBinding(): VB {
        return getClazz<VB>(this, 0).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, mContext.layoutInflater) as VB
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        if (event == Event.ON_STOP) {
            dismissStop()
        } else if (source.lifecycle.currentState <= Lifecycle.State.DESTROYED) {
            dismissOnDestroy()
        }
    }

    abstract fun VB.initUI()

    /**
     * 可以自行修改展示位置
     */
    open fun showPop() {
        showAtLocation(mContext.window.decorView, Gravity.BOTTOM, 0, 0)
    }

    open fun dismissStop() {
        try {
            dismiss()
        } catch (ignored: Exception) {
            // 暴力解决，可能的崩溃
        }
    }

    open fun dismissOnDestroy() {
        try {
            dismiss()
        } catch (_: Exception) {
            // 暴力解决，可能的崩溃
        }
    }
}
