package me.shetj.base.base

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import me.shetj.base.ktx.createSelectTracker
import java.util.UUID

abstract class BaseSAdapter<T, K : TackerBaseViewHolder>
@JvmOverloads constructor(
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, K>(layoutResId, data) {

    //是否是多选
    open val isMulti = false

    protected var mSelectTracker: SelectionTracker<Long?>? = null


    fun getSelectTracker() = mSelectTracker

    /**
     * 设置 SelectionTracker
     */
    fun setSelectionTracker(tracker: SelectionTracker<Long?>) {
        this.mSelectTracker = tracker
    }

    /**
     * 清除所有选择
     */
    fun clearSelection() {
        getSelectTracker()?.clearSelection()
    }

    protected fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    protected fun getString(@StringRes id: Int, vararg formatArgs: Any?): String {
        return context.getString(id, formatArgs)
    }

    @ColorInt
    protected fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    protected fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    protected fun getDimension(@DimenRes id: Int): Float {
        return context.resources.getDimension(id)
    }

    protected fun getDimensionPixelSize(@DimenRes id: Int): Int {
        return context.resources.getDimensionPixelSize(id)
    }
}
