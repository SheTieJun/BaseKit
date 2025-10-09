package me.shetj.base.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.shetj.base.tools.app.ArmsUtils

/**
 * Base binding bottom sheet dialog fragment
 * @param VB binding:可以是DataBinding/ViewBinding
 */
@Keep
abstract class BaseBindingBottomSheetDialogFragment<VB : ViewBinding> :
    BottomSheetDialogFragment(), BaseControllerFunctionsImpl {

    protected lateinit var mBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = initBinding(inflater, container)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = this
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBaseView()
        addObservers()
        setUpClicks()
        onInitialized()
    }

    open fun getBehavior(): BottomSheetBehavior<FrameLayout>? {
        if (dialog is BottomSheetDialog) {
            return (dialog as BottomSheetDialog).behavior
        }
        return null
    }

    /**
     * 展开到全屏
     */
    open fun expandScreen() {
        getBehavior()?.state = BottomSheetBehavior.STATE_EXPANDED
        getBehavior()?.peekHeight = ArmsUtils.getScreenHeight()
    }

    /**
     * 系统会默认生成对应的[ViewBinding]
     */
    @NonNull
    protected abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }

    fun tryShow(fragmentManager: FragmentManager, tag: String) {
        runCatching {
            fragmentManager.executePendingTransactions()
            if (this.isAdded) {
                val fragment = fragmentManager.findFragmentByTag(tag)
                if (fragment != null && fragment.isHidden) {
                    fragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
                }
                return
            }
            show(fragmentManager, tag)
        }
    }

    fun tryHide() {
        runCatching { dismissAllowingStateLoss() }
    }
}
