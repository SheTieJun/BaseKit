package me.shetj.base.base

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.ktx.getClazz

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/8/8<br>
 */
class BaseViewBindingHolder<BD : ViewDataBinding> (val view: View):BaseViewHolder(view){
    val dataBinding: BD by lazy {  initBinding() }

    private fun initBinding(): BD {
        return getClazz<BD>(this, 0).getMethod("bind", LayoutInflater::class.java)
            .invoke(null, view) as BD
    }
}