package me.shetj.base.tools.app

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import me.shetj.base.ktx.getWindowContent

object WindowKit {

    fun addView(activity: Activity, view: View, layoutParams: FrameLayout.LayoutParams) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        val windowContent = activity.getWindowContent()
        windowContent?.addView(view, layoutParams)
    }
}
