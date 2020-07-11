package me.shetj.base.tools.time

import android.widget.TextView
import androidx.annotation.Keep
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Keep
class CodeUtil {
    private var codeTV: TextView? = null
    private var second: Int = 0
    private var timeLength = 60
    private var mCompositeDisposable: CompositeDisposable? = null

    constructor(tv: TextView) {
        codeTV = tv
    }

    constructor(tv: TextView, timeLength: Int) {
        codeTV = tv
        this.timeLength = timeLength
    }

    fun start() {
        second = timeLength
        codeTV?.post { codeTV?.isEnabled = false }
        val subscribe =
                Flowable.interval(1, TimeUnit.SECONDS)
                        .take(timeLength.toLong())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            second--
                            codeTV?.text = String.format("%s秒后重发", second)
                        }, {
                            stop()
                        }, {
                            codeTV?.text = "获取验证码"
                            codeTV?.isEnabled = true
                        })
        addDispose(subscribe)
    }


    fun stop() {
        Timber.i("stop_code_util")
        unDispose()
        this.mCompositeDisposable = null
        codeTV?.post {
            codeTV?.text = "获取验证码"
            codeTV?.isEnabled = true
        }
    }

    /**
     * 将 [Disposable] 添加到 [CompositeDisposable] 中统一管理
     * 可在 [android.app.Activity.onDestroy]中使用
     */
    private fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 停止集合中正在执行的 [RxJava] 任务
     */
    private fun unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable?.clear()
        }
    }

}

