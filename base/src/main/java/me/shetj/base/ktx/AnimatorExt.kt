package me.shetj.base.ktx

import android.view.View
import androidx.core.animation.LinearInterpolator
import androidx.core.animation.ObjectAnimator
import androidx.core.animation.ValueAnimator


fun View?.rotation(duration:Long): ObjectAnimator? {
   return this?.let {
        ObjectAnimator.ofFloat(this, "rotation", 0f, 359f).apply {
            repeatCount = ValueAnimator.INFINITE;
            this.duration = duration
            interpolator = LinearInterpolator();
        }
    }
}


fun getColorAnim(colorFrom :Int,colorTo:Int): ValueAnimator? {
    return ValueAnimator.ofArgb(colorFrom,colorTo)
}

