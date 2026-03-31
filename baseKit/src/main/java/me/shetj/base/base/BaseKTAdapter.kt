package me.shetj.base.base

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.chad.library.adapter4.viewholder.QuickViewHolder

abstract class BaseKTAdapter<T : Any>
@JvmOverloads constructor(
    owner: Lifecycle,
    @LayoutRes private val layoutResId: Int,
    items: List<T> = emptyList()
) : BaseSAdapter<T, QuickViewHolder>(items) {
    protected val lifeKtScope by lazy { owner.coroutineScope }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(layoutResId, parent)
    }
}
