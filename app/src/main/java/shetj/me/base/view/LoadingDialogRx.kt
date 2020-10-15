package shetj.me.base.view

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import me.shetj.base.ktx.default
import me.shetj.base.ktx.doOnContext
import me.shetj.base.ktx.doOnMain
import me.shetj.base.ktx.runOnMain
import shetj.me.base.R
import kotlin.coroutines.CoroutineContext

/**
 * I think QMUIDialog is better but maybe use
 */
class LoadingDialogRx {
    private var mLoadingDialog: Dialog? = null

    private val mCompositeDisposable:CompositeDisposable = CompositeDisposable()

    fun showLoading(context: Activity?, cancelable: Boolean): Dialog? {
        if (null != mLoadingDialog) {
            mLoadingDialog!!.cancel()
        }
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        mLoadingDialog = Dialog(context!!, R.style.CustomProgressDialog)
        mLoadingDialog!!.setCancelable(cancelable)
        mLoadingDialog!!.setCanceledOnTouchOutside(false)
        mLoadingDialog!!.setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
        mLoadingDialog?.setOnDismissListener {
            mCompositeDisposable.clear()
        }
        mLoadingDialog!!.show()
        return mLoadingDialog
    }

    fun showLoading(context: Activity?): Dialog? {
        if (null != mLoadingDialog) {
            mLoadingDialog!!.cancel()
        }
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        mLoadingDialog = Dialog(context!!, R.style.CustomProgressDialog)
        mLoadingDialog!!.setCancelable(true)
        mLoadingDialog!!.setCanceledOnTouchOutside(false)
        mLoadingDialog!!.setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
        mLoadingDialog?.setOnDismissListener {
            mCompositeDisposable.clear()
        }
        mLoadingDialog!!.show()
        return mLoadingDialog
    }

    fun hideLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog!!.cancel()
        }
    }

    fun addDispose(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate) : CoroutineScope {

    }

}