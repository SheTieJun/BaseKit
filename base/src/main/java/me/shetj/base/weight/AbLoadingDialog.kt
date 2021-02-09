package me.shetj.base.weight

import android.app.Activity
import android.app.Dialog
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.LongDef
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import me.shetj.base.S.handler
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 *   must  【 android:configChanges="orientation|keyboardHidden|screenSize"】
 */
abstract class AbLoadingDialog {

    companion object {
        const val LOADING_LONG = 1800L

        const val LOADING_SHORT = 800L

    }

    @LongDef(LOADING_LONG, LOADING_SHORT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    public annotation class LoadingTipsDuration


    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate + handler) : CoroutineScope

    private var weakReference: WeakReference<Context>? = null
    private var mLoadingDialog: AlertDialog? = null
    private val lazyScope = lazy { LoadingScope() }
    private val lazyComposite = lazy { CompositeDisposable() }
    private val mCompositeDisposable: CompositeDisposable by lazyComposite

    val coroutineScope: LoadingScope by lazyScope

    abstract fun createLoading(context: Context, cancelable: Boolean = false, msg: CharSequence = "加载中...", @DrawableRes image: Int? = null): AlertDialog?


    fun showLoading(context: Context, cancelable: Boolean = true, msg: CharSequence = "加载中", @DrawableRes image: Int? = null): AlertDialog {
        initDialog(context, cancelable, msg, image)
        mLoadingDialog?.let {
            if ((context as Activity).isFinishing) {
                return mLoadingDialog!!
            }
            if (!mLoadingDialog!!.isShowing) {
                mLoadingDialog!!.show()
            }
        }
        return mLoadingDialog!!
    }

    fun hideLoading() {
        if (null != mLoadingDialog && mLoadingDialog!!.isShowing) {
            mLoadingDialog!!.dismiss()
        }
    }

    private fun initDialog(context: Context, cancelable: Boolean = true, msg: CharSequence = "加载中", @DrawableRes image: Int? = null) {
        if (mLoadingDialog == null || context != weakReference?.get()) {
            weakReference = WeakReference(context)
            mLoadingDialog = createLoading(context, cancelable, msg, image)?.apply {
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
    fun showWithRxAction(context: Context, action: (dialog: AbLoadingDialog) -> Disposable): AbLoadingDialog {
        showLoading(context)
        mCompositeDisposable.add(action(this))
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

    fun showTip(context: Context, cancelable: Boolean, msg: CharSequence = "加载中", @DrawableRes image: Int?, @LoadingTipsDuration time: Long = LOADING_SHORT): AbLoadingDialog {
        showLoading(context, cancelable, msg, image)
        mCompositeDisposable.add(AndroidSchedulers.mainThread().scheduleDirect({
            hideLoading()
        }, time, TimeUnit.MILLISECONDS))
        return this
    }
}