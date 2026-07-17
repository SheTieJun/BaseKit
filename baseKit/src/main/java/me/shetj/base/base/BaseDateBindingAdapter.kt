package me.shetj.base.base

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import me.shetj.base.R

/**
 * Base date binding adapter
 * 有dataBinding的时候，可以使用这个
 */
@Keep
abstract class BaseDateBindingAdapter<T : Any, BD : ViewDataBinding>
@JvmOverloads constructor(
    @param:LayoutRes private val layoutResId: Int,
    items: List<T> = emptyList()
) : BaseQuickAdapter<T, BaseDataBindingHolder<BD>>(items) {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingHolder<BD> {
        return BaseDataBindingHolder(parent.getItemView(layoutResId))
    }


    override fun onBindViewHolder(holder: BaseDataBindingHolder<BD>, position: Int, item: T?) {
        holder.binding?.let { convert(it, position, item) }
    }


    override fun onBindViewHolder(holder: BaseDataBindingHolder<BD>, position: Int, item: T?, payloads: List<Any>) {
        holder.binding?.let { convert(it, position, item, payloads) }
    }

    abstract fun convert(holder: BD, position: Int, item: T?)


    abstract fun convert(holder: BD, position: Int, item: T?, payloads: List<Any>)
}
