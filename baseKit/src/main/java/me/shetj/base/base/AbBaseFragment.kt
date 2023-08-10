package me.shetj.base.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 *
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 *
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 *
 * 可见: [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
abstract class AbBaseFragment : Fragment(), LifecycleEventObserver,BaseControllerFunctionsImpl{

    protected var enabledOnBack: Boolean = false
        set(value) {
            field = value
            onBackPressedCallback.isEnabled = value
        }

    // 拦截activity的onBack，默认不拦截
    protected val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            onBack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
        if (savedInstanceState != null && !isNavigationF()) {
            // 如果是导航的fragment，不需要恢复状态Navigation回去恢复
            val flag = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            if (flag) {
                transaction.show(this)
            } else {
                transaction.hide(this)
            }
            transaction.commit()
        }
    }

    /**
     * 是否是导航的fragment
     */
    open fun isNavigationF()  = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBaseView()
        addObservers()
        setUpClicks()
        onInitialized()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
    }

    open fun onBack() {
        enabledOnBack = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackPressedCallback.isEnabled = enabledOnBack
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            ON_RESUME -> onVisible()
            ON_PAUSE -> onInvisible()
            else -> {}
        }
    }

    /**
     * On visible.
     */
    open fun onVisible() {
    }

    /**
     * On invisible.
     */
    open fun onInvisible() {
    }


    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }
}
