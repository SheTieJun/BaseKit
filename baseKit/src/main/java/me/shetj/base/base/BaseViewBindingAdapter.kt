package me.shetj.base.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter


/**
 * Base view binding adapter
 * 当只有ViewBinding的时候，可以使用这个
 */
abstract class BaseViewBindingAdapter<T, BD : ViewDataBinding>
@JvmOverloads constructor(
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, BaseViewBindingHolder<BD>>(layoutResId, data) {

    override fun convert(holder: BaseViewBindingHolder<BD>, item: T) {
        convert(holder.dataBinding, item)
    }

    override fun convert(holder: BaseViewBindingHolder<BD>, item: T, payloads: List<Any>) {
        convert(holder.dataBinding, item, payloads)
    }

    abstract fun convert(holder: BD, item: T)

    abstract fun convert(holder: BD, item: T, payloads: List<Any>)
}