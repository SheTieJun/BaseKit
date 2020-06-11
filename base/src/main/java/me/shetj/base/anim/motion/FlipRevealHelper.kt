package me.shetj.base.anim.motion

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.motion.widget.MotionHelper
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 翻转
 */
class FlipRevealHelper @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionHelper(context, attrs, defStyleAttr) {


    override fun updatePostLayout(container: ConstraintLayout?) {
        super.updatePostLayout(container)
//        val views = getViews(container)
//        for (view in views) {
//            val animator = ObjectAnimator.ofFloat(view, "rotationY", 90f, 0f).setDuration(3000)
//            animator.start()
//        }
    }

    override fun setProgress(view: View?, progress: Float) {
        super.setProgress(view, progress)
        val animator = ObjectAnimator.ofFloat(view, "rotationY", 90f, 0f).setDuration(3000)
        animator.start()
    }
}