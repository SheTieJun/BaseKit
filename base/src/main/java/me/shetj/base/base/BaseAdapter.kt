package me.shetj.base.base

import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.kt.getRequestOptions

abstract class BaseAdapter<T, K : BaseViewHolder>
@JvmOverloads constructor(@LayoutRes layoutResId: Int,
                          data: MutableList<T>? = null)
    : BaseQuickAdapter<T, K>(layoutResId, data) {

    private var requestOptions: RequestOptions? = null

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

    protected val defaultRequestOptions: RequestOptions
        get() {
            return requestOptions ?: getRequestOptions().apply {
                requestOptions = this
            }
        }

    protected fun getDimension(@DimenRes id: Int): Float {
        return context.resources.getDimension(id)
    }

    protected fun getDimensionPixelSize(@DimenRes id: Int): Int {
        return context.resources.getDimensionPixelSize(id)
    }

}