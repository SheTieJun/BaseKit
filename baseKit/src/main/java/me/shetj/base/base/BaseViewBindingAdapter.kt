package me.shetj.base.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.lang.reflect.ParameterizedType

/**
 * Base view binding adapter
 * 当只有ViewBinding的时候，可以使用这个,注意 ViewBinding 不能被混淆
 */
abstract class BaseViewBindingAdapter<T, BD : ViewBinding>
@JvmOverloads constructor(
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data) {

    private val clazz by lazy { (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*> }
    private val method by lazy { clazz.getMethod("bind", View::class.java) }

    @Suppress("UNCHECKED_CAST")
    private fun getBinding(view: View): BD {
        return method.invoke(null, view) as BD
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        convert(getBinding(holder.itemView), item)
    }

    override fun convert(holder: BaseViewHolder, item: T, payloads: List<Any>) {
        convert(getBinding(holder.itemView), item, payloads)
    }

    abstract fun convert(holder: BD, item: T)

    open fun convert(holder: BD, item: T, payloads: List<Any>) {}
}
