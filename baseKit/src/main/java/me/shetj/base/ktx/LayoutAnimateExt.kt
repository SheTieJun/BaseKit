package me.shetj.base.ktx

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionManager

/**
 * [LayoutTransition]
 *
 * 1.[LayoutTransition.CHANGING] 由于非添加或删除子 view 而发生的布局变化
 *
 * 2.[LayoutTransition.APPEARING] 子View添加到容器中时的过渡动画效果
 *
 * 3.[LayoutTransition.DISAPPEARING] 子View从容器中移除时,其它子view位置改变的过渡动画
 *
 * 4.[LayoutTransition.CHANGE_APPEARING] 变化出现
 *
 * 5.[LayoutTransition.CHANGE_DISAPPEARING] 子View从容器中移除时的过渡动画效果
 *
 *   ObjectAnimator addAnimator = ObjectAnimator.ofFloat(null, "rotationY", 0, 90,0)
 *   .setDuration(mTransitioner.getDuration(LayoutTransition.APPEARING));
 *
 *   mTransitioner.setAnimator(LayoutTransition.APPEARING, addAnimator);
 *
 *
 *  注意这个地方存在一个 "BUG"
 *
 *   LayoutChangeAnim 如果隐藏再显示就会出现无法进行点击事件的BUG
 *
 */
inline fun ViewGroup?.addLayoutChangeAnim(crossinline updateAnimator: LayoutTransition.() -> Unit) {
    this?.layoutTransition?.apply {
        updateAnimator.invoke(this)
        setAnimateParentHierarchy(true)
    }
}

/**
 * 添加开始和结束监听
 */
fun LayoutTransition?.addTransitionListener(startTransition: (transition: LayoutTransition?, container: ViewGroup?,
                                                    view: View?, transitionType: Int) -> Unit = { _: LayoutTransition?, _: ViewGroup?, _: View?, _: Int -> },
                                            endTransition: (transition: LayoutTransition?, container: ViewGroup?,
                                                  view: View?, transitionType: Int) -> Unit = { _: LayoutTransition?, _: ViewGroup?, _: View?, _: Int -> }) {
    this?.addTransitionListener(object : LayoutTransition.TransitionListener {
        override fun startTransition(transition: LayoutTransition?, container: ViewGroup?, view: View?, transitionType: Int) {
            startTransition.invoke(transition, container, view, transitionType)
        }

        override fun endTransition(transition: LayoutTransition?, container: ViewGroup?, view: View?, transitionType: Int) {
            endTransition.invoke(transition, container, view, transitionType)
        }
    })
}

/**
 * 更新子view的是带上transition动画
 */
inline fun ViewGroup?.updateLayoutWithTransition(transition: Transition?=null,crossinline update:(ViewGroup) ->Unit){
    this?.apply {
        TransitionManager.beginDelayedTransition(this,transition)
        update.invoke(this)
    }
}