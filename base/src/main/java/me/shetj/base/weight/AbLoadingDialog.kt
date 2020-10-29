package me.shetj.base.weight

import android.app.Dialog
import android.content.Context
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 *   must   android:configChanges="orientation|keyboardHidden|screenSize"
 */
abstract class AbLoadingDialog {

    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate) : CoroutineScope

    private var weakReference :WeakReference<Context> ? =null
    private var mLoadingDialog: Dialog? = null
    private val lazyScope = lazy { LoadingScope() }
    private val lazyComposite = lazy { CompositeDisposable() }
    private val mCompositeDisposable: CompositeDisposable by lazyComposite

    val coroutineScope: LoadingScope by lazyScope

    abstract fun createLoading(context: Context, cancelable: Boolean): Dialog?

    fun showLoading(context: Context, cancelable: Boolean): Dialog? {
        initDialog(context)
        mLoadingDialog?.let {
            if (!mLoadingDialog!!.isShowing) {
                mLoadingDialog!!.show()
            }
        }
        return mLoadingDialog
    }

    fun showLoading(context: Context): Dialog? {
        initDialog(context)
        mLoadingDialog?.let {
            if (!mLoadingDialog!!.isShowing) {
                mLoadingDialog!!.show()
            }
        }
        return mLoadingDialog
    }



    fun hideLoading() {
        if (null != mLoadingDialog && mLoadingDialog!!.isShowing) {
            mLoadingDialog!!.dismiss()
        }
    }


    private fun initDialog(context: Context) {
        if (mLoadingDialog == null || context != weakReference?.get()) {
            weakReference = WeakReference(context)
            mLoadingDialog = createLoading(context, true)?.apply {
                initSetting()
            }
        }
    }

    private fun Dialog.initSetting() {
        setCanceledOnTouchOutside(false)
        setOnDismissListener {
            if (lazyScope.isInitialized()) {
                coroutineScope.cancel()
            }
            if (lazyComposite.isInitialized()) {
                mCompositeDisposable.clear()
            }
        }
        setOnCancelListener {
            if (lazyScope.isInitialized()) {
                coroutineScope.cancel()
            }
            if (lazyComposite.isInitialized()) {
                mCompositeDisposable.clear()
            }
        }
    }

    /**
     * 协程一起使用
     * 任务结束后自定退出
     */
    inline fun showWithAction(context: Context, crossinline action: suspend () -> Unit): AbLoadingDialog {
        showLoading(context)
        coroutineScope.launch {
            action()
            hideLoading()
        }
        return this
    }

    /**
     * 和RxJava 一起使用
     * 需要自行退出loading
     */
    fun showWithRxAction(context: Context, action: () -> Disposable): AbLoadingDialog {
        showLoading(context)
        mCompositeDisposable.add(action())
        return this
    }

    /**
     * 和RxJava 一起使用，
     * 绑定loading的生命周期
     */
    fun showWithRxAction(context: Context, action: Observable<*>): AbLoadingDialog {
        showLoading(context)
        action.doOnComplete(::hideLoading)
        mCompositeDisposable.add(action.subscribe())
        return this
    }

    /**
     * 和RxJava 一起使用
     * 需要自行退出loading
     */
    fun showWithDisposable(context: Context, disposable: Disposable): AbLoadingDialog {
        showLoading(context)
        mCompositeDisposable.add(disposable)
        return this
    }
}