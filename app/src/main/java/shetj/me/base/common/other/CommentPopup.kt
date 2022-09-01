/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package shetj.me.base.common.other


import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.toJson
import me.shetj.base.tools.app.KeyboardUtil
import me.shetj.base.tools.app.SoftInputUtil
import shetj.me.base.R


class CommentPopup : BottomSheetDialogFragment() {

    private var root: View? = null

    private var editContent: EditText? = null
    private var tvSend: View? = null
    private val softInputUtil by lazy { SoftInputUtil() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.popupwindow_comment, null)
        val hierarchy = PerformanceMetricsState.getHolderForHierarchy(rootView)
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
                KeyboardUtil.hideSoftKeyboard(requireActivity())
                dismissAllowingStateLoss()
            }
        }

        root = rootView.findViewById<View>(R.id.root).apply {
            setOnClickListener {
                KeyboardUtil.hideSoftKeyboard(requireActivity())
                dismissAllowingStateLoss()
            }
        }

        softInputUtil.attachSoftInput(dialog?.window, object : SoftInputUtil.ISoftInputChanged {
            override fun onChanged(isSoftInputShow: Boolean) {
                if (!isSoftInputShow) {
                    hierarchy.state?.putState("input","hide")
                    dismissAllowingStateLoss()
                }
            }
        })

        dialog?.setOnShowListener {
            editContent?.postDelayed({
                hierarchy.state?.putState("input","show")
                KeyboardUtil.focusEditShowKeyBoard(editContent!!)
            }, 50)
        }
        return rootView
    }

    fun show(manager: FragmentManager) {
        manager.executePendingTransactions()
        show(manager, "commentPopup")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.let {
            JankStats.createAndTrack(it){
                if (it.isJank){
                    ((it.frameDurationUiNanos/1000000).toString()+"毫秒").logI("JankStats")
                }
                it.toJson().logI("JankStats")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        softInputUtil.dismiss()
    }

    companion object {
        fun newInstance(): CommentPopup {
            return CommentPopup()
        }
    }
}
