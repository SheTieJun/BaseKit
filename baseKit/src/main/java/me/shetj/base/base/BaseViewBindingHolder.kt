package me.shetj.base.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.viewholder.QuickViewHolder
import me.shetj.base.R
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * 持有 ViewBinding 的 ViewHolder。
 *
 * 构造时通过反射调用 [VB].bind(view) 一次；必须写具体子类并声明 VB，例如：
 * `class XxxHolder(view: View) : BaseViewBindingHolder<ItemXxxBinding>(view)`
 */
open class BaseViewBindingHolder<VB : ViewBinding>(view: View) : RecyclerView.ViewHolder(view) {

    private val clazzVB = (javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[0] as Class<*>

    private val bindMethod: Method by lazy {
        clazzVB.getMethod("bind", View::class.java)
    }
    val binding = bindMethod.invoke(null, view) as VB
}


open class BaseDataBindingHolder<BD : ViewDataBinding>(view: View) : RecyclerView.ViewHolder(view) {
    val binding = DataBindingUtil.bind<BD>(view)
}


fun ViewGroup.getItemView(@LayoutRes layoutResId: Int): View {
    return LayoutInflater.from(this.context).inflate(layoutResId, this, false)
}


/**
 * 从  获取并缓存 [ViewBinding]。
 * 适合等多类型场景：在 `convert` 中按 `itemType` 调用对应的 VB。
 */
inline fun <reified VB : ViewBinding> QuickViewHolder.getViewBinding(): VB {
    val key = R.id.tag_view_binding
    (itemView.getTag(key) as? VB)?.let { return it }
    val binding = VB::class.java
        .getMethod("bind", View::class.java)
        .invoke(null, itemView) as VB
    itemView.setTag(key, binding)
    return binding
}