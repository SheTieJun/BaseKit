package me.shetj.base.ktx

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.LinearInterpolator
import androidx.core.animation.ObjectAnimator
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

fun View?.rotation(duration: Long): ObjectAnimator? {
    return this?.let {
        ObjectAnimator.ofFloat(this, "rotation", 0f, 359f).apply {
            repeatCount = ValueAnimator.INFINITE
            this.duration = duration
            interpolator = LinearInterpolator()
        }
    }
}

fun getColorAnim(colorFrom: Int, colorTo: Int): ValueAnimator {
    return ValueAnimator.ofArgb(colorFrom, colorTo)
}

fun ValueAnimator?.resetDurationScale() {
    try {
        if (this != null ) {
            val setAnimationScale: Method =
                android.animation.ValueAnimator::class.java.getMethod(
                    "setDurationScale",
                    Float::class.javaPrimitiveType
                )
            setAnimationScale.invoke(this, 1)
        }
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}