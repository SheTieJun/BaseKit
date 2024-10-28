package shetj.me.base.utils.video

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.widget.FrameLayout

class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {
    init {
        setBackgroundColor(Color.BLACK)
    }

    /**
     * 设置自己处理触摸时间
     *
     * @param event event
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }
}
