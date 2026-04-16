package me.shetj.base.tools.debug

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import me.shetj.base.ktx.dp2px

/**
 * 调试悬浮窗 View
 */
@SuppressLint("ViewConstructor")
class DebugFloatView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val tvClose: TextView
    private val tvVersion: TextView
    private val tvActivity: TextView
    private val tvCustomInfo: TextView

    private var downX = 0f
    private var downY = 0f
    private var lastX = 0f
    private var lastY = 0f
    
    var onDragListener: ((x: Float, y: Float) -> Unit)? = null
    var onCloseListener: (() -> Unit)? = null

    init {
        setBackgroundColor(Color.parseColor("#80000000")) // 半透明黑色背景
        setPadding(10.dp2px, 10.dp2px, 10.dp2px, 10.dp2px)
        
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        tvClose = TextView(context).apply {
            text = "×"
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(6.dp2px, 0, 6.dp2px, 0)
            setOnClickListener { onCloseListener?.invoke() }
        }

        tvVersion = createTextView()
        tvActivity = createTextView()
        tvCustomInfo = createTextView()

        layout.addView(tvVersion)
        layout.addView(tvActivity)
        layout.addView(tvCustomInfo)

        addView(layout, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addView(
            tvClose,
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.END or Gravity.TOP
            }
        )
    }

    private fun createTextView(): TextView {
        return TextView(context).apply {
            setTextColor(Color.WHITE)
            textSize = 10f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 4.dp2px
            }
        }
    }

    fun updateInfo(version: String, activityName: String, customInfo: String) {
        tvVersion.text = "版本: $version"
        tvActivity.text = "界面: $activityName"
        if (customInfo.isEmpty()) {
            tvCustomInfo.visibility = GONE
        } else {
            tvCustomInfo.visibility = VISIBLE
            tvCustomInfo.text = "其他: $customInfo"
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY
                
                // 更新自身位置
                translationX += dx
                translationY += dy
                
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
                onDragListener?.invoke(translationX, translationY)
            }
        }
        return true
    }
}
