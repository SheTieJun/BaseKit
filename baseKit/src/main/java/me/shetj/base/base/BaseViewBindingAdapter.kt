package me.shetj.base.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import java.lang.reflect.ParameterizedType

/**
 * Base view binding adapter
 * 当只有ViewBinding的时候，可以使用这个,注意 ViewBinding 不能被混淆
 */
abstract class BaseViewBindingAdapter<T : Any, BD : ViewBinding>
@JvmOverloads constructor(
    @LayoutRes private val layoutResId: Int,
    items: List<T> = emptyList()
) : BaseQuickAdapter<T, QuickViewHolder>(items) {

    private val clazz by lazy { (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*> }
    private val method by lazy { clazz.getMethod("bind", View::class.java) }

    @Suppress("UNCHECKED_CAST")
    private fun getBinding(view: View): BD {
        return method.invoke(null, view) as BD
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(layoutResId, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: T?) {
        if (item != null) {
            convert(getBinding(holder.itemView), item)
        }
    }

    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: T?,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, item, payloads)
        } else if (item != null) {
            convert(getBinding(holder.itemView), item, payloads)
        }
    }

    abstract fun convert(holder: BD, item: T)

    open fun convert(holder: BD, item: T, payloads: List<Any>) {}
}
