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
package me.shetj.base.ktx

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BOTTOM
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.constraintlayout.widget.ConstraintSet.TOP

/**
 * 重写布局
 */
inline fun ConstraintLayout?.reLayout(crossinline onLayout: ConstraintSet.() -> Unit) {
    this?.let { constraintLayout ->
        toConstraintSet()?.let {
            onLayout.invoke(it)
            it.applyTo(constraintLayout)
        }
    }
}

fun ConstraintLayout?.toConstraintSet(): ConstraintSet? {
    return this?.let {
        ConstraintSet().also {
            it.clone(this)
        }
    }
}

fun Context.toConstraintSetById(layout: Int): ConstraintSet {
    return ConstraintSet().also {
        it.clone(this, layout)
    }
}

fun ConstraintSet?.topToParent(
    viewId: Int = 0,
    view: View? = null,
    margin: Int = 0
) {
    this?.topToTop(viewId, PARENT_ID, view, margin = margin)
}

fun ConstraintSet?.bottomToParent(
    viewId: Int = 0,
    view: View? = null,
    margin: Int = 0
) {
    this?.bottomToBottom(viewId, PARENT_ID, view, margin = margin)
}

fun ConstraintSet?.startToParent(
    viewId: Int = 0,
    view: View? = null,
    margin: Int = 0
) {
    this?.startToStart(viewId, PARENT_ID, view, margin = margin)
}

fun ConstraintSet?.endToParent(
    viewId: Int = 0,
    view: View? = null,
    margin: Int = 0
) {
    this?.endToEnd(viewId, PARENT_ID, view, margin = margin)
}

fun ConstraintSet?.topToTop(
    viewId: Int,
    targetId: Int,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        TOP,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        TOP,
        margin
    )
}

fun ConstraintSet?.topToBottom(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        TOP,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        BOTTOM,
        margin
    )
}

fun ConstraintSet?.bottomToBottom(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        BOTTOM,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        BOTTOM,
        margin
    )
}

fun ConstraintSet?.bottomToTOP(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        BOTTOM,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        TOP,
        margin
    )
}

fun ConstraintSet?.startToStart(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        START,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        START,
        margin
    )
}

fun ConstraintSet?.startToEnd(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        START,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        END,
        margin
    )
}

fun ConstraintSet?.endToEnd(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        END,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        END,
        margin
    )
}

fun ConstraintSet?.endToStart(
    viewId: Int = 0,
    targetId: Int = 0,
    view: View? = null,
    targetView: View? = null,
    margin: Int = 0
) {
    if (this == null || viewId == 0) {
        return
    }
    this.connect(
        if (viewId == 0) (view?.id ?: 0) else viewId,
        END,
        if (targetId == 0) (targetView?.id ?: 0) else targetId,
        START,
        margin
    )
}
