

package shetj.me.base.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.*
import shetj.me.base.R
import kotlin.coroutines.CoroutineContext

/**
 * I think QMUIDialog is better but maybe use
 */
class LoadingFragmentDialog : DialogFragment {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)


    class LoadingScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate) : CoroutineScope

    private val lazyScope = lazy { LoadingScope() }
    val coroutineScope: LoadingScope by lazyScope

    fun showLoading(context: AppCompatActivity, cancelable: Boolean): DialogFragment? {
        isCancelable = cancelable
        setStyle(STYLE_NORMAL, R.style.transparent_dialog_fragment_style)
        show(context.supportFragmentManager, "Loading")
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_dialog_loading, container, false)

    }

    fun showLoading(context: AppCompatActivity): DialogFragment? {
        return showLoading(context, true)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (lazyScope.isInitialized()) {
            coroutineScope.cancel()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (lazyScope.isInitialized()) {
            coroutineScope.cancel()
        }
    }

    inline fun showWithAction(context: AppCompatActivity, crossinline action: suspend CoroutineScope.() -> Unit) {
        showLoading(context)
        coroutineScope.launch {
            action()
            dismissAllowingStateLoss()
        }
    }

}