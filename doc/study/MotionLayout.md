1. MotionHelper
```
可以设置id,tag
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
```

2. 自定义MotionLayout
```
自定义progress 获取 
class CollapsibleToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        progress = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.addOnOffsetChangedListener(this)
    }
}
```

3. 把父布局MotionLayout事件，传递给子布局
```
MotionLayout.TransitionListener 
```
