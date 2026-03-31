package me.shetj.base.base

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.SelectionTracker
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

abstract class BaseSAdapter<T : Any, VH : QuickViewHolder>(
    items: List<T> = emptyList()
) : BaseQuickAdapter<T, VH>(items) {

    open val isMulti: Boolean = false
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

    protected fun getString(context: Context, @StringRes resId: Int): String {
        return context.getString(resId)
    }

    protected fun getString(context: Context, @StringRes id: Int, vararg formatArgs: Any?): String {
        return context.getString(id, *formatArgs)
    }

    @ColorInt
    protected fun getColor(context: Context, @ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    protected fun getDrawable(context: Context, @DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    protected fun getDimension(context: Context, @DimenRes id: Int): Float {
        return context.resources.getDimension(id)
    }

    protected fun getDimensionPixelSize(context: Context, @DimenRes id: Int): Int {
        return context.resources.getDimensionPixelSize(id)
    }
}
