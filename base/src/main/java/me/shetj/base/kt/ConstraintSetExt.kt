
import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*


/**
 * 重写布局
 */
inline fun ConstraintLayout?.reLayout(crossinline onLayout :ConstraintSet.() -> Unit){
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
        it.clone(this,layout)
    }
}

fun ConstraintSet?.topToParent(
        viewId: Int = 0,
        view: View? = null,
        margin: Int = 0
) {
    this?.topToTop(viewId, PARENT_ID, view,margin = margin)
}

fun ConstraintSet?.bottomToParent(
        viewId: Int = 0,
        view: View? = null,
        margin: Int = 0
) {
    this?.bottomToBottom(viewId, PARENT_ID, view,margin = margin)
}

fun ConstraintSet?.startToParent(
        viewId: Int = 0,
        view: View? = null,
        margin: Int = 0
) {
    this?.startToStart(viewId, PARENT_ID, view,margin = margin)
}

fun ConstraintSet?.endToParent(
        viewId: Int = 0,
        view: View? = null,
        margin: Int = 0
) {
    this?.endToEnd(viewId, PARENT_ID, view,margin = margin)
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
        targetView: View? = null
        ,
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
        targetView: View? = null
        ,
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