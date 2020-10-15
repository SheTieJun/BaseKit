package shetj.me.base.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.coroutines.*
import shetj.me.base.R
import kotlin.coroutines.CoroutineContext

/**
 * I think QMUIDialog is better but maybe use
 */
class LoadingDialogKT {

    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate) : CoroutineScope

    private var mLoadingDialog: Dialog? = null
    private val lazyScope = lazy {LoadingScope()}

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

        inline fun showWithAction(context: Context, crossinline action: suspend CoroutineScope.() -> Unit): LoadingDialogKT {
            return LoadingDialogKT().apply {
                showLoading(context)
                coroutineScope.launch {
                    action()
                }
            }
        }

        fun showNoAction(context: Context): Dialog? {
            return LoadingDialogKT().showLoading(context)
        }
    }

}