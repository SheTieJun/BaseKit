package me.shetj.base.ktx

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator


fun View?.rotation(duration:Long):ObjectAnimator{
    return ObjectAnimator.ofFloat(this, "rotation", 0f, 359f).apply {
        repeatCount = ValueAnimator.INFINITE;
        this.duration = duration
        interpolator = LinearInterpolator();
    }
}