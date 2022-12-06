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
package me.shetj.base.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import me.shetj.base.base.AbBaseFragment
import me.shetj.base.ktx.getClazz
import me.shetj.base.tip.TipKit
import me.shetj.base.tip.TipType.DEFAULT
import me.shetj.base.tip.TipType.ERROR
import me.shetj.base.tip.TipType.INFO
import me.shetj.base.tip.TipType.SUCCESS
import me.shetj.base.tip.TipType.WARNING

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
 *
 * if stop -> 到可见，需要start
 */
@Keep
abstract class BaseBindingFragment<VB : ViewBinding, VM : BaseViewModel> : AbBaseFragment(), Observer<ViewAction> {

    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null

    private val lazyViewModel = lazy { initViewModel() }
    protected val mViewModel: VM by lazyViewModel

    protected lateinit var mViewBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = initViewBinding(inflater, container)
        return mViewBinding.root
    }

    /**
     * 会默认获取到对应的[ViewBinding]
     */
    @Suppress("UNCHECKED_CAST")
    @NonNull
    open fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return getClazz<VB>(this, 0).getMethod(
            "inflate",
            LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
            .invoke(null, inflater, container, false) as VB
    }


    override fun viewBindData() {
        super.viewBindData()
        mViewModel.baseAction.observe(this, this)
    }

    override fun onChanged(action: ViewAction?) {
        if (action is TipAction){
            when(action.tipType){
                DEFAULT -> TipKit.normal(requireActivity(),action.msg)
                INFO -> TipKit.info(requireActivity(),action.msg)
                ERROR -> TipKit.error(requireActivity(),action.msg)
                SUCCESS -> TipKit.success(requireActivity(),action.msg)
                WARNING -> TipKit.warn(requireActivity(),action.msg)
            }
        }
    }
    /**
     * 默认创建一个
     *
     * [useActivityVM] = true 才会使用activity的[ViewModel]
     */
    @NonNull
    open fun initViewModel(): VM {
        if (useActivityVM()) {
            return getActivityViewModel(getClazz(this, 1))
        }
        return getFragmentViewModel(getClazz(this, 1))
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * 是否使用Activity的VM，默认使用
     */
    open fun useActivityVM() = true

    protected open fun getFragmentViewModel(@NonNull modelClass: Class<VM>): VM {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelClass]
    }

    protected open fun getActivityViewModel(@NonNull modelClass: Class<VM>): VM {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(requireActivity())
        }
        return mActivityProvider!![modelClass]
    }
}
