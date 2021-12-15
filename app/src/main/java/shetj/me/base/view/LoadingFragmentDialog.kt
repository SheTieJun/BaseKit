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


package shetj.me.base.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.*
import shetj.me.base.R
import kotlin.coroutines.CoroutineContext

/**
 * I think QMUIDialog is better but maybe use
 */
class LoadingFragmentDialog : DialogFragment {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)


    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate) : CoroutineScope

    private val lazyScope = lazy { LoadingScope() }
    val coroutineScope: LoadingScope by lazyScope

    fun showLoading(context: AppCompatActivity, cancelable: Boolean): DialogFragment? {
        isCancelable = cancelable
        setStyle(STYLE_NORMAL, R.style.transparent_dialog_fragment_style)
        show(context.supportFragmentManager, "Loading")
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_dialog_loading, container, false)

    }

    fun showLoading(context: AppCompatActivity): DialogFragment? {
        return showLoading(context, true)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (lazyScope.isInitialized()) {
            coroutineScope.cancel()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (lazyScope.isInitialized()) {
            coroutineScope.cancel()
        }
    }

    inline fun showWithAction(context: AppCompatActivity, crossinline action: suspend CoroutineScope.() -> Unit) {
        showLoading(context)
        coroutineScope.launch {
            action()
            dismissAllowingStateLoss()
        }
    }

}