/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.base

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseKTAdapter<T, K : BaseViewHolder>
@JvmOverloads constructor(
    owner: LifecycleOwner,
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, K>(layoutResId, data), LifecycleKtScopeComponent {

    override val lifeKtScope: LifecycleCoroutineScope by owner.defLifeOwnerScope()

    protected fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    protected fun getString(@StringRes id: Int, vararg formatArgs: Any?): String {
        return context.getString(id, formatArgs)
    }

    @ColorInt
    protected fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    protected fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    protected fun getDimension(@DimenRes id: Int): Float {
        return context.resources.getDimension(id)
    }

    protected fun getDimensionPixelSize(@DimenRes id: Int): Int {
        return context.resources.getDimensionPixelSize(id)
    }
}
