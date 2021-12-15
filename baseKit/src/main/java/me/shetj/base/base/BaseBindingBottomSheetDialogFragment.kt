/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
           return (dialog as BottomSheetDialog).behavior
        }
        return null
    }

    /**
     * 展开到全屏
     */
    open fun expandScreen(){
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
            //防止动画没有结束，fragment又被点击了，然后导致的崩溃
            fragmentManager.executePendingTransactions()
            show(fragmentManager, tag)
        }
    }

    fun safetyHide() {
        runCatching { dismissAllowingStateLoss() }
    }
}
