package me.shetj.base.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder


/**
 * Base date binding adapter
 * 有dataBinding的时候，可以使用这个
 */
abstract class BaseDateBindingAdapter <T,BD : ViewDataBinding>
@JvmOverloads constructor(
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, BaseDataBindingHolder<BD>>(layoutResId, data) {

    override fun convert(holder: BaseDataBindingHolder<BD>, item: T) {
        holder.dataBinding?.let { convert(it,item) }
    }

    override fun convert(holder: BaseDataBindingHolder<BD>, item: T, payloads: List<Any>) {
        holder.dataBinding?.let { convert(it,item,payloads) }
    }

    abstract fun convert(holder: BD, item: T)

    abstract fun convert(holder: BD, item: T,payloads: List<Any>)
}