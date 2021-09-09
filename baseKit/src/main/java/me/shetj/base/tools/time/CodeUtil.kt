package me.shetj.base.tools.time

import android.widget.TextView
import androidx.annotation.Keep
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.shetj.base.base.ABKtScopeComponent
import me.shetj.base.base.LifecycleKtScopeComponent
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Keep
class CodeUtil : ABKtScopeComponent {
    private var codeTV: TextView? = null
    private var second: Int = 0
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

    fun start() {
        codeTV?.post { codeTV?.isEnabled = false }
        job?.cancel()
        job = ktScope.launch {
            repeat(timeLength) {
                codeTV?.text = String.format("%s秒后重新获取", second)
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

