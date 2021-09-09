package me.shetj.base.view.floatview

import android.content.*
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import me.shetj.base.tools.app.BarUtils
import me.shetj.base.tools.app.FloatKit
import me.shetj.base.tools.app.FloatKit.checkFloatPermission
import me.shetj.base.tools.app.FloatKit.getWinManager

abstract class BaseFloatView : FrameLayout {
    protected lateinit var windowParams: WindowManager.LayoutParams
    protected lateinit var winManager: WindowManager

    /**
     * 获取悬浮窗中的视频播放view
     */
    private var mStatusBarHeight = statusBarHeight // 系统状态栏的高度
    private var mXDownInScreen = 0f// 按下事件距离屏幕左边界的距离
    private var mYDownInScreen = 0f // 按下事件距离屏幕上边界的距离
    private var mXInScreen = 0f// 滑动事件距离屏幕左边界的距离
    private var mYInScreen = 0f // 滑动事件距离屏幕上边界的距离
    private var mXInView = 0f// 滑动事件距离自身左边界的距离
    private var mYInView = 0f// 滑动事件距离自身上边界的距离

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }


    /**
     * 初始化view
     */
   abstract fun initView(context: Context)

    open fun addToWindowManager(layout: ViewRect.() -> Unit) {
        if (context.checkFloatPermission()) {
            if (!this::winManager.isInitialized) {
                winManager = context.getWinManager()
                val rect = ViewRect(0, 0, 0, 0).apply(layout)
                windowParams = FloatKit.getWindowParams().apply{
                    x = rect.x
                    y = rect.y
                    width = rect.width
                    height = rect.height
                }
            }
        }
        if (this.parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        winManager.addView(this, windowParams)
    }

    open fun removeFormWindowManager() {
        if (this::winManager.isInitialized) {
            winManager.removeView(this)
        }
    }


    /**
     * 重写触摸事件监听，实现悬浮窗随手指移动
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mXInView = event.x
                mYInView = event.y
                mXInScreen = event.rawX
                mYInScreen = event.rawY - mStatusBarHeight
            }
            MotionEvent.ACTION_MOVE -> {
                mXInScreen = event.rawX
                mYInScreen = event.rawY - mStatusBarHeight
                updateViewPosition()
            }
            MotionEvent.ACTION_UP -> if (mXDownInScreen == mXInScreen
                && mYDownInScreen == mYInScreen
            ) { //手指没有滑动视为点击，回到窗口模式
                performClick()
            }
            else -> {
            }
        }
        return true
    }


    /**
     * 获取系统状态栏高度
     */
    private val statusBarHeight: Int
        get() {
            if (mStatusBarHeight == 0) {
                BarUtils.getStatusBarHeight(context).also {
                    mStatusBarHeight = it
                }
            }
            return mStatusBarHeight
        }

    private fun updateViewPosition() {
        if (this::winManager.isInitialized) {
            val x = (mXInScreen - mXInView).toInt()
            val y = (mYInScreen - mYInView).toInt()
            windowParams.x = x
            windowParams.y = y
            winManager.updateViewLayout(this, windowParams)
        }
    }
}