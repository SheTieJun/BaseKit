package me.shetj.base.base


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import me.shetj.base.R
import me.shetj.base.tools.app.KeyboardUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class AbBaseActivity: AppCompatActivity() , LifecycleEventObserver {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAnimation()
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
         when(event){
             Lifecycle.Event.ON_CREATE ->{
                 onActivityCreate()
             }
             Lifecycle.Event.ON_DESTROY->{
                 onActivityDestroy()
             }
         }
    }

    open fun onActivityCreate() {
        if (useEventBus()) {
            //注册到事件主线
            EventBus.getDefault().register(this)
        }
        findViewById<View>(R.id.toolbar_back)?.setOnClickListener { back() }
        initView()
        initData()
    }

    open fun onActivityDestroy() {
        if (useEventBus()) {
            //如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    open fun setTitle(title: String) {
        findViewById<TextView>(R.id.toolbar_title)?.apply {
            text = title
        }
    }

    //设置横竖屏
    open fun setOrientation(landscape: Boolean) {
        requestedOrientation = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * 连接view
     */
    protected abstract fun initView()

    /**
     * 连接数据
     */
    protected abstract fun initData()


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //true - 界面加载成功的时候
    }

    /**
     * 是否使用eventBus,默认为使用(false)，
     *
     * @return useEventBus
     */
    open fun useEventBus(): Boolean {
        return false
    }

    /**
     * 界面开始动画 (此处输入方法执行任务.)
     */
    open fun startAnimation() {}

    /**
     * 界面回退动画 (此处输入方法执行任务.)
     */
    open fun endAnimation() {}

    /**
     * 用来替换 [finish] 返回
     */
    open fun back() {
        finishAfterTransition()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
    }
}
