package me.shetj.base.base

import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseLifecycleKTAdapter<T, K : BaseViewHolder>
@JvmOverloads constructor(owner: LifecycleOwner,@LayoutRes layoutResId: Int,
                          data: MutableList<T>? = null)
    : BaseQuickAdapter<T, K>(layoutResId, data),LifecycleKtScopeComponent {

    override val lifeKtScope: LifecycleCoroutineScope by owner.defLifeOwnerScope()


    protected fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    protected fun getString(@StringRes id: Int, vararg formatArgs: Any?): String {
        return context.getString(id, formatArgs)
    }

    override fun convert(holder: K, item: T, payloads: List<Any>) {
        super.convert(holder, item, payloads)
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