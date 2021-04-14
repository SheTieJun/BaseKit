package me.shetj.base.mvvm

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseViewModel : ViewModel() {
    private var mCompositeDisposable: CompositeDisposable? = null

    /**
     * 将 [Disposable] 添加到 [CompositeDisposable] 中统一管理
     * 可在[android.app.Activity.onDestroy] 释放
     */
    fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    fun unDispose() {
        mCompositeDisposable?.clear()
    }

    override fun onCleared() {
        unDispose()
        super.onCleared()
    }
}