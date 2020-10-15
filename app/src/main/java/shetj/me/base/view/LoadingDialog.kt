package shetj.me.base.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import shetj.me.base.R
import kotlin.coroutines.CoroutineContext

/**
 * I think QMUIDialog is better but maybe use
 */
class LoadingDialog {

    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate) : CoroutineScope

    private var mLoadingDialog: Dialog? = null
    private val lazyScope = lazy {LoadingScope()}
    private val lazyComposite = lazy { CompositeDisposable() }
    private val mCompositeDisposable: CompositeDisposable by lazyComposite

    val coroutineScope: LoadingScope by lazyScope

    private fun createLoading(context: Context, cancelable: Boolean): Dialog? {
        if (mLoadingDialog == null) {
            val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
            mLoadingDialog = Dialog(context, R.style.CustomProgressDialog)
            mLoadingDialog!!.setCancelable(cancelable)
            mLoadingDialog!!.setCanceledOnTouchOutside(false)
            mLoadingDialog!!.setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
            mLoadingDialog?.setOnDismissListener {
                if (lazyScope.isInitialized()) {
                    coroutineScope.cancel()
                }
                if (lazyComposite.isInitialized()){
                    mCompositeDisposable.clear()
                }
            }
        }
        return mLoadingDialog
    }

    fun showLoading(context: Context, cancelable: Boolean): Dialog? {
        mLoadingDialog = createLoading(context, cancelable)
        if (!mLoadingDialog!!.isShowing){
            mLoadingDialog!!.show()
        }
        return mLoadingDialog
    }

    fun showLoading(context: Context): Dialog? {
        mLoadingDialog = createLoading(context, true)
        if (!mLoadingDialog!!.isShowing){
            mLoadingDialog!!.show()
        }
        return mLoadingDialog
    }

    fun hideLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog!!.dismiss()
        }
    }

    companion object {

        /**
         * 和协程一起使用
         */
        inline fun showWithAction(context: Context, crossinline action: suspend () -> Unit): LoadingDialog {
            return LoadingDialog().apply {
                showLoading(context)
                coroutineScope.launch {
                    action()
                }
            }
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithRxAction(context: Context,action :() -> Disposable): LoadingDialog {
            return LoadingDialog().apply {
                showLoading(context)
                mCompositeDisposable.add(action())
            }
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithDisposable(context: Context,disposable :Disposable): LoadingDialog {
            return LoadingDialog().apply {
                showLoading(context)
                mCompositeDisposable.add(disposable)
            }
        }

        @JvmStatic
        fun showNoAction(context: Context,cancelable: Boolean = true): Dialog? {
            return LoadingDialog().showLoading(context,cancelable)
        }
    }

}