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

import android.annotation.SuppressLint
import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import me.shetj.base.S
import me.shetj.base.base.AbBaseFragment
import me.shetj.base.ktx.getClazz
import me.shetj.base.ktx.toJson
import me.shetj.base.tools.json.EmptyUtils
import timber.log.Timber

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 * 可见:     [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
abstract class BaseFragment<T : BasePresenter<*>> : AbBaseFragment(), IView {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    override val rxContext: AppCompatActivity
        get() = (requireActivity() as AppCompatActivity?)!!


    /**
     * 抽象类不能反射
     *
     */
    open fun initPresenter(): T {
        return  getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }


    override fun onDestroyView() {
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
        super.onDestroyView()
    }

    @SuppressLint("unchecked")
    override fun updateView(message: Message) {
        if (S.isDebug && EmptyUtils.isNotEmpty(message)) {
            Timber.i(message.toJson())
        }
    }
}
