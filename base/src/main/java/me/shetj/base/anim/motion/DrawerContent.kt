package me.shetj.base.anim.motion

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.drawerlayout.widget.DrawerLayout

class DrawerContent @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), DrawerLayout.DrawerListener {

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        progress = slideOffset  //对应motionLayout 的动画位置
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //1. 获取到父布局
        //2. 监听父布局的变化
        (parent as? DrawerLayout)?.addDrawerListener(this) //监听drawerLayout 的
    }
}