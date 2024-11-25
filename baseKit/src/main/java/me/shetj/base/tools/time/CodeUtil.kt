package me.shetj.base.tools.time

import android.widget.TextView
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.shetj.base.lifecycle.ABKtScopeComponent

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
