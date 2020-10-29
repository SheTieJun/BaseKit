package shetj.me.base.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.reactivex.rxjava3.disposables.Disposable
import me.shetj.base.weight.AbLoadingDialog
import shetj.me.base.R


/**
 * android:configChanges="orientation|keyboardHidden|screenSize"
 */
class SimLoadingDialog : AbLoadingDialog() {

    override fun createLoading(context: Context, cancelable: Boolean): Dialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        return Dialog(context, R.style.CustomProgressDialog).apply {
            setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
            setCancelable(cancelable)
        }
    }




    companion object {
        /**
         * 和协程一起使用
         */
        inline fun showWithAction(context: Context, crossinline action: suspend () -> Unit): AbLoadingDialog {
            return SimLoadingDialog().showWithAction(context, action)
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithRxAction(context: Context, action: () -> Disposable): AbLoadingDialog {
            return SimLoadingDialog().showWithRxAction(context, action)
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithDisposable(context: Context, disposable: Disposable): AbLoadingDialog {
            return SimLoadingDialog().showWithDisposable(context,disposable)
        }

        @JvmStatic
        fun showNoAction(context: Context, cancelable: Boolean = true): Dialog? {
            return SimLoadingDialog().showLoading(context, cancelable)
        }
    }

}