package me.shetj.base.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.R
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import java.lang.reflect.ParameterizedType

/**
 * Base view binding adapter
 * 当只有ViewBinding的时候，可以使用这个,注意 ViewBinding 不能被混淆
 */
abstract class BaseViewBindingAdapter<T : Any, BD : ViewBinding> @JvmOverloads constructor(
    @param:LayoutRes private val layoutResId: Int,
    items: List<T> = emptyList()
) : BaseQuickAdapter<T, BaseViewBindingHolder<BD>>(items) {
    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewBindingHolder<BD> {
        return BaseViewBindingHolder( parent.getItemView(layoutResId))
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder<BD>, position: Int, item: T?) {
        convert(holder.binding, position, item)
    }


    override fun onBindViewHolder(holder: BaseViewBindingHolder<BD>, position: Int, item: T?, payloads: List<Any>) {
        convert(holder.binding, position, item, payloads)
    }

    abstract fun convert(holder: BD, position: Int, item: T?)


    abstract fun convert(holder: BD, position: Int, item: T?, payloads: List<Any>)
}
