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

import android.content.Intent
import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import me.shetj.base.ktx.getObjByClassArg
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.toMessage
import timber.log.Timber

/**
 *
 * 在model中获取数据，在view（Activity）中展示数据
 * @author shetj
 */
@Keep
open class BasePresenter<T : BaseModel>(protected var view: IView) : CoroutineScope {

    protected val model: T by lazy { initModel() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    internal val rxContext: AppCompatActivity
        get() = view.rxContext

    init {
        onStart()
    }

    open fun initModel(): T {
        return getObjByClassArg(this)
    }

    open fun onStart() {

    }

    /**
     * //解除订阅
     * [BaseActivity.onDestroy] 调用[IPresenter.onDestroy]
     */
    @CallSuper
      fun onDestroy() {
        coroutineContext.cancelChildren()
        model.onDestroy()
    }

    fun startActivity(intent: Intent) {
        view.rxContext.startActivity(intent)
    }

    fun updateView(code: Int, msg: Any) {
        view.updateView(msg.toMessage(code))
    }
}
