package shetj.me.base.common.other


import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.app.BarUtils
import me.shetj.base.tools.app.KeyboardUtil
import shetj.me.base.R
import java.util.concurrent.atomic.AtomicBoolean


class CommentPopup : DialogFragment() {

    private var root: View? = null
    private val handler = Handler(Handler.Callback {
        editContent?.let { it1 -> KeyboardUtil.requestFocusEdit(it1) }
        root?.apply {
            layoutParams = FrameLayout.LayoutParams(ArmsUtils.getScreenWidth(), ArmsUtils.getScreenHeight() - BarUtils.getStatusBarHeight(context))
        }
        false
    })

    private var editContent: EditText? = null
    private var tvSend: View? = null
    private val lock = AtomicBoolean(false) //用来防止连续的点击

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.transparent_dialog_fragment_style).apply {
            window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.popupwindow_comment, null)

        editContent = rootView.findViewById(R.id.edit_content)
        tvSend = rootView.findViewById(R.id.tv_send)
        tvSend?.isEnabled = false
        editContent?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                tvSend?.isEnabled = !TextUtils.isEmpty(editable.toString())
            }
        })
        tvSend = rootView.findViewById<View>(R.id.tv_send)?.apply {
            setOnClickListener {
                        KeyboardUtil.hideSoftInputMethod(editContent)
                        dismissAllowingStateLoss()
            }
        }

        root = rootView.findViewById<View>(R.id.root)
        root?.apply {
            layoutParams = FrameLayout.LayoutParams(ArmsUtils.getScreenWidth(), ArmsUtils.getScreenHeight() - BarUtils.getStatusBarHeight(context))
        }?.setOnClickListener {
            KeyboardUtil.hideSoftInputMethod(editContent)
            dismissAllowingStateLoss()
        }

        rootView.findViewById<View>(R.id.linearLayout_root)?.apply {
            addOnLayoutChangeListener { _, _, top, _, _, _, _, _, _ ->
                val location = IntArray(2)
                this.getLocationOnScreen(location)
                if (location[1] != 0 && location[1] <= top) {
                    KeyboardUtil.hideSoftInputMethod(editContent)
                    dismissAllowingStateLoss()
                }
            }
        }
        dialog?.setOnShowListener {
            editContent?.let {
                handler.sendEmptyMessageDelayed(0, 100)
            }
        }
        return rootView
    }

    fun show(manager: FragmentManager) {
        show(manager, "commentPopup")
    }

    companion object {
        fun newInstance(): CommentPopup {
            return CommentPopup().apply {
                isCancelable = false
            }
        }
    }
}
