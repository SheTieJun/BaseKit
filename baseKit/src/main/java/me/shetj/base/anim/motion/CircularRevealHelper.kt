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


package me.shetj.base.anim.motion

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import androidx.constraintlayout.motion.widget.MotionHelper
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.hypot


/**
 * 测量，布局，绘制
 * 在XML 中使用，可以设置多个view,可以通过id 执行不同的操作，或者tag
 */
class CircularRevealHelper
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        MotionHelper(context, attrs, defStyleAttr) {

    //updatePostLayout会在 onLayout 之后调用，在这里做动画就可以。
//    override fun updatePostLayout(container: ConstraintLayout?) {
//        super.updatePostLayout(container)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val views = getViews(container)
//            for (view in views) {
//                val anim = ViewAnimationUtils.createCircularReveal(view, view.width / 2,
//                        view.height / 2, 0f,
//                        hypot((view.height / 2).toDouble(), (view.width / 2).toDouble()).toFloat())
//                anim.duration = 3000
//                anim.start()
//            }
//        }
//    }

    //onLayout 之前
    override fun updatePreLayout(container: ConstraintLayout?) {
        super.updatePreLayout(container)
    }

    override fun setProgress(view: View?, progress: Float) {
        super.setProgress(view, progress)
        view?.let {
            val anim = ViewAnimationUtils.createCircularReveal(view, view.width / 2,
                    view.height / 2, 0f,
                    hypot((view.height / 2).toDouble(), (view.width / 2).toDouble()).toFloat())
            anim.duration = 3000
            anim.start()
        }
    }
}