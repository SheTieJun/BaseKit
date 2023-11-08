package me.shetj.base.anim.motion

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewAnimationUtils
import androidx.constraintlayout.motion.widget.MotionHelper
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.hypot

class CircularRevealHelper
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    MotionHelper(context, attrs, defStyleAttr) {

    /**
     *     onLayout 之前
     */
    override fun updatePreLayout(container: ConstraintLayout?) {
        super.updatePreLayout(container)
    }

    // updatePostLayout会在 onLayout 之后调用，在这里做动画就可以。
    override fun updatePostLayout(container: ConstraintLayout?) {
        super.updatePostLayout(container)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val views = getViews(container)
            for (view in views) {
                val anim = ViewAnimationUtils.createCircularReveal(
                    view,
                    view.width / 2,
                    view.height / 2,
                    0f,
                    hypot((view.height / 2).toDouble(), (view.width / 2).toDouble()).toFloat()
                )
                anim.duration = 3000
                anim.start()
            }
        }
    }

    override fun setProgress(progress: Float) {
        super.setProgress(progress)
    }
}
