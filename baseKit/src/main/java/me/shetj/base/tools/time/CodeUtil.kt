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
package me.shetj.base.tools.time

import android.widget.TextView
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.shetj.base.base.ABKtScopeComponent

@Keep
class CodeUtil : ABKtScopeComponent {
    private var codeTV: TextView? = null
    private var timeLength = 60
    private var job: Job? = null

    constructor(tv: TextView) {
        codeTV = tv
    }

    constructor(tv: TextView, timeLength: Int) {
        codeTV = tv
        this.timeLength = timeLength
        codeTV?.setOnClickListener {
            start()
        }
    }

    fun register(lifecycle: Lifecycle) {
        ktScope.register(lifecycle)
    }

    fun start() {
        codeTV?.post { codeTV?.isEnabled = false }
        job?.cancel()
        job = ktScope.launch {
            repeat(timeLength) { second ->
                codeTV?.text = String.format("%s秒后重新获取", timeLength - second)
                delay(1000)
            }
            codeTV?.text = "重新获取"
            codeTV?.isEnabled = true
        }
    }

    fun stop() {
        codeTV?.post {
            codeTV?.text = "获取验证码"
            codeTV?.isEnabled = true
        }
        job?.cancel()
    }
}
