package me.shetj.base.base

import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseKTAdapter<T, K : BaseViewHolder>
@JvmOverloads constructor(
    owner: Lifecycle,
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null
) : BaseSAdapter<T, K>(layoutResId, data) {
    protected val lifeKtScope by lazy { owner.coroutineScope }
}
