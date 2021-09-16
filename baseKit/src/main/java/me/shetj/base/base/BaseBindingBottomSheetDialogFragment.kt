package me.shetj.base.base

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.shetj.base.tools.app.ArmsUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Keep
abstract class BaseBindingBottomSheetDialogFragment<VB : ViewBinding> :
    BottomSheetDialogFragment() {

    protected lateinit var mViewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = initViewBinding(inflater, container)
        viewBindData()
        return mViewBinding.root
    }


    open fun getBehavior(): BottomSheetBehavior<FrameLayout>? {
        if (dialog is BottomSheetDialog) {
           return (dialog as BottomSheetDialog?)?.behavior
        }
        return null
    }

    /**
     * 展开到全屏
     */
    fun expandScreen(){
        getBehavior()?.state = BottomSheetBehavior.STATE_EXPANDED
        getBehavior()?.peekHeight = ArmsUtils.getScreenHeight()
    }

    /**
     * 系统会默认生成对应的[ViewBinding]
     */
    @NonNull
    abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    fun VB.setUp(action: VB.() -> Unit) {
        action.invoke(this)
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return boolean
     */
    open fun useEventBus(): Boolean {
        return false
    }

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    /**
     * 初始化数据和界面绑定
     */
    open fun viewBindData() {

    }


    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }

    fun safetyShow(fragmentManager: FragmentManager, tag: String) {
        runCatching {
            fragmentManager.executePendingTransactions()
            show(fragmentManager, tag)
        }
    }

    fun safetyHide() {
        runCatching { dismissAllowingStateLoss() }
    }
}
