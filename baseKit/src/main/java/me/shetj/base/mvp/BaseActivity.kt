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

import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.Disposable
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.ktx.getClazz

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class BaseActivity<T : BasePresenter<*>> : AbBaseActivity(), IView {
    protected val lazyPresenter = lazy { initPresenter() }
    protected val mPresenter: T by lazyPresenter

    override val rxContext: AppCompatActivity
        get() = this

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        if (lazyPresenter.isInitialized()) {
            mPresenter.onDestroy()
        }
    }

    override fun initView() {
    }

    override fun initData() {
    }

    /**
     * 默认通过反射创建 T：BasePresenter
     * 可以重新 返回对应的实例 或者单例
     * 实现思想：
     *    首先Activity<Presenter> -> Presenter.class -> Presenter的参数构造函数 -> newInstance
     */
    open fun initPresenter(): T {
        return getClazz<T>(this).getConstructor(IView::class.java).newInstance(this)
    }

    fun addDispose(disposable: Disposable) {
        mPresenter.addDispose(disposable)
    }

    override fun updateView(message: Message) {
    }
}
