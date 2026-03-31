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
    @LayoutRes private val layoutResId: Int,
    items: List<T> = emptyList()
) : BaseQuickAdapter<T, QuickViewHolder>(items) {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        val binding: BD = DataBindingUtil.inflate(
            android.view.LayoutInflater.from(context),
            layoutResId,
            parent,
            false
        )
        return QuickViewHolder(binding.root).apply {
            itemView.setTag(me.shetj.base.R.id.tag_view_binding, binding)
        }
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: T?) {
        if (item != null) {
            val binding = holder.itemView.getTag(R.id.tag_view_binding) as? BD
            if (binding != null) {
                convert(binding, item)
                binding.executePendingBindings()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: T?,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, item, payloads)
        } else if (item != null) {
            val binding = holder.itemView.getTag(me.shetj.base.R.id.tag_view_binding) as? BD
            if (binding != null) {
                convert(binding, item, payloads)
                binding.executePendingBindings()
            }
        }
    }

    abstract fun convert(holder: BD, item: T)

    open fun convert(holder: BD, item: T, payloads: List<Any>) {}
}
