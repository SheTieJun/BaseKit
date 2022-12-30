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
package me.shetj.base.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.viewbinding.ViewBinding
import me.shetj.base.ktx.getClazz

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 *
 *
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 *
 *
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 *
 *
 * 可见: [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
abstract class BaseBindingFragment<T : BasePresenter<*>, VB : ViewBinding> : BaseFragment<T>() {

    protected lateinit var mViewBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = initViewBinding(inflater, container)
        initEventAndData()
        return mViewBinding.root
    }

    @Suppress("UNCHECKED_CAST")
    open fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return getClazz<VB>(this, 1).getMethod(
            "inflate",
            LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        ).invoke(null, inflater, container, false) as VB
    }
}
